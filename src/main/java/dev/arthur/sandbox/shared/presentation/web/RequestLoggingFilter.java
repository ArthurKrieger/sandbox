package dev.arthur.sandbox.shared.presentation.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final long startTime = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            final long durationMs = System.currentTimeMillis() - startTime;
            log.info("{} {} -> {} ({}ms)",
                    kv("http_method", request.getMethod()),
                    kv("http_path", request.getRequestURI()),
                    kv("http_status", response.getStatus()),
                    kv("duration_ms", durationMs),
                    kv("http_query", request.getQueryString()));
        }
    }
}
