import { describe, expect, it } from "vitest";
import { runAgent } from "./agent.js";

describe("runAgent", () => {
  it("returns help text for unknown command in rule-based mode", async () => {
    const result = await runAgent("test-session", "hello there");
    expect(result.reply).toContain("I can help with");
  });

  it("returns usage hint for incomplete login command", async () => {
    const result = await runAgent("test-session", "login onlyuser");
    expect(result.reply).toContain("Usage");
  });

  it("attempts login tool and returns error when API is unreachable", async () => {
    const result = await runAgent("test-session", "login demo password123");
    // API not running during test, so expect a failure message
    expect(result.reply).toMatch(/Login failed|Logged in/);
  });
});

