/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.synapse.ui.components;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author FERNANDO
 */
public class TableHeaderAlignment implements TableCellRenderer {

    // Usamos el nombre de variable que tú tienes ("renderer")
    private final TableCellRenderer renderer;

    public TableHeaderAlignment(JTable table) {
        renderer = table.getTableHeader().getDefaultRenderer();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel label = (JLabel) renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // --- LÓGICA CORREGIDA PARA 5 COLUMNAS ---
        // Centrar "Rol" (Col 1) y "Codigo" (Col 2)
        if (column == 1 || column == 2 || column == 3 || column == 4) { 
            label.setHorizontalAlignment(SwingConstants.CENTER);
        } else { 
            // Alinear a la izquierda "Miembro" (0) y "Estado" (3)
            label.setHorizontalAlignment(SwingConstants.LEADING);
        }
        
        return label;
    }

}
