/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.synapse.ui.components;

/**
 *
 * @author FERNANDO
 */
import java.awt.Component;
import javax.swing.JPanel; // Es mejor usar JPanel como contenedor
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ToggleButtonRenderer extends JPanel implements TableCellRenderer {

    private final ToggleButton toggleButton;

    public ToggleButtonRenderer() {
        setOpaque(true); 
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 0));
        toggleButton = new ToggleButton();
        toggleButton.setEnabled(false);
        
        add(toggleButton);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        if (value instanceof Boolean) {
            toggleButton.setSelected((Boolean) value, false);
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
        } else {
            setBackground(table.getBackground());
        }
        
        return this; 
    }
}
