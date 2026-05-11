package com.upc.gessi.qrapids.app.config.security;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtKeyProvider {

    private static final int MIN_HS512_KEY_LENGTH_BYTES = 64;

    private final Key signingKey;

    public JwtKeyProvider(@Value("${security.jwt.secret}") String jwtSecret) {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_HS512_KEY_LENGTH_BYTES) {
            throw new IllegalStateException("security.jwt.secret must be at least 64 bytes for HS512");
        }
        this.signingKey = new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    }

    public Key getSigningKey() {
        return signingKey;
    }
}
