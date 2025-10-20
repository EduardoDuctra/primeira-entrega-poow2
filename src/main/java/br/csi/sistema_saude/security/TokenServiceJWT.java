package br.csi.sistema_saude.security;

import br.csi.sistema_saude.model.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenServiceJWT {

    //gera o Token de validação
    public String gerarToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256("poow2"); // Define o algoritmo HMAC256 com a chave secreta/senha
        return JWT.create()
                .withIssuer("Api de saude") //Define quem emite o token (issuer)
                .withSubject(user.getUsername()) //nome usuário
                .withClaim("ROLE", user.getAuthorities().stream().toList().get(0).toString()) //autorização/role
                .withExpiresAt(dataExpiracao()) //tempo para expirar o token
                .sign(algorithm); // Assina o token usando o algoritmo definido
    }

    private Instant dataExpiracao() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getSubject(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("poow2");
            return JWT.require(algorithm)
                    .withIssuer("Api de saude")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            throw new RuntimeException("Token invalido ou expirado");
        }
    }

}