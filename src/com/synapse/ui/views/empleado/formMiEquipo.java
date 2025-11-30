package com.synapse.ui.views.empleado;

import com.formdev.flatlaf.FlatClientProperties;
import com.synapse.core.models.Equipo;
import com.synapse.core.models.Tarea;
import com.synapse.core.models.Usuario;
import com.synapse.data.dao.EquipoDAO;
import com.synapse.data.dao.TareaDAO;
import com.synapse.ui.components.TaskCardEmpleado;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author FERNANDO
 */
public class formMiEquipo extends javax.swing.JPanel {

    private JPanel panelContenidoEquipos;

    // --- CONECTADO AL BACKEND ---
    private final Usuario usuarioLogueado;
    private final EquipoDAO equipoDAO;
    private final TareaDAO tareaDAO;
    // ----------------------------

    private class DatosEquiposYTareas {

        List<Equipo> equipos;
        Map<Integer, List<Tarea>> mapaTareas;
    }

    /**
     * Constructor modificado para recibir al usuario
     */
    public formMiEquipo(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.equipoDAO = new EquipoDAO();
        this.tareaDAO = new TareaDAO();

        initComponents();
        initPersonalizado();
        cargarMisEquiposConSwingWorker();
    }
    // ----------------------------

    private void initPersonalizado() {
        panelContenidoEquipos = new JPanel(new MigLayout("wrap 1, fillx, insets 10", "[grow, fill]"));
        panelContenidoEquipos.setOpaque(false);

        scrollEquipos.setViewportView(panelContenidoEquipos);
        scrollEquipos.setOpaque(false);
        scrollEquipos.getViewport().setOpaque(false);
        scrollEquipos.setBorder(null);
        scrollEquipos.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollEquipos.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                + "trackArc:999; trackInsets:3,3,3,3; thumbInsets:3,3,3,3;");

        lblTituloNoEquipos.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");
        lblSubtituloNoEquipos.putClientProperty(FlatClientProperties.STYLE, "font: -1");

        // Corregimos el texto del título
        jLabel1.putClientProperty(FlatClientProperties.STYLE, "font:bold +5;");
        jLabel1.setText("Mis Equipos");
    }

    /**
     * Método corregido para usar los DAOs en lugar de DataSimulada
     */
    private void cargarMisEquiposConSwingWorker() {
        CardLayout cl = (CardLayout) panelContenido.getLayout();
        // (Opcional: puedes crear una "cardCargando" y mostrarla aquí)

        SwingWorker<DatosEquiposYTareas, Void> worker = new SwingWorker<DatosEquiposYTareas, Void>() {

            @Override
            protected DatosEquiposYTareas doInBackground() throws Exception {
                // ESTO SE EJECUTA EN OTRO HILO
                DatosEquiposYTareas datos = new DatosEquiposYTareas();

                // 1. Obtenemos los equipos
                datos.equipos = equipoDAO.getEquiposPorUsuario(usuarioLogueado.getIdUsuario());

                // 2. Para CADA equipo, obtenemos sus tareas (Evitamos N+1 anidado)
                datos.mapaTareas = new HashMap<>();
                for (Equipo equipo : datos.equipos) {
                    List<Tarea> tareasDelEquipo = tareaDAO.getTareasPorEquipo(equipo.getIdEquipo());
                    datos.mapaTareas.put(equipo.getIdEquipo(), tareasDelEquipo);
                }
                return datos;
            }

            @Override
            protected void done() {
                // ESTO SE EJECUTA DE VUELTA EN EL HILO DE UI
                try {
                    DatosEquiposYTareas datos = get();
                    panelContenidoEquipos.removeAll();

                    if (datos.equipos == null || datos.equipos.isEmpty()) {
                        cl.show(panelContenido, "cardNoEquipos");
                    } else {
                        for (Equipo equipo : datos.equipos) {
                            JPanel equipoBlockPanel = new JPanel(new MigLayout("wrap 1, fillx, insets 10", "[grow, fill]"));
                            equipoBlockPanel.setBorder(BorderFactory.createTitledBorder(equipo.getNombre()));
                            equipoBlockPanel.putClientProperty(FlatClientProperties.STYLE, "background: $Panel.background; arc: 15;");

                            List<Tarea> tareasDelEquipo = datos.mapaTareas.get(equipo.getIdEquipo());

                            if (tareasDelEquipo.isEmpty()) {
                                JLabel lblNoTareas = new JLabel("Este equipo no tiene tareas asignadas.");
                                lblNoTareas.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground;");
                                equipoBlockPanel.add(lblNoTareas, "al center, gapbottom 10");
                            } else {
                                for (Tarea tarea : tareasDelEquipo) {
                                    equipoBlockPanel.add(new TaskCardEmpleado(tarea, tareaDAO), "growx, gaptop 10");
                                }
                            }
                            panelContenidoEquipos.add(equipoBlockPanel, "growx, gaptop 10");
                        }
                        cl.show(panelContenido, "cardGridEquipos");
                    }
                    panelContenidoEquipos.revalidate();
                    panelContenidoEquipos.repaint();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelHeader = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panelContenido = new javax.swing.JPanel();
        scrollEquipos = new javax.swing.JScrollPane();
        panelGridEquipos = new javax.swing.JPanel();
        panelNoEquipos = new javax.swing.JPanel();
        panelCCTA = new javax.swing.JPanel();
        panelTexto = new javax.swing.JPanel();
        lblTituloNoEquipos = new javax.swing.JLabel();
        lblSubtituloNoEquipos = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("MIs Equipos");

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addContainerGap(565, Short.MAX_VALUE))
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel1)
                .addContainerGap(59, Short.MAX_VALUE))
        );

        add(panelHeader, java.awt.BorderLayout.PAGE_START);

        panelContenido.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout panelGridEquiposLayout = new javax.swing.GroupLayout(panelGridEquipos);
        panelGridEquipos.setLayout(panelGridEquiposLayout);
        panelGridEquiposLayout.setHorizontalGroup(
            panelGridEquiposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 698, Short.MAX_VALUE)
        );
        panelGridEquiposLayout.setVerticalGroup(
            panelGridEquiposLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 453, Short.MAX_VALUE)
        );

        scrollEquipos.setViewportView(panelGridEquipos);

        panelContenido.add(scrollEquipos, "cardGridEquipos");

        panelNoEquipos.setLayout(new java.awt.GridBagLayout());

        panelCCTA.setLayout(new java.awt.BorderLayout());

        lblTituloNoEquipos.setText("Aun no perteneces a un equipo");

        lblSubtituloNoEquipos.setText("Pronto seras asignado a uno");

        javax.swing.GroupLayout panelTextoLayout = new javax.swing.GroupLayout(panelTexto);
        panelTexto.setLayout(panelTextoLayout);
        panelTextoLayout.setHorizontalGroup(
            panelTextoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTextoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTextoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloNoEquipos)
                    .addComponent(lblSubtituloNoEquipos))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTextoLayout.setVerticalGroup(
            panelTextoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTextoLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lblTituloNoEquipos)
                .addGap(18, 18, 18)
                .addComponent(lblSubtituloNoEquipos)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        panelCCTA.add(panelTexto, java.awt.BorderLayout.CENTER);

        panelNoEquipos.add(panelCCTA, new java.awt.GridBagConstraints());

        panelContenido.add(panelNoEquipos, "cardNoEquipos\n");

        add(panelContenido, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblSubtituloNoEquipos;
    private javax.swing.JLabel lblTituloNoEquipos;
    private javax.swing.JPanel panelCCTA;
    private javax.swing.JPanel panelContenido;
    private javax.swing.JPanel panelGridEquipos;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelNoEquipos;
    private javax.swing.JPanel panelTexto;
    private javax.swing.JScrollPane scrollEquipos;
    // End of variables declaration//GEN-END:variables
}
