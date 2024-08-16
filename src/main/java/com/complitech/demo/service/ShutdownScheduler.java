package com.complitech.demo.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ShutdownScheduler {

    @Value("${application.shutdown-time}")
    private LocalDateTime shutdownTime;

    /**
     * End's execution of application if shutdownTime is after current date.
     */
    @Scheduled(fixedRate = 6000)
    public void checkShutdown() {
        if (shutdownTime != null && LocalDateTime.now().isAfter(shutdownTime)) {
            System.exit(0);
        }
    }
}
