package com.synapse.ui.components;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Renderizador de texto simple que alinea:
 * - Col 2 (Código) al CENTRO
 * - El resto a la IZQUIERDA (LEADING)
 */
public class SimpleTextRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        // Llama al padre para que maneje colores de fondo, selección, etc.
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Tu lógica de alineación (igual a la del header)
        if (column == 2) { // Col "Código"
            setHorizontalAlignment(SwingConstants.CENTER);
        } else { // Col "Nombre", "Correo"
            setHorizontalAlignment(SwingConstants.LEADING);
        }
        
        return this;
    }
}
