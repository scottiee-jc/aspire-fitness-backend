import { z } from "zod";
import * as api from "./apiClient.js";
import { sessionStore } from "./sessionStore.js";
import { ToolResult } from "./types.js";

const addWorkoutSchema = z.object({ userId: z.string().min(1), name: z.string().min(1), notes: z.string().optional() });
const updateWorkoutSchema = z.object({ userId: z.string().min(1), workoutId: z.string().min(1), name: z.string().optional(), notes: z.string().optional() });
const removeWorkoutSchema = z.object({ userId: z.string().min(1), workoutId: z.string().min(1) });

export async function executeTool(sessionId: string, toolName: string, args: unknown): Promise<ToolResult> {
  try {
    const token = requireToken(sessionId);
    const userId = requireUserId(sessionId);

    // Inject userId into args if not already present
    const withUser = { userId, ...(args as Record<string, unknown>) };

    switch (toolName) {
      case "get_user": {
        const data = await api.getUser(userId, token);
        return { ok: true, data };
      }
      case "add_workout": {
        const parsed = addWorkoutSchema.parse(withUser);
        const data = await api.addWorkout(parsed, token);
        return { ok: true, data };
      }
      case "update_workout": {
        const parsed = updateWorkoutSchema.parse(withUser);
        const data = await api.updateWorkout(parsed, token);
        return { ok: true, data };
      }
      case "remove_workout": {
        const parsed = removeWorkoutSchema.parse(withUser);
        const data = await api.removeWorkout(parsed, token);
        return { ok: true, data };
      }
      default:
        return { ok: false, error: `Unknown tool: ${toolName}` };
    }
  } catch (error) {
    return { ok: false, error: error instanceof Error ? error.message : String(error) };
  }
}

function requireToken(sessionId: string): string {
  const session = sessionStore.getOrCreate(sessionId);
  if (!session.accessToken) {
    throw new Error("Not authenticated. Please sign in first.");
  }
  return session.accessToken;
}

function requireUserId(sessionId: string): string {
  const session = sessionStore.getOrCreate(sessionId);
  if (!session.userId) {
    throw new Error("No userId in session. Please sign in first.");
  }
  return session.userId;
}

