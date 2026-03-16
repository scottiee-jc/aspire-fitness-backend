# 🤖 AspireFit AI Agent Service

An AI-powered assistant for the AspireFit Workout API. Features two tiers of access:

- **Public** — Anyone can ask general fitness, exercise, and nutrition questions.
- **Authenticated** — Sign in via the API to unlock personalised features: manage workouts, view your profile, and get tailored advice.

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
  - [Ask — General Fitness Q\&A (public)](#ask--general-fitness-qa-public)
  - [Chat — Personalised Agent (authenticated)](#chat--personalised-agent-authenticated)
- [Agent Modes](#agent-modes)
- [Available Tools (authenticated)](#available-tools-authenticated)
- [Session Management](#session-management)
- [Interactive CLI](#interactive-cli)
- [Usage Examples](#usage-examples)
- [Error Handling](#error-handling)
- [Running Tests](#running-tests)
- [Building for Production](#building-for-production)
- [Security Notes](#security-notes)
- [Troubleshooting](#troubleshooting)

---

## Overview

```
                    ┌─────────────────────────────────────────────┐
                    │           Agent Service (Node.js)           │
                    │                                             │
  Anyone ──────────▶│  POST /agent/ask                            │
                    │  General fitness Q&A                        │
                    │  (OpenAI or rule-based, no auth needed)     │
                    │                                             │
  Signed-in ───────▶│  POST /agent/chat                           │
  user              │  Personalised workout management            │──▶ Workout API
  (Bearer token)    │  (tool-calling agent, auth required)        │    (Java/Spring)
                    │                                             │
                    └─────────────────────────────────────────────┘
```

**Key design principle:** The agent does **not** handle login. Users authenticate via the normal API (`/authz/v1/login`), receive a token, and pass it to the agent's `/agent/chat` endpoint. This mirrors how a real application works — you sign in once, then use the AI features.

---

## Architecture

```
┌──────────────┐                ┌──────────────────────┐              ┌──────────────────┐
│  Client App  │                │  Agent Service       │              │  Workout API     │
│              │   /agent/ask   │                      │              │  (Spring Boot)   │
│  browser,    │───────────────▶│  General Q&A         │              │  localhost:8080   │
│  mobile,     │   (no auth)    │  (OpenAI / rules)    │              │                  │
│  curl,       │                │                      │              │                  │
│  CLI         │   /agent/chat  │  Personalised agent  │  HTTP calls  │  /authz/v1/login │
│              │───────────────▶│  (tool-calling)      │─────────────▶│  /user/v1/...    │
│              │   (Bearer tok) │                      │              │  /workouts/...   │
└──────────────┘                └──────────────────────┘              └──────────────────┘
```

---

## Project Structure

```
agent-service/
├── .env.example          # Environment variable template
├── package.json          # Dependencies and scripts
├── tsconfig.json         # TypeScript configuration
├── src/
│   ├── server.ts         # Express HTTP server entry point
│   ├── cli.ts            # Interactive CLI client
│   ├── config.ts         # Loads and validates env vars
│   ├── types.ts          # Shared TypeScript types
│   ├── sessionStore.ts   # In-memory per-user session/token store
│   ├── apiClient.ts      # HTTP wrappers for the Workout API
│   ├── tools.ts          # Tool executor with Zod validation
│   ├── agent.ts          # Agent logic: runAsk (public) + runChat (auth)
│   ├── agent.test.ts     # Unit tests
│   └── routes/
│       └── chat.ts       # Route handlers: /agent/ask and /agent/chat
```

---

## Prerequisites

| Requirement        | Version | Notes                                      |
|--------------------|---------|---------------------------------------------|
| **Node.js**        | 20+     | Required for `fetch` and ES module support  |
| **npm**            | 10+     | Ships with Node 20                          |
| **Workout API**    | —       | Your Java backend running on port 8080      |
| **OpenAI API key** | —       | Optional — enables LLM-powered responses    |

---

## Installation

```bash
cd agent-service
cp .env.example .env    # then edit .env with your settings
npm install
```

---

## Configuration

Edit `.env`:

```dotenv
PORT=3001
WORKOUT_API_BASE_URL=http://localhost:8080
LLM_MODE=rule-based          # "rule-based" or "openai"
OPENAI_API_KEY=              # Your sk-... key (only for openai mode)
LLM_MODEL=gpt-4.1-mini
SESSION_SECRET=change-me
```

| Variable               | Required | Default        | Description                    |
|------------------------|----------|----------------|--------------------------------|
| `PORT`                 | No       | `3001`         | Agent HTTP listen port         |
| `WORKOUT_API_BASE_URL` | **Yes**  | —              | Your Spring Boot API URL       |
| `LLM_MODE`             | No       | `rule-based`   | `rule-based` or `openai`       |
| `OPENAI_API_KEY`       | No*      | —              | *Required if `LLM_MODE=openai` |
| `LLM_MODEL`            | No       | `gpt-4.1-mini` | OpenAI model to use            |
| `SESSION_SECRET`       | No       | `change-me`    | Seed for session hashing       |

---

## Running the Service

### Development (hot-reload)

```bash
npm run dev
```

### Production

```bash
npm run build
npm start
```

### Interactive CLI

```bash
npm run cli
```

The CLI auto-starts the server if it's not running. See [Interactive CLI](#interactive-cli) for details.

---

## API Reference

### Health Check

```
GET /health
→ { "ok": true }
```

---

### Ask — General Fitness Q&A (public)

No authentication required. Anyone can ask general fitness questions.

```
POST /agent/ask
Content-Type: application/json
```

**Request body:**

| Field     | Type   | Required | Description                |
|-----------|--------|----------|----------------------------|
| `message` | string | **Yes**  | Your fitness question      |

**Response body:**

| Field   | Type   | Description                     |
|---------|--------|---------------------------------|
| `reply` | string | The agent's answer              |

**Example:**

```bash
curl -X POST http://localhost:3001/agent/ask \
  -H 'Content-Type: application/json' \
  -d '{"message": "How much protein should I eat to build muscle?"}'
```

```json
{
  "reply": "For muscle building, aim for 1.6–2.2 g of protein per kg of body weight daily..."
}
```

---

### Chat — Personalised Agent (authenticated)

Requires a Bearer token from the Workout API. The agent validates your token, then can manage your workouts and give personalised advice.

```
POST /agent/chat
Content-Type: application/json
Authorization: Bearer <your-api-token>
```

**Request body:**

| Field       | Type   | Required | Description                                            |
|-------------|--------|----------|--------------------------------------------------------|
| `message`   | string | **Yes**  | Command or natural language message                    |
| `sessionId` | string | No       | Reuse a session ID to maintain context across requests |

**Response body:**

| Field        | Type   | Always present | Description                           |
|--------------|--------|----------------|---------------------------------------|
| `sessionId`  | string | Yes            | Session ID for subsequent requests    |
| `reply`      | string | Yes            | Human-readable response               |
| `toolResult` | object | No             | Raw data from the Workout API         |

**Example:**

```bash
# Step 1: Sign in via the Spring Boot API
TOKEN=$(curl -s 'http://localhost:8080/authz/v1/login?username=scott&password=mypass')

# Step 2: Use the token with the agent
curl -X POST http://localhost:3001/agent/chat \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"message": "Add a new workout called Upper Body Push"}'
```

```json
{
  "sessionId": "a1b2c3...",
  "reply": "Done — I've created your 'Upper Body Push' workout.",
  "toolResult": { "id": "w-123", "name": "Upper Body Push" }
}
```

**Without a token:**

```json
{
  "error": "Authentication required. Please sign in via the API first and include your token as: Authorization: Bearer <token>"
}
```

---

## Agent Modes

### Rule-Based Mode (default)

Fast, deterministic, no external API calls. The **ask** endpoint responds with curated fitness advice for common topics (protein, recovery, warm-ups, beginner tips, fat loss). The **chat** endpoint matches structured commands.

### OpenAI Mode

Set `LLM_MODE=openai` with a valid API key. Both endpoints use OpenAI:

- **Ask** — natural language fitness Q&A powered by GPT
- **Chat** — tool-calling agent that interprets natural language and automatically calls the right API endpoints

---

## Available Tools (authenticated)

These tools are only available on `/agent/chat` for signed-in users:

| Tool             | Arguments                              | Description                                  |
|------------------|----------------------------------------|----------------------------------------------|
| `get_user`       | *(none — uses session)*                | Fetch your profile                           |
| `add_workout`    | `name`, `notes` (optional)             | Create a new workout                         |
| `update_workout` | `workoutId`, `name`, `notes` (optional)| Update an existing workout                   |
| `remove_workout` | `workoutId`                            | Delete a workout                             |

> **Note:** `userId` is automatically injected from your session — neither you nor the LLM need to specify it.

---

## Session Management

1. **First chat request** — omit `sessionId`. The agent generates one and returns it.
2. **Subsequent requests** — include the `sessionId`. Your auth context is reused.

Sessions are stored **in-memory** and lost on restart.

> **Production:** Replace `InMemorySessionStore` with Redis or a database.

---

## Interactive CLI

The CLI provides an interactive terminal experience:

```bash
npm run cli
```

```
🤖 AspireFit AI Agent
   Backend API: http://localhost:8080
   Agent server: http://localhost:3001

   Commands:
     ask <question>       — general fitness Q&A (no sign-in needed)
     login <user> <pass>  — sign in to unlock personalised features
     <anything else>      — personalised chat (requires sign-in)
     quit                 — exit

🔒 Not signed in — use 'ask' or 'login' first

agent> ask what are good exercises for beginners?

💬 If you're just starting out, focus on compound movements: squats, deadlifts,
   bench press, rows, and overhead press. Start light, nail your form, and add
   weight gradually.

agent> login scott mypassword

🔓 Signed in successfully! Token: eyJhbGciOiJIUzI1...
   You can now use personalised features.

agent> add a new workout called Leg Day

💬 Done — I've created your 'Leg Day' workout.
📦 Result: { "id": "w-456", "name": "Leg Day" }

agent> quit
Bye!
```

The CLI automatically:
- Starts the agent server if it's not already running
- Handles login by calling the Spring Boot API directly
- Routes `ask` commands to the public endpoint
- Routes everything else to the authenticated endpoint (with your token)

---

## Usage Examples

### Ask a general question (no sign-in)

```bash
curl -X POST http://localhost:3001/agent/ask \
  -H 'Content-Type: application/json' \
  -d '{"message": "How should I warm up before lifting?"}'
```

### Sign in and manage workouts

```bash
# 1. Login via the Spring Boot API
TOKEN=$(curl -s 'http://localhost:8080/authz/v1/login?username=scott&password=mypass')

# 2. Add a workout
curl -X POST http://localhost:3001/agent/chat \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"message": "Add a workout called Push Day"}'

# 3. Use the returned sessionId for follow-ups
curl -X POST http://localhost:3001/agent/chat \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"sessionId": "<from-previous>", "message": "Now add a Pull Day workout too"}'
```

---

## Error Handling

| Scenario                     | HTTP Status | Response                                               |
|------------------------------|-------------|--------------------------------------------------------|
| Missing/invalid token        | `401`       | `{"error": "Authentication required..."}`              |
| Token expired or invalid     | `401`       | `{"error": "Invalid or expired token..."}`             |
| Missing `message` field      | `400`       | `{"error": { ... }}` (Zod validation details)          |
| Tool execution failed        | `200`       | `{"reply": "Action failed: ..."}` in reply text        |
| Backend API not running      | `200`/`500` | Error details in reply or HTTP 500                     |

---

## Running Tests

```bash
npm test
```

Tests run in rule-based mode (no OpenAI key needed).

---

## Building for Production

```bash
npm run build
NODE_ENV=production npm start
```

---

## Security Notes

- **Login is external.** The agent never handles credentials — users sign in via the API and pass the resulting token.
- **Tokens are server-side.** Auth tokens are stored in the agent's session store, never exposed to the LLM.
- **No credentials sent to OpenAI.** Only the user's chat message is sent. Tokens and API responses are not forwarded.
- **Input validation.** Every tool argument is validated with Zod before any API call.
- **Production hardening:**
  - Replace in-memory session store with Redis.
  - Add rate limiting (`express-rate-limit`).
  - Add HTTPS termination.
  - Store secrets in a vault, not `.env`.
  - Add request logging and audit trails.

---

## Troubleshooting

| Problem                                 | Solution                                                             |
|-----------------------------------------|----------------------------------------------------------------------|
| `Missing required environment variable` | Ensure `.env` exists with `WORKOUT_API_BASE_URL`                     |
| `Authentication required...`            | Pass `Authorization: Bearer <token>` header to `/agent/chat`         |
| `Invalid or expired token`              | Sign in again via `/authz/v1/login`                                  |
| Agent server not starting               | Check port: `lsof -i :3001`                                         |
| LLM mode not activating                 | Verify `LLM_MODE=openai` and `OPENAI_API_KEY=sk-...`                |
| `ask` returns generic response          | Enable `LLM_MODE=openai` for richer answers                         |
| Backend API unreachable                 | Make sure Spring Boot is running on `WORKOUT_API_BASE_URL`           |
