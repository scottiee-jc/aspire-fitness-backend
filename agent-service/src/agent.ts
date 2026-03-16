import OpenAI from "openai";
import { config } from "./config.js";
import { executeTool } from "./tools.js";
import { sessionStore } from "./sessionStore.js";

export type AgentResponse = { reply: string; toolResult?: unknown };

// ---------------------------------------------------------------------------
// OpenAI client
// ---------------------------------------------------------------------------
function isRealApiKey(key: string): boolean {
  return Boolean(key) && !key.includes("your_key") && key.startsWith("sk-");
}

const openai = isRealApiKey(config.openAiApiKey)
  ? new OpenAI({ apiKey: config.openAiApiKey })
  : null;

// ---------------------------------------------------------------------------
// PUBLIC: ask – general fitness Q&A (no auth required)
// ---------------------------------------------------------------------------
const ASK_SYSTEM_PROMPT =
  `You are a knowledgeable fitness and exercise assistant called AspireFit AI. ` +
  `Answer general questions about exercise form, nutrition basics, workout ` +
  `programming, stretching, recovery, and healthy habits. ` +
  `Keep answers concise and practical. ` +
  `If the user asks you to modify their personal data, create workouts for ` +
  `them, or anything that requires an account, politely tell them they need ` +
  `to sign in first to use personalised features.`;

export async function runAsk(message: string): Promise<AgentResponse> {
  if (!openai) {
    return runAskRuleBased(message);
  }

  const completion = await openai.chat.completions.create({
    model: config.llmModel,
    temperature: 0.6,
    messages: [
      { role: "system", content: ASK_SYSTEM_PROMPT },
      { role: "user", content: message }
    ]
  });

  return { reply: completion.choices[0].message.content ?? "I'm not sure how to answer that." };
}

function runAskRuleBased(message: string): AgentResponse {
  const lower = message.toLowerCase();

  if (lower.includes("stretch") || lower.includes("warm up") || lower.includes("warmup")) {
    return { reply: "A good warm-up includes 5-10 minutes of light cardio followed by dynamic stretches targeting the muscles you'll be using. Never static-stretch cold muscles." };
  }
  if (lower.includes("protein") || lower.includes("nutrition") || lower.includes("diet")) {
    return { reply: "For muscle building, aim for 1.6–2.2 g of protein per kg of body weight daily. Spread intake across 3-5 meals. Stay hydrated and don't neglect carbs for energy." };
  }
  if (lower.includes("rest") || lower.includes("recovery") || lower.includes("sleep")) {
    return { reply: "Rest is when your muscles actually grow. Aim for 7-9 hours of sleep and at least 1-2 rest days per week. Active recovery like walking or light yoga is great." };
  }
  if (lower.includes("beginner") || lower.includes("start")) {
    return { reply: "If you're just starting out, focus on compound movements: squats, deadlifts, bench press, rows, and overhead press. Start light, nail your form, and add weight gradually." };
  }
  if (lower.includes("lose weight") || lower.includes("fat loss") || lower.includes("cut")) {
    return { reply: "Fat loss comes down to a caloric deficit. Combine resistance training to preserve muscle with moderate cardio. A 300-500 calorie daily deficit is sustainable." };
  }

  return {
    reply:
      "I can answer general fitness questions about exercise, nutrition, recovery, and more. " +
      "Just ask! For personalised workout management, please sign in first."
  };
}

// ---------------------------------------------------------------------------
// AUTHENTICATED: chat – personalised agent with tool-calling (auth required)
// ---------------------------------------------------------------------------
const authenticatedToolSpecs = [
  {
    type: "function" as const,
    function: {
      name: "get_user",
      description: "Fetch the current user's profile details.",
      parameters: { type: "object", properties: {}, additionalProperties: false }
    }
  },
  {
    type: "function" as const,
    function: {
      name: "add_workout",
      description: "Add a new workout for the current user.",
      parameters: {
        type: "object",
        properties: {
          name: { type: "string", description: "Workout name, e.g. 'Upper Body Push'" },
          notes: { type: "string", description: "Optional notes or description" }
        },
        required: ["name"],
        additionalProperties: false
      }
    }
  },
  {
    type: "function" as const,
    function: {
      name: "update_workout",
      description: "Update an existing workout for the current user.",
      parameters: {
        type: "object",
        properties: {
          workoutId: { type: "string", description: "The workout ID to update" },
          name: { type: "string", description: "New workout name" },
          notes: { type: "string", description: "New notes" }
        },
        required: ["workoutId"],
        additionalProperties: false
      }
    }
  },
  {
    type: "function" as const,
    function: {
      name: "remove_workout",
      description: "Remove a workout for the current user.",
      parameters: {
        type: "object",
        properties: {
          workoutId: { type: "string", description: "The workout ID to delete" }
        },
        required: ["workoutId"],
        additionalProperties: false
      }
    }
  }
];

const CHAT_SYSTEM_PROMPT =
  `You are a personalised workout assistant for AspireFit. The user is signed in. ` +
  `You can manage their workouts (add, update, remove) and fetch their profile. ` +
  `Use tools for concrete actions. If missing required info, ask a concise follow-up. ` +
  `You may also answer general fitness questions without using tools.`;

export async function runChat(sessionId: string, message: string): Promise<AgentResponse> {
  if (openai) {
    return runChatOpenAI(sessionId, message);
  }
  return runChatRuleBased(sessionId, message);
}

async function runChatOpenAI(sessionId: string, message: string): Promise<AgentResponse> {
  const session = sessionStore.getOrCreate(sessionId);

  const completion = await openai!.chat.completions.create({
    model: config.llmModel,
    temperature: 0.2,
    messages: [
      { role: "system", content: CHAT_SYSTEM_PROMPT },
      { role: "user", content: message }
    ],
    tools: authenticatedToolSpecs
  });

  const choice = completion.choices[0];
  const msg = choice.message;

  // No tool call → plain conversational reply
  if (!msg.tool_calls || msg.tool_calls.length === 0) {
    return { reply: msg.content ?? "How can I help with your workouts?" };
  }

  const call = msg.tool_calls[0];
  const toolName = call.function.name;
  let args: Record<string, unknown> = {};

  try {
    args = JSON.parse(call.function.arguments || "{}");
  } catch {
    return { reply: "I could not parse tool arguments. Please rephrase your request." };
  }

  // Inject the userId from the session so the LLM never needs to know it
  if (session.userId) {
    args.userId = session.userId;
  }

  const result = await executeTool(sessionId, toolName, args);

  if (!result.ok) {
    return { reply: `Action failed: ${result.error}` };
  }

  // Ask the LLM to summarise the tool result in a friendly way
  const summary = await openai!.chat.completions.create({
    model: config.llmModel,
    temperature: 0.3,
    messages: [
      { role: "system", content: "Summarise this tool result for the user in a friendly, concise way." },
      { role: "user", content: `Tool "${toolName}" returned:\n${JSON.stringify(result.data, null, 2)}` }
    ]
  });

  return {
    reply: summary.choices[0].message.content ?? `Done — ${toolName} completed.`,
    toolResult: result.data
  };
}

function runChatRuleBased(sessionId: string, message: string): AgentResponse {
  const session = sessionStore.getOrCreate(sessionId);
  const lower = message.toLowerCase();

  const helpText =
    "Available commands:\n" +
    "  my profile              — view your profile\n" +
    "  add workout <name>      — create a new workout\n" +
    "  update workout <id> <name> — rename a workout\n" +
    "  remove workout <id>     — delete a workout\n" +
    "\nYou can also ask me any general fitness question!";

  if (lower === "help") {
    return { reply: helpText };
  }

  if (lower === "my profile" || lower === "profile") {
    return { reply: `Your userId is ${session.userId ?? "unknown"}. Use the API to fetch full profile details.` };
  }

  if (lower.startsWith("add workout ")) {
    const name = message.slice("add workout ".length).trim();
    if (!name) return { reply: "Please provide a workout name. Example: add workout Upper Body Push" };
    return { reply: `To add workout "${name}", this would call the API. (Rule-based mode — enable OpenAI for full functionality.)` };
  }

  if (lower.startsWith("update workout ")) {
    return { reply: "To update workouts, enable OpenAI mode for full functionality." };
  }

  if (lower.startsWith("remove workout ")) {
    return { reply: "To remove workouts, enable OpenAI mode for full functionality." };
  }

  // Fall through to general fitness Q&A even in authenticated mode
  const askResult = runAskRuleBased(message);
  if (askResult.reply.startsWith("I can answer general")) {
    return { reply: helpText };
  }
  return askResult;
}
