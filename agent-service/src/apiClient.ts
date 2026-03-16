import { config } from "./config.js";
import { AddWorkoutArgs, RemoveWorkoutArgs, UpdateWorkoutArgs } from "./types.js";

async function request(path: string, options: RequestInit): Promise<unknown> {
  const response = await fetch(`${config.apiBaseUrl}${path}`, options);
  const text = await response.text();
  const body = text ? safeParseJson(text) : undefined;

  if (!response.ok) {
    throw new Error(`API ${response.status} ${response.statusText}: ${text || "no response body"}`);
  }

  return body;
}

function safeParseJson(value: string): unknown {
  try {
    return JSON.parse(value);
  } catch {
    return value;
  }
}


export async function getUser(userId: string, token: string): Promise<unknown> {
  return request(`/users/${encodeURIComponent(userId)}`, {
    method: "GET",
    headers: { Authorization: `Bearer ${token}` }
  });
}

export async function addWorkout(args: AddWorkoutArgs, token: string): Promise<unknown> {
  return request(`/users/${encodeURIComponent(args.userId)}/workouts`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify({ name: args.name, notes: args.notes })
  });
}

export async function updateWorkout(args: UpdateWorkoutArgs, token: string): Promise<unknown> {
  return request(`/users/${encodeURIComponent(args.userId)}/workouts/${encodeURIComponent(args.workoutId)}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`
    },
    body: JSON.stringify({ name: args.name, notes: args.notes })
  });
}

export async function removeWorkout(args: RemoveWorkoutArgs, token: string): Promise<unknown> {
  return request(`/users/${encodeURIComponent(args.userId)}/workouts/${encodeURIComponent(args.workoutId)}`, {
    method: "DELETE",
    headers: { Authorization: `Bearer ${token}` }
  });
}
