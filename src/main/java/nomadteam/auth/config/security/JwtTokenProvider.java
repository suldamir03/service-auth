package nomadteam.auth.config.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.crypto.SecretKey;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nomadteam.auth.persistence.entity.Role;
import nomadteam.auth.persistence.entity.UserCredentials;
import nomadteam.auth.persistence.repo.UserCredentialsRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String JWT_ACCESS_SECRET = "B24F80D9D2148D08791DE709E28C6C4F5DD68AF097AC5143DD6D4EFF942F2D17";
    private final String JWT_REFRESH_SECRET = "C54F80D3HI148D08791DE7HFO48C6C4F5DD68AF097AC5143RK6D4EFF942F2D19";
    private final SecretKey jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_ACCESS_SECRET));
    private final SecretKey jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_REFRESH_SECRET));

    private final UserCredentialsRepository userRepository;
    public ObjectMapper mapper = new ObjectMapper();


    public JwtTokenProvider(UserCredentialsRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateAccessToken(String username, Collection<? extends GrantedAuthority> roles) {
        UserCredentials user = userRepository.findUserCredentialsByUsername(username);

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("auth", roles.stream().toList());
        claims.put("id", user.getId());

        Date now = new Date();
        long validityInMilliseconds = 864_000_000;
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(jwtAccessSecret)
                .compact();
    }

    public String generateRefreshToken(@NonNull String username) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public CustomAuthentication getAuthentication(String token) {
        UserCredentials user = userRepository.findUserCredentialsByUsername(getUsername(token));
        if (Objects.equals(user.getId(), getId(token))) {
            throw new RuntimeException(
                    String.format(
                            "Invalid token, User with id %s and username %s doesn't exists in system",
                            getId(token), user.getUsername())
            );
        }
        CustomUserDetails userDetails = CustomUserDetails.builder()
                .username(getUsername(token))
                .password("")
                .roles(getRoles(token))
                .build();
        return new CustomAuthentication(
                userDetails, "",
                userDetails.getAuthorities(),
                getId(token)
        );
    }

    public Long getId(String token) {
        return Long.valueOf(getAccessClaims(token).get("id").toString());
    }

    public String getUsername(String token) {
        return getAccessClaims(token).getSubject();
    }

    public List<Role> getRoles(String token) {
        return mapper.convertValue(
                getAccessClaims(token).get("auth"),
                new TypeReference<>() {
                }
        );
    }


    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    public boolean validateToken(String token, @NonNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Token expired", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Unsupported jwt", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Malformed jwt", mjEx);
        } catch (SignatureException sEx) {
            log.error("Invalid signature", sEx);
        } catch (Exception e) {
            log.error("invalid token", e);
        }
        return false;
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    public Claims getClaims(String token, SecretKey secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
