package com.example;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import java.util.Scanner;

public final class WarehouseTriageApp {

    private WarehouseTriageApp() {

    }

    private static final String TRIAGE_INSTRUCTIONS = """
            You are assisting a warehouse operations manager.

              Analyse the warehouse incident report.

              Return exactly four lines:
              Severity: LOW, MEDIUM, HIGH, or CRITICAL
              Summary: one clear sentence
              Recommended action: one practical next step
              Human review required: YES or NO

              Use a cautious approach.
              Require human review if:
              - production may stop
              - safety may be affected
              - important information is missing
              """;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Describe the warehouse incident: ");
        String incidentReport = scanner.nextLine();

        if (incidentReport.isBlank()) {
            System.err.println("The incident report cannot be blank.");
            return;
        }

        System.out.println("Incident received:");
        System.out.println(incidentReport);

        boolean requiresImmediateEscalation = containsHighRiskTerm(incidentReport);
        if (requiresImmediateEscalation) {
            System.out.println("""
            Deterministic safety alert:
            This report contains a high-risk term.
            Immediate human review is required.
                    """);
        }

        String triagePrompt = createTriagePrompt(incidentReport);

        Message triageResponse = requestTriageFromClaude(triagePrompt);

        System.out.println("\nWarehouse triage result:");
        printClaudeResponse(triageResponse);

        // System.out.println("Prompt created:");
        // System.out.println(triagePrompt);

    }

    private static String createTriagePrompt(String incidentReport) {
        return TRIAGE_INSTRUCTIONS
                + "\n<incident_report>\n"
                + incidentReport
                + "\n</incident_report>";
    }

    private static boolean containsHighRiskTerm(String incidentReport){
        String normalisedReport = incidentReport.toLowerCase();

        return normalisedReport.contains("fire")
                || normalisedReport.contains("chemical spill")
                || normalisedReport.contains("injury")
                || normalisedReport.contains("production cannot continue")
                || normalisedReport.contains("line has stopped");
    }

    private static Message requestTriageFromClaude(String triagePrompt) {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "ANTHROPIC_API_KEY is missing. Check your Cursor terminal.");
        }
        AnthropicClient claudeClient = AnthropicOkHttpClient.builder()
                .apiKey(apiKey.trim())
                .build();

        MessageCreateParams request = MessageCreateParams.builder()
                .model(Model.CLAUDE_OPUS_4_8)
                .maxTokens(300L)
                .addUserMessage(triagePrompt)
                .build();
        return claudeClient.messages().create(request);
    }

    private static void printClaudeResponse(Message triageResponse) {
        triageResponse.content()
                .stream()
                .flatMap(contentBlock -> contentBlock.text().stream())
                .forEach(textBlock -> System.out.println(textBlock.text()));
    }
}
