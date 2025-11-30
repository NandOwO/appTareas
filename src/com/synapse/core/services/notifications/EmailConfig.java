package com.synapse.core.services.notifications;

import java.util.Properties;

/**
 * Configuración para el servicio de email
 * Centraliza las propiedades SMTP
 * 
 * @author FERNANDO
 */
public class EmailConfig {

    // Configuración SMTP - Gmail
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_AUTH = "true";
    private static final String SMTP_STARTTLS = "true";

    // Credenciales (en producción, usar variables de entorno o archivo properties)
    private static final String USERNAME = "system.momentum.noreply@gmail.com";
    private static final String PASSWORD = "qyyfqwcvkblygnes";

    // Configuración del remitente
    private static final String FROM_EMAIL = "system.momentum.noreply@gmail.com";
    private static final String FROM_NAME = "Sistema de Tareas Synapse";

    /**
     * Obtiene las propiedades SMTP configuradas
     */
    public static Properties getProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", SMTP_AUTH);
        props.put("mail.smtp.starttls.enable", SMTP_STARTTLS);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", SMTP_HOST);

        return props;
    }

    /**
     * Obtiene el username para autenticación
     */
    public static String getUsername() {
        return USERNAME;
    }

    /**
     * Obtiene el password para autenticación
     */
    public static String getPassword() {
        return PASSWORD;
    }

    /**
     * Obtiene el email del remitente
     */
    public static String getFromEmail() {
        return FROM_EMAIL;
    }

    /**
     * Obtiene el nombre del remitente
     */
    public static String getFromName() {
        return FROM_NAME;
    }

    /**
     * Verifica si la configuración está completa
     */
    public static boolean isConfigured() {
        return !USERNAME.equals("tu-email@gmail.com") &&
                !PASSWORD.equals("tu-app-password");
    }
}
