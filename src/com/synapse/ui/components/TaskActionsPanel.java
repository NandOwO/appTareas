package com.synapse.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel que contiene los 3 botones de acción (Ver, Editar, Eliminar).
 * Usado por TaskActionsRenderer y TaskActionsEditor.
 */
public class TaskActionsPanel extends JPanel {

    public final JButton btnVer;
    public final JButton btnEditar;
    public final JButton btnEliminar;

    public TaskActionsPanel() {
        // Centramos los botones
        super(new FlowLayout(FlowLayout.CENTER, 5, 0));
        setOpaque(false); // El panel es transparente

        btnVer = createIconButton(new FlatSVGIcon("icons/general/verContraseña.svg")); // (Reusa un icono)
        btnEditar = createIconButton(new FlatSVGIcon("icons/general/editar.svg")); //
        btnEliminar = createIconButton(new FlatSVGIcon("icons/general/archivar.svg")); // (Reusa un icono)

        add(btnVer);
        add(btnEditar);
        add(btnEliminar);
    }

    private JButton createIconButton(FlatSVGIcon icon) {
        JButton button = new JButton(icon);
        button.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"         // Redondo
                + "margin: 5,5,5,5;"
                + "background: null;"  // Sin fondo
                + "borderColor: null;");
        button.setFocusable(false);
        button.setBorderPainted(false);
        return button;
    }
}