package huellitassoft_web.huellitasoft.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import huellitassoft_web.huellitasoft.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Sube imagen Base64 y retorna solo la URL
     */
    @Override
    public String uploadImageUsuarios(String base64Image) {
        try {
            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("folder", "huellitasoft/usuarios");
            uploadParams.put("transformation", new Transformation()
                    .width(800).height(800).crop("limit")
                    .quality("auto:good")
                    .fetchFormat("webp"));

            Map uploadResult = cloudinary.uploader().upload(base64Image, uploadParams);
            String imageUrl = (String) uploadResult.get("secure_url");

            log.info("✅ Imagen de usuarios subida exitosamente: {}", imageUrl);
            return imageUrl;

        } catch (IOException e) {
            log.error("❌ Error al subir imagen a Cloudinary", e);
            throw new RuntimeException("Error al subir imagen: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina imagen usando la URL
     */
    @Override
    public void deleteImageUsuarios(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            log.warn("⚠️ Se intentó eliminar una imagen de usuario con URL vacía o nula");
            return;
        }

        try {
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                Map result = cloudinary.uploader().destroy(publicId, new HashMap<>());
                log.info("🗑️ Imagen de usuario eliminada: {} - Resultado: {}", publicId, result.get("result"));
            } else {
                log.warn("⚠️ No se pudo extraer el publicId de la URL: {}", imageUrl);
            }
        } catch (IOException e) {
            log.error("❌ Error al eliminar imagen de usuario de Cloudinary: {}", e.getMessage());
            // No lanzamos excepción para no interrumpir el flujo principal
        }
    }


    public String uploadImageMascotas(String base64Image) {
        try {
            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("folder", "huellitasoft/mascotas");
            uploadParams.put("transformation", new Transformation()
                    .width(800).height(800).crop("limit")
                    .quality("auto:good")
                    .fetchFormat("webp"));

            Map uploadResult = cloudinary.uploader().upload(base64Image, uploadParams);
            String imageUrl = (String) uploadResult.get("secure_url");

            log.info("✅ Imagen de mascota subida exitosamente: {}", imageUrl);
            return imageUrl;

        } catch (IOException e) {
            log.error("❌ Error al subir imagen de mascota a Cloudinary", e);
            throw new RuntimeException("Error al subir imagen de mascota : " + e.getMessage(), e);
        }
    }

    /**
     * Elimina imagen usando la URL
     */
    @Override
    public void deleteImageMascotas(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            log.warn("⚠️ Se intentó eliminar una imagen  de mascota con URL vacía o nula");
            return;
        }

        try {
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                Map result = cloudinary.uploader().destroy(publicId, new HashMap<>());
                log.info("🗑️ Imagen eliminada: {} - Resultado: {}", publicId, result.get("result"));
            } else {
                log.warn("⚠️ No se pudo extraer el publicId de la URL: {}", imageUrl);
            }
        } catch (IOException e) {
            log.error("❌ Error al eliminar imagen de mascota de Cloudinary: {}", e.getMessage());
            // No lanzamos excepción para no interrumpir el flujo principal
        }
    }


    /**
     * Extrae el public_id de la URL de Cloudinary
     * Ejemplo: https://res.cloudinary.com/cloud/image/upload/v123456789/folder/image.jpg
     * Retorna: folder/image
     */
    private String extractPublicId(String url) {
        Pattern pattern = Pattern.compile("/v\\d+/(.+?)\\.(jpg|jpeg|png|gif|webp)$");
        Matcher matcher = pattern.matcher(url);
        return matcher.find() ? matcher.group(1) : null;
    }


}
