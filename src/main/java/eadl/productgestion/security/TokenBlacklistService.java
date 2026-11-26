package eadl.productgestion.security;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Date> revokedTokens = new ConcurrentHashMap<>();

    public void revoke(String token, Date expiration) {
        purgeExpired();
        revokedTokens.put(token, expiration);
    }

    public boolean isRevoked(String token) {
        return revokedTokens.containsKey(token);
    }

    private void purgeExpired() {
        Date now = new Date();
        revokedTokens.values().removeIf(expiration -> expiration.before(now));
    }
}
