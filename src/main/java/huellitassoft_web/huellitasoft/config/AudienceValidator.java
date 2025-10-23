package huellitassoft_web.huellitasoft.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Validador personalizado de audiencia para tokens JWT.
 * Verifica que el token sea destinado a nuestra aplicación (audience).
 */
@Slf4j
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final String audience;

    public AudienceValidator(String audience) {
        this.audience = audience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        // Obtener la audiencia del token
        java.util.List<String> tokenAudience = token.getAudience();
        
        // Verificar que la audiencia coincida
        if (tokenAudience != null && tokenAudience.contains(audience)) {
            return OAuth2TokenValidatorResult.success();
        }
        
        log.warn("Token con audiencia inválida. Se esperaba: {}, Se obtuvo: {}", audience, tokenAudience);
        OAuth2Error error = new OAuth2Error("invalid_token", "La audiencia del token es inválida", null);
        return OAuth2TokenValidatorResult.failure(error);
    }
}
