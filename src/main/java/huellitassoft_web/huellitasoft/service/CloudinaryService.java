package huellitassoft_web.huellitasoft.service;

public interface CloudinaryService {
    /**
     * Subir  imagen en formato Base64 a Cloudinary
     *
     * @param base64Image Imagen codificada en Base64
     * @return URL segura de la imagen subida
     * @throws RuntimeException si ocurre un error al subir la imagen
     */
    String uploadImageUsuarios(String base64Image);

    /**
     * Eliminar una imagen de Cloudinary usando su URL
     *
     * @param imageUrl URL de la imagen a eliminar
     */
    void deleteImageUsuarios(String imageUrl);

    /**
     * Subir  imagen en formato Base64 a Cloudinary
     *
     * @param base64Image Imagen codificada en Base64
     * @return URL segura de la imagen subida
     * @throws RuntimeException si ocurre un error al subir la imagen
     */
    /*-------------M A S C O T A S -----------------------------*/

    String uploadImageMascotas(String base64Image);

    /**
     * Eliminar una imagen de Cloudinary usando su URL
     *
     * @param imageUrl URL de la imagen a eliminar
     */
    void deleteImageMascotas(String imageUrl);
}
