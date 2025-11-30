package com.synapse.ui.views.gerente;

import com.formdev.flatlaf.FlatClientProperties;
import com.synapse.core.models.Equipo;
import com.synapse.core.models.Tarea;
import com.synapse.core.models.Usuario;
import com.synapse.core.services.TareaService;
import com.synapse.data.dao.EquipoDAO;
import com.synapse.data.dao.UsuarioDAO;
import com.synapse.ui.components.ButtonAction;
import java.awt.Frame;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.datetime.component.date.DatePicker;
import raven.toast.Notifications;

/**
 * Dialog para editar una tarea existente
 */
public class EditarTareaDialog extends JDialog {

    private final Tarea tarea;
    private final TareaService tareaService;
    private final UsuarioDAO usuarioDAO;
    private final EquipoDAO equipoDAO;
    private final Frame parentFrame; // Guardar referencia al JFrame parent
    private boolean exitoso = false;

    // Componentes
    private JTextField txtTitulo;
    private JTextArea txtDescripcion;
    private JTextField txtFecha;
    private JComboBox<String> comboPrioridad;
    private JComboBox<String> comboEstado;
    private JComboBox<String> comboTipoAsignacion;
    private JComboBox<Object> comboAsignado;
    private ButtonAction btnGuardar;
    private ButtonAction btnCancelar;

    private List<Usuario> listaEmpleados;
    private List<Equipo> listaEquipos;

    public EditarTareaDialog(Frame parent, boolean modal, Tarea tarea) {
        super(parent, "Editar Tarea", modal);
        this.parentFrame = parent; // Guardar referencia
        this.tarea = tarea;
        this.tareaService = new TareaService();
        this.usuarioDAO = new UsuarioDAO();
        this.equipoDAO = new EquipoDAO();

        initComponents();
        configurarEstilos();
        cargarDatos();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 20", "[100!][grow]", ""));

        // Título
        JLabel lblHeader = new JLabel("Editar Tarea");
        lblHeader.putClientProperty(FlatClientProperties.STYLE, "font:bold +3");
        panel.add(lblHeader, "span 2, wrap, gapbottom 15");

        // Título de la tarea
        panel.add(new JLabel("Título:"));
        txtTitulo = new JTextField(tarea.getTitulo());
        panel.add(txtTitulo, "growx, wrap");

        // Descripción
        panel.add(new JLabel("Descripción:"), "top");
        txtDescripcion = new JTextArea(tarea.getDescripcion(), 4, 30);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panel.add(scrollDesc, "growx, height 100!, wrap");

        // Fecha límite (sin DatePicker para evitar ClassCastException)
        panel.add(new JLabel("Fecha límite:"));
        txtFecha = new JTextField();
        txtFecha.setToolTipText("Formato: YYYY-MM-DD");
        if (tarea.getFechaLimite() != null) {
            txtFecha.setText(tarea.getFechaLimite().toLocalDateTime().toLocalDate().toString());
        }
        panel.add(txtFecha, "growx, wrap");

        // Prioridad
        panel.add(new JLabel("Prioridad:"));
        comboPrioridad = new JComboBox<>(new String[] { "Baja", "Media", "Alta" });
        comboPrioridad.setSelectedIndex(tarea.getIdPrioridad() - 1);
        panel.add(comboPrioridad, "growx, wrap");

        // Estado
        panel.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>(new String[] { "Pendiente", "En Progreso", "Completada", "Pausada" });
        comboEstado.setSelectedIndex(tarea.getIdEstado() - 1);
        panel.add(comboEstado, "growx, wrap");

        // Tipo de asignación
        panel.add(new JLabel("Asignar a:"));
        comboTipoAsignacion = new JComboBox<>(new String[] { "Empleado", "Equipo" });
        comboTipoAsignacion.addActionListener(e -> actualizarComboAsignado());
        panel.add(comboTipoAsignacion, "growx, wrap");

        // Asignado
        panel.add(new JLabel("Seleccionar:"));
        comboAsignado = new JComboBox<>();
        panel.add(comboAsignado, "growx, wrap");

        // Botones
        JPanel panelBotones = new JPanel(new MigLayout("fillx", "[grow][][]"));
        btnCancelar = new ButtonAction();
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(e -> dispose());

        btnGuardar = new ButtonAction();
        btnGuardar.setText("Guardar Cambios");
        btnGuardar.addActionListener(e -> guardarCambios());

        panelBotones.add(new JLabel(), "grow");
        panelBotones.add(btnCancelar, "width 120!");
        panelBotones.add(btnGuardar, "width 150!");

        panel.add(panelBotones, "span 2, growx, gaptop 15");

        setContentPane(panel);
    }

    private void configurarEstilos() {
        txtTitulo.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;borderWidth:1;focusWidth:1;innerFocusWidth:0");
        // JTextArea no soporta estilos personalizados de FlatLaf
    }

    private void cargarDatos() {
        SwingWorker<DatosDialog, Void> worker = new SwingWorker<DatosDialog, Void>() {
            @Override
            protected DatosDialog doInBackground() throws Exception {
                DatosDialog datos = new DatosDialog();
                datos.empleados = usuarioDAO.getUsuarios();
                datos.equipos = equipoDAO.getEquipos();

                // Cargar asignaciones de la tarea desde la base de datos
                com.synapse.data.dao.TareaDAO tareaDAO = new com.synapse.data.dao.TareaDAO();
                List<Usuario> usuariosAsignados = tareaDAO.getUsuariosAsignadosPorTarea(tarea.getIdTarea());
                List<Equipo> equiposAsignados = tareaDAO.getEquiposAsignadosPorTarea(tarea.getIdTarea());

                // Actualizar la tarea con las asignaciones
                tarea.setUsuariosAsignados(usuariosAsignados);
                tarea.setEquiposAsignados(equiposAsignados);

                return datos;
            }

            @Override
            protected void done() {
                try {
                    DatosDialog datos = get();
                    listaEmpleados = datos.empleados;
                    listaEquipos = datos.equipos;

                    // Determinar tipo de asignación actual
                    if (tarea.getUsuariosAsignados() != null && !tarea.getUsuariosAsignados().isEmpty()) {
                        comboTipoAsignacion.setSelectedIndex(0); // Empleado
                    } else if (tarea.getEquiposAsignados() != null && !tarea.getEquiposAsignados().isEmpty()) {
                        comboTipoAsignacion.setSelectedIndex(1); // Equipo
                    }

                    actualizarComboAsignado();
                } catch (Exception e) {
                    e.printStackTrace();
                    Notifications.getInstance().show(Notifications.Type.ERROR,
                            Notifications.Location.TOP_CENTER,
                            "Error al cargar datos");
                }
            }
        };
        worker.execute();
    }

    private void actualizarComboAsignado() {
        comboAsignado.removeAllItems();

        if (comboTipoAsignacion.getSelectedIndex() == 0) {
            // Empleados
            if (listaEmpleados != null) {
                for (Usuario empleado : listaEmpleados) {
                    if (empleado.isActivo()) {
                        comboAsignado.addItem(empleado);
                    }
                }

                // Seleccionar empleado actual
                if (tarea.getUsuariosAsignados() != null && !tarea.getUsuariosAsignados().isEmpty()) {
                    Usuario actual = tarea.getUsuariosAsignados().get(0);
                    for (int i = 0; i < comboAsignado.getItemCount(); i++) {
                        Usuario u = (Usuario) comboAsignado.getItemAt(i);
                        if (u.getIdUsuario() == actual.getIdUsuario()) {
                            comboAsignado.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        } else {
            // Equipos
            if (listaEquipos != null) {
                for (Equipo equipo : listaEquipos) {
                    comboAsignado.addItem(equipo);
                }

                // Seleccionar equipo actual
                if (tarea.getEquiposAsignados() != null && !tarea.getEquiposAsignados().isEmpty()) {
                    Equipo actual = tarea.getEquiposAsignados().get(0);
                    for (int i = 0; i < comboAsignado.getItemCount(); i++) {
                        Equipo e = (Equipo) comboAsignado.getItemAt(i);
                        if (e.getIdEquipo() == actual.getIdEquipo()) {
                            comboAsignado.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void guardarCambios() {
        // Validaciones
        String titulo = txtTitulo.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String fechaTexto = txtFecha.getText().trim();

        if (titulo.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ El título es obligatorio");
            txtTitulo.requestFocus();
            return;
        }

        if (descripcion.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ La descripción es obligatoria");
            txtDescripcion.requestFocus();
            return;
        }

        // Parsear fecha del campo de texto
        java.time.LocalDate localDate = null;
        if (fechaTexto.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ La fecha límite es obligatoria");
            txtFecha.requestFocus();
            return;
        }

        try {
            localDate = java.time.LocalDate.parse(fechaTexto);
        } catch (Exception e) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ Formato de fecha inválido. Use: YYYY-MM-DD");
            txtFecha.requestFocus();
            return;
        }

        if (comboAsignado.getSelectedItem() == null) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ Debes asignar la tarea a alguien");
            return;
        }

        // Actualizar tarea
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        // Configurar fecha límite a las 23:59 en lugar de 00:00
        tarea.setFechaLimite(java.sql.Timestamp.valueOf(localDate.atTime(23, 59)));
        tarea.setIdPrioridad(comboPrioridad.getSelectedIndex() + 1);
        tarea.setIdEstado(comboEstado.getSelectedIndex() + 1);

        // Determinar asignación
        Integer idUsuario = null;
        Integer idEquipo = null;

        if (comboTipoAsignacion.getSelectedIndex() == 0) {
            Usuario usuario = (Usuario) comboAsignado.getSelectedItem();
            idUsuario = usuario.getIdUsuario();
        } else {
            Equipo equipo = (Equipo) comboAsignado.getSelectedItem();
            idEquipo = equipo.getIdEquipo();
        }

        final Integer finalIdUsuario = idUsuario;
        final Integer finalIdEquipo = idEquipo;

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return tareaService.actualizarTareaCompleta(tarea, finalIdUsuario, finalIdEquipo);
            }

            @Override
            protected void done() {
                try {
                    Boolean exito = get();
                    if (exito) {
                        exitoso = true;
                        Notifications.getInstance().show(Notifications.Type.SUCCESS,
                                Notifications.Location.TOP_CENTER,
                                "✅ Tarea actualizada exitosamente");
                        dispose();
                    } else {
                        Notifications.getInstance().show(Notifications.Type.ERROR,
                                Notifications.Location.TOP_CENTER,
                                "❌ Error al actualizar la tarea");
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

    // Clase auxiliar
    private static class DatosDialog {
        List<Usuario> empleados;
        List<Equipo> equipos;
    }
}
