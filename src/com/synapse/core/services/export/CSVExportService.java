package com.synapse.core.services.export;

import com.synapse.core.models.Tarea;
import com.synapse.core.models.Usuario;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Servicio para exportar datos a CSV
 */
public class CSVExportService {

    /**
     * Exporta lista de tareas a CSV
     */
    public File exportarTareas(List<Tarea> tareas, String nombreArchivo) throws IOException {
        File archivo = new File(System.getProperty("java.io.tmpdir"), nombreArchivo + ".csv");

        try (FileWriter writer = new FileWriter(archivo)) {
            // Encabezados
            writer.append("ID,Título,Descripción,Estado,Prioridad,Fecha Límite,Creador\n");

            // Datos
            for (Tarea tarea : tareas) {
                writer.append(String.valueOf(tarea.getIdTarea())).append(",");
                writer.append(escaparCSV(tarea.getTitulo())).append(",");
                writer.append(escaparCSV(tarea.getDescripcion())).append(",");
                writer.append(escaparCSV(tarea.getNombreEstado())).append(",");
                writer.append(escaparCSV(tarea.getNombrePrioridad())).append(",");
                writer.append(tarea.getFechaLimite() != null ? tarea.getFechaLimite().toString() : "").append(",");
                writer.append(tarea.getCreador() != null ? escaparCSV(tarea.getCreador().getNombre()) : "");
                writer.append("\n");
            }
        }

        return archivo;
    }

    /**
     * Exporta lista de usuarios a CSV
     */
    public File exportarUsuarios(List<Usuario> usuarios, String nombreArchivo) throws IOException {
        File archivo = new File(System.getProperty("java.io.tmpdir"), nombreArchivo + ".csv");

        try (FileWriter writer = new FileWriter(archivo)) {
            // Encabezados
            writer.append("ID,Nombre,Email,Rol,Estado\n");

            // Datos
            for (Usuario usuario : usuarios) {
                writer.append(String.valueOf(usuario.getIdUsuario())).append(",");
                writer.append(escaparCSV(usuario.getNombre())).append(",");
                writer.append(escaparCSV(usuario.getEmail())).append(",");
                writer.append(escaparCSV(usuario.getRol())).append(",");
                writer.append(usuario.isActivo() ? "Activo" : "Inactivo");
                writer.append("\n");
            }
        }

        return archivo;
    }

    /**
     * Exporta estadísticas generales a CSV
     */
    public File exportarEstadisticas(String titulo, String[][] datos, String nombreArchivo) throws IOException {
        File archivo = new File(System.getProperty("java.io.tmpdir"), nombreArchivo + ".csv");

        try (FileWriter writer = new FileWriter(archivo)) {
            writer.append(titulo).append("\n\n");

            // Datos
            for (String[] fila : datos) {
                for (int i = 0; i < fila.length; i++) {
                    writer.append(escaparCSV(fila[i]));
                    if (i < fila.length - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }
        }

        return archivo;
    }

    /**
     * Escapa caracteres especiales para CSV
     */
    private String escaparCSV(String valor) {
        if (valor == null) {
            return "";
        }

        // Si contiene coma, comilla o salto de línea, envolver en comillas
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }

        return valor;
    }
}
