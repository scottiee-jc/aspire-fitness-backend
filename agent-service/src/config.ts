import dotenv from "dotenv";

dotenv.config();

function required(key: string, fallback?: string): string {
  const value = process.env[key] ?? fallback;
  if (!value) {
    throw new Error(`Missing required environment variable: ${key}`);
  }
  return value;
}

export const config = {
  port: Number(process.env.PORT ?? 3001),
  apiBaseUrl: required("WORKOUT_API_BASE_URL"),
  llmMode: process.env.LLM_MODE ?? "rule-based",
  openAiApiKey: process.env.OPENAI_API_KEY ?? "",
  llmModel: process.env.LLM_MODEL ?? "gpt-4.1-mini",
  sessionSecret: required("SESSION_SECRET", "change-me")
};

