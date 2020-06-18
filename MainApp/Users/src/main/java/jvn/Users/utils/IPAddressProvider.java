package jvn.Users.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class IPAddressProvider {

    private HttpServletRequest request;

    public String get() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @Autowired
    public IPAddressProvider(HttpServletRequest request) {
        this.request = request;
    }
}
