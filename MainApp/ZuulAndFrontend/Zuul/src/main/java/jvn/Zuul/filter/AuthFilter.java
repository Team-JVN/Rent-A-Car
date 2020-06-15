package jvn.Zuul.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import feign.FeignException;
import jvn.Zuul.client.AuthClient;
import jvn.Zuul.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AuthFilter extends ZuulFilter {

    @Autowired
    private AuthClient authClient;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        if (request.getMethod().equals("OPTIONS")) {
            return false;
        }
        String url = request.getRequestURL().toString();
        String method = request.getMethod();
        if (url.contains("users")) {
            return false;
        }
        if (url.contains("/search/h2")) {
            return false;
        }

        if (url.contains("/ws")) {
            return false;
        }
        if (new AntPathMatcher().match("**/api/car/{id}/picture", url) && method.equals("GET")) {
            return false;
        }
        if (method.equals("GET") && url.contains("/api/body-style")) {
            return false;
        }
        if (method.equals("GET") && url.contains("/api/fuel-type")) {
            return false;
        }
        if (method.equals("GET") && url.contains("/api/gearbox-type")) {
            return false;
        }
        if (method.equals("GET") && url.contains("/api/make")) {
            return false;
        }
        if (method.equals("GET") && url.contains("/api/make/{makeId}/models")) {
            return false;
        }
        if (new AntPathMatcher().match("**/api/advertisement/{id}", url) && method.equals("GET")) {
            return false;
        }
        return !method.equals("POST") || !request.getRequestURL().toString().contains("/api/advertisement/search");
    }

    private void setFailedRequest(String body, int code) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        if (ctx.getResponseBody() == null) {
            ctx.setResponseBody(body);
            ctx.setSendZuulResponse(false);
        }
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String header = request.getHeader("Auth");
        if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {
            ctx.setResponseStatusCode(401);
            ctx.setSendZuulResponse(false);
        } else {
            try {
                System.out.println("Verifikacija");
                UserDTO userDTO = authClient.verify(header);
                ctx.addZuulRequestHeader("user", jsonToString(userDTO));
                ctx.addZuulRequestHeader("Auth", header);
            } catch (FeignException.NotFound e) {
                setFailedRequest("Something goes wrong. Please try again.", 403);
            }
        }
        return null;
    }

    private String jsonToString(UserDTO userDTO) {
        ObjectMapper Obj = new ObjectMapper();

        try {
            return Obj.writeValueAsString(userDTO);
        } catch (IOException e) {
            setFailedRequest("Something is wrong. Please try again.", 403);
        }
        return null;
    }
}
