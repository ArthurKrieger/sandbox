package dev.arthur.sandbox.shared.presentation.web;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLoggingFilterTest {

    @Test
    void passesTheRequestOnUnchanged() throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/counters");
        request.setQueryString("limit=5");
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final MockFilterChain chain = new MockFilterChain();

        new RequestLoggingFilter().doFilter(request, response, chain);

        assertThat(chain.getRequest()).isSameAs(request);
        assertThat(chain.getResponse()).isSameAs(response);
    }
}
