package com.synapse.ui.views.admin;

import com.synapse.core.models.Equipo;
import com.synapse.core.models.Usuario;
import com.synapse.data.dao.EquipoDAO;
import com.synapse.data.dao.UsuarioDAO;
import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

public class dialogCrearEquipo extends JDialog {

    // --- Componentes de UI ---
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JPanel panelEmpleados;
    private List<JCheckBox> checkboxesEmpleados = new ArrayList<>();

    // --- Lógica de Backend ---
    private final EquipoDAO equipoDAO;
    private final UsuarioDAO usuarioDAO;
    private final Usuario admin;
    private boolean creadoConExito = false;

    public dialogCrearEquipo(Frame owner, Usuario admin) {
        super(owner, "Crear Nuevo Equipo", true);

        this.admin = admin;
        this.equipoDAO = new EquipoDAO();
        this.usuarioDAO = new UsuarioDAO();

        // --- Configuración del Diálogo ---
        setSize(600, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // --- Panel del Formulario ---
        JPanel formPanel = new JPanel(new MigLayout("wrap 2, fillx, insets 20", "[right, 100!][grow, fill]"));

        // Título
        JLabel titleLabel = new JLabel("Crear Nuevo Equipo");
        titleLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");
        formPanel.add(titleLabel, "span 2, growx, gapbottom 15");

        // Nombre
        JLabel nameLabel = new JLabel("Nombre *");
        nameField = new JTextField();
        nameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ej: Equipo de Marketing");
        formPanel.add(nameLabel);
        formPanel.add(nameField, "growx");

        // Descripción
        JLabel descriptionLabel = new JLabel("Descripción");
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.putClientProperty(FlatClientProperties.STYLE, "arc:10;");
        formPanel.add(descriptionLabel, "gaptop 10, top");
        formPanel.add(descriptionScrollPane, "gaptop 10, growx, height 80!");

        // Empleados
        JLabel empleadosLabel = new JLabel("Empleados *");
        empleadosLabel.putClientProperty(FlatClientProperties.STYLE, "font:bold");
        formPanel.add(empleadosLabel, "gaptop 15");
        formPanel.add(new JLabel("Marca los empleados para el equipo:"), "gaptop 15, growx");

        // Panel de checkboxes para empleados
        panelEmpleados = new JPanel();
        panelEmpleados.setLayout(new BoxLayout(panelEmpleados, BoxLayout.Y_AXIS));

        JScrollPane empleadosScrollPane = new JScrollPane(panelEmpleados);
        empleadosScrollPane.putClientProperty(FlatClientProperties.STYLE, "arc:10;");
        empleadosScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formPanel.add(new JLabel(), "skip");
        formPanel.add(empleadosScrollPane, "span, growx, height 150!");

        // --- Panel de Botones ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(0, 20, 10, 20));

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.addActionListener(e -> dispose());

        JButton createButton = new JButton("Crear Equipo");
        createButton.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        createButton.addActionListener(e -> onCreate());

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);

        // --- Ensamblaje ---
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Cargar empleados
        cargarEmpleados();
    }

    /**
     * Carga solo empleados (rol = "empleado") y crea checkboxes
     */
    private void cargarEmpleados() {
        SwingWorker<List<Usuario>, Void> worker = new SwingWorker<List<Usuario>, Void>() {
            @Override
            protected List<Usuario> doInBackground() throws Exception {
                return usuarioDAO.getUsuariosPorRol("empleado");
            }

            @Override
            protected void done() {
                try {
                    List<Usuario> empleados = get();
                    panelEmpleados.removeAll();
                    checkboxesEmpleados.clear();

                    if (empleados != null && !empleados.isEmpty()) {
                        for (Usuario emp : empleados) {
                            JCheckBox checkbox = new JCheckBox(emp.getNombre() + " (" + emp.getEmail() + ")");
                            checkbox.putClientProperty("usuario", emp);
                            checkbox.setBorder(new EmptyBorder(5, 10, 5, 10));
                            checkboxesEmpleados.add(checkbox);
                            panelEmpleados.add(checkbox);
                        }
                        panelEmpleados.revalidate();
                        panelEmpleados.repaint();
                    } else {
                        JLabel noEmpleados = new JLabel("No hay empleados disponibles");
                        noEmpleados.setBorder(new EmptyBorder(10, 10, 10, 10));
                        panelEmpleados.add(noEmpleados);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Notifications.getInstance().show(
                            Notifications.Type.ERROR,
                            Notifications.Location.TOP_CENTER,
                            "Error al cargar empleados");
                }
            }
        };
        worker.execute();
    }

    /**
     * Crea el equipo y asigna los empleados seleccionados
     */
    private void onCreate() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            Notifications.getInstance().show(
                    Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ El nombre del equipo es obligatorio");
            nameField.requestFocus();
            return;
        }

        // Obtener empleados seleccionados de los checkboxes
        List<Usuario> empleadosSeleccionados = new ArrayList<>();
        for (JCheckBox checkbox : checkboxesEmpleados) {
            if (checkbox.isSelected()) {
                Usuario emp = (Usuario) checkbox.getClientProperty("usuario");
                if (emp != null) {
                    empleadosSeleccionados.add(emp);
                }
            }
        }

        if (empleadosSeleccionados.isEmpty()) {
            Notifications.getInstance().show(
                    Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ Debes seleccionar al menos un empleado");
            return;
        }

        String description = descriptionArea.getText().trim();

        // Crear equipo en background
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // 1. Crear el equipo
                Equipo nuevoEquipo = new Equipo();
                nuevoEquipo.setNombre(name);
                nuevoEquipo.setDescripcion(description);
                nuevoEquipo.setIdLider(admin.getIdUsuario());

                boolean equipoCreado = equipoDAO.crearEquipo(nuevoEquipo);
                if (!equipoCreado) {
                    return false;
                }

                // 2. Obtener el ID del equipo recién creado
                List<Equipo> equipos = equipoDAO.getEquiposPorLider(admin.getIdUsuario());
                Equipo equipoCreado2 = null;
                for (Equipo eq : equipos) {
                    if (eq.getNombre().equals(name)) {
                        equipoCreado2 = eq;
                        break;
                    }
                }

                if (equipoCreado2 == null) {
                    return false;
                }

                // 3. Agregar miembros al equipo
                for (Usuario empleado : empleadosSeleccionados) {
                    equipoDAO.agregarMiembro(equipoCreado2.getIdEquipo(), empleado.getIdUsuario());
                }

                return true;
            }

            @Override
            protected void done() {
                try {
                    boolean exito = get();
                    if (exito) {
                        creadoConExito = true;
                        Notifications.getInstance().show(
                                Notifications.Type.SUCCESS,
                                Notifications.Location.TOP_CENTER,
                                "✅ Equipo '" + name + "' creado con " + empleadosSeleccionados.size() + " miembros");
                        dispose();
                    } else {
                        Notifications.getInstance().show(
                                Notifications.Type.ERROR,
                                Notifications.Location.TOP_CENTER,
                                "❌ Error al crear el equipo");
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

    public boolean isCreadoConExito() {
        return creadoConExito;
    }
}
