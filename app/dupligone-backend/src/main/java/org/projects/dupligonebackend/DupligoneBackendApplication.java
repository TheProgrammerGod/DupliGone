package org.projects.dupligonebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DupligoneBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(DupligoneBackendApplication.class, args);
    }

}
