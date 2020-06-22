package com.rdc.zrj.flow.hystrixcloud.filter;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author leviathanstan
 * @date 05/03/2020 20:00
 */
@Component
public class HystrixFilter implements Filter {

    @Override
    @SuppressWarnings("unused")
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try(HystrixRequestContext context = HystrixRequestContext.initializeContext()){
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
