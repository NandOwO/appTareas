package com.synapse.core.services.notifications;

import com.synapse.core.models.Tarea;
import com.synapse.data.dao.TareaDAO;
import com.synapse.data.dao.UsuarioDAO;
import com.synapse.core.models.Usuario;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * Patrón Observer (Observador Concreto)
 * Envía notificaciones por correo electrónico con soporte para adjuntos
 * 
 * @author FERNANDO
 */
public class EmailService implements TaskObserver {

    private final TareaDAO tareaDAO;
    private final UsuarioDAO usuarioDAO;

    public EmailService() {
        this.tareaDAO = new TareaDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    @Override
    public void onTaskDue(List<Tarea> tareas) {
        System.out.println("EmailService: Enviando correos de vencimiento...");

        for (Tarea tarea : tareas) {
            try {
                List<Usuario> usuariosAsignados = tareaDAO.getUsuariosAsignadosPorTarea(tarea.getIdTarea());

                for (Usuario usuario : usuariosAsignados) {
                    long diff = tarea.getFechaLimite().getTime() - System.currentTimeMillis();
                    int horasRestantes = (int) (diff / (1000 * 60 * 60));

                    String htmlBody = EmailTemplates.getTemplateVencimientoProximo(
                            usuario.getNombre(),
                            tarea.getTitulo(),
                            tarea.getFechaLimite().toString(),
                            horasRestantes);

                    sendEmail(
                            usuario.getEmail(),
                            "⚠️ Tarea próxima a vencer: " + tarea.getTitulo(),
                            htmlBody,
                            null);
                }
            } catch (Exception e) {
                System.err.println("Error enviando email para tarea " + tarea.getIdTarea());
                e.printStackTrace();
            }
        }
    }

    /**
     * Envía un email con HTML y opcionalmente con adjuntos
     */
    public boolean sendEmail(String to, String subject, String htmlBody, List<File> attachments) {
        if (!EmailConfig.isConfigured()) {
            System.err.println("EmailService no está configurado. Por favor actualiza EmailConfig.java");
            return false;
        }

        try {
            Properties props = EmailConfig.getProperties();

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                            EmailConfig.getUsername(),
                            EmailConfig.getPassword());
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EmailConfig.getFromEmail(), EmailConfig.getFromName()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            Multipart multipart = new MimeMultipart();

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody, "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);

            if (attachments != null && !attachments.isEmpty()) {
                for (File file : attachments) {
                    if (file.exists() && file.isFile()) {
                        MimeBodyPart attachmentPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(file);
                        attachmentPart.setDataHandler(new DataHandler(source));
                        attachmentPart.setFileName(file.getName());
                        multipart.addBodyPart(attachmentPart);
                    }
                }
            }

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Email enviado exitosamente a: " + to);
            return true;

        } catch (Exception e) {
            System.err.println("Error enviando email a " + to + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envía email de asignación de tarea con adjuntos
     */
    public boolean enviarEmailAsignacion(Tarea tarea, Usuario usuario, List<File> adjuntos) {
        try {
            String listaAdjuntos = "";
            if (adjuntos != null && !adjuntos.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (File file : adjuntos) {
                    sb.append("• ").append(file.getName()).append("<br>");
                }
                listaAdjuntos = sb.toString();
            }

            String htmlBody = EmailTemplates.getTemplateAsignacionTarea(
                    usuario.getNombre(),
                    tarea.getTitulo(),
                    tarea.getDescripcion(),
                    tarea.getFechaLimite() != null ? tarea.getFechaLimite().toString() : "Sin fecha límite",
                    adjuntos != null && !adjuntos.isEmpty(),
                    listaAdjuntos);

            return sendEmail(
                    usuario.getEmail(),
                    "📋 Nueva tarea asignada: " + tarea.getTitulo(),
                    htmlBody,
                    adjuntos);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envía email de cambio de estado
     */
    public boolean enviarEmailCambioEstado(Tarea tarea, Usuario usuario, String nuevoEstado) {
        try {
            String htmlBody = EmailTemplates.getTemplateCambioEstado(
                    usuario.getNombre(),
                    tarea.getTitulo(),
                    nuevoEstado);

            return sendEmail(
                    usuario.getEmail(),
                    "🔄 Cambio de estado: " + tarea.getTitulo(),
                    htmlBody,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envía email de tarea completada
     */
    public boolean enviarEmailTareaCompletada(Tarea tarea, Usuario usuario) {
        try {
            String htmlBody = EmailTemplates.getTemplateTareaCompletada(
                    usuario.getNombre(),
                    tarea.getTitulo());

            return sendEmail(
                    usuario.getEmail(),
                    "✅ Tarea completada: " + tarea.getTitulo(),
                    htmlBody,
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene los emails de usuarios asignados a una tarea
     */
    public List<String> getEmailsUsuariosAsignados(int idTarea) {
        List<String> emails = new ArrayList<>();
        try {
            List<Usuario> usuarios = tareaDAO.getUsuariosAsignadosPorTarea(idTarea);
            for (Usuario usuario : usuarios) {
                if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
                    emails.add(usuario.getEmail());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emails;
    }
}
