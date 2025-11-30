package com.synapse.core.services.reports;

import com.synapse.core.models.Tarea;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Estrategia de exportación a PDF usando iText 7
 * Patrón: Strategy
 * 
 * @author FERNANDO
 */
public class PdfStrategy implements IReporteStrategy {

    @Override
    public boolean generar(List<Tarea> tareas) {
        if (tareas == null || tareas.isEmpty()) {
            System.err.println("No hay tareas para exportar");
            return false;
        }

        // Seleccionar ubicación del archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF files", "pdf"));
        fileChooser.setSelectedFile(new File("reporte_tareas.pdf"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getAbsolutePath().endsWith(".pdf")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
        }

        try {
            // Crear documento PDF
            PdfWriter writer = new PdfWriter(fileToSave);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Fuente
            PdfFont font = PdfFontFactory.createFont();

            // Título
            Paragraph title = new Paragraph("Reporte de Tareas")
                    .setFont(font)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(title);

            // Fecha de generación
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Paragraph fecha = new Paragraph("Generado el: " + sdf.format(new java.util.Date()))
                    .setFont(font)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(20);
            document.add(fecha);

            // Resumen
            Paragraph resumen = new Paragraph("Total de tareas: " + tareas.size())
                    .setFont(font)
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(10);
            document.add(resumen);

            // Tabla de tareas
            float[] columnWidths = { 1, 3, 2, 2, 2 };
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            String[] headers = { "ID", "Título", "Estado", "Prioridad", "Fecha Límite" };
            for (String header : headers) {
                Cell cell = new Cell()
                        .add(new Paragraph(header).setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5);
                table.addHeaderCell(cell);
            }

            // Datos
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            for (Tarea tarea : tareas) {
                // ID
                table.addCell(new Cell()
                        .add(new Paragraph(String.valueOf(tarea.getIdTarea())).setFont(font))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));

                // Título
                table.addCell(new Cell()
                        .add(new Paragraph(tarea.getTitulo()).setFont(font))
                        .setPadding(5));

                // Estado
                String estado = tarea.getNombreEstado() != null ? tarea.getNombreEstado() : "N/A";
                Cell estadoCell = new Cell()
                        .add(new Paragraph(estado).setFont(font))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5);

                // Color según estado
                if (estado.equalsIgnoreCase("completada")) {
                    estadoCell.setBackgroundColor(ColorConstants.GREEN);
                } else if (estado.equalsIgnoreCase("en progreso")) {
                    estadoCell.setBackgroundColor(ColorConstants.YELLOW);
                }
                table.addCell(estadoCell);

                // Prioridad
                String prioridad = tarea.getNombrePrioridad() != null ? tarea.getNombrePrioridad() : "N/A";
                Cell prioridadCell = new Cell()
                        .add(new Paragraph(prioridad).setFont(font))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5);

                // Color según prioridad
                if (prioridad.equalsIgnoreCase("alta")) {
                    prioridadCell.setBackgroundColor(ColorConstants.RED);
                } else if (prioridad.equalsIgnoreCase("media")) {
                    prioridadCell.setBackgroundColor(ColorConstants.ORANGE);
                }
                table.addCell(prioridadCell);

                // Fecha Límite
                String fechaLimite = tarea.getFechaLimite() != null
                        ? dateFormat.format(tarea.getFechaLimite())
                        : "Sin fecha";
                table.addCell(new Cell()
                        .add(new Paragraph(fechaLimite).setFont(font))
                        .setTextAlignment(TextAlignment.CENTER)
                        .setPadding(5));
            }

            document.add(table);

            // Pie de página
            Paragraph footer = new Paragraph("\n\nSistema de Gestión de Tareas Synapse")
                    .setFont(font)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(footer);

            // Cerrar documento
            document.close();

            System.out.println("PDF generado exitosamente: " + fileToSave.getAbsolutePath());
            return true;

        } catch (Exception e) {
            System.err.println("Error generando PDF: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

