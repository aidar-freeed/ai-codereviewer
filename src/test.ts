import { Octokit } from "@octokit/rest";
import OpenAI from "openai";
import parseDiff, { File } from "parse-diff";
import minimatch from "minimatch";
import dotenv from "dotenv";
import { getDiff, diffToString } from "./main"; // Import the functions from main.ts

// Load environment variables
dotenv.config();

const GITHUB_TOKEN = process.env.GITHUB_TOKEN;
const OPENAI_API_KEY = process.env.OPENAI_API_KEY;
const INCLUDE_PATHS = process.env.INCLUDE_PATHS?.split(",").map(p => p.trim()) || [];

const octokit = new Octokit({ auth: GITHUB_TOKEN });

// const openai = new OpenAI({
//   apiKey: OPENAI_API_KEY,
// });

// Copy the necessary functions from your main.ts file
// Include getDiff, diffToString, fileToString, chunkToString, etc.

async function testGetDiff() {
  const owner = "yousef-hindy-ai";
  const repo = "diffusion_demos";
  const pull_number = 5; // Replace with an actual PR number

  const diff = await getDiff(owner, repo, pull_number);
  if (diff) {
    console.log("Filtered diff:");
    console.log(diffToString(diff));
  } else {
    console.log("No diff found");
  }
}

testGetDiff().catch(console.error);
