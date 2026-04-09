package com.matiasmeira.back_reservas.auth.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.matiasmeira.back_reservas.auth.model.Usuario;
import com.matiasmeira.back_reservas.exception.JwtTokenException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

/**
 * Servicio para manejar la generación y validación de tokens JWT.
 * Utiliza la librería JJWT para firmar y verificar tokens.
 */
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${security.jwt.secret-key:mySuperSecretKeyForJwtTokenGenerationAndValidationPleaseChangeThis}")
    private String secretKey;

    @Value("${security.jwt.expiration:86400000}")  // 24 horas por defecto
    private long tokenExpiration;

    /**
     * Genera un token JWT para un usuario.
     *
     * @param usuario El usuario para el cual generar el token
     * @return Token JWT generado
     */
    public String generateToken(@NotNull Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", usuario.getRol());
        claims.put("email", usuario.getEmail());
        claims.put("nombre", usuario.getNombre());
        
        return buildToken(claims, usuario.getEmail());
    }

    /**
     * Genera un token JWT con claims adicionales.
     *
     * @param extraClaims Claims adicionales a incluir
     * @param userDetails Los detalles del usuario
     * @return Token JWT generado
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails.getUsername());
    }

    /**
     * Construye el token JWT.
     *
     * @param claims Los claims a incluir
     * @param subject El sujeto (email) del token
     * @return Token JWT construccionado
     */
    private String buildToken(Map<String, Object> claims, String subject) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(tokenExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiryDate))
                .signWith(
                    Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)),
                    SignatureAlgorithm.HS256
                )
                .compact();
    }

    /**
     * Extrae el email (username) del token JWT.
     *
     * @param token El token JWT
     * @return El email contenido en el token
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrae un claim específico del token.
     *
     * @param token El token JWT
     * @param claimsResolver Función para extraer el claim deseado
     * @param <T> Tipo del claim
     * @return El claim extraído
     */
    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Valida que el token sea válido para el usuario dado.
     *
     * @param token El token JWT
     * @param userDetails Los detalles del usuario
     * @return true si el token es válido, false en caso contrario
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Verifica si el token ha expirado.
     *
     * @param token El token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrae la fecha de expiración del token.
     *
     * @param token El token JWT
     * @return La fecha de expiración
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae todos los claims del token.
     *
     * @param token El token JWT
     * @return Todos los claims
     * @throws JwtTokenException Si el token es inválido
     */
    private Claims extractAllClaims(String token) throws JwtTokenException {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (io.jsonwebtoken.security.SignatureException e) {
            throw new JwtTokenException("Firma del token inválida", e);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new JwtTokenException("El token ha expirado", e);
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            throw new JwtTokenException("Token JWT no soportado", e);
        } catch (IllegalArgumentException e) {
            throw new JwtTokenException("Claim string vacío", e);
        } catch (Exception e) {
            throw new JwtTokenException("Error validando el token JWT", e);
        }
    }

    /**
     * Extrae el rol del token.
     *
     * @param token El token JWT
     * @return El rol contenido en el token
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("rol", String.class));
    }
}
