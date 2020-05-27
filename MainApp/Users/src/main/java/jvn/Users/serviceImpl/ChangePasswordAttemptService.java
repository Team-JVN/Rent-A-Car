package jvn.Users.serviceImpl;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class ChangePasswordAttemptService {

    private final int MAX_ATTEMPT = 3;

    @Autowired
    private HttpServletRequest request;

    private LoadingCache<String, Integer> changePassAttemptsCache;

    public ChangePasswordAttemptService() {
        super();
        changePassAttemptsCache = CacheBuilder.newBuilder().
                expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }

    public void changePassSucceeded() {
        String key = getClientIP();
        changePassAttemptsCache.invalidate(key);
    }

    public void changePassFailed() {
        String key = getClientIP();
        int attempts = 0;
        try {
            attempts = changePassAttemptsCache.get(key);
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        changePassAttemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {
        try {
            return changePassAttemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
