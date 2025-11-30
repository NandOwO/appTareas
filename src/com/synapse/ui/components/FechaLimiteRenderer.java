package com.synapse.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.time.LocalDate; // Importante
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderizador de texto que alinea y colorea la fecha si está vencida.
 */
public class FechaLimiteRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Por defecto, color normal
        setForeground(table.getForeground());
        if (isSelected) {
            setForeground(table.getSelectionForeground());
        }

        // --- Lógica de Fecha Vencida ---
        if (value instanceof LocalDate) {
            LocalDate fechaLimite = (LocalDate) value;

            // Comparamos si la fecha límite es ANTERIOR a hoy
            if (fechaLimite.isBefore(LocalDate.now())) {
                // ¡Vencida! La ponemos en rojo.
                setForeground(Color.RED);
            }
        }

        // Centramos el texto de la fecha
        setHorizontalAlignment(SwingConstants.CENTER);

        return this;
    }
}
