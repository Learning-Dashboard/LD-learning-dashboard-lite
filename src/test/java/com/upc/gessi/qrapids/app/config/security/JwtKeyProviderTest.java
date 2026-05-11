package com.upc.gessi.qrapids.app.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JwtKeyProviderTest {

    @Test
    public void acceptsHs512SecretWithAtLeast64Bytes() {
        JwtKeyProvider jwtKeyProvider = new JwtKeyProvider(secretOfLength(64));

        assertNotNull(jwtKeyProvider.getSigningKey());
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsSecretShorterThanHs512Minimum() {
        new JwtKeyProvider(secretOfLength(63));
    }

    @Test
    public void signsAndParsesTokenWithConfiguredSecret() {
        JwtKeyProvider jwtKeyProvider = new JwtKeyProvider(secretOfLength(64));
        String token = Jwts.builder()
                .setSubject("admin")
                .signWith(SignatureAlgorithm.HS512, jwtKeyProvider.getSigningKey())
                .compact();

        String subject = Jwts.parser()
                .setSigningKey(jwtKeyProvider.getSigningKey())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        assertEquals("admin", subject);
    }

    private static String secretOfLength(int length) {
        StringBuilder secret = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            secret.append('a');
        }
        return secret.toString();
    }
}
