package com.synapse.ui.views.admin;

import com.synapse.core.models.Equipo;
import com.synapse.data.dao.EquipoDAO;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

public class dialogEditarEquipo extends JDialog {

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private final Equipo equipo;
    private final EquipoDAO equipoDAO;
    private boolean editado = false;

    public dialogEditarEquipo(Frame owner, Equipo equipo) {
        super(owner, "Editar Equipo", true);

        this.equipo = equipo;
        this.equipoDAO = new EquipoDAO();

        setSize(500, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Panel del formulario
        JPanel formPanel = new JPanel(new MigLayout("wrap 2, fillx, insets 20", "[right, 80!][grow, fill]"));

        // Título
        JLabel titleLabel = new JLabel("Editar Equipo");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");
        formPanel.add(titleLabel, "span 2, growx, gapbottom 15");

        // Nombre
        JLabel nameLabel = new JLabel("Nombre *");
        txtNombre = new JTextField(equipo.getNombre());
        formPanel.add(nameLabel);
        formPanel.add(txtNombre, "growx");

        // Descripción
        JLabel descLabel = new JLabel("Descripción");
        txtDescripcion = new JTextArea(equipo.getDescripcion(), 4, 30);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtDescripcion);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc:10;");
        formPanel.add(descLabel, "gaptop 10, top");
        formPanel.add(scrollPane, "gaptop 10, growx, height 100!");

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(0, 20, 10, 20));

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> dispose());

        JButton saveButton = new JButton("Guardar");
        saveButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        saveButton.addActionListener(e -> guardarCambios());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void guardarCambios() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            Notifications.getInstance().show(
                    Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ El nombre es obligatorio");
            txtNombre.requestFocus();
            return;
        }

        String descripcion = txtDescripcion.getText().trim();

        // Actualizar el objeto equipo
        equipo.setNombre(nombre);
        equipo.setDescripcion(descripcion);

        // Guardar en base de datos
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return equipoDAO.actualizarEquipo(equipo);
            }

            @Override
            protected void done() {
                try {
                    boolean exito = get();
                    if (exito) {
                        editado = true;
                        dispose();
                    } else {
                        Notifications.getInstance().show(
                                Notifications.Type.ERROR,
                                Notifications.Location.TOP_CENTER,
                                "❌ Error al actualizar el equipo");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Notifications.getInstance().show(
                            Notifications.Type.ERROR,
                            Notifications.Location.TOP_CENTER,
                            "❌ Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    public boolean isEditado() {
        return editado;
    }
}
