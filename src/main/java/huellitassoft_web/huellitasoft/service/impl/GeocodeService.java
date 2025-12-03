package huellitassoft_web.huellitasoft.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GeocodeService {

    private static final String GOOGLE_GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    @Value("${googlemaps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeocodeService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    /**
     * Geocodifica una dirección usando Google Geocoding API.
     * Devuelve un Map con lat/lng como BigDecimal.
     * Intenta múltiples variaciones si la primera búsqueda falla.
     */
    public Map<String, BigDecimal> geocodeAddress(String address) {
        try {
            if (address == null || address.isBlank()) {
                log.warn("Dirección vacía proporcionada para geocodificación");
                return null;
            }

            String trimmedAddress = address.trim();

            // Lista de variaciones para intentar
            String[] variations = generateAddressVariations(trimmedAddress);

            for (String variation : variations) {
                log.info("Intentando geocodificación para: '{}'", variation);

                String url = UriComponentsBuilder
                        .fromHttpUrl(GOOGLE_GEOCODE_URL)
                        .queryParam("address", variation)
                        .queryParam("key", apiKey)
                        .toUriString();

                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                    if (!response.getStatusCode().is2xxSuccessful() || 
                        response.getBody() == null || 
                        response.getBody().isBlank()) {
                        continue;
                    }

                    JsonNode root = objectMapper.readTree(response.getBody());
                    String status = root.path("status").asText("");

                    if ("OK".equals(status)) {
                        JsonNode results = root.path("results");

                        if (results.isArray() && !results.isEmpty()) {
                            JsonNode location = results.get(0)
                                    .path("geometry")
                                    .path("location");

                            if (!location.isMissingNode() &&
                                    location.get("lat") != null &&
                                    location.get("lng") != null) {

                                BigDecimal latitude = location.get("lat").decimalValue();
                                BigDecimal longitude = location.get("lng").decimalValue();

                                log.info("✓ Geocodificación exitosa — '{}' → lat: {}, lng: {}", 
                                        trimmedAddress, latitude, longitude);

                                Map<String, BigDecimal> coords = new HashMap<>();
                                coords.put("latitude", latitude);
                                coords.put("longitude", longitude);
                                return coords;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("Variación '{}' falló: {}", variation, e.getMessage());
                    continue;
                }
            }

            log.warn("✗ No se pudo geocodificar '{}' después de intentar {} variaciones", 
                    trimmedAddress, variations.length);
            return null;

        } catch (Exception e) {
            log.error("Error geocodificando dirección '{}': {}", address, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Genera variaciones de la dirección para intentar búsquedas más flexibles.
     * Ejemplo: "Limas y Pomelos, Ambato" genera múltiples combinaciones.
     */
    private String[] generateAddressVariations(String address) {
        java.util.List<String> variations = new java.util.ArrayList<>();

        // Variación 1: Original
        variations.add(address);

        // Variación 2: Reemplazar " y " por " & "
        String withAmpersand = address.replace(" y ", " & ");
        if (!withAmpersand.equals(address)) {
            variations.add(withAmpersand);
        }

        // Variación 3: Invertir el orden de las calles (si tiene & o y)
        if (address.contains(" & ") || address.contains(" y ")) {
            String[] parts = address.split(" [&y] ", 2);
            if (parts.length == 2) {
                String separator = address.contains(" & ") ? " & " : " y ";
                variations.add(parts[1] + separator + parts[0]);
                // También con el otro separador
                String otherSeparator = " & ".equals(separator) ? " y " : " & ";
                variations.add(parts[1] + otherSeparator + parts[0]);
            }
        }

        // Variación 4: Solo ciudad y país (búsqueda menos específica)
        String[] addressParts = address.split(",");
        if (addressParts.length > 1) {
            String cityCountry = String.join(", ", java.util.Arrays.copyOfRange(addressParts, 1, addressParts.length)).trim();
            variations.add(cityCountry);
        }

        return variations.toArray(new String[0]);
    }

    public BigDecimal getLatitude(String address) {
        Map<String, BigDecimal> coords = geocodeAddress(address);
        return coords != null ? coords.get("latitude") : null;
    }

    public BigDecimal getLongitude(String address) {
        Map<String, BigDecimal> coords = geocodeAddress(address);
        return coords != null ? coords.get("longitude") : null;
    }
}
