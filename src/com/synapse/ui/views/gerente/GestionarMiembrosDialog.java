package com.synapse.ui.views.gerente;

import com.formdev.flatlaf.FlatClientProperties;
import com.synapse.core.models.Equipo;
import com.synapse.core.models.Usuario;
import com.synapse.data.dao.EquipoDAO;
import com.synapse.data.dao.UsuarioDAO;
import com.synapse.ui.components.ButtonAction;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.List;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

/**
 * Dialog para gestionar miembros de un equipo
 */
public class GestionarMiembrosDialog extends JDialog {

    private final EquipoDAO equipoDAO;
    private final UsuarioDAO usuarioDAO;
    private final int idEquipo;
    private Equipo equipo;

    // Componentes
    private JLabel lblNombreEquipo;
    private DefaultListModel<Usuario> modelMiembros;
    private JList<Usuario> listaMiembros;
    private JComboBox<Usuario> comboUsuarios;
    private ButtonAction btnAgregar;
    private ButtonAction btnEliminar;
    private ButtonAction btnCerrar;

    public GestionarMiembrosDialog(Frame parent, int idEquipo) {
        super(parent, "Gestionar Miembros", true);
        this.idEquipo = idEquipo;
        this.equipoDAO = new EquipoDAO();
        this.usuarioDAO = new UsuarioDAO();

        initComponents();
        configurarEstilos();
        cargarDatos();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 20", "[grow]", "[]10[]10[]10[]20[]"));

        // Título
        JLabel lblTitulo = new JLabel("Gestionar Miembros del Equipo");
        lblTitulo.putClientProperty(FlatClientProperties.STYLE, "font:bold +3");

        lblNombreEquipo = new JLabel();
        lblNombreEquipo.putClientProperty(FlatClientProperties.STYLE, "font:italic");

        // Lista de miembros actuales
        JLabel lblMiembros = new JLabel("Miembros actuales:");
        modelMiembros = new DefaultListModel<>();
        listaMiembros = new JList<>(modelMiembros);
        listaMiembros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollMiembros = new JScrollPane(listaMiembros);

        // Panel para agregar miembros
        JPanel panelAgregar = new JPanel(new MigLayout("fillx", "[grow][]"));
        JLabel lblAgregar = new JLabel("Agregar miembro:");
        comboUsuarios = new JComboBox<>();
        btnAgregar = new ButtonAction();
        btnAgregar.setText("Agregar");
        btnAgregar.addActionListener(e -> agregarMiembro());

        panelAgregar.add(lblAgregar, "wrap");
        panelAgregar.add(comboUsuarios, "growx, split 2");
        panelAgregar.add(btnAgregar, "width 100!");

        // Botones
        JPanel panelBotones = new JPanel(new MigLayout("fillx", "[grow][][]"));
        btnEliminar = new ButtonAction();
        btnEliminar.setText("Eliminar Seleccionado");
        btnEliminar.addActionListener(e -> eliminarMiembro());

        btnCerrar = new ButtonAction();
        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(new JLabel(), "grow");
        panelBotones.add(btnEliminar, "width 150!");
        panelBotones.add(btnCerrar, "width 100!");

        // Agregar componentes
        panel.add(lblTitulo, "wrap");
        panel.add(lblNombreEquipo, "wrap");
        panel.add(lblMiembros, "wrap");
        panel.add(scrollMiembros, "growx, height 200!, wrap");
        panel.add(panelAgregar, "growx, wrap");
        panel.add(panelBotones, "growx");

        setContentPane(panel);
    }

    private void configurarEstilos() {
        comboUsuarios.putClientProperty(FlatClientProperties.STYLE,
                "arc:10;borderWidth:1");
    }

    private void cargarDatos() {
        SwingWorker<DatosEquipo, Void> worker = new SwingWorker<DatosEquipo, Void>() {
            @Override
            protected DatosEquipo doInBackground() throws Exception {
                DatosEquipo datos = new DatosEquipo();
                datos.equipo = equipoDAO.getEquipoPorId(idEquipo);
                datos.miembros = equipoDAO.getMiembros(idEquipo);
                datos.todosUsuarios = usuarioDAO.getUsuarios();
                return datos;
            }

            @Override
            protected void done() {
                try {
                    DatosEquipo datos = get();
                    equipo = datos.equipo;

                    if (equipo != null) {
                        lblNombreEquipo.setText("Equipo: " + equipo.getNombre());
                    }

                    // Cargar miembros
                    modelMiembros.clear();
                    for (Usuario miembro : datos.miembros) {
                        modelMiembros.addElement(miembro);
                    }

                    // Cargar usuarios disponibles (excluir miembros actuales)
                    comboUsuarios.removeAllItems();
                    for (Usuario usuario : datos.todosUsuarios) {
                        boolean esMiembro = datos.miembros.stream()
                                .anyMatch(m -> m.getIdUsuario() == usuario.getIdUsuario());
                        if (!esMiembro && usuario.isActivo()) {
                            comboUsuarios.addItem(usuario);
                        }
                    }
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

    private void agregarMiembro() {
        Usuario usuario = (Usuario) comboUsuarios.getSelectedItem();
        if (usuario == null) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ Selecciona un usuario");
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return equipoDAO.agregarMiembro(idEquipo, usuario.getIdUsuario());
            }

            @Override
            protected void done() {
                try {
                    Boolean exito = get();
                    if (exito) {
                        Notifications.getInstance().show(Notifications.Type.SUCCESS,
                                Notifications.Location.TOP_CENTER,
                                "✅ Miembro agregado");
                        cargarDatos(); // Recargar
                    } else {
                        Notifications.getInstance().show(Notifications.Type.ERROR,
                                Notifications.Location.TOP_CENTER,
                                "❌ Error al agregar miembro");
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

    private void eliminarMiembro() {
        Usuario usuario = listaMiembros.getSelectedValue();
        if (usuario == null) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                    Notifications.Location.TOP_CENTER,
                    "⚠️ Selecciona un miembro para eliminar");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + usuario.getNombre() + " del equipo?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return equipoDAO.removerMiembro(idEquipo, usuario.getIdUsuario());
            }

            @Override
            protected void done() {
                try {
                    Boolean exito = get();
                    if (exito) {
                        Notifications.getInstance().show(Notifications.Type.SUCCESS,
                                Notifications.Location.TOP_CENTER,
                                "✅ Miembro eliminado");
                        cargarDatos(); // Recargar
                    } else {
                        Notifications.getInstance().show(Notifications.Type.ERROR,
                                Notifications.Location.TOP_CENTER,
                                "❌ Error al eliminar miembro");
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

    // Clase auxiliar para datos
    private static class DatosEquipo {
        Equipo equipo;
        List<Usuario> miembros;
        List<Usuario> todosUsuarios;
    }
}
