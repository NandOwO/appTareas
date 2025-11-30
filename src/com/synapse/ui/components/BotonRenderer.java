package com.synapse.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Component;
import java.awt.FlowLayout; // Importante
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderiza un panel transparente con un botón de icono redondo y centrado.
 */
public class BotonRenderer extends JPanel implements TableCellRenderer {

    private final JButton actionButton;

    public BotonRenderer() {
        // 1. El Panel "wrapper" (this) es transparente y centrado
        super(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setOpaque(false);

        // 2. El Botón de acción
        actionButton = new JButton();
        
        // --- ¡AQUÍ LA MAGIA! ---
        // Usamos el ícono del lápiz que ya tienes
        actionButton.setIcon(new FlatSVGIcon("icons/general/editar.svg")); 
        
        // Estilo de FlatLaf para hacerlo redondo (arc: 999) y sin fondo
        actionButton.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 25;"
                + "margin: 5,5,5,5;" // Un poco de espacio interno
                + "background: null;" // Sin fondo
                + "borderColor: #000000;" // Sin borde
        );
        
        // Estilo "Button.toolbar.hoverBackground" es un gris sutil al pasar el mouse
        actionButton.setBackground(null);
        actionButton.setFocusable(false);
        actionButton.setBorderPainted(false);
        
        add(actionButton);
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