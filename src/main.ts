import { readFileSync } from "fs";

import * as core from "@actions/core";
import OpenAI from "openai";
import { Octokit } from "@octokit/rest";
import parseDiff, { Chunk, File } from "parse-diff";
import minimatch from "minimatch";

const GITHUB_TOKEN: string = core.getInput("GITHUB_TOKEN") || process.env.GITHUB_TOKEN || "";
const OPENAI_API_KEY: string = core.getInput("OPENAI_API_KEY") || process.env.OPENAI_API_KEY || "";
const OPENAI_API_MODEL: string = core.getInput("OPENAI_API_MODEL");

const octokit = new Octokit({ auth: GITHUB_TOKEN });

const openai = new OpenAI({
  apiKey: OPENAI_API_KEY,
});

interface PRDetails {
  owner: string;
  repo: string;
  pull_number: number;
  title: string;
  description: string;
}

const MAX_LINES_TO_REVIEW: number = 250;
const INCLUDE_PATHS: string[] = (core.getInput("INCLUDE_PATHS") || process.env.INCLUDE_PATHS || "").split(",").map(p => p.trim());

async function getPRDetails(): Promise<PRDetails> {
  try {
    let owner: string;
    let repo: string;
    let pull_number: number;

    const eventName = process.env.GITHUB_EVENT_NAME;
    console.log("Event name:", eventName);

    if (eventName === 'issue_comment' || eventName === 'pull_request_review') {
      // For comment and review triggers, we need to fetch PR details separately
      const prNumber = process.env.PR_NUMBER;
      if (!prNumber) {
        throw new Error("PR_NUMBER is not set for comment/review trigger");
      }
      [owner, repo] = process.env.GITHUB_REPOSITORY?.split('/') || [];
      if (!owner || !repo) {
        throw new Error("Unable to parse owner and repo from GITHUB_REPOSITORY");
      }
      pull_number = parseInt(prNumber);
    } else {
      // For other triggers, use the event file
      const eventPath = process.env.GITHUB_EVENT_PATH;
      if (!eventPath) {
        throw new Error("GITHUB_EVENT_PATH is not set");
      }
      console.log("GITHUB_EVENT_PATH:", eventPath);
      const eventContent = readFileSync(eventPath, "utf8");
      console.log("Event file content:", eventContent);

      const eventData = JSON.parse(eventContent);
      if (!eventData.repository || !eventData.pull_request || !eventData.pull_request.number) {
        throw new Error("Unable to parse repository or PR number from event file");
      }

      owner = eventData.repository.owner.login;
      repo = eventData.repository.name;
      pull_number = eventData.pull_request.number;
    }

    console.log("Owner:", owner);
    console.log("Repo:", repo);
    console.log("PR number:", pull_number);

    const prResponse = await octokit.pulls.get({
      owner,
      repo,
      pull_number,
    });

    return {
      owner,
      repo,
      pull_number,
      title: prResponse.data.title ?? "",
      description: prResponse.data.body ?? "",
    };
  } catch (error) {
    console.error("Error in getPRDetails:", error);
    throw error;
  }
}

export async function getDiff(
  owner: string,
  repo: string,
  pull_number: number
): Promise<File[] | null> {
  try {
    const response = await octokit.pulls.get({
      owner,
      repo,
      pull_number,
      mediaType: { format: "diff" },
    });

    // Explicitly assert the type of response.data
    const diffContent = response.data as unknown as string;

    if (typeof diffContent !== 'string') {
      console.error("Unexpected response format. Expected string, got:", typeof diffContent);
      return null;
    }

    console.log("Raw diff content sample:", diffContent.substring(0, 200));
    
    const parsedDiff = parseDiff(diffContent);
    console.log("Parsed diff length:", parsedDiff.length);

    // Filter the diff to only include files that match the include paths
    console.log("Filtering diff based on include paths:", INCLUDE_PATHS);
    const filteredDiff = parsedDiff.filter(file => {
      const matchesPattern = INCLUDE_PATHS.some(pattern => minimatch(file.to || "", pattern));
      console.log(`File ${file.to}: ${matchesPattern ? 'included' : 'excluded'}`);
      return matchesPattern;
    });
    console.log("Filtered diff files:", filteredDiff.map(file => file.to));
    console.log("Filtered diff length:", filteredDiff.length);

    // Log the string representation of the filtered diff
    console.log("Filtered diff string:\n", diffToString(filteredDiff));

    return filteredDiff;
  } catch (error) {
    console.error("Error fetching diff:", error);
    return null;
  }
}

export function diffToString(files: File[]): string {
  return files.map(fileToString).join('\n\n');
}

function fileToString(file: File): string {
  const header = `diff --git a/${file.from} b/${file.to}
${file.index ? `index ${file.index}\n` : ''}${file.deleted ? '--- ' + file.from : ''}
${file.new ? '+++ ' + file.to : ''}`;

  const chunks = file.chunks.map(chunkToString).join('\n');

  return `${header}\n${chunks}`;
}

function chunkToString(chunk: Chunk): string {
  const header = `@@ -${chunk.oldStart},${chunk.oldLines} +${chunk.newStart},${chunk.newLines} @@`;
  const changes = chunk.changes.map(change => {
    const prefix = change.type === 'add' ? '+' : (change.type === 'del' ? '-' : ' ');
    return prefix + change.content;
  }).join('\n');

  return `${header}\n${changes}`;
}

async function analyzeEntirePR(
  files: File[],
  prDetails: PRDetails
): Promise<Array<{ body: string; path: string; line: number }>> {
  const fileContents = files.map(file => `File: ${file.to}\n${file.chunks.map(chunk => chunk.changes.map(change => change.content).join('\n')).join('\n')}`).join('\n\n');

  const prompt = `Imagine that you are a senior developer reviewing this entire pull request. 
  Your task is to review this entire pull request for clarity, readability, and best practices. 

  Instructions:
- Provide the response in the following JSON format: {"reviews": [{"path": "<file_path>", "line": <line_number>, "comment": "<review comment>"}]}
- Do not give positive comments or compliments.
- Provide comments and suggestions ONLY if there is something to improve.
- Write the comments in GitHub Markdown format.
- Use the given description for overall context.
- Please ask for docstrings if they are missing.
- IMPORTANT: NEVER suggest adding comments to the code (except for docstrings).
- IMPORTANT: Only comment on lines that are part of the diff (added or modified lines).

Review the following pull request and take the title and description into account when writing the response.

Pull request title: ${prDetails.title}
Pull request description:

---
${prDetails.description}
---

Git diff to review:

${fileContents}
`;

  const aiResponse = await getAIResponse(prompt);
  return aiResponse ? aiResponse.map(review => ({
    body: review.comment,
    path: review.path,
    line: review.line
  })) : [];
}

async function getAIResponse(prompt: string): Promise<Array<{
  path: string;
  line: number;
  comment: string;
}> | null> {
  const queryConfig = {
    model: OPENAI_API_MODEL,
    temperature: 0.2,
    max_tokens: 2000,
    top_p: 1,
    frequency_penalty: 0,
    presence_penalty: 0,
  };
  console.log("Sending prompt to AI...", prompt);

  try {
    const response = await openai.chat.completions.create({
      ...queryConfig,
      messages: [
        {
          role: "system",
          content: prompt,
        },
      ],
    });

    let res = response.choices[0].message?.content?.trim() || "{}";
    console.info("Raw AI response:", res);

    // Remove any markdown code block syntax
    res = res.replace(/^```json\s*/, '').replace(/\s*```$/, '');

    // Attempt to parse the JSON
    try {
      const parsedResponse = JSON.parse(res);
      console.info("Parsed AI response:", parsedResponse);
      return parsedResponse.reviews;
    } catch (parseError) {
      console.error("Error parsing AI response:", parseError);
      return null;
    }
  } catch (error) {
    console.error("Error:", error);
    return null;
  }
}

function createComment(
  file: File,
  chunk: Chunk,
  aiResponses: Array<{
    lineNumber: string;
    reviewComment: string;
  }>
): Array<{ body: string; path: string; line: number }> {
  return aiResponses.flatMap((aiResponse) => {
    if (!file.to) {
      return [];
    }
    return {
      body: aiResponse.reviewComment,
      path: file.to,
      line: Number(aiResponse.lineNumber),
    };
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

export async function main() {
  console.info("Starting main function");
  const prDetails = await getPRDetails();
  console.info("PR Details:", prDetails);

  const filteredDiff = await getDiff(
    prDetails.owner,
    prDetails.repo,
    prDetails.pull_number
  );

  if (!filteredDiff) {
    console.log("No diff found");
    return;
  }
  console.log("Filtered diff length:", filteredDiff.length);

  const comments = await analyzeEntirePR(filteredDiff, prDetails);
  console.log("Generated comments:", comments.length);

  // Filter comments to ensure they're on changed lines and adjust line numbers
  const validComments = comments.filter(comment => {
    const file = filteredDiff.find(f => f.to === comment.path);
    if (!file) return false;

    const changedLines = new Set();
    file.chunks.forEach(chunk => {
      for (let i = 0; i < chunk.changes.length; i++) {
        const change = chunk.changes[i];
        if (change.type === 'add' || change.type === 'del') {
          changedLines.add(chunk.newStart + i);
        }
      }
    });

    return changedLines.has(comment.line);
  });

  console.log("Valid comments:", validComments.length);

  if (validComments.length > 0) {
    console.log("Creating review comments");
    await createReviewComment(
      prDetails.owner,
      prDetails.repo,
      prDetails.pull_number,
      validComments
    );
  }
  console.log("Main function completed");
}

// Add this condition to prevent automatic execution when imported
if (require.main === module) {
  main().catch((error) => {
    console.error("Error in main function:", error);
    process.exit(1);
  });
}
