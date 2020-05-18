package jvn.Zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import feign.FeignException;
import jvn.Zuul.client.AuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

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
        return true;
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
        String header = request.getHeader("Authorization");

//        if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {
//            ctx.setResponseStatusCode(401);
//            ctx.setSendZuulResponse(false);
//        }else {
//            try {
//                System.out.println("Verifikacija");
//                authClient.verify();
//
//            } catch (FeignException.NotFound e) {
//                setFailedRequest("Consumer does not exist!", 403);
//            }
//        }

        try {
            System.out.println("Verifikacija");
            authClient.verify();

        } catch (FeignException.NotFound e) {
            setFailedRequest("User does not exist!", 403);
        }

        return null;
    }

}

