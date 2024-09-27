package org.safescan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Start function of the back-end system.
 */
@SpringBootApplication
public class SpringBootSafeScanApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootSafeScanApplication.class, args);
    }
}
