package dev.arthur.sandbox.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * One INFO line per request. Fields are passed via StructuredArguments (kv),
 * which the JSON encoder (see logback-spring.xml) turns into their own
 * top-level Elasticsearch fields (http_method, http_status, duration_ms...)
 * instead of burying them in one text blob — Kibana can then filter/sort/
 * aggregate on them directly (e.g. duration_ms > 1000, http_status >= 500).
 * trace_id/span_id come along for free via MDC, injected automatically by
 * the OTel Java agent whenever a span is active.
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
            log.info("{} {} -> {} ({}ms)",
                    kv("http_method", request.getMethod()),
                    kv("http_path", request.getRequestURI()),
                    kv("http_status", response.getStatus()),
                    kv("duration_ms", durationMs),
                    kv("http_query", request.getQueryString()));
        }
    }
}
