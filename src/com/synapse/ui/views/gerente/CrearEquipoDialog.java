package com.synapse.ui.views.gerente;

import com.formdev.flatlaf.FlatClientProperties;
import com.synapse.core.models.Equipo;
import com.synapse.data.dao.EquipoDAO;
import com.synapse.ui.components.ButtonAction;
import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

/**
 * Dialog para crear un nuevo equipo
 */
public class CrearEquipoDialog extends JDialog {

    private final EquipoDAO equipoDAO;
    private final int idLider;
    private boolean exitoso = false;

    // Componentes
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private ButtonAction btnGuardar;
    private ButtonAction btnCancelar;

    public CrearEquipoDialog(Frame parent, int idLider) {
        super(parent, "Crear Nuevo Equipo", true);
        this.idLider = idLider;
        this.equipoDAO = new EquipoDAO();

        initComponents();
        configurarEstilos();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 20", "[grow]", "[]10[]10[]20[]"));

        // Título
        JLabel lblTitulo = new JLabel("Crear Nuevo Equipo");
        lblTitulo.putClientProperty(FlatClientProperties.STYLE, "font:bold +3");

        // Nombre
        JLabel lblNombre = new JLabel("Nombre del equipo:");
        txtNombre = new JTextField();
        txtNombre.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ej: Equipo de Desarrollo");

        // Descripción
        JLabel lblDescripcion = new JLabel("Descripción:");
        txtDescripcion = new JTextArea(4, 30);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);

        // Botones
        JPanel panelBotones = new JPanel(new MigLayout("fillx", "[grow][][][]"));
        btnCancelar = new ButtonAction();
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        btnGuardar = new ButtonAction();
        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(e -> guardarEquipo());

        panelBotones.add(new JLabel(), "grow");
        panelBotones.add(btnCancelar, "width 120!");
        panelBotones.add(btnGuardar, "width 120!");

        // Agregar componentes
        panel.add(lblTitulo, "wrap");
        panel.add(lblNombre, "wrap");
        panel.add(txtNombre, "growx, wrap");
        panel.add(lblDescripcion, "wrap");
        panel.add(scrollDesc, "growx, height 100!, wrap");
        panel.add(panelBotones, "growx");

        setContentPane(panel);
    }

    private void configurarEstilos() {
        txtNombre.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;borderWidth:1;focusWidth:1;innerFocusWidth:0");

        // JTextArea no soporta estilos personalizados, aplicar al ScrollPane
        JScrollPane scrollDesc = (JScrollPane) txtDescripcion.getParent().getParent();
        scrollDesc.putClientProperty(FlatClientProperties.STYLE, "arc:10;");
    }

    private void guardarEquipo() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        // Validaciones
        if (nombre.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ El nombre del equipo es obligatorio");
            txtNombre.requestFocus();
            return;
        }

        if (nombre.length() < 3) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ El nombre debe tener al menos 3 caracteres");
            txtNombre.requestFocus();
            return;
        }

        // Crear equipo
        Equipo equipo = new Equipo();
        equipo.setNombre(nombre);
        equipo.setDescripcion(descripcion);
        equipo.setIdLider(idLider);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return equipoDAO.crearEquipo(equipo);
            }

            @Override
            protected void done() {
                try {
                    Boolean exito = get();
                    if (exito) {
                        exitoso = true;
                        Notifications.getInstance().show(Notifications.Type.SUCCESS,
                                Notifications.Location.TOP_CENTER,
                                "✅ Equipo creado exitosamente");
                        dispose();
                    } else {
                        Notifications.getInstance().show(Notifications.Type.ERROR,
                                Notifications.Location.TOP_CENTER,
                                "❌ Error al crear el equipo");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Notifications.getInstance().show(Notifications.Type.ERROR,
                            Notifications.Location.TOP_CENTER,
                            "Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    public boolean isExitoso() {
        return exitoso;
    }
}
