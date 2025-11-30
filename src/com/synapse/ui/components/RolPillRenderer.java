package com.synapse.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.FlowLayout; // El "wrapper"
import java.awt.BorderLayout; // El "fondoPill"
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.SwingConstants;

/**
 * Renderizador BASADO EN TU EJEMPLO "crearPillLabel" Solo se usa para la
 * columna "Rol"
 */
public class RolPillRenderer extends JPanel implements TableCellRenderer {

    private final JPanel pillPanel;
    private final JLabel label;

    public RolPillRenderer() {
        // 1. El "wrapper" (this) - Transparente y centrado
        super(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setOpaque(false);

        // 2. El "fondoPill" (pillPanel) - Opaco y con BorderLayout
        pillPanel = new JPanel(new BorderLayout());
        pillPanel.setOpaque(true);

        // 3. El "lblValor" (label) - Centrado
        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);

        pillPanel.add(label, BorderLayout.CENTER);
        add(pillPanel); // Añade el fondoPill al wrapper
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        String text = (value == null) ? "" : value.toString().trim();
        label.setText(text);

        // --- Definir Estilos ---
        String pillStyle = "arc: 999; border: 3,10,3,10;"; // arc + padding
        String backgroundColor = "";
        String foregroundColorKey = "";

        switch (text.toLowerCase()) {
            case "admin":
                backgroundColor = "$Component.accentColor";
                foregroundColorKey = "Component.accentColor.foreground";
                break;
            case "gerente":
                backgroundColor = "$Component.warningColor";
                foregroundColorKey = "Component.warningColor.foreground";
                break;
            case "empleado":
                backgroundColor = "$Component.infoColor";
                foregroundColorKey = "Component.infoColor.foreground";
                break;
            default:
                backgroundColor = "$Table.selectionInactiveBackground";
                foregroundColorKey = "Table.foreground";
                break;
        }

        // ... (dentro de getTableCellRendererComponent en RolPillRenderer.java)
        // --- Aplicar Estilos ---
        if (isSelected) {
            // El wrapper (this) se pinta del color de selección
            setOpaque(true);
            setBackground(table.getSelectionBackground());

            // --- INICIO DE LA CORRECCIÓN ---
            // El fondoPill (pillPanel) TAMBIÉN se pinta del color de selección
            pillPanel.setOpaque(true);
            pillPanel.setBackground(table.getSelectionBackground());
            // --- FIN DE LA CORRECCIÓN ---

            // El label (texto) toma el color de texto seleccionado
            label.setForeground(table.getSelectionForeground());

            // Mantenemos la forma y el padding
            pillPanel.putClientProperty(FlatClientProperties.STYLE, pillStyle);
            label.putClientProperty(FlatClientProperties.STYLE, "font: bold;");

        } else {
            // El wrapper (this) es transparente
            setOpaque(false);
            setBackground(table.getBackground());

            // El fondoPill (pillPanel) toma su color
            pillPanel.setOpaque(true);
            pillPanel.putClientProperty(FlatClientProperties.STYLE,
                    pillStyle + "background:" + backgroundColor + ";");

            // El label (texto) toma su color
            label.setForeground(UIManager.getColor(foregroundColorKey));
            label.putClientProperty(FlatClientProperties.STYLE, "font: bold;");
        }

        return this; // Devuelve el wrapper
    }
}
