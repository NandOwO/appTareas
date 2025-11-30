package com.synapse.core.services;

import com.synapse.core.models.Adjunto;
import com.synapse.core.models.Tarea;
import com.synapse.core.models.Usuario;
import com.synapse.core.services.notifications.EmailService;
import com.synapse.data.dao.AdjuntoDAO;
import com.synapse.data.dao.TareaDAO;
import com.synapse.utils.Validator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestión de adjuntos enviados por email
 * Los archivos se envían por email y solo se guardan metadatos en BD
 * 
 * @author FERNANDO
 */
public class EmailAttachmentService {

    private final AdjuntoDAO adjuntoDAO;
    private final TareaDAO tareaDAO;
    private final EmailService emailService;

    // Configuración
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25 MB (límite de Gmail)
    private static final String[] ALLOWED_EXTENSIONS = {
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "jpg", "jpeg", "png", "gif", "bmp",
            "txt", "csv", "zip", "rar"
    };

    public EmailAttachmentService() {
        this.adjuntoDAO = new AdjuntoDAO();
        this.tareaDAO = new TareaDAO();
        this.emailService = new EmailService();
    }

    /**
     * Procesa y envía adjuntos por email al crear/actualizar una tarea
     */
    public boolean procesarAdjuntos(int idTarea, List<File> archivos, Usuario usuarioAsignado) {
        try {
            // Validar que la tarea existe
            Tarea tarea = tareaDAO.getTareaPorId(idTarea);
            if (tarea == null) {
                throw new IllegalArgumentException("Tarea no encontrada");
            }

            // Validar archivos
            List<File> archivosValidos = validarArchivos(archivos);
            if (archivosValidos.isEmpty()) {
                System.out.println("No hay archivos válidos para procesar");
                return true; // No es un error, simplemente no hay archivos
            }

            // 1. Guardar metadatos en BD
            List<Adjunto> adjuntosGuardados = guardarMetadatos(idTarea, archivosValidos);

            // 2. Enviar email con adjuntos
            boolean emailEnviado = emailService.enviarEmailAsignacion(
                    tarea,
                    usuarioAsignado,
                    archivosValidos);

            if (!emailEnviado) {
                System.err.println("Advertencia: No se pudo enviar el email con adjuntos");
                // No retornamos false porque los metadatos ya se guardaron
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valida una lista de archivos
     */
    private List<File> validarArchivos(List<File> archivos) {
        List<File> archivosValidos = new ArrayList<>();

        if (archivos == null || archivos.isEmpty()) {
            return archivosValidos;
        }

        long tamañoTotal = 0;

        for (File archivo : archivos) {
            // Verificar que el archivo existe
            if (!archivo.exists() || !archivo.isFile()) {
                System.err.println("Archivo no existe o no es válido: " + archivo.getName());
                continue;
            }

            // Validar tamaño individual
            long tamañoArchivo = archivo.length();
            if (!Validator.validarTamañoArchivo(tamañoArchivo, MAX_FILE_SIZE)) {
                System.err.println("Archivo demasiado grande: " + archivo.getName() +
                        " (" + (tamañoArchivo / 1024 / 1024) + " MB)");
                continue;
            }

            // Validar extensión
            if (!Validator.validarExtensionArchivo(archivo.getName(), ALLOWED_EXTENSIONS)) {
                System.err.println("Extensión no permitida: " + archivo.getName());
                continue;
            }

            // Acumular tamaño
            tamañoTotal += tamañoArchivo;
            if (tamañoTotal > MAX_FILE_SIZE) {
                System.err.println("Tamaño total de adjuntos excede el límite");
                break;
            }

            archivosValidos.add(archivo);
        }

        return archivosValidos;
    }

    /**
     * Guarda los metadatos de los adjuntos en la BD
     */
    private List<Adjunto> guardarMetadatos(int idTarea, List<File> archivos) {
        List<Adjunto> adjuntosGuardados = new ArrayList<>();

        for (File archivo : archivos) {
            try {
                Adjunto adjunto = new Adjunto();
                adjunto.setIdTarea(idTarea);
                adjunto.setNombreArchivo(archivo.getName());
                adjunto.setRutaArchivo("EMAIL"); // Indicador de que se envió por email
                adjunto.setTipoArchivo(Validator.obtenerExtension(archivo.getName()));
                adjunto.setTamanioBytes(archivo.length());

                int idAdjunto = adjuntoDAO.agregarAdjunto(adjunto);
                if (idAdjunto > 0) {
                    adjunto.setIdAdjunto(idAdjunto);
                    adjuntosGuardados.add(adjunto);
                }
            } catch (Exception e) {
                System.err.println("Error guardando metadatos de " + archivo.getName());
                e.printStackTrace();
            }
        }

        return adjuntosGuardados;
    }

    /**
     * Obtiene información de adjuntos de una tarea (solo metadatos)
     */
    public List<Adjunto> getAdjuntosTarea(int idTarea) {
        return adjuntoDAO.getAdjuntosPorTarea(idTarea);
    }

    /**
     * Obtiene un resumen de adjuntos para mostrar en la UI
     */
    public String getResumenAdjuntos(int idTarea) {
        List<Adjunto> adjuntos = adjuntoDAO.getAdjuntosPorTarea(idTarea);

        if (adjuntos.isEmpty()) {
            return "Sin adjuntos";
        }

        StringBuilder resumen = new StringBuilder();
        resumen.append("Adjuntos (").append(adjuntos.size()).append("): ");

        for (int i = 0; i < adjuntos.size(); i++) {
            if (i > 0)
                resumen.append(", ");
            resumen.append(adjuntos.get(i).getNombreArchivo());
        }

        resumen.append(" (enviados por correo)");
        return resumen.toString();
    }

    /**
     * Elimina los metadatos de un adjunto
     */
    public boolean eliminarAdjunto(int idAdjunto) {
        return adjuntoDAO.eliminarAdjunto(idAdjunto);
    }

    /**
     * Obtiene el tamaño total de adjuntos de una tarea
     */
    public String getTamañoTotalFormateado(int idTarea) {
        long bytes = adjuntoDAO.getTamañoTotalAdjuntos(idTarea);
        return formatearTamaño(bytes);
    }

    /**
     * Formatea un tamaño en bytes a formato legible
     */
    private String formatearTamaño(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        }
    }

    /**
     * Obtiene las extensiones permitidas
     */
    public static String[] getExtensionesPermitidas() {
        return ALLOWED_EXTENSIONS;
    }

    /**
     * Obtiene el tamaño máximo de archivo
     */
    public static long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
}
