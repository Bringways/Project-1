package com.example.devopspractice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * A tiny hand-rolled health endpoint at GET /health.
 *
 * This matters a lot in DevOps: when you later deploy this app in Docker,
 * or behind a load balancer, or in Kubernetes, the platform needs a cheap
 * URL it can poll every few seconds to know "is this container alive and
 * ready to serve traffic?" This is that URL.
 *
 * (Spring Boot Actuator provides a much richer /actuator/health endpoint
 * out of the box, but we're keeping this simple and dependency-free for now
 * so you can see exactly how a health check works under the hood.)
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
