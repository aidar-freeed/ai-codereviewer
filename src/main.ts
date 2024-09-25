import { readFileSync } from "fs";
import * as core from "@actions/core";
import OpenAI from "openai";
import { Octokit } from "@octokit/rest";
import parseDiff, { Chunk, File, Change } from "parse-diff";
import minimatch from "minimatch";

const GITHUB_TOKEN: string = core.getInput("GITHUB_TOKEN");
const OPENAI_API_KEY: string = core.getInput("OPENAI_API_KEY");
const OPENAI_API_MODEL = core.getInput("OPENAI_API_MODEL");

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
  commit_id: string;
}

async function getPRDetails(): Promise<PRDetails> {
  const eventData = JSON.parse(
    readFileSync(process.env.GITHUB_EVENT_PATH || "", "utf8")
  );
  const { repository, number } = eventData;

  // Fetch PR details
  const prResponse = await octokit.pulls.get({
    owner: repository.owner.login,
    repo: repository.name,
    pull_number: number,
  });

  // Fetch the list of commits in the PR
  const commitsResponse = await octokit.pulls.listCommits({
    owner: repository.owner.login,
    repo: repository.name,
    pull_number: number,
    per_page: 100,
  });

  // Get the latest commit
  const latestCommit = commitsResponse.data[commitsResponse.data.length - 1];

  // Get the SHA of the latest commit
  const latestCommitSha = latestCommit.sha;

  return {
    owner: repository.owner.login,
    repo: repository.name,
    pull_number: number,
    title: prResponse.data.title ?? "",
    description: prResponse.data.body ?? "",
    commit_id: latestCommitSha,
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
  // @ts-expect-error - response.data is a string
  return response.data;
}

function createPrompt(file: File, chunk: Chunk, prDetails: PRDetails): string {
  const diffContent = chunk.changes
    .map((change) => {
      let lineNumber = "";
      if (change.type === "add" || change.type === "del") {
        lineNumber = change.ln ? change.ln.toString() : "";
      } else if (change.type === "normal") {
        lineNumber = change.ln1 ? change.ln1.toString() : "";
      }
      return `${lineNumber} ${change.content}`;
    })
    .join("\n");

  return `Your task is to review pull requests. Instructions:
- Provide a JSON array of review comments in the following format. Return the JSON inside a markdown code block:
  \`\`\`json
  {
    "reviews": [
      {
        "lineNumber": "Line number where the issue is found",
        "reviewComment": "Your review comment"
      }
    ]
  }
  \`\`\`
- Only provide the JSON output inside the code block and nothing else.
- Do not give positive comments or compliments, be critical, include funny emojis.
- IMPORTANT: only comment for performance, typo, or best practices. Not on possible side effect.
- Write the comment in GitHub Markdown format.
- Always propose a code solution to the issue.
- Don't check package imports.
- Don't suggest adding comments.

Review the following code diff in the file "${file.to}" and take the pull request title and description into account when writing the response.

Pull request title: ${prDetails.title}
Pull request description:

---
${prDetails.description}
---

Git diff to review:

\`\`\`diff
${chunk.content}
${diffContent}
\`\`\`
`;
}

function fixInvalidEscapeSequences(jsonString: string): string {
  return jsonString.replace(/\\(?!["\\/bfnrtu])/g, "\\\\");
}

async function getAIResponse(
  prompt: string
): Promise<
  Array<{
    lineNumber: string;
    reviewComment: string;
  }> | null
> {
  const queryConfig = {
    model: OPENAI_API_MODEL,
    top_p: 1,
    frequency_penalty: 0,
    presence_penalty: 0,
  };

  try {
    const response = await openai.chat.completions.create({
      ...queryConfig,
      messages: [
        {
          role: "user",
          content: prompt,
        },
      ],
    });

    // Log the prompt and the raw response
    console.log("Prompt:", prompt);
    const res = response.choices[0].message?.content?.trim() || "{}";
    console.log("Raw AI Response:", res);

    // Extract the JSON string from the response
    const jsonMatch = res.match(/({[\s\S]*})/);
    if (!jsonMatch) {
      console.error("No JSON found in AI response");
      return null;
    }
    let jsonString = jsonMatch[1].trim();

    console.log("Extracted JSON:", jsonString);

    // Fix invalid escape sequences
    jsonString = fixInvalidEscapeSequences(jsonString);

    // Parse the JSON string
    const parsed = JSON.parse(jsonString);

    // Return the reviews array
    return parsed.reviews;
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
): Array<{ body: string; path: string; position: number }> {
  const comments: Array<{ body: string; path: string; position: number }> = [];
  const lineNumberToPosition = new Map<number, number>();
  let position = 0;

  // Build the mapping from line numbers to positions
  for (const change of chunk.changes) {
    position++;
    let lineNumber: number | undefined;
    if (change.type === "add" || change.type === "del") {
      lineNumber = change.ln;
    } else if (change.type === "normal") {
      lineNumber = change.ln1;
    }

    if (lineNumber != null) {
      lineNumberToPosition.set(lineNumber, position);
    }
  }

  for (const aiResponse of aiResponses) {
    const lineNumber = Number(aiResponse.lineNumber);
    const pos = lineNumberToPosition.get(lineNumber);
    if (pos != null) {
      comments.push({
        body: aiResponse.reviewComment,
        path: file.to!,
        position: pos,
      });
    } else {
      console.error(
        `Line number ${lineNumber} not found in diff for file ${file.to}`
      );
    }
  }

  return comments;
}

async function analyzeCode(
  parsedDiff: File[],
  prDetails: PRDetails
): Promise<Array<{ body: string; path: string; position: number }>> {
  const comments: Array<{ body: string; path: string; position: number }> = [];

  for (const file of parsedDiff) {
    if (file.to === "/dev/null") continue; // Ignore deleted files
    for (const chunk of file.chunks) {
      const prompt = createPrompt(file, chunk, prDetails);
      const aiResponse = await getAIResponse(prompt);
      if (aiResponse) {
        const newComments = createComment(file, chunk, aiResponse);
        if (newComments) {
          comments.push(...newComments);
        }
      }
    }
  }
  return comments;
}

async function createReviewComment(
  owner: string,
  repo: string,
  pull_number: number,
  commit_id: string,
  comments: Array<{ body: string; path: string; position: number }>
): Promise<void> {
  try {
    console.log("Creating review with comments:", comments);
    await octokit.pulls.createReview({
      owner,
      repo,
      pull_number,
      commit_id,
      comments,
      event: "COMMENT",
    });
  } catch (error: any) {
    console.error(`Error creating review comment: ${error}`);
    if (error.status === 422) {
      console.error("One or more comments have invalid positions or lines.");
    }
  }
}

async function main() {
  const prDetails = await getPRDetails();
  let diff: string | null;
  const eventData = JSON.parse(
    readFileSync(process.env.GITHUB_EVENT_PATH ?? "", "utf8")
  );

  if (eventData.action === "opened" || eventData.action === "synchronize") {
    diff = await getDiff(
      prDetails.owner,
      prDetails.repo,
      prDetails.pull_number
    );
  } else {
    console.log("Unsupported event:", process.env.GITHUB_EVENT_NAME);
    return;
  }

  if (!diff) {
    console.log("No diff found");
    return;
  }

  const parsedDiff = parseDiff(diff);

  const excludePatterns = core
    .getInput("exclude")
    .split(",")
    .map((s) => s.trim())
    .filter((s) => s.length > 0);

  const filteredDiff = parsedDiff.filter((file) => {
    return !excludePatterns.some((pattern) =>
      minimatch(file.to ?? "", pattern)
    );
  });

  const comments = await analyzeCode(filteredDiff, prDetails);
  if (comments.length > 0) {
    await createReviewComment(
      prDetails.owner,
      prDetails.repo,
      prDetails.pull_number,
      prDetails.commit_id,
      comments
    );
  } else {
    console.log("No comments to post.");
  }
}

main().catch((error) => {
  console.error("Error:", error);
  process.exit(1);
});
