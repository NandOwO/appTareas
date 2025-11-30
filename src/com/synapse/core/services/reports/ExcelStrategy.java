package com.synapse.core.services.reports;

import com.synapse.core.models.Tarea;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Estrategia de exportación a Excel usando Apache POI
 * Patrón: Strategy
 * 
 * @author FERNANDO
 */
public class ExcelStrategy implements IReporteStrategy {

    @Override
    public boolean generar(List<Tarea> tareas) {
        if (tareas == null || tareas.isEmpty()) {
            System.err.println("No hay tareas para exportar");
            return false;
        }

        // Seleccionar ubicación del archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar reporte Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files", "xlsx"));
        fileChooser.setSelectedFile(new File("reporte_tareas.xlsx"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return false;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getAbsolutePath().endsWith(".xlsx")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".xlsx");
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tareas");

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle completadaStyle = createCompletadaStyle(workbook);
            CellStyle enProgresoStyle = createEnProgresoStyle(workbook);
            CellStyle altaPrioridadStyle = createAltaPrioridadStyle(workbook);
            CellStyle mediaPrioridadStyle = createMediaPrioridadStyle(workbook);

            // Encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = { "ID", "Título", "Descripción", "Estado", "Prioridad",
                    "Fecha Creación", "Fecha Límite", "Creado Por" };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            int rowNum = 1;

            for (Tarea tarea : tareas) {
                Row row = sheet.createRow(rowNum++);

                // ID
                row.createCell(0).setCellValue(tarea.getIdTarea());

                // Título
                row.createCell(1).setCellValue(tarea.getTitulo());

                // Descripción
                String descripcion = tarea.getDescripcion() != null ? tarea.getDescripcion() : "";
                row.createCell(2).setCellValue(descripcion);

                // Estado
                String estado = tarea.getNombreEstado() != null ? tarea.getNombreEstado() : "N/A";
                Cell estadoCell = row.createCell(3);
                estadoCell.setCellValue(estado);
                if (estado.equalsIgnoreCase("completada")) {
                    estadoCell.setCellStyle(completadaStyle);
                } else if (estado.equalsIgnoreCase("en progreso")) {
                    estadoCell.setCellStyle(enProgresoStyle);
                }

                // Prioridad
                String prioridad = tarea.getNombrePrioridad() != null ? tarea.getNombrePrioridad() : "N/A";
                Cell prioridadCell = row.createCell(4);
                prioridadCell.setCellValue(prioridad);
                if (prioridad.equalsIgnoreCase("alta")) {
                    prioridadCell.setCellStyle(altaPrioridadStyle);
                } else if (prioridad.equalsIgnoreCase("media")) {
                    prioridadCell.setCellStyle(mediaPrioridadStyle);
                }

                // Fecha Creación
                if (tarea.getFechaCreacion() != null) {
                    Cell fechaCreacionCell = row.createCell(5);
                    fechaCreacionCell.setCellValue(dateFormat.format(tarea.getFechaCreacion()));
                    fechaCreacionCell.setCellStyle(dateStyle);
                }

                // Fecha Límite
                if (tarea.getFechaLimite() != null) {
                    Cell fechaLimiteCell = row.createCell(6);
                    fechaLimiteCell.setCellValue(dateFormat.format(tarea.getFechaLimite()));
                    fechaLimiteCell.setCellStyle(dateStyle);
                }

                // Creado Por
                if (tarea.getCreador() != null) {
                    row.createCell(7).setCellValue(tarea.getCreador().getNombre());
                }
            }

            // Auto-ajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                workbook.write(fileOut);
            }

            System.out.println("Excel generado exitosamente: " + fileToSave.getAbsolutePath());
            return true;

        } catch (Exception e) {
            System.err.println("Error generando Excel: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[] { (byte) 200, (byte) 200, (byte) 200 }, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createCompletadaStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[] { (byte) 144, (byte) 238, (byte) 144 }, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createEnProgresoStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[] { (byte) 255, (byte) 255, (byte) 153 }, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createAltaPrioridadStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[] { (byte) 255, (byte) 153, (byte) 153 }, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createMediaPrioridadStyle(Workbook workbook) {
        XSSFCellStyle style = (XSSFCellStyle) workbook.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(new byte[] { (byte) 255, (byte) 204, (byte) 153 }, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}
