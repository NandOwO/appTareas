package com.synapse.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.synapse.core.models.Equipo;
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
 * Renderizador que crea Píldoras de Estado (Pendiente, Completada, etc.)
 */
public class TaskStatusPillRenderer extends JPanel implements TableCellRenderer {

    private final JPanel pillPanel;
    private final JLabel label;

    public TaskStatusPillRenderer() {
        super(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setOpaque(false);
        pillPanel = new JPanel(new BorderLayout());
        pillPanel.setOpaque(true);
        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        pillPanel.add(label, BorderLayout.CENTER);
        add(pillPanel);
    }

    // ... (imports y constructor)
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        String text = (value == null) ? "" : value.toString().trim();
        label.setText(text);

        // ... (tu switch/case para los colores de estado) ...
        // (El switch/case se queda igual)
        String pillStyle = "font: bold; arc: 999; border: 3,10,3,10;";
        String backgroundColor = "";
        String foregroundColorKey = "";

        switch (text.toLowerCase()) {
            case "pendiente":
                backgroundColor = "$Table.selectionInactiveBackground";
                foregroundColorKey = "Table.foreground";
                break;
            case "en progreso":
                backgroundColor = "$Component.infoColor";
                foregroundColorKey = "Component.infoColor.foreground";
                break;
            case "completada":
                backgroundColor = "$Component.successColor";
                foregroundColorKey = "Component.successColor.foreground";
                break;
            case "vencida":
                backgroundColor = "$Component.warningColor";
                foregroundColorKey = "Component.warningColor.foreground";
                break;
            default:
                backgroundColor = "$Table.selectionInactiveBackground";
                foregroundColorKey = "Table.foreground";
                break;
        }

        // --- INICIO DE LA CORRECCIÓN ---
        if (isSelected) {
            setOpaque(true);

            if (table != null) {
                setBackground(table.getSelectionBackground());
                pillPanel.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                setBackground(UIManager.getColor("Table.selectionBackground"));
                pillPanel.setBackground(UIManager.getColor("Table.selectionBackground"));
                label.setForeground(UIManager.getColor("Table.selectionForeground"));
            }

            label.putClientProperty(FlatClientProperties.STYLE, "font: bold;");
        } else {
            setOpaque(false);

            if (table != null) {
                setBackground(table.getBackground());
            } else {
                setBackground(null);
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
