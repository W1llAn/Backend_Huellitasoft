package huellitassoft_web.huellitasoft.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CorsConfig {

    @Value("${web.cors.allowed-origins}")
    private String allowedOriginsCsv;
    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${web.cors.allowed-origin-patterns:}")
    private String allowedOriginPatternsCsv;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        cfg.setAllowCredentials(true);

        List<String> origins = Arrays.stream((allowedOriginsCsv == null ? "" : allowedOriginsCsv).split("\\s*,\\s*"))
                .map(s -> s.replaceAll("/+$", "")) // quitar slash final
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
        if (!origins.isEmpty()) {
            cfg.setAllowedOrigins(origins);
            log.info("CORS - allowedOrigins: {}", origins);
        }

        // Patrones (wildcards) opcionales
        List<String> patterns = Arrays.stream((allowedOriginPatternsCsv == null ? "" : allowedOriginPatternsCsv).split("\\s*,\\s*"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toList());
        if (!patterns.isEmpty()) {
            cfg.setAllowedOriginPatterns(patterns);
        }

        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Headers que el cliente puede enviar (incluye Authorization si usas Bearer token)
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));

        // Headers que el navegador puede exponer al cliente JavaScript
        cfg.setExposedHeaders(List.of("Authorization", "Location"));
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
