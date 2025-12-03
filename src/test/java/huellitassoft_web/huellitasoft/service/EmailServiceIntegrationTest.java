package huellitassoft_web.huellitasoft.service;

import huellitassoft_web.huellitasoft.service.impl.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Integration Tests")
class EmailServiceIntegrationTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private String testMailFrom;
    private String testTo;
    private String testSubject;
    private String testBody;

    @BeforeEach
    void setUp() {
        testMailFrom = "test@huellitasoft.com";
        testTo = "recipient@example.com";
        testSubject = "Test Subject";
        testBody = "Test Body Content";
        
        // Inject the mail from value using reflection
        ReflectionTestUtils.setField(emailService, "mailFrom", testMailFrom);
    }

    @Test
    @DisplayName("Debe enviar email simple exitosamente")
    void testSendEmail_Success() throws MessagingException {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendEmail(testTo, testSubject, testBody);

        // Then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando falla el envío de email")
    void testSendEmail_ThrowsException() throws MessagingException {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(MimeMessage.class));

        // When & Then
        assertThatThrownBy(() -> emailService.sendEmail(testTo, testSubject, testBody))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Mail server error"); // Exacto

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debe enviar email de notificación con template HTML")
    void testSendNotificationEmail_Success() throws MessagingException {
        // Given
        String titulo = "Notificación Importante";
        String asunto = "Nueva Cita Programada";
        String mensaje = "Se ha programado una cita para el día 15 de diciembre.";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendNotificationEmail(testTo, titulo, asunto, mensaje);

        // Then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debe enviar email de notificación con múltiples líneas en el mensaje")
    void testSendNotificationEmail_MultilineMessage_Success() throws MessagingException {
        // Given
        String titulo = "Recordatorio";
        String asunto = "Cita Próxima";
        String mensaje = "Hola,\n\nEste es un recordatorio de tu cita.\n\nFecha: 15/12/2025\nHora: 10:00 AM\n\nGracias.";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendNotificationEmail(testTo, titulo, asunto, mensaje);

        // Then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando falla el envío de notificación")
    void testSendNotificationEmail_ThrowsException() throws MessagingException {
        // Given
        String titulo = "Test";
        String asunto = "Test Subject";
        String mensaje = "Test Message";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Connection timeout")).when(mailSender).send(any(MimeMessage.class));

        // When & Then
        assertThatThrownBy(() -> emailService.sendNotificationEmail(testTo, titulo, asunto, mensaje))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Connection timeout"); // Exacto

        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debe enviar email con caracteres especiales en el asunto")
    void testSendEmail_SpecialCharactersInSubject_Success() throws MessagingException {
        // Given
        String specialSubject = "Test Subject with special chars: áéíóú ñ ¿? ¡!";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendEmail(testTo, specialSubject, testBody);

        // Then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debe enviar email con HTML en el cuerpo")
    void testSendEmail_HTMLBody_Success() throws MessagingException {
        // Given
        String htmlBody = "<html><body><h1>Test Title</h1><p>Test paragraph</p></body></html>";
        
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendEmail(testTo, testSubject, htmlBody);

        // Then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debe manejar múltiples envíos consecutivos")
    void testSendEmail_MultipleConsecutiveSends_Success() throws MessagingException {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendEmail("user1@example.com", "Subject 1", "Body 1");
        emailService.sendEmail("user2@example.com", "Subject 2", "Body 2");
        emailService.sendEmail("user3@example.com", "Subject 3", "Body 3");

        // Then
        verify(mailSender, times(3)).createMimeMessage();
        verify(mailSender, times(3)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Debe enviar notificación con contenido vacío en mensaje")
    void testSendNotificationEmail_EmptyMessage_Success() throws MessagingException {
        // Given
        String titulo = "Título";
        String asunto = "Asunto";
        String mensaje = "";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendNotificationEmail(testTo, titulo, asunto, mensaje);

        // Then
        verify(mailSender, times(1)).createMimeMessage();
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
