package org.greenloop.circularfashion.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        
        log.info("Request URL: {} {}", request.getMethod(), request.getRequestURI());
        log.debug("Request from IP: {}", request.getRemoteAddr());
        
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long executeTime = endTime - startTime;
        
        log.info("Request completed: {} {} - Status: {} - Time: {}ms", 
                request.getMethod(), 
                request.getRequestURI(), 
                response.getStatus(), 
                executeTime);
        
        // Special logging for health check endpoints
        if (request.getRequestURI().contains("/actuator/health")) {
            log.warn("HEALTH CHECK: {} {} - Status: {} - Time: {}ms", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    response.getStatus(), 
                    executeTime);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            log.error("Request failed: {} {} - Error: {}", 
                    request.getMethod(), 
                    request.getRequestURI(), 
                    ex.getMessage());
        }
    }
}
