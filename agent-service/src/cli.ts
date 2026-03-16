import readline from "readline";
import { spawn, ChildProcess } from "child_process";
import { config } from "./config.js";

const BASE = `http://localhost:${config.port}`;
const API_BASE = config.apiBaseUrl;
let sessionId: string | undefined;
let authToken: string | undefined;
let serverProcess: ChildProcess | null = null;

async function waitForServer(maxRetries = 15, delayMs = 500): Promise<boolean> {
  for (let i = 0; i < maxRetries; i++) {
    try {
      const res = await fetch(`${BASE}/health`);
      if (res.ok) return true;
    } catch {
      // not ready yet
    }
    await new Promise((r) => setTimeout(r, delayMs));
  }
  return false;
}

async function ensureServerRunning(): Promise<boolean> {
  try {
    const res = await fetch(`${BASE}/health`);
    if (res.ok) return true;
  } catch {
    // server not running — start it
  }

  console.log("⏳ Agent server not running. Starting it automatically...");
  serverProcess = spawn("npx", ["tsx", "src/server.ts"], {
    cwd: new URL("../", import.meta.url).pathname,
    stdio: "ignore",
    detached: false
  });

  serverProcess.on("error", (err) => {
    console.error("❌ Failed to start server:", err.message);
  });

  const ready = await waitForServer();
  if (ready) {
    console.log("✅ Agent server started on port " + config.port + "\n");
    return true;
  } else {
    console.error("❌ Agent server failed to start within timeout. Check for errors.");
    serverProcess.kill();
    serverProcess = null;
    return false;
  }
}

function cleanup() {
  if (serverProcess && !serverProcess.killed) {
    serverProcess.kill();
  }
}
process.on("exit", cleanup);
process.on("SIGINT", () => { cleanup(); process.exit(0); });
process.on("SIGTERM", () => { cleanup(); process.exit(0); });

// --- Main ---
const serverOk = await ensureServerRunning();
if (!serverOk) {
  console.error('Start the server manually with "npm run dev" and try again.');
  process.exit(1);
}

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
  prompt: "agent> "
});

console.log("🤖 AspireFit AI Agent");
console.log(`   Backend API: ${API_BASE}`);
console.log(`   Agent server: ${BASE}`);
console.log("");
console.log("   Commands:");
console.log("     ask <question>       — general fitness Q&A (no sign-in needed)");
console.log("     login <user> <pass>  — sign in to unlock personalised features");
console.log("     <anything else>      — personalised chat (requires sign-in)");
console.log("     quit                 — exit");
console.log("");
console.log(authToken ? "🔓 Signed in" : "🔒 Not signed in — use 'ask' or 'login' first");
console.log("");

rl.prompt();

rl.on("line", async (line) => {
  const message = line.trim();
  if (!message) {
    rl.prompt();
    return;
  }

  if (message === "quit" || message === "exit") {
    console.log("Bye!");
    process.exit(0);
  }

  // --- Login: call the Spring Boot API directly ---
  if (message.toLowerCase().startsWith("login ")) {
    const parts = message.split(" ");
    if (parts.length < 3) {
      console.log("\n⚠️  Usage: login <username> <password>\n");
      rl.prompt();
      return;
    }
    const [, username, password] = parts;
    try {
      const res = await fetch(`${API_BASE}/authz/v1/login?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`);
      if (!res.ok) {
        const text = await res.text();
        console.log(`\n❌ Login failed (${res.status}): ${text}\n`);
      } else {
        const body = await res.text();
        // The auth endpoint returns a token string or JSON
        let token: string;
        try {
          const json = JSON.parse(body);
          token = json.token ?? json.accessToken ?? body;
        } catch {
          token = body;
        }
        authToken = token;
        sessionId = undefined; // reset session for fresh auth
        console.log(`\n🔓 Signed in successfully! Token: ${token.slice(0, 20)}...`);
        console.log("   You can now use personalised features.\n");
      }
    } catch (err) {
      console.log(`\n❌ Could not reach API at ${API_BASE}. Is your Spring Boot app running?\n`);
    }
    rl.prompt();
    return;
  }

  // --- Ask: public endpoint (no auth) ---
  if (message.toLowerCase().startsWith("ask ")) {
    const question = message.slice(4).trim();
    if (!question) {
      console.log("\n⚠️  Usage: ask <your question>\n");
      rl.prompt();
      return;
    }
    try {
      const res = await fetch(`${BASE}/agent/ask`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message: question })
      });
      const data = await res.json() as { reply?: string; error?: unknown };
      if (data.reply) console.log(`\n💬 ${data.reply}\n`);
      if (data.error) console.log(`\n❌ ${JSON.stringify(data.error)}\n`);
    } catch {
      console.log(`\n❌ Could not reach agent server at ${BASE}.\n`);
    }
    rl.prompt();
    return;
  }

  // --- Chat: authenticated endpoint ---
  if (!authToken) {
    console.log("\n🔒 You need to sign in first to use personalised features.");
    console.log('   Use "login <username> <password>" or "ask <question>" for general Q&A.\n');
    rl.prompt();
    return;
  }

  try {
    const body: Record<string, string> = { message };
    if (sessionId) {
      body.sessionId = sessionId;
    }

    const res = await fetch(`${BASE}/agent/chat`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${authToken}`
      },
      body: JSON.stringify(body)
    });

    const data = await res.json() as { sessionId?: string; reply?: string; toolResult?: unknown; error?: unknown };

    if (data.sessionId) {
      sessionId = data.sessionId;
    }

    if (data.reply) {
      console.log(`\n💬 ${data.reply}`);
    }
    if (data.toolResult) {
      console.log("📦 Result:", JSON.stringify(data.toolResult, null, 2));
    }
    if (data.error) {
      console.log("❌", typeof data.error === "string" ? data.error : JSON.stringify(data.error));
    }
    console.log();
  } catch {
    console.log(`\n❌ Could not reach agent server at ${BASE}. Is it running?\n`);
  }

  rl.prompt();
});

rl.on("close", () => {
  console.log("\nBye!");
  process.exit(0);
});

