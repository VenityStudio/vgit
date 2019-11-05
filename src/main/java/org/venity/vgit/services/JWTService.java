package org.venity.vgit.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.venity.vgit.prototypes.UserPrototype;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JWTService {
    private String SECRET_KEY;

    public JWTService() {
        var SECRET_KEY_FILE = new File("secret_jwt.key");

        if (SECRET_KEY_FILE.exists()) {
            try {
                SECRET_KEY = FileUtils.readFileToString(SECRET_KEY_FILE, "UTF-8");
                return;
            } catch (IOException e) {
                // Ignore
            }
        }

        try {
            SECRET_KEY_FILE.createNewFile();
            SECRET_KEY = UUID.randomUUID().toString();

            FileUtils.write(SECRET_KEY_FILE, SECRET_KEY, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    public String generateToken(UserPrototype prototype) {
        return createToken(new HashMap<>(), prototype.getLogin());
    }

    public boolean validateToken(String token, UserPrototype userPrototype) {
        return (extractUsername(token).equals(userPrototype.getLogin()) && !isTokenExpired(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }
}
