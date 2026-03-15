import { z } from "zod";
import * as api from "./apiClient.js";
import { sessionStore } from "./sessionStore.js";
import { ToolResult } from "./types.js";

const loginSchema = z.object({ username: z.string().min(1), password: z.string().min(1) });
const getUserSchema = z.object({ userId: z.string().min(1) });
const addWorkoutSchema = z.object({ userId: z.string().min(1), name: z.string().min(1), notes: z.string().optional() });
const updateWorkoutSchema = z.object({ userId: z.string().min(1), workoutId: z.string().min(1), name: z.string().optional(), notes: z.string().optional() });
const removeWorkoutSchema = z.object({ userId: z.string().min(1), workoutId: z.string().min(1) });

export async function executeTool(sessionId: string, toolName: string, args: unknown): Promise<ToolResult> {
  try {
    switch (toolName) {
      case "login": {
        const parsed = loginSchema.parse(args);
        const res = await api.login(parsed);
        sessionStore.update(sessionId, { accessToken: res.token, userId: res.userId });
        return { ok: true, data: { authenticated: true, userId: res.userId ?? null } };
      }
      case "get_user": {
        const parsed = getUserSchema.parse(args);
        const token = requireToken(sessionId);
        const data = await api.getUser(parsed.userId, token);
        return { ok: true, data };
      }
      case "add_workout": {
        const parsed = addWorkoutSchema.parse(args);
        const token = requireToken(sessionId);
        const data = await api.addWorkout(parsed, token);
        return { ok: true, data };
      }
      case "update_workout": {
        const parsed = updateWorkoutSchema.parse(args);
        const token = requireToken(sessionId);
        const data = await api.updateWorkout(parsed, token);
        return { ok: true, data };
      }
      case "remove_workout": {
        const parsed = removeWorkoutSchema.parse(args);
        const token = requireToken(sessionId);
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
    throw new Error("Not authenticated in this session. Call login first.");
  }
  return session.accessToken;
}

