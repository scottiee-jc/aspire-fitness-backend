# 🤖 Workout Agent Service

An AI-powered gateway that calls your Workout API on behalf of authenticated users. Send natural language or structured commands, and the agent figures out which API calls to make.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Service](#running-the-service)
- [API Reference](#api-reference)
  - [Health Check](#health-check)
  - [Chat Endpoint](#chat-endpoint)
- [Agent Modes](#agent-modes)
  - [Rule-Based Mode (default)](#rule-based-mode-default)
  - [OpenAI Tool-Calling Mode](#openai-tool-calling-mode)
- [Available Tools](#available-tools)
- [Session Management](#session-management)
- [Usage Examples](#usage-examples)
  - [Step 1 — Start a Session & Log In](#step-1--start-a-session--log-in)
  - [Step 2 — Fetch User Details](#step-2--fetch-user-details)
  - [Step 3 — Add a Workout](#step-3--add-a-workout)
  - [Step 4 — Update a Workout](#step-4--update-a-workout)
  - [Step 5 — Remove a Workout](#step-5--remove-a-workout)
- [Error Handling](#error-handling)
- [Running Tests](#running-tests)
- [Building for Production](#building-for-production)
- [Security Notes](#security-notes)
- [Troubleshooting](#troubleshooting)

---

## Overview

This service sits between your client application and your Java Workout API. Instead of the client making raw HTTP calls, it sends a chat-style message to the agent, which:

1. Interprets the user's intent (via rules or an LLM).
2. Authenticates with your backend using stored session tokens.
3. Calls the appropriate Workout API endpoint(s).
4. Returns a human-friendly reply plus the raw API data.

---

## Architecture

```
┌──────────────┐       ┌────────────────────┐       ┌──────────────────┐
│  Client App  │──────▶│  Agent Service     │──────▶│  Workout API     │
│  (browser,   │ POST  │  (Node/TypeScript)  │ HTTP  │  (Java/Spring)   │
│   mobile,    │/agent │                    │       │  localhost:8080   │
│   curl)      │/chat  │  • Session store   │       │                  │
└──────────────┘       │  • Tool executor   │       │  • /auth/login   │
                       │  • LLM or rules    │       │  • /users/{id}   │
                       └────────────────────┘       │  • /users/{id}/  │
                                                    │    workouts      │
                                                    └──────────────────┘
```

---

## Project Structure

```
agent-service/
├── .env.example          # Environment variable template
├── package.json          # Dependencies and scripts
├── tsconfig.json         # TypeScript configuration
├── src/
│   ├── server.ts         # Express app entry point
│   ├── config.ts         # Loads and validates env vars
│   ├── types.ts          # Shared TypeScript types
│   ├── sessionStore.ts   # In-memory per-user session/token store
│   ├── apiClient.ts      # HTTP wrappers for the Workout API
│   ├── tools.ts          # Tool definitions with Zod validation
│   ├── agent.ts          # Agent logic (rule-based + OpenAI modes)
│   ├── agent.test.ts     # Unit tests
│   └── routes/
│       └── chat.ts       # POST /agent/chat route handler
```

---

## Prerequisites

| Requirement        | Version  | Notes                                       |
|--------------------|----------|---------------------------------------------|
| **Node.js**        | 20+      | Required for `fetch` and ES module support  |
| **npm**            | 10+      | Ships with Node 20                          |
| **Workout API**    | —        | Your Java backend running on port 8080      |
| **OpenAI API key** | —        | Only needed if you want LLM mode            |

---

## Installation

```bash
cd agent-service
cp .env.example .env
npm install
```

---

## Configuration

Edit the `.env` file created during installation:

```dotenv
# ── Server ──────────────────────────────────
PORT=3001                                # Port the agent listens on

# ── Workout API ─────────────────────────────
WORKOUT_API_BASE_URL=http://localhost:8080  # Base URL of your Java backend

# ── Agent Mode ──────────────────────────────
LLM_MODE=rule-based                      # "rule-based" or "openai"

# ── OpenAI (only needed when LLM_MODE=openai) ──
OPENAI_API_KEY=                          # Your sk-... key
LLM_MODEL=gpt-4.1-mini                  # Any OpenAI chat model

# ── Security ────────────────────────────────
SESSION_SECRET=change-me                 # Used for session ID generation
```

| Variable               | Required | Default                  | Description                          |
|------------------------|----------|--------------------------|--------------------------------------|
| `PORT`                 | No       | `3001`                   | HTTP listen port                     |
| `WORKOUT_API_BASE_URL` | **Yes**  | —                        | Your Java API base URL               |
| `LLM_MODE`             | No       | `rule-based`             | `rule-based` or `openai`             |
| `OPENAI_API_KEY`       | No*      | —                        | *Required if `LLM_MODE=openai`       |
| `LLM_MODEL`            | No       | `gpt-4.1-mini`           | OpenAI model to use                  |
| `SESSION_SECRET`       | No       | `change-me`              | Seed for session ID hashing          |

> **Note:** If `LLM_MODE=openai` but the API key is missing, empty, or a placeholder, the agent automatically falls back to rule-based mode.

---

## Running the Service

### Development (hot-reload)

```bash
npm run dev
```

### Production

```bash
npm run build    # Compiles TypeScript → dist/
npm start        # Runs dist/server.js
```

You should see:

```
Agent service listening on port 3001
```

---

## API Reference

### Health Check

```
GET /health
```

**Response:**

```json
{ "ok": true }
```

Use this to verify the agent service is running before sending chat requests.

---

### Chat Endpoint

```
POST /agent/chat
Content-Type: application/json
```

**Request body:**

| Field       | Type   | Required | Description                                                        |
|-------------|--------|----------|--------------------------------------------------------------------|
| `message`   | string | **Yes**  | The command or natural language message                             |
| `sessionId` | string | No       | Re-use a previous session ID to keep your auth token across calls  |

**Response body:**

| Field        | Type   | Always present | Description                                  |
|--------------|--------|----------------|----------------------------------------------|
| `sessionId`  | string | Yes            | Session ID (generated if not provided)        |
| `reply`      | string | Yes            | Human-readable result message                 |
| `toolResult` | object | No             | Raw data returned from the Workout API        |

---

## Agent Modes

### Rule-Based Mode (default)

The agent matches your message against fixed command patterns. Fast, deterministic, no external API calls.

**Supported commands:**

| Command                                           | Action                      |
|---------------------------------------------------|-----------------------------|
| `login <username> <password>`                     | Authenticate and store token|
| `get user <userId>`                               | Fetch user by ID            |
| `add workout <userId> <workoutName>`              | Create a workout            |
| `update workout <userId> <workoutId> <newName>`   | Update a workout            |
| `remove workout <userId> <workoutId>`             | Delete a workout            |

Any unrecognised message returns a help summary listing the commands above.

---

### OpenAI Tool-Calling Mode

The agent sends your message to an OpenAI model with strict tool/function schemas. The LLM decides which tool to call and extracts the arguments from natural language.

**To enable:** set `LLM_MODE=openai` and provide a valid `OPENAI_API_KEY` in `.env`.

**Example natural language messages the LLM understands:**

- *"Log me in as scott with password mypass123"*
- *"Show me the profile for user abc-456"*
- *"Create a new push day workout for user abc-456"*
- *"Rename workout w-789 for user abc-456 to Upper Body Strength"*
- *"Delete workout w-789 from user abc-456"*

If the LLM can't determine a required argument, it will ask a follow-up question instead of guessing.

---

## Available Tools

These are the tools the agent can execute (in either mode):

| Tool             | Arguments                                      | Auth required | Description                        |
|------------------|-------------------------------------------------|---------------|------------------------------------|
| `login`          | `username`, `password`                          | No            | Calls `POST /auth/login`           |
| `get_user`       | `userId`                                        | Yes           | Calls `GET /users/{userId}`        |
| `add_workout`    | `userId`, `name`, `notes` (optional)            | Yes           | Calls `POST /users/{userId}/workouts` |
| `update_workout` | `userId`, `workoutId`, `name`, `notes` (both optional) | Yes  | Calls `PUT /users/{userId}/workouts/{workoutId}` |
| `remove_workout` | `userId`, `workoutId`                           | Yes           | Calls `DELETE /users/{userId}/workouts/{workoutId}` |

All tool arguments are validated with [Zod](https://zod.dev/) schemas before any API call is made.

---

## Session Management

Sessions track authentication state so you don't need to log in on every request.

1. **First request** — omit `sessionId`. The agent generates one and returns it.
2. **Subsequent requests** — include the `sessionId` from the first response. Your stored auth token is re-used automatically.

Sessions are stored **in-memory** and are lost when the service restarts.

> **Production recommendation:** Replace `InMemorySessionStore` in `src/sessionStore.ts` with Redis, a database, or any persistent store.

---

## Usage Examples

All examples use `curl`. Replace values in `<angle brackets>` with your own.

### Step 1 — Start a Session & Log In

```bash
curl -X POST http://localhost:3001/agent/chat \
  -H 'Content-Type: application/json' \
  -d '{"message": "login myuser mypassword"}'
```

**Response:**

```json
{
  "sessionId": "a1b2c3d4e5...long-hash",
  "reply": "Logged in successfully.",
  "toolResult": {
    "authenticated": true,
    "userId": "abc-123"
  }
}
```

Save the `sessionId` for use in all following requests.

---

### Step 2 — Fetch User Details

```bash
curl -X POST http://localhost:3001/agent/chat \
  -H 'Content-Type: application/json' \
  -d '{
    "sessionId": "<sessionId-from-step-1>",
    "message": "get user abc-123"
  }'
```

**Response:**

```json
{
  "sessionId": "<same-session-id>",
  "reply": "Fetched user.",
  "toolResult": {
    "id": "abc-123",
    "username": "myuser",
    "email": "myuser@example.com"
  }
}
```

---

### Step 3 — Add a Workout

```bash
curl -X POST http://localhost:3001/agent/chat \
  -H 'Content-Type: application/json' \
  -d '{
    "sessionId": "<sessionId>",
    "message": "add workout abc-123 Leg Day"
  }'
```

**Response:**

```json
{
  "sessionId": "<same-session-id>",
  "reply": "Workout added.",
  "toolResult": {
    "id": "w-789",
    "name": "Leg Day",
    "userId": "abc-123"
  }
}
```

---

### Step 4 — Update a Workout

```bash
curl -X POST http://localhost:3001/agent/chat \
  -H 'Content-Type: application/json' \
  -d '{
    "sessionId": "<sessionId>",
    "message": "update workout abc-123 w-789 Heavy Leg Day"
  }'
```

**Response:**

```json
{
  "sessionId": "<same-session-id>",
  "reply": "Workout updated.",
  "toolResult": {
    "id": "w-789",
    "name": "Heavy Leg Day"
  }
}
```

---

### Step 5 — Remove a Workout

```bash
curl -X POST http://localhost:3001/agent/chat \
  -H 'Content-Type: application/json' \
  -d '{
    "sessionId": "<sessionId>",
    "message": "remove workout abc-123 w-789"
  }'
```

**Response:**

```json
{
  "sessionId": "<same-session-id>",
  "reply": "Workout removed."
}
```

---

## Error Handling

The agent returns errors inline in the `reply` field. Common scenarios:

| Scenario                        | Example `reply`                                               |
|---------------------------------|---------------------------------------------------------------|
| Not logged in yet               | `"Tool call failed: Not authenticated in this session. Call login first."` |
| Invalid credentials             | `"Login failed: API 401 Unauthorized: ..."` |
| User/workout not found          | `"Could not fetch user: API 404 Not Found: ..."` |
| Backend not running             | `"Login failed: fetch failed"` |
| Missing required field          | `"Tool call failed: Expected string, received undefined"` (Zod validation) |
| Invalid request body to agent   | HTTP `400` with Zod error details |

The HTTP status from the agent is always `200` for successfully processed chat messages — check the `reply` text or absence of `toolResult` to detect logical errors.

---

## Running Tests

```bash
npm test
```

Tests use [Vitest](https://vitest.dev/) and run in rule-based mode (no OpenAI key needed):

```
 ✓ src/agent.test.ts (3)
   ✓ runAgent (3)
     ✓ returns help text for unknown command in rule-based mode
     ✓ returns usage hint for incomplete login command
     ✓ attempts login tool and returns error when API is unreachable

 Test Files  1 passed (1)
      Tests  3 passed (3)
```

---

## Building for Production

```bash
npm run build
```

This compiles TypeScript to `dist/`. Then run:

```bash
NODE_ENV=production npm start
```

---

## Security Notes

- **Tokens are server-side only.** Auth tokens from your Java API are stored in the agent's session store — they are never sent to the client.
- **No credentials sent to OpenAI.** In LLM mode, only the user's chat message is sent to OpenAI. Tokens, passwords (after login), and API responses are not forwarded to the LLM.
- **API key validation.** The agent will not attempt OpenAI calls if the key is empty, contains `your_key`, or doesn't start with `sk-`. It silently falls back to rule-based mode.
- **Input validation.** Every tool argument is validated with Zod before any API call is made.
- **Production hardening checklist:**
  - Replace in-memory session store with Redis or a database.
  - Add rate limiting (e.g., `express-rate-limit`).
  - Add HTTPS termination (via reverse proxy or directly).
  - Rotate `SESSION_SECRET` and store secrets in a vault, not `.env`.
  - Add request logging and audit trails for all tool executions.

---

## Troubleshooting

| Problem                                    | Solution                                                                                  |
|--------------------------------------------|-------------------------------------------------------------------------------------------|
| `Agent service listening on port 3001` never appears | Check `PORT` isn't in use: `lsof -i :3001`                                     |
| `Missing required environment variable`    | Make sure `.env` exists and has `WORKOUT_API_BASE_URL` set                               |
| `Login failed: fetch failed`               | Your Java Workout API isn't running on the configured `WORKOUT_API_BASE_URL`             |
| `Not authenticated in this session`        | You forgot to include `sessionId` from the login response, or the session expired (restart) |
| LLM mode not activating                    | Verify `LLM_MODE=openai` **and** `OPENAI_API_KEY=sk-...` (must start with `sk-`)        |
| `Incorrect API key provided`               | Your OpenAI key is invalid; double-check at https://platform.openai.com/api-keys         |
| Tests fail with OpenAI errors              | Tests should run in rule-based mode; make sure `.env` doesn't have a fake `sk-` key      |
