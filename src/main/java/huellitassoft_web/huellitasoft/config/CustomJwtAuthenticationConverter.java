package huellitassoft_web.huellitasoft.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;

/**
 * Convertidor personalizado de JWT a Authentication.
 * Extrae los roles del JWT y los convierte en GrantedAuthorities.
 * Auth0 almacena los roles en el claim "https://huellitasoft/roles".
 */
public class CustomJwtAuthenticationConverter extends JwtAuthenticationConverter {

    private static final String ROLES_CLAIM = "https://huellitasoft/roles";
    private static final String USER_METADATA_CLAIM = "https://huellitasoft/user_metadata";
    private static final String METADATA_ROLE_KEY = "rol";

    public CustomJwtAuthenticationConverter() {
        setJwtGrantedAuthoritiesConverter(new CustomGrantedAuthoritiesConverter());
    }

    private static class CustomGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Collection<GrantedAuthority> authorities = new ArrayList<>(defaultConverter.convert(jwt));
            authorities.addAll(extractAuthorities(jwt));
            return authorities;
        }

        private List<GrantedAuthority> extractAuthorities(Jwt jwt) {
            List<GrantedAuthority> authorities = new ArrayList<>();

            // Intentar obtener los roles del claim estándar
            List<String> roles = jwt.getClaimAsStringList(ROLES_CLAIM);

            // Si no hay roles, buscar dentro de la metadata
            if (roles == null || roles.isEmpty()) {
                Map<String, Object> userMetadata = jwt.getClaim(USER_METADATA_CLAIM);
                if (userMetadata != null && userMetadata.containsKey(METADATA_ROLE_KEY)) {
                    Object roleValue = userMetadata.get(METADATA_ROLE_KEY);
                    if (roleValue instanceof String roleStr && !roleStr.isEmpty()) {
                        roles = Collections.singletonList(roleStr);
                    }
                }
            }
            // Convertir roles a GrantedAuthority
            if (roles != null) {
                for (String role : roles) {
                    String normalized = role.toUpperCase();
                    if (!normalized.startsWith("ROLE_")) {
                        normalized = "ROLE_" + normalized;
                    }
                    authorities.add(new SimpleGrantedAuthority(normalized));
                }
            }

            return authorities;
        }
    }
}

