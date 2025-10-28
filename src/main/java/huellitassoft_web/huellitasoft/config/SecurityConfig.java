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
    private static final String MASCOTAS_ENDPOINT = "/api/mascotas";
    private static final String MASCOTAS_WILDCARD = "/api/mascotas/**";
    private static final String CLIENTES_ENDPOINT = "/api/clientes";
    private static final String CLIENTES_WILDCARD = "/api/clientes/**";
    private static final String HISTORIALES_ENDPOINT = "/api/historiales-clinicos";
    private static final String HISTORIALES_WILDCARD = "/api/historiales-clinicos/**";
    private static final String CONSULTAS_ENDPOINT = "/api/consultas";
    private static final String CONSULTAS_WILDCARD = "/api/consultas/**";
    private static final String TRATAMIENTOS_ENDPOINT = "/api/tratamientos";
    private static final String TRATAMIENTOS_WILDCARD = "/api/tratamientos/**";
    private static final String CITAS_ENDPOINT = "/api/citas";
    private static final String CITAS_WILDCARD = "/api/citas/**";
    private static final String ROLE_ADMIN = "ADMINISTRADOR";
    private static final String ROLE_ADMIN_VET = "ADMINISTRADOR_VETERINARIA";
    private static final String ROLE_VETERINARIO = "VETERINARIO";

    private static final String VACUNAS_ENDPOINT = "/api/vacunas";
    private static final String VACUNAS_WILDCARD = "/api/vacunas/**";
    private static final String ESQ_VAC_ENDPOINT = "/api/esquemas-vacunacion";
    private static final String ESQ_VAC_WILDCARD = "/api/esquemas-vacunacion/**";
    private static final String PET_SCHEME_ENDPOINT = "/api/mascota-esquemas";
    private static final String PET_SCHEME_WILDCARD = "/api/mascota-esquemas/**";

    private static final String PET_VACC_ENDPOINT       = "/api/vacunacion-mascota";
    private static final String PET_VACC_WILDCARD       = "/api/vacunacion-mascota/**";

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

                        // Endpoints de MASCOTAS - GET permitido para autenticados
                        .requestMatchers(HttpMethod.GET, MASCOTAS_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.GET, MASCOTAS_WILDCARD).authenticated()
                        // POST, PUT, DELETE solo para no-clientes (VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA)
                        .requestMatchers(HttpMethod.POST, MASCOTAS_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.PUT, MASCOTAS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, MASCOTAS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)

                        // Endpoints de CLIENTES - GET permitido para autenticados
                        .requestMatchers(HttpMethod.GET, CLIENTES_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.GET, CLIENTES_WILDCARD).authenticated()
                        // POST, PUT, PATCH, DELETE solo para no-clientes (VETERINARIO, ADMINISTRADOR, ADMINISTRADOR_VETERINARIA)
                        .requestMatchers(HttpMethod.POST, CLIENTES_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.PUT, CLIENTES_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.PATCH, CLIENTES_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, CLIENTES_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)

                        // Endpoints de HISTORIAL CLÍNICO - solo para veterinarios y administradores
                        .requestMatchers(HttpMethod.GET, HISTORIALES_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.GET, HISTORIALES_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.POST, HISTORIALES_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.PUT, HISTORIALES_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, HISTORIALES_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)

                        // Endpoints de CONSULTAS - solo para veterinarios y administradores
                        .requestMatchers(HttpMethod.GET, CONSULTAS_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.GET, CONSULTAS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.POST, CONSULTAS_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.PUT, CONSULTAS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, CONSULTAS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)

                        // Endpoints de TRATAMIENTOS - solo para veterinarios y administradores
                        .requestMatchers(HttpMethod.GET, TRATAMIENTOS_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.GET, TRATAMIENTOS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.POST, TRATAMIENTOS_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.PUT, TRATAMIENTOS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, TRATAMIENTOS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        //Endpoints de citas permitido para autenticados
                        .requestMatchers(HttpMethod.GET, CITAS_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.GET, CITAS_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.POST, CITAS_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.POST, CITAS_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.PUT, CITAS_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.PUT, CITAS_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.DELETE, CITAS_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.DELETE, CITAS_WILDCARD).authenticated()


                        // Endpoints protegidos - requieren autenticación
                        .requestMatchers(HttpMethod.GET, USERS_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.PUT, USERS_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.PATCH, USERS_WILDCARD).authenticated()

                        // Endpoints restringidos a administradores (ADMINISTRADOR o ADMINISTRADOR_VETERINARIA)
                        .requestMatchers(HttpMethod.GET, USERS_ENDPOINT).hasAnyRole(ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.GET, USERS_ENDPOINT + "/role/**").hasAnyRole(ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, USERS_WILDCARD).hasAnyRole(ROLE_ADMIN, ROLE_ADMIN_VET)

                        // Endpoints de VACUNAS - GET autenticado; mutaciones solo roles no-cliente
                        .requestMatchers(HttpMethod.GET, VACUNAS_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.GET, VACUNAS_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.POST, VACUNAS_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.PUT, VACUNAS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, VACUNAS_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)

                        // Endpoints de ESQUEMAS DE VACUNACIÓN - CRUD por roles, GET autenticado
                        .requestMatchers(HttpMethod.GET, ESQ_VAC_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.GET, ESQ_VAC_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.POST, ESQ_VAC_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.PUT, ESQ_VAC_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, ESQ_VAC_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)

                        // Endpoints de MASCOTA↔ESQUEMA (seguimiento)
                        // GET autenticado; asignar/actualizar estado/eliminar solo roles no-cliente
                        .requestMatchers(HttpMethod.GET, PET_SCHEME_WILDCARD).authenticated()
                        .requestMatchers(HttpMethod.POST, PET_SCHEME_ENDPOINT).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.PATCH, PET_SCHEME_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.DELETE, PET_SCHEME_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)

                        // Vacunación de mascota PetVaccination
                        .requestMatchers(HttpMethod.POST, PET_VACC_ENDPOINT).hasRole(ROLE_VETERINARIO)
                        .requestMatchers(HttpMethod.DELETE, PET_VACC_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)
                        .requestMatchers(HttpMethod.GET, PET_VACC_WILDCARD).hasAnyRole(ROLE_VETERINARIO, ROLE_ADMIN, ROLE_ADMIN_VET)

                        // Si se quiere permitir que el CLIENTE consulte las vacunaciones de su mascota:
                        // .requestMatchers(HttpMethod.GET, "/api/vacunacion-mascota/mascota/**").hasAnyRole("VETERINARIO","ADMINISTRADOR","CLIENTE")
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
