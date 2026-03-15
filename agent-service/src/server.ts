import express from "express";
import { config } from "./config.js";
import { chatRouter } from "./routes/chat.js";

const app = express();
app.use(express.json());

app.get("/health", (_req, res) => {
  res.json({ ok: true });
});

app.use("/agent", chatRouter);

app.listen(config.port, () => {
  console.log(`Agent service listening on port ${config.port}`);
});

