package jvn.RentACar.serviceImpl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jvn.RentACar.model.Log;
import jvn.RentACar.service.LogService;
import jvn.RentACar.utils.IPAddressProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class ChangePasswordAttemptService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final int MAX_ATTEMPT = 3;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LogService logService;

    @Autowired
    private IPAddressProvider ipAddressProvider;

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
            if (changePassAttemptsCache.get(key) == MAX_ATTEMPT) {
                logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CPW", String.format("Because of too many attempts, user from %s is blocked.", ipAddressProvider.get())));
            }
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
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
