import { Router } from "express";
import { z } from "zod";
import { runAsk, runChat } from "../agent.js";
import { sessionStore } from "../sessionStore.js";
import * as api from "../apiClient.js";

const askSchema = z.object({
  message: z.string().min(1)
});

const chatSchema = z.object({
  message: z.string().min(1),
  sessionId: z.string().min(1).optional()
});

export const chatRouter = Router();

// ── PUBLIC: General fitness Q&A (no auth required) ──────────────────────
chatRouter.post("/ask", async (req, res) => {
  const parsed = askSchema.safeParse(req.body);
  if (!parsed.success) {
    return res.status(400).json({ error: parsed.error.flatten() });
  }

  try {
    const result = await runAsk(parsed.data.message);
    return res.json(result);
  } catch (err) {
    console.error("Ask error:", err);
    return res.status(500).json({ error: "Failed to process your question." });
  }
});

// ── AUTHENTICATED: Personalised workout agent ───────────────────────────
chatRouter.post("/chat", async (req, res) => {
  // Require Authorization: Bearer <token>
  const authHeader = req.headers.authorization;
  if (!authHeader?.startsWith("Bearer ")) {
    return res.status(401).json({
      error: "Authentication required. Please sign in via the API first and include your token as: Authorization: Bearer <token>"
    });
  }
  const token = authHeader.slice(7);

  const parsed = chatSchema.safeParse(req.body);
  if (!parsed.success) {
    return res.status(400).json({ error: parsed.error.flatten() });
  }

  // Create or reuse a session, and store the user's token
  const sessionId = parsed.data.sessionId ?? sessionStore.createSessionId(req.ip ?? "anon");

  // Validate token against the API and get user info
  try {
    const user = await api.getUser(token, token);
    const userId = (user as Record<string, unknown>).id as string
      ?? (user as Record<string, unknown>).userId as string
      ?? (user as Record<string, unknown>).accountNumber as string
      ?? "unknown";
    sessionStore.update(sessionId, { accessToken: token, userId });
  } catch {
    return res.status(401).json({ error: "Invalid or expired token. Please sign in again." });
  }

  try {
    const result = await runChat(sessionId, parsed.data.message);
    return res.json({ sessionId, ...result });
  } catch (err) {
    console.error("Chat error:", err);
    return res.status(500).json({ error: "Failed to process your request." });
  }
});

