package com.synapse.ui.views.gerente;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.synapse.core.models.Equipo;
import com.synapse.core.models.Usuario;
import com.synapse.data.dao.EquipoDAO;
import com.synapse.ui.components.ButtonAction;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;
import raven.toast.Notifications;

/**
 * Vista para gestionar equipos del gerente
 * 
 * @author FERNANDO
 */
public class formMisEquipos extends javax.swing.JPanel {

    private final Usuario usuarioLogueado;
    private final EquipoDAO equipoDAO;
    private DefaultTableModel model;
    private javax.swing.table.TableRowSorter<DefaultTableModel> sorter;

    // Componentes UI
    private JPanel panelHeader;
    private JLabel lblTitulo;
    private JTextField txtBuscar;
    private ButtonAction btnCrearEquipo;
    private JScrollPane scroll;
    private JTable tablaEquipos;

    public formMisEquipos(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.equipoDAO = new EquipoDAO();

        initComponents();
        configurarEstilos();
        configurarTabla();
        cargarEquipos();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Panel Header
        panelHeader = new JPanel(new MigLayout("fillx, insets 20", "[grow][]", "[]20[]"));

        lblTitulo = new JLabel("Mis Equipos");
        lblTitulo.putClientProperty(FlatClientProperties.STYLE, "font:bold +5");

        txtBuscar = new JTextField();
        txtBuscar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar equipo...");
        txtBuscar.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                new FlatSVGIcon("icons/general/buscar.svg"));

        btnCrearEquipo = new ButtonAction();
        btnCrearEquipo.setText("Crear Equipo");
        btnCrearEquipo.addActionListener(e -> crearEquipo());

        panelHeader.add(lblTitulo, "wrap");
        panelHeader.add(txtBuscar, "width 300!, split 2");
        panelHeader.add(btnCrearEquipo, "width 150!");

        // Tabla
        tablaEquipos = new JTable();
        scroll = new JScrollPane(tablaEquipos);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        add(panelHeader, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void configurarEstilos() {
        txtBuscar.putClientProperty(FlatClientProperties.STYLE,
                "arc:15;borderWidth:0;focusWidth:0;innerFocusWidth:0;margin:5,20,5,20;Background:$Panel.background");

        scroll.putClientProperty(FlatClientProperties.STYLE,
                "arc:25;background:$Table.background");

        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "trackArc:999;trackInsets:3,3,3,3;thumbInsets:3,3,3,3;background:$Table.background");
    }

    private void configurarTabla() {
        model = new DefaultTableModel(
                new Object[][] {},
                new String[] { "ID", "Nombre", "Descripción", "Miembros", "Acciones" }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Solo columna Acciones
            }
        };

        tablaEquipos.setModel(model);
        tablaEquipos.setRowHeight(50);
        tablaEquipos.getTableHeader().setReorderingAllowed(false);

        // Ocultar columna ID
        tablaEquipos.getColumnModel().getColumn(0).setMinWidth(0);
        tablaEquipos.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaEquipos.getColumnModel().getColumn(0).setWidth(0);

        // Anchos de columnas
        tablaEquipos.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaEquipos.getColumnModel().getColumn(2).setPreferredWidth(300);
        tablaEquipos.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaEquipos.getColumnModel().getColumn(4).setPreferredWidth(150);

        // Centrar columna Miembros
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tablaEquipos.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        // Configurar búsqueda
        sorter = new javax.swing.table.TableRowSorter<>(model);
        tablaEquipos.setRowSorter(sorter);

        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String texto = txtBuscar.getText().trim();
                if (texto.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + texto, 1, 2));
                }
            }
        });

        // Botones de acciones
        tablaEquipos.getColumnModel().getColumn(4).setCellRenderer(new AccionesRenderer());
        tablaEquipos.getColumnModel().getColumn(4).setCellEditor(new AccionesEditor());
    }

    private void cargarEquipos() {
        SwingWorker<java.util.List<Equipo>, Void> worker = new SwingWorker<java.util.List<Equipo>, Void>() {
            @Override
            protected java.util.List<Equipo> doInBackground() throws Exception {
                return equipoDAO.getEquiposPorLider(usuarioLogueado.getIdUsuario());
            }

            @Override
            protected void done() {
                try {
                    java.util.List<Equipo> equipos = get();
                    model.setRowCount(0);

                    for (Equipo equipo : equipos) {
                        int cantidadMiembros = equipoDAO.contarMiembros(equipo.getIdEquipo());
                        model.addRow(new Object[] {
                                equipo.getIdEquipo(),
                                equipo.getNombre(),
                                equipo.getDescripcion(),
                                cantidadMiembros,
                                "" // Columna de acciones
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Notifications.getInstance().show(Notifications.Type.ERROR,
                            Notifications.Location.TOP_CENTER,
                            "Error al cargar equipos");
                }
            }
        };
        worker.execute();
    }

    private void crearEquipo() {
        com.synapse.ui.views.admin.dialogCrearEquipo dialog = new com.synapse.ui.views.admin.dialogCrearEquipo(
                (Frame) SwingUtilities.getWindowAncestor(this),
                usuarioLogueado);
        dialog.setVisible(true);

        if (dialog.isCreadoConExito()) {
            cargarEquipos(); // Recargar tabla
        }
    }

    private void gestionarMiembros(int idEquipo) {
        GestionarMiembrosDialog dialog = new GestionarMiembrosDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                idEquipo);
        dialog.setVisible(true);

        // Recargar para actualizar contador de miembros
        cargarEquipos();
    }

    // Renderer para botones de acciones
    private class AccionesRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private ButtonAction btnGestionar;

        public AccionesRenderer() {
            setLayout(new MigLayout("insets 5, fillx", "[grow]"));
            btnGestionar = new ButtonAction();
            btnGestionar.setText("Gestionar Miembros");
            add(btnGestionar, "growx");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Editor para botones de acciones
    private class AccionesEditor extends DefaultCellEditor {
        private JPanel panel;
        private ButtonAction btnGestionar;
        private int currentRow;

        public AccionesEditor() {
            super(new JCheckBox());
            panel = new JPanel(new MigLayout("insets 5, fillx", "[grow]"));
            btnGestionar = new ButtonAction();
            btnGestionar.setText("Gestionar Miembros");

            btnGestionar.addActionListener(e -> {
                int idEquipo = (int) model.getValueAt(currentRow, 0);
                gestionarMiembros(idEquipo);
                fireEditingStopped();
            });

            panel.add(btnGestionar, "growx");
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
