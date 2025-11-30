package com.synapse.ui.views.admin;

import com.synapse.ui.components.BotonEditar;
import com.synapse.ui.components.BotonRenderer;
import com.synapse.ui.components.TableHeaderAlignment;
import com.synapse.ui.components.ToggleButtonEditor;
import com.synapse.ui.components.ToggleButtonRenderer;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.synapse.ui.components.SimpleUserRenderer;
import com.synapse.core.models.Usuario;
import com.synapse.data.dao.UsuarioDAO;
import com.synapse.ui.components.RolPillRenderer;
import com.synapse.ui.components.SimpleTextRenderer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import javax.swing.SwingWorker;

/**
 *
 * @author FERNANDO
 */
public class formUsuarios extends javax.swing.JPanel {

    private DefaultTableModel model;
    private javax.swing.table.TableRowSorter<DefaultTableModel> sorter;

    // --- CONECTADO AL BACKEND ---
    private final UsuarioDAO usuarioDAO;
    // ----------------------------

    public formUsuarios() {
        // --- CONECTADO AL BACKEND ---
        this.usuarioDAO = new UsuarioDAO(); // Instanciamos el DAO
        // ----------------------------

        initComponents();

        // --- (Tu código de estilos... se queda igual) ---
        tablaUsuarios.setRowHeight(50); // Reducido de 100 a 50 (sin fotos)
        DefaultTableCellRenderer rendererTexto = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 2) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEADING);
                }
                return this;
            }
        };
        tablaUsuarios.setDefaultRenderer(Object.class, rendererTexto);
        tablaUsuarios.setFillsViewportHeight(true);
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
        // ------------------------------------------------

        model = new DefaultTableModel(
                new Object[][] {},
                new String[] { "Miembro", "Rol", "Codigo", "Estado", "Acciones" }) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Usuario.class; // Para MemberRenderer
                    case 3:
                        return Boolean.class; // ¡CLAVE! Para el ToggleButton
                    default:
                        return Object.class;
                }
            }
        };
        tablaUsuarios.setModel(model);

        // --- (Tu código de Renderizadores... se queda igual) ---
        tablaUsuarios.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "height:30;"
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background;"
                + "font:bold;");
        tablaUsuarios.putClientProperty(FlatClientProperties.STYLE, ""
                + "rowHeight:60;" // <-- ¡IMPORTANTE! Aumentamos el alto
                + "showHorizontalLines:true;"
                + "intercellSpacing:0,1;"
                + "cellFocusColor:$TableHeader.hoverBackground;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:999;"
                + "trackInsets:3,3,3,3;"
                + "thumbInsets:3,3,3,3;"
                + "background:$Table.background;");
        tablaUsuarios.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(tablaUsuarios));
        tablaUsuarios.getColumn("Miembro").setCellRenderer(new SimpleUserRenderer());
        tablaUsuarios.getColumn("Miembro").setPreferredWidth(250);
        SimpleTextRenderer textRenderer = new SimpleTextRenderer();
        tablaUsuarios.setDefaultRenderer(Object.class, textRenderer);
        tablaUsuarios.setDefaultRenderer(String.class, textRenderer);
        tablaUsuarios.getColumn("Rol").setCellRenderer(new RolPillRenderer());
        tablaUsuarios.getColumn("Rol").setPreferredWidth(100);
        tablaUsuarios.getColumn("Codigo").setCellRenderer(textRenderer);
        tablaUsuarios.getColumn("Codigo").setPreferredWidth(100);
        tablaUsuarios.getColumn("Estado").setCellRenderer(new ToggleButtonRenderer());
        tablaUsuarios.getColumn("Estado").setCellEditor(new ToggleButtonEditor());
        tablaUsuarios.getColumn("Estado").setPreferredWidth(50);
        tablaUsuarios.getColumn("Acciones").setCellRenderer(new BotonRenderer());
        tablaUsuarios.getColumn("Acciones").setCellEditor(new BotonEditar());
        tablaUsuarios.getColumn("Acciones").setPreferredWidth(80);
        // ------------------------------------------------

        // Configurar TableRowSorter para búsqueda
        sorter = new javax.swing.table.TableRowSorter<>(model);
        tablaUsuarios.setRowSorter(sorter);

        // Agregar listener para búsqueda en tiempo real
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String texto = txtBuscar.getText().trim();
                if (texto.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + texto, 0, 1));
                }
            }
        });

        cargarDatosConSwingWorker();
    }

    private void cargarDatosConSwingWorker() {
        // ... (el inicio de tu SwingWorker) ...

        SwingWorker<List<Usuario>, Void> worker = new SwingWorker<List<Usuario>, Void>() {

            @Override
            protected List<Usuario> doInBackground() throws Exception {
                return usuarioDAO.getUsuarios();
            }

            @Override
            protected void done() {
                try {
                    List<Usuario> lista = get();

                    // --- ¡SOLUCIÓN A LOS DUPLICADOS! ---
                    // Limpia la tabla ANTES de añadir las nuevas filas.
                    model.setRowCount(0);
                    // ---------------------------------

                    for (Usuario u : lista) {
                        String rol = u.getRol();
                        String codigo = (rol != null && rol.equalsIgnoreCase("empleado")) ? u.getCodigoEmpleado()
                                : "N/A";
                        boolean estadoActivo = u.isActivo();

                        model.addRow(new Object[] {
                                u, rol, codigo, estadoActivo, "Editar"
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

        contenedorTabla = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        tablaUsuarios = new javax.swing.JTable();
        panelHeader = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        buttonAction1 = new com.synapse.ui.components.ButtonAction();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setLayout(new java.awt.BorderLayout());

        contenedorTabla.setLayout(new java.awt.BorderLayout());

        scroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tablaUsuarios.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tablaUsuarios.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null },
                        { null, null, null, null, null }
                },
                new String[] {
                        "Nombre", "Email", "Rol", "Estado", "Acciones"
                }) {
            boolean[] canEdit = new boolean[] {
                    false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tablaUsuarios.setOpaque(false);
        tablaUsuarios.setShowGrid(false);
        tablaUsuarios.setShowHorizontalLines(true);
        tablaUsuarios.getTableHeader().setReorderingAllowed(false);
        scroll.setViewportView(tablaUsuarios);
        if (tablaUsuarios.getColumnModel().getColumnCount() > 0) {
            tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(100);
        }

        contenedorTabla.add(scroll, java.awt.BorderLayout.CENTER);

        panelHeader.setOpaque(false);

        lblTitulo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTitulo.setText("Gestion de Usuarios");

        buttonAction1.setBackground(new java.awt.Color(0, 0, 0));
        buttonAction1.setForeground(new java.awt.Color(255, 255, 255));
        buttonAction1.setText("Crear Usuario");
        buttonAction1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAction1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
                panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelHeaderLayout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addGroup(panelHeaderLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelHeaderLayout.createSequentialGroup()
                                                .addComponent(lblTitulo)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(panelHeaderLayout.createSequentialGroup()
                                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 199,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(buttonAction1, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)))));
        panelHeaderLayout.setVerticalGroup(
                panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelHeaderLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 33,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(panelHeaderLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 37,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(buttonAction1, javax.swing.GroupLayout.PREFERRED_SIZE, 36,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(21, Short.MAX_VALUE)));

        contenedorTabla.add(panelHeader, java.awt.BorderLayout.NORTH);

        add(contenedorTabla, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void buttonAction1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_buttonAction1ActionPerformed
        java.awt.Frame parent = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
        CrearUsuarioDialog crearUsuario = new CrearUsuarioDialog(parent);
        crearUsuario.pack();
        crearUsuario.setLocationRelativeTo(parent);
        crearUsuario.setVisible(true);

        // Recargar tabla si se creó exitosamente
        if (crearUsuario.isExitoso()) {
            cargarDatosConSwingWorker();
        }
    }// GEN-LAST:event_buttonAction1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.synapse.ui.components.ButtonAction buttonAction1;
    private javax.swing.JPanel contenedorTabla;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable tablaUsuarios;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
