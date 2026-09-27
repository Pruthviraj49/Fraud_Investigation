package com.afis.fraud.ai;

import org.springframework.stereotype.Service;

@Service
public class AiTestService {

    private final FraudAssistant fraudAssistant;

    public AiTestService(
            FraudAssistant fraudAssistant) {

        this.fraudAssistant = fraudAssistant;
    }

    public String test() {

        return fraudAssistant.investigate(
                "Investigate transaction TXN-68998930");
    }
}