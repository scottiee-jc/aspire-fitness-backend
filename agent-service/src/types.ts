export type ToolResult = {
  ok: boolean;
  data?: unknown;
  error?: string;
};

export type SessionRecord = {
  sessionId: string;
  accessToken?: string;
  userId?: string;
  createdAt: number;
  updatedAt: number;
};

export type ChatRequest = {
  sessionId: string;
  message: string;
};

export type ChatRequest = {
  userId: string;
  name: string;
  notes?: string;
};

export type UpdateWorkoutArgs = {
  userId: string;
  workoutId: string;
  name?: string;
  notes?: string;
};

export type RemoveWorkoutArgs = {
  userId: string;
  workoutId: string;
};

