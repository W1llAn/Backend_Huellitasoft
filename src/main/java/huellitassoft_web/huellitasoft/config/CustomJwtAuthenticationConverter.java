package huellitassoft_web.huellitasoft.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Convertidor personalizado de JWT a Authentication.
 * Extrae los roles del JWT y los convierte en GrantedAuthorities.
 * Auth0 almacena los roles en el claim "https://huellitasoft/roles".
 */
public class CustomJwtAuthenticationConverter extends JwtAuthenticationConverter {

    private static final String ROLES_CLAIM = "https://huellitasoft/roles";

    public CustomJwtAuthenticationConverter() {
        super();
        // Establecer el convertidor personalizado de autoridades
        setJwtGrantedAuthoritiesConverter(new CustomGrantedAuthoritiesConverter());
    }

    /**
     * Convertidor personalizado de autoridades que combina scopes y roles.
     */
    private static class CustomGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            // Obtener las autoridades por defecto (scopes)
            Collection<GrantedAuthority> authorities = new ArrayList<>(defaultConverter.convert(jwt));
            
            // Agregar roles personalizados
            List<GrantedAuthority> customAuthorities = extractAuthorities(jwt);
            authorities.addAll(customAuthorities);
            
            return authorities;
        }

        /**
         * Extrae los roles del claim personalizado del JWT.
         * Los roles ya vienen con el prefijo "ROLE_" en el JWT de Auth0.
         *
         * @param jwt el token JWT
         * @return lista de GrantedAuthority basada en los roles
         */
        private List<GrantedAuthority> extractAuthorities(Jwt jwt) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            
            // Obtener los roles del claim personalizado
            List<String> roles = jwt.getClaimAsStringList(ROLES_CLAIM);
            
            if (roles != null && !roles.isEmpty()) {
                for (String role : roles) {
                    // Los roles de Auth0 ya vienen con ROLE_ prefix
                    // Si no lo tienen, lo agregamos
                    if (role.startsWith("ROLE_")) {
                        authorities.add(new SimpleGrantedAuthority(role));
                    } else {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                    }
                }
            }
            
            return authorities;
        }
    }
}

