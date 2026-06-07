# Warehouse Triage Assistant

A Java CLI that triages warehouse incident reports using the Anthropic Claude API. It combines a deterministic high-risk keyword check with a Claude-generated triage summary.

## What it does

Given a free-text incident description, the app:

1. Flags the report for immediate human review if it contains a high-risk term (e.g. `fire`, `chemical spill`, `injury`, `production cannot continue`, `line has stopped`).
2. Sends the report to Claude with a structured triage prompt.
3. Prints a four-line result:
   - `Severity:` LOW / MEDIUM / HIGH / CRITICAL
   - `Summary:` one sentence
   - `Recommended action:` one practical next step
   - `Human review required:` YES / NO

## Requirements

- Java 17+
- Maven
- An Anthropic API key exported as `ANTHROPIC_API_KEY`

## Build and run

```bash
cd warehousetriageassistant
export ANTHROPIC_API_KEY=sk-ant-...
mvn -q compile exec:java -Dexec.mainClass=com.example.WarehouseTriageApp
```

Then describe the incident at the prompt.

## Project layout

```
warehousetriageassistant/
├── pom.xml
└── src/
    ├── main/java/com/example/WarehouseTriageApp.java
    └── test/java/com/example/AppTest.java
```
