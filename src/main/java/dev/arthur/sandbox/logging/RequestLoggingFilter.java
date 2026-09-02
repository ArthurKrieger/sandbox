package dev.arthur.sandbox.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * One INFO line per request: method, path, status, duration. trace_id/span_id
 * come along for free — they're already injected into MDC by the OTel Java
 * agent whenever a span is active, and printed by the log pattern in
 * application.yml — so every line here is directly correlatable with its
 * trace in Jaeger.
 */
@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - startTime;
            String query = request.getQueryString();
            String uri = query != null ? request.getRequestURI() + "?" + query : request.getRequestURI();
            log.info("{} {} -> {} ({}ms)", request.getMethod(), uri, response.getStatus(), durationMs);
        }
    }
}
