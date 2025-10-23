package huellitassoft_web.huellitasoft.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${web.cors.allowed-origins}")
    private String allowedOriginsCsv;


    @Value("${web.cors.allowed-origin-patterns:}")
    private String allowedOriginPatternsCsv;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();

        cfg.setAllowCredentials(true);

        List<String> origins = Arrays.stream(allowedOriginsCsv.split("\\s*,\\s*"))
                .map(s -> s.replaceAll("/+$", "")) // quita barras finales
                .filter(s -> !s.isBlank())
                .toList();
        if (!origins.isEmpty()) {
            cfg.setAllowedOrigins(origins);
        }

        // Patrones (wildcards) opcionales
        List<String> patterns = Arrays.stream(allowedOriginPatternsCsv.split("\\s*,\\s*"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
        if (!patterns.isEmpty()) {
            cfg.setAllowedOriginPatterns(patterns);
        }

        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Authorization","Location"));
        cfg.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
