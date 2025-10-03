package org.greenloop.circularfashion.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontEndUrl;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/ws")
                .setAllowedOrigins(frontEndUrl, "http://localhost:3000", "http://localhost:3001")
                .addInterceptors(new WebSocketAuthInterceptor())
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    // Custom interceptor to handle JWT token from query parameters
    public static class WebSocketAuthInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request,
                                     org.springframework.http.server.ServerHttpResponse response,
                                     org.springframework.web.socket.WebSocketHandler wsHandler,
                                     Map<String, Object> attributes) throws Exception {
            // Extract token from query parameters
            String token = request.getURI().getQuery();
            if (token != null && token.startsWith("token=")) {
                token = token.substring(6); // Remove "token=" prefix
                if (!token.isEmpty()) {
                    attributes.put("token", token);
                    log.info("WebSocket connection with token: {}", token.substring(0, Math.min(20, token.length())) + "...");
                }
            }
            return true;
        }

        @Override
        public void afterHandshake(org.springframework.http.server.ServerHttpRequest request,
                                 org.springframework.http.server.ServerHttpResponse response,
                                 org.springframework.web.socket.WebSocketHandler wsHandler,
                                 Exception exception) {
            // Handshake completed
        }
    }
}
