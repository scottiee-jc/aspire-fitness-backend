import OpenAI from "openai";
import { config } from "./config.js";
import { executeTool } from "./tools.js";

type AgentResponse = { reply: string; toolResult?: unknown };

const toolSpecs = [
  {
    type: "function" as const,
    function: {
      name: "login",
      description: "Authenticate user and store access token in the current session.",
      parameters: {
        type: "object",
        properties: {
          username: { type: "string" },
          password: { type: "string" }
        },
        required: ["username", "password"],
        additionalProperties: false
      }
    }
  },
  {
    type: "function" as const,
    function: {
      name: "get_user",
      description: "Fetch user details by userId.",
      parameters: {
        type: "object",
        properties: { userId: { type: "string" } },
        required: ["userId"],
        additionalProperties: false
      }
    }
  },
  {
    type: "function" as const,
    function: {
      name: "add_workout",
      description: "Add a workout for a user.",
      parameters: {
        type: "object",
        properties: {
          userId: { type: "string" },
          name: { type: "string" },
          notes: { type: "string" }
        },
        required: ["userId", "name"],
        additionalProperties: false
      }
    }
  },
  {
    type: "function" as const,
    function: {
      name: "update_workout",
      description: "Update an existing workout.",
      parameters: {
        type: "object",
        properties: {
          userId: { type: "string" },
          workoutId: { type: "string" },
          name: { type: "string" },
          notes: { type: "string" }
        },
        required: ["userId", "workoutId"],
        additionalProperties: false
      }
    }
  },
  {
    type: "function" as const,
    function: {
      name: "remove_workout",
      description: "Remove a workout.",
      parameters: {
        type: "object",
        properties: {
          userId: { type: "string" },
          workoutId: { type: "string" }
        },
        required: ["userId", "workoutId"],
        additionalProperties: false
      }
    }
  }
];

function isRealApiKey(key: string): boolean {
  return Boolean(key) && !key.includes("your_key") && key.startsWith("sk-");
}

const openai = isRealApiKey(config.openAiApiKey) ? new OpenAI({ apiKey: config.openAiApiKey }) : null;

export async function runAgent(sessionId: string, message: string): Promise<AgentResponse> {
  if (config.llmMode === "openai" && openai) {
    return runOpenAIToolCalling(sessionId, message);
  }
  return runRuleBased(sessionId, message);
}

async function runOpenAIToolCalling(sessionId: string, message: string): Promise<AgentResponse> {
  const systemPrompt =
    "You are a workout assistant. Use tools for concrete actions. " +
    "If user asks to perform API actions, call one tool with best arguments. " +
    "If missing required info, ask concise follow-up.";

  const completion = await openai!.chat.completions.create({
    model: config.llmModel,
    temperature: 0.1,
    messages: [
      { role: "system", content: systemPrompt },
      { role: "user", content: message }
    ],
    tools: toolSpecs
  });

  const choice = completion.choices[0];
  const msg = choice.message;

  if (!msg.tool_calls || msg.tool_calls.length === 0) {
    return { reply: msg.content ?? "How can I help with your workout tasks?" };
  }

  const call = msg.tool_calls[0];
  const toolName = call.function.name;
  let args: unknown = {};

  try {
    args = JSON.parse(call.function.arguments || "{}");
  } catch {
    return { reply: "I could not parse tool arguments. Please rephrase your request." };
  }

  const result = await executeTool(sessionId, toolName, args);

  if (!result.ok) {
    return { reply: `Tool call failed: ${result.error}` };
  }

  return {
    reply: `Executed ${toolName} successfully.`,
    toolResult: result.data
  };
}

// Existing rule-based fallback.
async function runRuleBased(sessionId: string, message: string): Promise<AgentResponse> {
  const lower = message.toLowerCase();

  if (lower.startsWith("login ")) {
    const parts = message.split(" ");
    if (parts.length < 3) {
      return { reply: "Usage: login <username> <password>" };
    }
    const result = await executeTool(sessionId, "login", { username: parts[1], password: parts[2] });
    return result.ok
      ? { reply: "Logged in successfully.", toolResult: result.data }
      : { reply: `Login failed: ${result.error}` };
  }

  if (lower.startsWith("get user ")) {
    const userId = message.slice("get user ".length).trim();
    const result = await executeTool(sessionId, "get_user", { userId });
    return result.ok
      ? { reply: "Fetched user.", toolResult: result.data }
      : { reply: `Could not fetch user: ${result.error}` };
  }

  if (lower.startsWith("add workout ")) {
    const raw = message.slice("add workout ".length).trim();
    const [userId, ...nameParts] = raw.split(" ");
    const name = nameParts.join(" ").trim();
    const result = await executeTool(sessionId, "add_workout", { userId, name });
    return result.ok
      ? { reply: "Workout added.", toolResult: result.data }
      : { reply: `Could not add workout: ${result.error}` };
  }

  if (lower.startsWith("update workout ")) {
    const raw = message.slice("update workout ".length).trim();
    const [userId, workoutId, ...nameParts] = raw.split(" ");
    const name = nameParts.join(" ").trim();
    const result = await executeTool(sessionId, "update_workout", { userId, workoutId, name });
    return result.ok
      ? { reply: "Workout updated.", toolResult: result.data }
      : { reply: `Could not update workout: ${result.error}` };
  }

  if (lower.startsWith("remove workout ")) {
    const raw = message.slice("remove workout ".length).trim();
    const [userId, workoutId] = raw.split(" ");
    const result = await executeTool(sessionId, "remove_workout", { userId, workoutId });
    return result.ok
      ? { reply: "Workout removed.", toolResult: result.data }
      : { reply: `Could not remove workout: ${result.error}` };
  }

  return {
    reply:
      "I can help with: login <username> <password>, get user <userId>, add workout <userId> <name>, update workout <userId> <workoutId> <name>, remove workout <userId> <workoutId>."
  };
}
