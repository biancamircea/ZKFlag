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
                .toggleServerAPI("http://localhost:8080")
                .apiKey("Q4z23ZaK:Ml2JXk0j:nmVMBZ0e:1.398203CADFF80B0723E92D1E485713AAF1EF6C05599CF1852F947D76F12EC171")
                .appName("concedii")
                .remoteEvaluation(true)
                .build();

        return new ToggleSystemClient(toggleSystemConfig);
    }

}
