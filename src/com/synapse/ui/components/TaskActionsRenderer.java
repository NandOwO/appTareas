package com.synapse.ui.components;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class TaskActionsRenderer extends TaskActionsPanel implements TableCellRenderer {

    public TaskActionsRenderer() {
        super();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        // El panel (wrapper) toma el color de fondo de la selección
        if (isSelected) {
            setOpaque(true);
            setBackground(table.getSelectionBackground());
        } else {
            setOpaque(false);
            setBackground(table.getBackground());
        }
        return this;
    }
}
