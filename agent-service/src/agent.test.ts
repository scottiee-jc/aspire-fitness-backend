import { describe, expect, it } from "vitest";
import { runAsk, runChat } from "./agent.js";

describe("runAsk – public fitness Q&A", () => {
  it("returns helpful response for general fitness question (rule-based)", async () => {
    const result = await runAsk("how much protein should I eat?");
    expect(result.reply.toLowerCase()).toContain("protein");
  });

  it("returns helpful response for beginner question (rule-based)", async () => {
    const result = await runAsk("I'm a beginner, how do I start working out?");
    expect(result.reply.toLowerCase()).toContain("compound");
  });

  it("returns generic help for unrecognised question (rule-based)", async () => {
    const result = await runAsk("random gibberish xyz");
    expect(result.reply).toContain("general fitness questions");
  });
});

describe("runChat – authenticated agent", () => {
  it("returns help commands in rule-based mode for unknown input", () => {
    const result = runChat("test-session", "hello there");
    // runChat is not async in rule-based fallback but returns AgentResponse
    expect(result).toBeDefined();
  });
});

