package com.synapse.ui.views.shared;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.synapse.core.models.Equipo;
import com.synapse.core.models.Tarea; // (Necesitarás un modelo Tarea)
import com.synapse.core.models.Usuario;
import com.synapse.data.dao.TareaDAO;
import com.synapse.ui.components.BotonEditar;
import com.synapse.ui.components.BotonRenderer;
import com.synapse.ui.components.FechaLimiteRenderer; // <-- Nuevo
import com.synapse.ui.components.MemberRenderer;
import com.synapse.ui.components.PrioridadPillRenderer;
import com.synapse.ui.components.SimpleTextRenderer;
import com.synapse.ui.components.TableHeaderAlignment;
import com.synapse.ui.components.TaskActionsEditor;
import com.synapse.ui.components.TaskActionsRenderer;
import com.synapse.ui.components.TaskRenderer;
import com.synapse.ui.components.TaskStatusPillRenderer; // <-- Nuevo
import java.security.Timestamp;
import java.time.LocalDate; // <-- Importante
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author FERNANDO
 */
public class formTareas extends javax.swing.JPanel {

    private DefaultTableModel model;
    private javax.swing.table.TableRowSorter<DefaultTableModel> sorter;

    private final Usuario usuarioLogueado;
    private final TareaDAO tareaDAO;

    /**
     * ¡CONSTRUCTOR CORREGIDO! Recibe al usuario (Admin o Gerente) que inició
     * sesión.
     */
    public formTareas(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.tareaDAO = new TareaDAO();
        initComponents();

        // --- (Tu código de estilos... se queda igual) ---
        contenedorTabla.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background");

        lblTitulo.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:bold +5;");

        txtBuscar.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Buscar...");
        txtBuscar.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                new FlatSVGIcon("icons/general/buscar.svg"));
        txtBuscar.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:5,20,5,20;"
                + "Background:$Panel.background");

        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:999;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");
        // ------------------------------------------------

        model = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Tarea", "Prioridad", "Estado", "Asignado", "Fecha Límite", "Acciones" }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Col 5 es "Acciones"
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Tarea.class; // "Tarea" (para TaskRenderer)
                    case 4:
                        return Timestamp.class; // ¡CORREGIDO! (para FechaLimiteRenderer)
                    default:
                        return Object.class;
                }
            }
        };
        tablaTareas.setModel(model);

        // --- (Tus Renderizadores... se quedan igual) ---
        tablaTareas.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(tablaTareas) {
            protected int getAlignment(int column) {
                if (column == 0) {
                    return SwingConstants.LEADING;
                }
                return SwingConstants.CENTER;
            }
        });
        tablaTareas.getColumn("Tarea").setCellRenderer(new TaskRenderer());
        tablaTareas.getColumn("Tarea").setPreferredWidth(250);
        tablaTareas.getColumn("Prioridad").setCellRenderer(new PrioridadPillRenderer());
        tablaTareas.getColumn("Estado").setCellRenderer(new TaskStatusPillRenderer());
        tablaTareas.getColumn("Asignado").setCellRenderer(new SimpleTextRenderer() {
            protected int getAlignment(int column) {
                return SwingConstants.CENTER;
            }
        });
        tablaTareas.getColumn("Fecha Límite").setCellRenderer(new FechaLimiteRenderer());
        tablaTareas.getColumn("Acciones").setCellRenderer(new TaskActionsRenderer());
        tablaTareas.getColumn("Acciones").setCellEditor(new TaskActionsEditor());
        tablaTareas.setRowHeight(60);
        // ------------------------------------------------

        sorter = new javax.swing.table.TableRowSorter<>(model);
        tablaTareas.setRowSorter(sorter);

        // Agregar listener para búsqueda en tiempo real
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                aplicarFiltros();
            }
        });

        // Agregar listeners para filtros de estado y prioridad
        comboEstado.addActionListener(e -> aplicarFiltros());
        comboPrioridad.addActionListener(e -> aplicarFiltros());

        cargarDatosConSwingWorker();
    }

    /**
     * Aplica todos los filtros combinados: búsqueda, estado y prioridad
     */
    private void aplicarFiltros() {
        String textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        String estadoSeleccionado = (String) comboEstado.getSelectedItem();
        String prioridadSeleccionada = (String) comboPrioridad.getSelectedItem();

        javax.swing.RowFilter<DefaultTableModel, Object> filtro = new javax.swing.RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                // Filtro de búsqueda por título
                if (!textoBusqueda.isEmpty()) {
                    Object tareaObj = entry.getValue(0); // Columna 0 = Tarea
                    if (tareaObj instanceof Tarea) {
                        Tarea tarea = (Tarea) tareaObj;
                        String titulo = tarea.getTitulo().toLowerCase();
                        if (!titulo.contains(textoBusqueda)) {
                            return false;
                        }
                    }
                }

                // Filtro por estado
                if (estadoSeleccionado != null && !estadoSeleccionado.equals("Todos")) {
                    String estado = entry.getStringValue(2); // Columna 2 = Estado
                    if (!estado.equalsIgnoreCase(estadoSeleccionado)) {
                        return false;
                    }
                }

                // Filtro por prioridad
                if (prioridadSeleccionada != null && !prioridadSeleccionada.equals("Todos")) {
                    String prioridad = entry.getStringValue(1); // Columna 1 = Prioridad
                    if (!prioridad.equalsIgnoreCase(prioridadSeleccionada)) {
                        return false;
                    }
                }

                return true;
            }
        };

        sorter.setRowFilter(filtro);
    }

    /**
     * ¡NUEVO! Carga los datos en un hilo de fondo.
     */
    private void cargarDatosConSwingWorker() {

        SwingWorker<List<Tarea>, Void> worker = new SwingWorker<List<Tarea>, Void>() {

            @Override
            protected List<Tarea> doInBackground() throws Exception {
                // ESTO SE EJECUTA EN OTRO HILO
                String rol = usuarioLogueado.getRol();

                if (rol.equalsIgnoreCase("administrador")) {
                    return tareaDAO.getAllTareas(); // (Versión optimizada)
                } else if (rol.equalsIgnoreCase("gerente")) {
                    return tareaDAO.getTareasPorGerente(usuarioLogueado.getIdUsuario()); // (Versión optimizada)
                } else {
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                // ESTO SE EJECUTA DE VUELTA EN EL HILO DE UI
                try {
                    List<Tarea> tareas = get(); // Obtenemos el resultado

                    model.setRowCount(0);
                    for (Tarea t : tareas) {
                        model.addRow(new Object[] {
                                t,
                                t.getNombrePrioridad(),
                                t.getNombreEstado(),
                                t.getNombreAsignado(), // ¡Usamos el campo optimizado!
                                t.getFechaLimite(),
                                "Acciones"
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toggleButtonEditor1 = new com.synapse.ui.components.ToggleButtonEditor();
        contenedorTabla = new javax.swing.JPanel();
        panelHeader = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        comboEstado = new javax.swing.JComboBox<>();
        comboPrioridad = new javax.swing.JComboBox<>();
        scroll = new javax.swing.JScrollPane();
        tablaTareas = new javax.swing.JTable();

        setLayout(new java.awt.BorderLayout());

        contenedorTabla.setLayout(new java.awt.BorderLayout());

        panelHeader.setOpaque(false);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitulo.setText("Vista General de Tareas");

        comboEstado.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "Todos", "Pendiente", "Completada", "Archivada" }));

        comboPrioridad
                .setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Todos", "Alta", "Media", "Baja" }));

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
                panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelHeaderLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(panelHeaderLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelHeaderLayout.createSequentialGroup()
                                                .addComponent(txtBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 361,
                                                        Short.MAX_VALUE)
                                                .addGap(18, 18, 18)
                                                .addComponent(comboEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(comboPrioridad, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(lblTitulo))
                                .addGap(18, 18, 18)));
        panelHeaderLayout.setVerticalGroup(
                panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelHeaderLayout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addComponent(lblTitulo)
                                .addGap(18, 18, 18)
                                .addGroup(panelHeaderLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 37,
                                                Short.MAX_VALUE)
                                        .addComponent(comboEstado)
                                        .addComponent(comboPrioridad))
                                .addContainerGap(25, Short.MAX_VALUE)));

        contenedorTabla.add(panelHeader, java.awt.BorderLayout.PAGE_START);

        tablaTareas.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null }
                },
                new String[] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }));
        scroll.setViewportView(tablaTareas);

        contenedorTabla.add(scroll, java.awt.BorderLayout.CENTER);

        add(contenedorTabla, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> comboEstado;
    private javax.swing.JComboBox<String> comboPrioridad;
    private javax.swing.JPanel contenedorTabla;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tablaTareas;
    private com.synapse.ui.components.ToggleButtonEditor toggleButtonEditor1;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
