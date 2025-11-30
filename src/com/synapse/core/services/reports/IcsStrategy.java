package com.synapse.core.services.reports;

import com.synapse.core.models.Tarea;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author FERNANDO
 */
public class IcsStrategy implements IReporteStrategy {

    @Override
    public boolean generar(List<Tarea> tareas) {
        System.out.println("Generando calendario .ics...");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Calendario");
        fileChooser.setSelectedFile(new java.io.File("MisTareas.ics"));
        if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
            return false; // Usuario canceló
        }

        // Formato de fecha para iCalendar (YYYYMMDDTHHMMSSZ)
        SimpleDateFormat icsFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

        try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile())) {
            fw.write("BEGIN:VCALENDAR\n");
            fw.write("VERSION:2.0\n");
            fw.write("PRODID:-//Synapse//AppTareas//ES\n");

            for (Tarea tarea : tareas) {
                if (tarea.getFechaLimite() != null) {
                    fw.write("BEGIN:VEVENT\n");
                    fw.write("UID:" + tarea.getIdTarea() + "@synapse.com\n");
                    fw.write("DTSTAMP:" + icsFormat.format(new java.util.Date()) + "\n");
                    // Asumimos que la fecha de inicio es la misma que la de fin (evento de un día)
                    fw.write("DTSTART:" + icsFormat.format(tarea.getFechaLimite()) + "\n");
                    fw.write("DTEND:" + icsFormat.format(tarea.getFechaLimite()) + "\n");
                    fw.write("SUMMARY:Vencimiento: " + tarea.getTitulo() + "\n");
                    fw.write("DESCRIPTION:" + tarea.getDescripcion() + "\n");
                    fw.write("END:VEVENT\n");
                }
            }

            fw.write("END:VCALENDAR\n");

            JOptionPane.showMessageDialog(null, "Calendario .ics generado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al generar .ics: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
