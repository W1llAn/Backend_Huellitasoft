package huellitassoft_web.huellitasoft.enums;

public enum NotificationTitle {
    CITA_CREADA("Nueva Cita Programada"),
    CITA_CONFIRMADA("Cita Confirmada"),
    CITA_CANCELADA("Cita Cancelada"),
    CITA_RECORDATORIO("Recordatorio de Cita"),
    VACUNA_PROXIMA("Próxima Vacuna"),
    VACUNA_VENCIDA("Vacuna Vencida"),
    VACUNA_APLICADA("Vacuna Aplicada"),
    ESQUEMA_COMPLETO("Esquema de Vacunación Completo");

    private final String displayName;

    NotificationTitle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}