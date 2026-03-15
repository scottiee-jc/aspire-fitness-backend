import crypto from "crypto";
import { SessionRecord } from "./types.js";

class InMemorySessionStore {
  private readonly sessions = new Map<string, SessionRecord>();

  getOrCreate(sessionId: string): SessionRecord {
    const existing = this.sessions.get(sessionId);
    if (existing) {
      return existing;
    }
    const now = Date.now();
    const record: SessionRecord = {
      sessionId,
      createdAt: now,
      updatedAt: now
    };
    this.sessions.set(sessionId, record);
    return record;
  }

  update(sessionId: string, patch: Partial<SessionRecord>): SessionRecord {
    const current = this.getOrCreate(sessionId);
    const updated: SessionRecord = {
      ...current,
      ...patch,
      updatedAt: Date.now()
    };
    this.sessions.set(sessionId, updated);
    return updated;
  }

  createSessionId(seed = ""): string {
    return crypto.createHash("sha256").update(`${seed}-${Date.now()}-${Math.random()}`).digest("hex");
  }
}

export const sessionStore = new InMemorySessionStore();

