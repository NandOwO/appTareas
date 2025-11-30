package com.synapse.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.synapse.core.models.Tarea; // Importamos el modelo Tarea
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

/**
 * Renderizador para la celda "Tarea". Muestra el nombre de la tarea y su
 * descripción en dos líneas.
 */
public class TaskRenderer extends JPanel implements TableCellRenderer {

    private final JLabel lblTareaNombre;
    private final JLabel lblTareaDesc;

    public TaskRenderer() {
        setOpaque(true);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(5, 10, 5, 10));

        lblTareaNombre = new JLabel();
        lblTareaNombre.putClientProperty(FlatClientProperties.STYLE, "font: bold");

        lblTareaDesc = new JLabel();
        lblTareaDesc.putClientProperty(FlatClientProperties.STYLE, "font: -1");

        add(lblTareaNombre);
        add(lblTareaDesc);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof Tarea) {
            Tarea tarea = (Tarea) value;

            // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
            // Usamos getTitulo() (del backend) en lugar de getNombre() (de DataSimulada)
            lblTareaNombre.setText(tarea.getTitulo());
            // ---------------------------------

            // Acortar la descripción (esto ya estaba bien)
            String desc = tarea.getDescripcion();
            if (desc.length() > 50) {
                desc = desc.substring(0, 47) + "...";
            }
            lblTareaDesc.setText(desc);

        } else {
            lblTareaNombre.setText("Error: No es un objeto Tarea");
            lblTareaDesc.setText("");
        }

        // Manejar selección de fila
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            lblTareaNombre.setForeground(table.getSelectionForeground());
            lblTareaDesc.setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            lblTareaNombre.setForeground(table.getForeground());
            lblTareaDesc.setForeground(UIManager.getColor("Label.secondaryForeground"));
        }

        return this;
    }
}
