import { Router } from "express";
import { z } from "zod";
import { runAgent } from "../agent.js";
import { sessionStore } from "../sessionStore.js";

const schema = z.object({
  message: z.string().min(1),
  sessionId: z.string().min(1).optional()
});

export const chatRouter = Router();

chatRouter.post("/chat", async (req, res) => {
  const parsed = schema.safeParse(req.body);
  if (!parsed.success) {
    return res.status(400).json({ error: parsed.error.flatten() });
  }

  const sessionId = parsed.data.sessionId ?? sessionStore.createSessionId(req.ip);
  const result = await runAgent(sessionId, parsed.data.message);

  return res.json({ sessionId, ...result });
});

