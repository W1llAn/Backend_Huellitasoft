package huellitassoft_web.huellitasoft.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import huellitassoft_web.huellitasoft.service.impl.CloudinaryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloudinaryServiceIntegrationTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryServiceImpl cloudinaryService;

    private String testBase64Image;
    private String testImageUrl;

    @BeforeEach
    void setUp() {
        testBase64Image = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==";
        testImageUrl = "https://res.cloudinary.com/cloud/image/upload/v123456789/huellitasoft/usuarios/test-image.webp";
    }

    @Test
    void testUploadImageUsuarios_Success() throws IOException {
        // Arrange
        when(cloudinary.uploader()).thenReturn(uploader);
        // Arrange
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", testImageUrl);
        when(uploader.upload(eq(testBase64Image), anyMap())).thenReturn(uploadResult);

        // Act
        String result = cloudinaryService.uploadImageUsuarios(testBase64Image);

        // Assert
        assertThat(result).isEqualTo(testImageUrl);
        verify(cloudinary).uploader();
        verify(uploader).upload(eq(testBase64Image), anyMap());
    }

    @Test
    void testUploadImageUsuarios_ThrowsExceptionOnIOError() throws IOException {
        // Arrange
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(eq(testBase64Image), anyMap())).thenThrow(new IOException("Upload failed"));

        // Act & Assert
        assertThatThrownBy(() -> cloudinaryService.uploadImageUsuarios(testBase64Image))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error al subir imagen");
    }

    @Test
    void testDeleteImageUsuarios_Success() throws IOException {
        // Arrange
        when(cloudinary.uploader()).thenReturn(uploader);
        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("result", "ok");
        when(uploader.destroy(eq("huellitasoft/usuarios/test-image"), anyMap())).thenReturn(deleteResult);

        // Act
        cloudinaryService.deleteImageUsuarios(testImageUrl);

        // Assert
        verify(cloudinary).uploader();
        verify(uploader).destroy(eq("huellitasoft/usuarios/test-image"), anyMap());
    }

    @Test
    void testDeleteImageUsuarios_WithNullUrl() throws IOException {
        // Act
        cloudinaryService.deleteImageUsuarios(null);

        // Assert
        verify(cloudinary, never()).uploader();
        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void testDeleteImageUsuarios_WithEmptyUrl() throws IOException {
        // Act
        cloudinaryService.deleteImageUsuarios("");

        // Assert
        verify(cloudinary, never()).uploader();
        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void testDeleteImageUsuarios_WithInvalidUrl() throws IOException {
        // Arrange
        String invalidUrl = "https://example.com/invalid-url";

        // Act
        cloudinaryService.deleteImageUsuarios(invalidUrl);

        // Assert
        verify(cloudinary, never()).uploader();
        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void testDeleteImageUsuarios_HandlesIOException() throws IOException {
        // Arrange
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(eq("huellitasoft/usuarios/test-image"), anyMap())).thenThrow(new IOException("Delete failed"));

        // Act - should not throw exception
        cloudinaryService.deleteImageUsuarios(testImageUrl);

        // Assert
        verify(cloudinary).uploader();
        verify(uploader).destroy(eq("huellitasoft/usuarios/test-image"), anyMap());
    }

    @Test
    void testUploadImageMascotas_Success() throws IOException {
        // Arrange
        when(cloudinary.uploader()).thenReturn(uploader);
        String mascotaImageUrl = "https://res.cloudinary.com/cloud/image/upload/v123456789/huellitasoft/mascotas/pet-image.webp";
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", mascotaImageUrl);
        when(uploader.upload(eq(testBase64Image), anyMap())).thenReturn(uploadResult);

        // Act
        String result = cloudinaryService.uploadImageMascotas(testBase64Image);

        // Assert
        assertThat(result).isEqualTo(mascotaImageUrl);
        verify(cloudinary).uploader();
        verify(uploader).upload(eq(testBase64Image), anyMap());
    }

    @Test
    void testUploadImageMascotas_ThrowsExceptionOnIOError() throws IOException {
        // Arrange
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(eq(testBase64Image), anyMap())).thenThrow(new IOException("Upload failed"));

        // Act & Assert
        assertThatThrownBy(() -> cloudinaryService.uploadImageMascotas(testBase64Image))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Error al subir imagen de mascota");
    }

    @Test
    void testDeleteImageMascotas_Success() throws IOException {
        // Arrange
        when(cloudinary.uploader()).thenReturn(uploader);
        String mascotaImageUrl = "https://res.cloudinary.com/cloud/image/upload/v123456789/huellitasoft/mascotas/pet-image.webp";
        Map<String, Object> deleteResult = new HashMap<>();
        deleteResult.put("result", "ok");
        when(uploader.destroy(eq("huellitasoft/mascotas/pet-image"), anyMap())).thenReturn(deleteResult);

        // Act
        cloudinaryService.deleteImageMascotas(mascotaImageUrl);

        // Assert
        verify(cloudinary).uploader();
        verify(uploader).destroy(eq("huellitasoft/mascotas/pet-image"), anyMap());
    }

    @Test
    void testDeleteImageMascotas_WithNullUrl() throws IOException {
        // Act
        cloudinaryService.deleteImageMascotas(null);

        // Assert
        verify(cloudinary, never()).uploader();
        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void testDeleteImageMascotas_WithEmptyUrl() throws IOException {
        // Act
        cloudinaryService.deleteImageMascotas("");

        // Assert
        verify(cloudinary, never()).uploader();
        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void testDeleteImageMascotas_WithInvalidUrl() throws IOException {
        // Arrange
        String invalidUrl = "https://example.com/invalid-url";

        // Act
        cloudinaryService.deleteImageMascotas(invalidUrl);

        // Assert
        verify(cloudinary, never()).uploader();
        verify(uploader, never()).destroy(anyString(), anyMap());
    }

    @Test
    void testDeleteImageMascotas_HandlesIOException() throws IOException {
        // Arrange
        when(cloudinary.uploader()).thenReturn(uploader);
        String mascotaImageUrl = "https://res.cloudinary.com/cloud/image/upload/v123456789/huellitasoft/mascotas/pet-image.webp";
        when(uploader.destroy(eq("huellitasoft/mascotas/pet-image"), anyMap())).thenThrow(new IOException("Delete failed"));

        // Act - should not throw exception
        cloudinaryService.deleteImageMascotas(mascotaImageUrl);

        // Assert
        verify(cloudinary).uploader();
        verify(uploader).destroy(eq("huellitasoft/mascotas/pet-image"), anyMap());
    }

    @Test
    void testUploadImageUsuarios_VerifyFolderParameter() throws IOException {
        // Arrange
        when(cloudinary.uploader()).thenReturn(uploader);
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", testImageUrl);
        when(uploader.upload(eq(testBase64Image), anyMap())).thenReturn(uploadResult);

        // Act
        cloudinaryService.uploadImageUsuarios(testBase64Image);

        // Assert - verify folder is usuarios
        verify(uploader).upload(eq(testBase64Image), argThat(params -> {
            Map<String, Object> map = (Map<String, Object>) params;
            return "huellitasoft/usuarios".equals(map.get("folder"));
        }));
    }

    @Test
    void testUploadImageMascotas_VerifyFolderParameter() throws IOException {
        // Arrange
        when(cloudinary.uploader()).thenReturn(uploader);
        String mascotaImageUrl = "https://res.cloudinary.com/cloud/image/upload/v123456789/huellitasoft/mascotas/pet-image.webp";
        Map<String, Object> uploadResult = new HashMap<>();
        uploadResult.put("secure_url", mascotaImageUrl);
        when(uploader.upload(eq(testBase64Image), anyMap())).thenReturn(uploadResult);

        // Act
        cloudinaryService.uploadImageMascotas(testBase64Image);

        // Assert - verify folder is mascotas
        verify(uploader).upload(eq(testBase64Image), argThat(params -> {
            Map<String, Object> map = (Map<String, Object>) params;
            return "huellitasoft/mascotas".equals(map.get("folder"));
        }));
    }
}
