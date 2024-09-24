import { readFileSync } from "fs";
import * as core from "@actions/core";
import OpenAI from "openai";
import { Octokit } from "@octokit/rest";
import parseDiff, { Chunk, File, Change } from "parse-diff";
import minimatch from "minimatch";

const GITHUB_TOKEN: string = core.getInput("GITHUB_TOKEN");
const OPENAI_API_KEY: string = core.getInput("OPENAI_API_KEY");
const OPENAI_API_MODEL: string = core.getInput("OPENAI_API_MODEL");
const FRAMEWORK: string = core.getInput("framework");

const octokit = new Octokit({ auth: GITHUB_TOKEN });
const openai = new OpenAI({ apiKey: OPENAI_API_KEY });

interface PRDetails {
  owner: string;
  repo: string;
  pull_number: number;
  title: string;
  description: string;
}

async function getPRDetails(): Promise<PRDetails> {
  const { repository, number } = JSON.parse(
      readFileSync(process.env.GITHUB_EVENT_PATH || "", "utf8")
  );
  const prResponse = await octokit.pulls.get({
    owner: repository.owner.login,
    repo: repository.name,
    pull_number: number,
  });
  return {
    owner: repository.owner.login,
    repo: repository.name,
    pull_number: number,
    title: prResponse.data.title ?? "",
    description: prResponse.data.body ?? "",
  };
}

async function getDiff(
    owner: string,
    repo: string,
    pull_number: number
): Promise<string | null> {
  const response = await octokit.pulls.get({
    owner,
    repo,
    pull_number,
    mediaType: { format: "diff" },
  });

  // Fix: return the response as a string
  return response.data as unknown as string; // Explicitly cast it to string
}

async function analyzeCode(
    parsedDiff: File[],
    prDetails: PRDetails
): Promise<Array<{ body: string; path: string; line: number }>> {
  const comments: Array<{ body: string; path: string; line: number }> = [];
  const existingComments = new Set<string>();

  for (const file of parsedDiff) {
    if (file.to === "/dev/null") continue;

    for (const chunk of file.chunks) {
      for (const change of chunk.changes) {
        if (!change.content.startsWith("+")) continue;

        const prompt = createPrompt(file, chunk, prDetails);
        const aiResponse = await getAIResponse(prompt);

        if (aiResponse) {
          const newComments = createComment(file, chunk, aiResponse, existingComments);
          if (newComments) {
            comments.push(...newComments);
          }
        }
      }
    }
  }
  return comments;
}

function getRailsGuidelines(): string {
  return `
  - **Environment Specific Code**: Avoid hard-coding values. Use configuration files or environment variables.
  - **Legacy Code**: Clean up unused code and ensure cross-environment compatibility.
  - **OOP Principles**: Follow SOLID principles.
  - **Database Performance**: Avoid N+1 queries. Use \`includes\` or \`preload\`. 
  `;
}

function getAngularGuidelines(): string {
  return `
  - **Component Structure**: Ensure components are small and focused on a single responsibility.
  - **Reactive Programming**: Prefer the use of RxJS for asynchronous operations.
  - **Testing**: Ensure comprehensive unit tests for components, services, and other classes.
  `;
}

function createPrompt(file: File, chunk: Chunk, prDetails: PRDetails): string {
  let guidelines = "";

  if (FRAMEWORK === "Ruby on Rails") {
    guidelines = getRailsGuidelines();
  } else if (FRAMEWORK === "Angular") {
    guidelines = getAngularGuidelines();
  }

  return `Your task is to review a pull request for ${FRAMEWORK} code. Follow these instructions:
  
  - Provide your response in JSON format: {"reviews": [{"lineNumber": <line_number>, "reviewComment": "<review comment>", "optimizedCode": "<optimized code>", "severity": "Low"|"Medium"|"High"}]}
  - Comment only where there is an issue or a suggestion for improvement. No positive comments.
  - Only include comments with a severity of "Medium" or "High".
  
${guidelines}

Git diff to review:
\`\`\`diff
${chunk.content}
${chunk.changes.map((c) => `${'ln' in c ? c.ln : 'ln2' in c ? c.ln2 : ''} ${c.content}`).join("\n")}
\`\`\`
  `;
}

async function getAIResponse(prompt: string): Promise<Array<{
  lineNumber: string;
  reviewComment: string;
  optimizedCode: string;
  severity: string;
}> | null> {
  const queryConfig = {
    model: OPENAI_API_MODEL,
    temperature: 0.2,
    max_tokens: 700,
    top_p: 1,
    frequency_penalty: 0,
    presence_penalty: 0,
  };

  try {
    const response = await openai.chat.completions.create({
      ...queryConfig,
      messages: [{ role: "system", content: prompt }],
    });

    const res = response.choices[0].message?.content?.trim() || "";
    const jsonContent = res.match(/```json([\s\S]*)```/)?.[1];

    if (!jsonContent) {
      return null;
    }

    return JSON.parse(jsonContent).reviews;
  } catch (error) {
    return null;
  }
}

function getSimilarityRatio(original: string, optimized: string): number {
  let changes = 0;
  const minLength = Math.min(original.length, optimized.length);
  for (let i = 0; i < minLength; i++) {
    if (original[i] !== optimized[i]) {
      changes++;
    }
  }
  return 1 - (changes / Math.max(original.length, optimized.length));
}

function isSignificantDifference(original: string, optimized: string): boolean {
  const threshold = 0.9;
  const similarityRatio = getSimilarityRatio(original, optimized);
  return similarityRatio < threshold;
}

function isDuplicateComment(comment: string, existingComments: Set<string>): boolean {
  if (existingComments.has(comment)) {
    return true;
  } else {
    existingComments.add(comment);
    return false;
  }
}

function createComment(
    file: File,
    chunk: Chunk,
    aiResponses: Array<{
      lineNumber: string;
      reviewComment: string;
      optimizedCode: string;
    }>,
    existingComments: Set<string>
): Array<{ body: string; path: string; line: number }> {
  return aiResponses.flatMap((aiResponse) => {
    if (!file.to) {
      return [];
    }

    const lineNumber = Number(aiResponse.lineNumber);
    const change = chunk.changes.find((c) => ("ln" in c && c.ln === lineNumber) || ("ln2" in c && c.ln2 === lineNumber));

    if (!change) {
      return [];
    }

    const commentLine = "ln" in change ? change.ln : "ln2" in change ? change.ln2 : 0;

    if (!isSignificantDifference(change.content, aiResponse.optimizedCode) || isDuplicateComment(aiResponse.reviewComment, existingComments)) {
      return [];
    }

    return [{
      body: `${aiResponse.reviewComment}\n\n**Optimized Code:**\n\`\`\`${FRAMEWORK === 'Ruby on Rails' ? 'ruby' : 'typescript'}\n${aiResponse.optimizedCode}\n\`\`\``,
      path: file.to,
      line: commentLine,
    }];
  });
}

async function createReviewComment(
    owner: string,
    repo: string,
    pull_number: number,
    comments: Array<{ body: string; path: string; line: number }>
): Promise<void> {
  await octokit.pulls.createReview({
    owner,
    repo,
    pull_number,
    comments,
    event: "COMMENT",
  });
}

async function removeOldComments(owner: string, repo: string, pull_number: number) {
  const reviews = await octokit.pulls.listReviews({ owner, repo, pull_number });
  const reviewIds = reviews.data.map((review) => review.id);

  for (const reviewId of reviewIds) {
    await octokit.pulls.deletePendingReview({ owner, repo, pull_number, review_id: reviewId }); // Fix: Use deletePendingReview
  }
}

async function main() {
  const prDetails = await getPRDetails();
  let diff: string | null;
  const eventData = JSON.parse(readFileSync(process.env.GITHUB_EVENT_PATH ?? "", "utf8"));

  if (eventData.action === "opened") {
    diff = await getDiff(prDetails.owner, prDetails.repo, prDetails.pull_number);
  } else if (eventData.action === "synchronize") {
    if (eventData.forced) {
      await removeOldComments(prDetails.owner, prDetails.repo, prDetails.pull_number);
    }
    const newBaseSha = eventData.before;
    const newHeadSha = eventData.after;

    const response = await octokit.repos.compareCommits({
      headers: { accept: "application/vnd.github.v3.diff" },
      owner: prDetails.owner,
      repo: prDetails.repo,
      base: newBaseSha,
      head: newHeadSha,
    });

    diff = String(response.data);
  } else {
    return;
  }

  if (!diff) return;

  const parsedDiff = parseDiff(diff);
  const excludePatterns = core.getInput("exclude").split(",").map((s) => s.trim());

  const filteredDiff = parsedDiff.filter((file) => !excludePatterns.some((pattern) => minimatch(file.to ?? "", pattern)));
  const comments = await analyzeCode(filteredDiff, prDetails);

  if (comments.length > 0) {
    await createReviewComment(prDetails.owner, prDetails.repo, prDetails.pull_number, comments);
  }
}

main().catch((error) => {
  process.exit(1);
});