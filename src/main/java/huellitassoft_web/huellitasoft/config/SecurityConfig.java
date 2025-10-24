package huellitassoft_web.huellitasoft.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Configuración de seguridad de la aplicación.
 * Implementa autenticación OAuth2 con Auth0 y autorización basada en roles.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    private static final String USERS_ENDPOINT = "/api/users";
    private static final String USERS_WILDCARD = "/api/users/**";
    private static final String RAZAS_ENDPOINT = "/api/razas";
    private static final String RAZAS_WILDCARD = "/api/razas/**";
    private static final String ESPECIES_ENDPOINT = "/api/especies";
    private static final String ESPECIES_WILDCARD = "/api/especies/**";
    private static final String ROLE_ADMIN = "ADMINISTRADOR";
    private static final String ROLE_ADMIN_VET = "ADMINISTRADOR_VETERINARIA";
    private static final String ROLE_VETERINARIO = "VETERINARIO";

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${auth0.audience}")
    private String audience;

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                
                // CSRF deshabilitado (ya que usamos JWT stateless)
                .csrf(csrf -> csrf.disable())
                
                // Modo stateless para APIs REST
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Configuración de autorización
                .authorizeHttpRequests(authz -> authz
                        // Endpoints públicos - sin autenticación
                        .requestMatchers(HttpMethod.POST, USERS_ENDPOINT + "/register-from-auth0").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // Endpoints de RAZAS - GET permitido para autenticados
                        .requestMatchers(HttpMethod.GET, RAZAS_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.GET, RAZAS_WILDCARD).authenticated()
                        // POST, PUT, DELETE solo para no-clientes (VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA)
                        .requestMatchers(HttpMethod.POST, RAZAS_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.PUT, RAZAS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, RAZAS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        
                        // Endpoints de ESPECIES - GET permitido para autenticados
                        .requestMatchers(HttpMethod.GET, ESPECIES_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.GET, ESPECIES_WILDCARD).authenticated()
                        // POST, PUT, DELETE solo para no-clientes (VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA)
                        .requestMatchers(HttpMethod.POST, ESPECIES_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.PUT, ESPECIES_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, ESPECIES_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        
                        // Endpoints protegidos - requieren autenticación
                        .requestMatchers(HttpMethod.GET, USERS_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.PUT, USERS_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.PATCH, USERS_WILDCARD).authenticated()
                        
                        // Endpoints restringidos a administradores (ADMINISTRADOR o ADMINISTRADOR_VETERINARIA)
                        .requestMatchers(HttpMethod.GET, USERS_ENDPOINT).hasAnyRole(ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.GET, USERS_ENDPOINT + "/role/**").hasAnyRole(ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, USERS_WILDCARD).hasAnyRole(ROLE_ADMIN, ROLE_ADMIN_VET)
                        
                        // Todos los demás requieren autenticación
                        .anyRequest().authenticated()
                )
                
                // Configuración OAuth2 Resource Server con JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(new CustomJwtAuthenticationConverter())
                        )
                );

        return http.build();
    }

    /**
     * Configura el decodificador JWT.
     * Valida la firma, el issuer y la audiencia del token.
     *
     * @return JwtDecoder configurado
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withIssuerLocation(issuerUri).build();
        
        // Validadores personalizados
        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        
        // Combina los validadores
        OAuth2TokenValidator<Jwt> delegatingTokenValidator =
                new DelegatingOAuth2TokenValidator<>(issuerValidator, audienceValidator);

        jwtDecoder.setJwtValidator(delegatingTokenValidator);

        return jwtDecoder;
    }

    /**
     * Configura el codificador de contraseñas.
     * Utiliza BCrypt para encriptar contraseñas de forma segura.
     *
     * @return PasswordEncoder configurado
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
