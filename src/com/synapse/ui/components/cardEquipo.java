package com.synapse.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.synapse.core.models.Equipo;
import com.synapse.core.models.Usuario;
import com.synapse.data.dao.EquipoDAO;
import com.synapse.data.dao.UsuarioDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

/**
 * Componente visual para mostrar los detalles de un Equipo en una "tarjeta".
 */
public class cardEquipo extends JPanel {

    private final Equipo equipo;
    private final EquipoDAO equipoDAO;
    private final UsuarioDAO usuarioDAO;

    // --- Componentes UI ---
    private JLabel lblNombreEquipo;
    private JTextArea lblDescripcion;
    private JLabel lblLider;
    private JLabel lblMiembros;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnVerMiembros;

    public cardEquipo(Equipo equipo) {
        this.equipo = equipo;
        this.equipoDAO = new EquipoDAO();
        this.usuarioDAO = new UsuarioDAO();
        initUI();
        cargarDatosEquipo();
        addListeners();
    }

    private void initUI() {
        // --- Estilo general de la tarjeta ---
        putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 15;"
                + "background: $Panel.background;"
                + "border: 8,8,8,8");

        setLayout(new MigLayout("wrap 2, fillx, insets 10", "[][grow, fill]", "[]10[]10[]10[]"));

        // --- Icono de equipo ---
        JLabel iconEquipo = new JLabel(new FlatSVGIcon("icons/menu/equipo.svg", 24, 24));
        add(iconEquipo, "cell 0 0, aligny top");

        // --- Nombre del Equipo ---
        lblNombreEquipo = new JLabel("Nombre del Equipo");
        lblNombreEquipo.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");
        add(lblNombreEquipo, "cell 1 0, growx");

        // --- Descripción ---
        lblDescripcion = new JTextArea("Descripción del equipo.");
        lblDescripcion.setLineWrap(true);
        lblDescripcion.setWrapStyleWord(true);
        lblDescripcion.setEditable(false);
        lblDescripcion.setOpaque(false);
        lblDescripcion.putClientProperty(FlatClientProperties.STYLE,
                "font: -1; foreground: $Label.secondaryForeground");
        lblDescripcion.setBorder(BorderFactory.createEmptyBorder());
        add(lblDescripcion, "span 2, growx, gaptop 5");

        // --- Líder del Equipo ---
        JLabel iconLider = new JLabel(new FlatSVGIcon("icons/general/user.svg", 16, 16));
        lblLider = new JLabel("Líder: N/A");
        lblLider.putClientProperty(FlatClientProperties.STYLE, "font: -1");
        add(iconLider, "cell 0 2, aligny center");
        add(lblLider, "cell 1 2, growx");

        // --- Número de Miembros ---
        JLabel iconMiembros = new JLabel(new FlatSVGIcon("icons/general/group.svg", 16, 16));
        lblMiembros = new JLabel("Miembros: 0");
        lblMiembros.putClientProperty(FlatClientProperties.STYLE, "font: -1");
        add(iconMiembros, "cell 0 3, aligny center");
        add(lblMiembros, "cell 1 3, growx");

        // --- Separador ---
        JSeparator separator = new JSeparator();
        add(separator, "span 2, growx, gaptop 10, gapbottom 10");

        // --- Panel de Acciones ---
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelAcciones.setOpaque(false);

        btnVerMiembros = new JButton("Miembros");
        btnVerMiembros.setIcon(new FlatSVGIcon("icons/general/view.svg", 16, 16));
        btnVerMiembros.putClientProperty(FlatClientProperties.STYLE,
                "borderWidth:0; background:null; font:bold; foreground:$Component.accentColor");

        btnEditar = new JButton("Editar");
        btnEditar.setIcon(new FlatSVGIcon("icons/general/edit.svg", 16, 16));
        btnEditar.putClientProperty(FlatClientProperties.STYLE,
                "borderWidth:0; background:null; font:bold; foreground:$Component.accentColor");

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setIcon(new FlatSVGIcon("icons/general/delete.svg", 16, 16));
        btnEliminar.putClientProperty(FlatClientProperties.STYLE,
                "borderWidth:0; background:null; font:bold; foreground:$Component.error.foreground");

        panelAcciones.add(btnVerMiembros);
        panelAcciones.add(btnEditar);
        panelAcciones.add(btnEliminar);

        add(panelAcciones, "span 2, growx, align right");
    }

    private void cargarDatosEquipo() {
        if (equipo != null) {
            lblNombreEquipo.setText(equipo.getNombre());
            lblDescripcion.setText(equipo.getDescripcion());

            Usuario lider = equipo.getLider();
            if (lider != null) {
                lblLider.setText("Líder: " + lider.getNombre());
            } else {
                lblLider.setText("Líder: Sin asignar");
            }
            lblMiembros.setText("Miembros: " + equipo.getNumeroMiembros());
        } else {
            lblNombreEquipo.setText("Equipo Desconocido");
            lblDescripcion.setText("No hay datos disponibles para este equipo.");
            lblLider.setText("Líder: N/A");
            lblMiembros.setText("Miembros: 0");
        }
    }

    private void addListeners() {
        btnVerMiembros.addActionListener(e -> {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            com.synapse.ui.views.gerente.GestionarMiembrosDialog dialog = new com.synapse.ui.views.gerente.GestionarMiembrosDialog(
                    parentFrame, equipo.getIdEquipo());
            dialog.setVisible(true);

            // Recargar contador de miembros
            int nuevoNumMiembros = equipoDAO.contarMiembros(equipo.getIdEquipo());
            equipo.setNumeroMiembros(nuevoNumMiembros);
            lblMiembros.setText("Miembros: " + nuevoNumMiembros);
        });

        btnEditar.addActionListener(e -> {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            com.synapse.ui.views.admin.dialogEditarEquipo dialog = new com.synapse.ui.views.admin.dialogEditarEquipo(
                    parentFrame, equipo);
            dialog.setVisible(true);

            if (dialog.isEditado()) {
                lblNombreEquipo.setText(equipo.getNombre());
                lblDescripcion.setText(equipo.getDescripcion());
                Notifications.getInstance().show(
                        Notifications.Type.SUCCESS,
                        Notifications.Location.TOP_CENTER,
                        "✅ Equipo actualizado");
            }
        });

        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que quieres desactivar el equipo '" + equipo.getNombre() + "'?\n"
                            + "Ya no será visible, pero sus tareas se conservarán.",
                    "Confirmar Desactivación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (equipoDAO.desactivarEquipo(equipo.getIdEquipo())) {
                    Notifications.getInstance().show(
                            Notifications.Type.SUCCESS,
                            Notifications.Location.TOP_RIGHT,
                            "Equipo '" + equipo.getNombre() + "' desactivado.");

                    Container parent = this.getParent();
                    parent.remove(this);
                    parent.revalidate();
                    parent.repaint();
                } else {
                    Notifications.getInstance().show(
                            Notifications.Type.ERROR,
                            Notifications.Location.TOP_RIGHT,
                            "Error al desactivar el equipo.");
                }
            }
        });
    }
}
