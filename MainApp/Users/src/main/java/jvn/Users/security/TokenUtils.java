package jvn.Users.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jvn.Users.model.Permission;
import jvn.Users.model.User;

import jvn.Users.utils.TimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Set;

@Component
public class TokenUtils {
    @Value("${TOKEN_UTILS_APP_NAME}")
    private String APP_NAME;

    @Value("${TOKEN_UTILS_SECRET}")
    public String SECRET;

    @Value("${TOKEN_UTILS_EXPIRES_IN}")
    private int EXPIRES_IN;

    @Value("${TOKEN_UTILS_REFRESH_TOKEN_EXPIRES_IN}")
    private int REFRESH_TOKEN_EXPIRES_IN;

    @Value("${TOKEN_UTILS_AUTH_HEADER}")
    private String AUTH_HEADER;

    static final String AUDIENCE_WEB = "web";

    @Autowired
    private TimeProvider timeProvider;

    private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    // Funkcija za generisanje JWT token
    public String generateToken(String username, String role, Set<Permission> permissions) {
        return Jwts.builder().setIssuer(APP_NAME).setSubject(username).setAudience(generateAudience())
                .setIssuedAt(timeProvider.now()).setExpiration(generateExpirationDate()).claim("role", role)
                .claim("permissions", permissions) // postavljanje proizvoljnih podataka u telo JWT tokena
                .signWith(SIGNATURE_ALGORITHM, SECRET).compact();
    }

    // Funkcija za generisanje JWT token
    public String generateRefreshToken(String username) {
        return Jwts.builder().setIssuer(APP_NAME).setSubject(username).setAudience(generateAudience())
                .setIssuedAt(timeProvider.now())
                .setExpiration(new Date(timeProvider.now().getTime() + REFRESH_TOKEN_EXPIRES_IN))
                .signWith(SIGNATURE_ALGORITHM, SECRET).compact();
    }

    private String generateAudience() {
        return AUDIENCE_WEB;
    }

    private Date generateExpirationDate() {
        return new Date(timeProvider.now().getTime() + EXPIRES_IN);
    }

    public String refreshToken(String token, User user) {
        String refreshedToken;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            claims.setIssuedAt(timeProvider.now());
            refreshedToken = Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate())
                    .claim("role", user.getRole().getName()).claim("permissions", user.getRole().getPermissions())
                    .signWith(SIGNATURE_ALGORITHM, SECRET).compact();
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = this.getIssuedAtDateFromToken(token);
        return (!(this.isCreatedBeforeLastPasswordReset(created, lastPasswordReset))
                && (!(this.isTokenExpired(token))));
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        User user = (User) userDetails;
        final String username = getUsernameFromToken(token);
        final Date created = getIssuedAtDateFromToken(token);

        return (username != null && username.equals(userDetails.getUsername())
                && !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()));
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getIssuedAtDateFromToken(String token) {
        Date issueAt;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            issueAt = claims.getIssuedAt();
        } catch (Exception e) {
            issueAt = null;
        }
        return issueAt;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = this.getAllClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public int getExpiredIn() {
        return EXPIRES_IN;
    }

    public String getToken(HttpServletRequest request) {
        String authHeader = getAuthHeaderFromHeader(request);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    public String getAuthHeaderFromHeader(HttpServletRequest request) {
        return request.getHeader(AUTH_HEADER);
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(timeProvider.now());
    }

    private Claims getAllClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

}
