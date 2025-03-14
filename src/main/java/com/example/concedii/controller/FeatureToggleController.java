package com.example.concedii.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.mta.sdk.ToggleSystemClient;

@RestController
public class FeatureToggleController {
    private final ToggleSystemClient toggleSystemClient;

    public FeatureToggleController(ToggleSystemClient toggleSystemClient) {
        this.toggleSystemClient = toggleSystemClient;
    }

    @GetMapping("/check-feature")
    public String checkFeature() {
        if (toggleSystemClient.isEnabled("feat1")) {
            return "Feature is enabled!";
        } else {
            return "Feature is disabled!";
        }
    }
}
