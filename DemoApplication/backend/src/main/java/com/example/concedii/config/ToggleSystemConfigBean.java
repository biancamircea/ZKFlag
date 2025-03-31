package com.example.concedii.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.mta.sdk.ToggleSystemClient;
import ro.mta.sdk.ToggleSystemConfig;

@Configuration
public class ToggleSystemConfigBean {
    @Bean
    public ToggleSystemClient toggleSystemClient() {
        ToggleSystemConfig toggleSystemConfig = ToggleSystemConfig.builder()
                .toggleServerAPI("https://localhost:8443")
                .apiKey("Q4z23ZaK:Ml20Xk0j:YGuOqVxL:0.11874B303D0F7F9A451579BE23B60A0EFD7510D26D07B8897F1A9728A85B285C")
                .appName("concedii")
                .build();

        return new ToggleSystemClient(toggleSystemConfig);
    }

}
