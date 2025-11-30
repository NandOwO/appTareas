package com.synapse.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.SwingConstants;

/**
 * Renderizador que crea Píldoras de Prioridad (Alta, Media, Baja)
 */
public class PrioridadPillRenderer extends JPanel implements TableCellRenderer {

    private final JPanel pillPanel;
    private final JLabel label;

    public PrioridadPillRenderer() {
        super(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setOpaque(false);
        pillPanel = new JPanel(new BorderLayout());
        pillPanel.setOpaque(true);
        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        pillPanel.add(label, BorderLayout.CENTER);
        add(pillPanel);
    }



    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        String text = (value == null) ? "" : value.toString().trim();
        label.setText(text);

        // ... (tu switch/case para los colores) ...
        // (El switch/case se queda igual)
        String pillStyle = "font: bold; arc: 999; border: 3,10,3,10;";
        String backgroundColor = "";
        String foregroundColorKey = "";
        
        switch (text.toLowerCase()) {
            case "alta":
                backgroundColor = "$Component.errorColor"; 
                foregroundColorKey = "Component.errorColor.foreground";
                break;
            case "media":
                backgroundColor = "$Component.warningColor";
                foregroundColorKey = "Component.warningColor.foreground";
                break;
            case "baja":
                backgroundColor = "$Component.infoColor";
                foregroundColorKey = "Component.infoColor.foreground";
                break;
            default:
                backgroundColor = "$Table.selectionInactiveBackground";
                foregroundColorKey = "Table.foreground";
                break;
        }

        // --- INICIO DE LA CORRECCIÓN ---
        if (isSelected) {
            setOpaque(true);
            
            // Comprobar si 'table' es nulo
            if (table != null) {
                setBackground(table.getSelectionBackground());
                pillPanel.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                // Colores por defecto si no estamos en una tabla
                setBackground(UIManager.getColor("Table.selectionBackground"));
                pillPanel.setBackground(UIManager.getColor("Table.selectionBackground"));
                label.setForeground(UIManager.getColor("Table.selectionForeground"));
            }
            
            label.putClientProperty(FlatClientProperties.STYLE, "font: bold;");
        } else {
            setOpaque(false);
            
            // Comprobar si 'table' es nulo (AQUÍ FUE EL CRASH)
            if (table != null) {
                setBackground(table.getBackground());
            } else {
                setBackground(null); // Fondo transparente por defecto
            }
            
            pillPanel.setOpaque(true);
            pillPanel.putClientProperty(FlatClientProperties.STYLE, 
                pillStyle + "background:" + backgroundColor + ";");
            label.setForeground(UIManager.getColor(foregroundColorKey));
            label.putClientProperty(FlatClientProperties.STYLE, "font: bold;");
        }
        // --- FIN DE LA CORRECCIÓN ---
        
        return this;
    }
}
