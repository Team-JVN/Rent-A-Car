package jvn.Zuul.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import feign.FeignException;
import jvn.Zuul.client.AuthClient;
import jvn.Zuul.dto.UserDTO;
import jvn.Zuul.dto.UserSignedDTO;
import jvn.Zuul.dto.message.Log;
import jvn.Zuul.producer.LogProducer;
import jvn.Zuul.service.DigitalSignatureService;
import jvn.Zuul.utils.IPAddressProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AuthFilter extends ZuulFilter {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final String USERS_ALIAS = "users";

    @Autowired
    private AuthClient authClient;

    @Autowired
    private LogProducer logProducer;

    @Autowired
    private IPAddressProvider ipAddressProvider;

    @Autowired
    private DigitalSignatureService digitalSignatureService;

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
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "ATZ", String.format("User from %s tried to access %s API", ipAddressProvider.get(), request.getRequestURI())));
            ctx.setResponseStatusCode(401);
            ctx.setSendZuulResponse(false);
        } else {
            try {
                UserSignedDTO userSignedDTO = authClient.verify(header);
                if (digitalSignatureService.decrypt(USERS_ALIAS, userSignedDTO.getUserBytes(), userSignedDTO.getDigitalSignature())) {
                    UserDTO userDTO = bytesToObject(userSignedDTO.getUserBytes());
                    ctx.addZuulRequestHeader("user", jsonToString(userDTO));
                    ctx.addZuulRequestHeader("Auth", header);
                } else {
                    logProducer.send(new Log(Log.WARN, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SGN", "Invalid digital signature"));
                    setFailedRequest("Something went wrong. Please try again.", 403);
                }
            } catch (FeignException.NotFound e) {
                logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "FGN", "Users service is not responding"));
                setFailedRequest("Something went wrong. Please try again.", 403);
            }
        }
        return null;
    }

    private UserDTO bytesToObject(byte[] byteArray) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(byteArray, UserDTO.class);
        } catch (IOException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping byte array to %s failed", UserDTO.class.getSimpleName())));
            return null;
        }
    }

    private String jsonToString(UserDTO userDTO) {
        ObjectMapper Obj = new ObjectMapper();
        try {
            return Obj.writeValueAsString(userDTO);
        } catch (IOException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", UserDTO.class.getSimpleName())));
            setFailedRequest("Something went wrong. Please try again.", 403);
        }
        return null;
    }
}
