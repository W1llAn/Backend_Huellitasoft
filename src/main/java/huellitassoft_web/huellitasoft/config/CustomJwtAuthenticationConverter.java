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
import java.util.stream.Stream;

/**
 * Convertidor personalizado de JWT a Authentication.
 * Extrae los roles del JWT y los convierte en GrantedAuthorities.
 * Auth0 almacena los roles en el claim "https://huellitasoft/roles".
 */
public class CustomJwtAuthenticationConverter extends JwtAuthenticationConverter {

    private static final String ROLES_CLAIM = "https://huellitasoft/roles";
    private static final String ROLE_PREFIX = "ROLE_";

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
            Collection<GrantedAuthority> authorities = defaultConverter.convert(jwt);
            
            // Agregar roles personalizados
            List<GrantedAuthority> customAuthorities = extractAuthorities(jwt);
            
            // Combinar ambas colecciones
            if (authorities != null) {
                return Stream.concat(authorities.stream(), customAuthorities.stream())
                        .toList();
            }
            
            return customAuthorities;
        }

        /**
         * Extrae los roles del claim personalizado del JWT.
         *
         * @param jwt el token JWT
         * @return lista de GrantedAuthority basada en los roles
         */
        private List<GrantedAuthority> extractAuthorities(Jwt jwt) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            
            // Obtener los roles del claim personalizado
            List<String> roles = jwt.getClaimAsStringList(ROLES_CLAIM);
            
            if (roles != null) {
                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.toUpperCase()));
                }
            }
            
            return authorities;
        }
    }
}

