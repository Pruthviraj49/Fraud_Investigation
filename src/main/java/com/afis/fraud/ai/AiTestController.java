package com.afis.fraud.ai;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiTestController {

    private final AiTestService aiTestService;

    public AiTestController(
            AiTestService aiTestService) {

        this.aiTestService = aiTestService;
    }

    @GetMapping("/api/v1/ai/test")
    public String test() {

        return aiTestService.test();
    }
}