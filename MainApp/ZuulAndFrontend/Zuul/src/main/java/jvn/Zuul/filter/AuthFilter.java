package jvn.Zuul.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Zuul.dto.UserDTO;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    public AuthFilter() {
        super(Config.class);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (!haveToCheckRequest(request)) {
                return chain.filter(exchange);
            }
            List<String> headers = request.getHeaders().get("Auth");
            if (headers == null || headers.isEmpty()) {
                return this.onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
            }
            String header = headers.get(0);
            if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {
                return this.onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
            } else {
                WebClient webClient = createWebClient();
                UserDTO userDto = webClient.get()
                        .uri("/users/api/verify")
                        .header("Auth", header)
                        .retrieve()
                        .bodyToMono(UserDTO.class).block();
                if (userDto == null) {
                    return this.onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
                }
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().header("Auth", header).header("user", jsonToString(userDto)).build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            }
        };
    }

    public static class Config {
    }


    private boolean haveToCheckRequest(ServerHttpRequest request) {
        if (Objects.equals(request.getMethod(), HttpMethod.OPTIONS)) {
            return false;
        }
        String url = request.getURI().toString();
        HttpMethod method = request.getMethod();
        if (new AntPathMatcher().match("**/api/advertisement/{id}", url) && request.getMethod().equals(HttpMethod.GET)) {
            return false;
        }
        if (url.contains("/advertisements/websocket")) {
            return false;
        }
        if (url.contains("/search/h2")) {
            return false;
        }
        if (new AntPathMatcher().match("**/api/car/{id}/picture", url) && Objects.equals(method, HttpMethod.GET)) {
            return false;
        }
        if (Objects.equals(method, HttpMethod.GET) && url.contains("/api/body-style")) {
            return false;
        }
        if (Objects.equals(method, HttpMethod.GET) && url.contains("/api/fuel-type")) {
            return false;
        }
        if (Objects.equals(method, HttpMethod.GET) && url.contains("/api/gearbox-type")) {
            return false;
        }
        if (Objects.equals(method, HttpMethod.GET) && url.contains("/api/make")) {
            return false;
        }
        if (Objects.equals(method, HttpMethod.GET) && url.contains("/api/make/{makeId}/models")) {
            return false;
        }
        return !Objects.equals(method, HttpMethod.POST) || !url.contains("/api/advertisement/search");
    }

    private WebClient createWebClient() {
        return WebClient.create("http://localhost:8080");
    }

    private String jsonToString(UserDTO userDTO) {
        ObjectMapper Obj = new ObjectMapper();

        try {
            return Obj.writeValueAsString(userDTO);
        } catch (IOException e) {
            return null;
        }
    }
}
