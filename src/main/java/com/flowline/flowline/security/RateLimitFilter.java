package com.flowline.flowline.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 60_000;

    private record AttemptRecord(int count, long windowStart) {}

    private final ConcurrentHashMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (!request.getRequestURI().equals("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        String ip = request.getRemoteAddr();

        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            filterChain.doFilter(request, response);
            return;
        }
        long now = System.currentTimeMillis();

        AttemptRecord record = attempts.compute(ip, (key, existing) -> {
            if (existing == null || now - existing.windowStart() > WINDOW_MS) {
                return new AttemptRecord(1, now); // nova janela
            }
            return new AttemptRecord(existing.count() + 1, existing.windowStart());
        });

        if (record.count() > MAX_ATTEMPTS) {
            log.warn("Rate limit exceeded for IP: {}", ip);
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "status": 429,
                    "message": "Too many login attempts. Try again in 1 minute.",
                    "timestamp": "%s"
                }
                """.formatted(LocalDateTime.now()));
            return;
        }

        log.info("Login attempt {}/{} from IP: {}", record.count(), MAX_ATTEMPTS, ip);
        filterChain.doFilter(request, response);
    }
}
