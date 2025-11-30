package com.synapse.ui.components;

import com.synapse.core.models.Usuario;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import net.miginfocom.swing.MigLayout;

/**
 * Renderer simple para usuarios SIN foto (optimizado para performance)
 */
public class SimpleUserRenderer extends javax.swing.JPanel implements TableCellRenderer {

    private final JLabel labelNombre;
    private final JLabel labelEmail;

    public SimpleUserRenderer() {
        setLayout(new MigLayout("insets 5 10 5 10, fill", "[]", "[][]"));

        labelNombre = new JLabel("Nombre de Usuario");
        labelNombre.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "font: bold;");

        labelEmail = new JLabel("email@usuario.com");
        labelEmail.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE,
                "foreground: $Label.secondaryForeground;");

        add(labelNombre, "wrap, gapy 0");
        add(labelEmail, "gapy 0");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof Usuario) {
            Usuario usuario = (Usuario) value;
            labelNombre.setText(usuario.getNombre());
            labelEmail.setText(usuario.getEmail());
        }

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            labelNombre.setForeground(table.getSelectionForeground());
            labelEmail.setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            labelNombre.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "font: bold;");
            labelEmail.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE,
                    "foreground: $Label.secondaryForeground;");
        }

        return this;
    }
}
