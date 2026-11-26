package sgcf_back.camilaronzzani.com.github.sgcf_back.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sgcf_back.camilaronzzani.com.github.sgcf_back.entity.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final String issuer;

    public JwtService(
            @Value("${api.security.token.secret}") String secret,
            @Value("${api.security.token.issuer:minha-aplicacao}") String issuer) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
    }

    public String createToken(User usuario) {
        try {
            return JWT.create()
                    .withIssuer(issuer)
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId())
                    .withClaim("Permission", usuario.getPermission().toString())
                    .withExpiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new IllegalStateException("Erro ao gerar token JWT", exception);
        }
    }

    public String validateToken(String token) {
        try {
            return JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return null;
        }
    }
}
