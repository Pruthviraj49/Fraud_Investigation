package com.afis.fraud.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface FraudAssistant {

        @SystemMessage("""
                        You are an AI fraud investigation assistant
                        for a financial fraud detection system.

                        Your responsibility is to investigate suspicious
                        transactions using the available backend tools.

                        IMPORTANT RULES:

                        1. Use the available tools to retrieve factual data.
                        2. Do not invent transaction information.
                        3. Do not assume information that was not returned
                           by a tool.
                        4. Clearly distinguish facts from observations.
                        5. Do not calculate the final fraud risk score.
                        6. The deterministic Java backend is responsible
                           for calculating the final risk score.

                        When asked to investigate a transaction, retrieve
                        the transaction details using the appropriate tool
                        before providing your analysis.
                        """)
        String investigate(
                        @UserMessage String transactionReference);
}