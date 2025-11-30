package com.synapse.ui.views.empleado;

import com.synapse.ui.components.Card;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.toast.Notifications;
import com.formdev.flatlaf.extras.FlatSVGIcon.ColorFilter;
import java.awt.Component;

/**
 *
 * @author Raven
 */
public class DashbordEmpleado extends javax.swing.JPanel {

    private final com.synapse.core.models.Usuario usuarioLogueado;
    private final com.synapse.data.dao.TareaDAO tareaDAO;

    // Credenciales para los envios de correo
    public DashbordEmpleado(com.synapse.core.models.Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.tareaDAO = new com.synapse.data.dao.TareaDAO();

        initComponents();

        panelFilaIndicadores.setLayout(new GridLayout(1, 4, 30, 0));
        panelResumenSistema.setLayout(new javax.swing.BoxLayout(panelResumenSistema, javax.swing.BoxLayout.Y_AXIS));
        panelEstadoTareas.setLayout(new javax.swing.BoxLayout(panelEstadoTareas, javax.swing.BoxLayout.Y_AXIS));

        lb.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$h1.font");
        card1.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background");
        card2.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background");
        card3.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background");
        card4.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background");
        panelResumenSistema.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background;"
                + "border:15,15,15,15");
        panelEstadoTareas.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background;"
                + "border:15,15,15,15");

        cargarDatosDashboard();
    }

    private void cargarDatosDashboard() {
        int iconSize = 24;

        ImageIcon iconUsuario = new FlatSVGIcon("apptareas/icon/user.svg", iconSize, iconSize);
        ImageIcon iconTareas = new FlatSVGIcon("apptareas/icon/chart-line-green.svg", iconSize, iconSize);
        ImageIcon iconPendientes = new FlatSVGIcon("apptareas/icon/box-orange.svg", iconSize, iconSize);
        ImageIcon iconVencidas = new FlatSVGIcon("apptareas/icon/alert-triangle-red.svg", iconSize, iconSize);

        // Cargar datos reales del backend con SwingWorker
        javax.swing.SwingWorker<DashboardData, Void> worker = new javax.swing.SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                java.util.List<com.synapse.core.models.Tarea> tareas = tareaDAO
                        .getTareasPorUsuario(usuarioLogueado.getIdUsuario());

                int total = tareas.size();
                int pendientes = 0;
                int enProgreso = 0;
                int completadas = 0;
                int vencidas = 0;

                java.time.LocalDate hoy = java.time.LocalDate.now();

                for (com.synapse.core.models.Tarea t : tareas) {
                    // Contar por estado
                    if (t.getIdEstado() == 1)
                        pendientes++;
                    else if (t.getIdEstado() == 2)
                        enProgreso++;
                    else if (t.getIdEstado() == 3)
                        completadas++;

                    // Contar vencidas (fecha límite pasada y no completada)
                    if (t.getFechaLimite() != null && t.getIdEstado() != 3) {
                        java.time.LocalDate fechaLimite = t.getFechaLimite().toLocalDateTime().toLocalDate();
                        if (fechaLimite.isBefore(hoy)) {
                            vencidas++;
                        }
                    }
                }

                return new DashboardData(total, pendientes, enProgreso, completadas, vencidas);
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();

                    // Configurar las cards con datos reales
                    card1.setTitle("Mis Tareas");
                    card1.setValue(data.total);
                    card1.setIcon(iconTareas);

                    card2.setTitle("Pendientes");
                    card2.setValue(data.pendientes);
                    card2.setIcon(iconPendientes);

                    card3.setTitle("En Progreso");
                    card3.setValue(data.enProgreso);
                    card3.setIcon(iconUsuario);

                    card4.setTitle("Vencidas");
                    card4.setValue(data.vencidas);
                    card4.setIcon(iconVencidas);

                    // Actualizar paneles de resumen
                    actualizarPanelesResumen(data);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void actualizarPanelesResumen(DashboardData data) {
        panelResumenSistema.removeAll();
        panelEstadoTareas.removeAll();

        // Título de la tarjeta izquierda
        JLabel titleResumen = new JLabel("Mis Tareas");
        titleResumen.setFont(titleResumen.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        titleResumen.setAlignmentX(LEFT_ALIGNMENT);
        titleResumen.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panelResumenSistema.add(titleResumen);

        // Datos del Resumen
        panelResumenSistema.add(crearFilaResumen("Total de Tareas:", data.total, null));
        panelResumenSistema.add(crearFilaResumen("Pendientes:", data.pendientes, "#FF9800"));
        panelResumenSistema.add(crearFilaResumen("En Progreso:", data.enProgreso, "#2196F3"));
        panelResumenSistema.add(crearFilaResumen("Completadas:", data.completadas, "#4CAF50"));

        // Título de la tarjeta derecha
        JLabel titleEstado = new JLabel("Estado de Tareas");
        titleEstado.setFont(titleEstado.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        titleEstado.setAlignmentX(LEFT_ALIGNMENT);
        titleEstado.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panelEstadoTareas.add(titleEstado);

        // Datos del Estado de Tareas
        panelEstadoTareas.add(crearFilaResumen("Completadas:", data.completadas, "#7B68EE"));
        panelEstadoTareas.add(crearFilaResumen("En Progreso:", data.enProgreso, null));
        panelEstadoTareas.add(crearFilaResumen("Pendientes:", data.pendientes, null));
        panelEstadoTareas.add(crearFilaResumen("Vencidas:", data.vencidas, "#FF5252"));

        // Forzar el redibujado
        panelResumenSistema.revalidate();
        panelResumenSistema.repaint();
        panelEstadoTareas.revalidate();
        panelEstadoTareas.repaint();
    }

    // Clase interna para datos del dashboard
    private static class DashboardData {
        int total, pendientes, enProgreso, completadas, vencidas;

        DashboardData(int total, int pendientes, int enProgreso, int completadas, int vencidas) {
            this.total = total;
            this.pendientes = pendientes;
            this.enProgreso = enProgreso;
            this.completadas = completadas;
            this.vencidas = vencidas;
        }
    }

    private JPanel crearFilaResumen(String etiqueta, int valor, String colorStyle) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        // Etiqueta (Ej: "Usuarios Totales:") -> WEST
        JLabel lblEtiqueta = new JLabel(etiqueta);
        fila.add(lblEtiqueta, BorderLayout.WEST);

        // Espaciador en CENTER para separar
        JPanel espaciador = new JPanel();
        espaciador.setOpaque(false);
        fila.add(espaciador, BorderLayout.CENTER);

        // -------------------------------------------------------------------------
        // LÓGICA DE LA PÍLDORA (Capsule)
        // -------------------------------------------------------------------------
        // 1. Definir los colores base y de contraste
        String finalBackgroundColor = (colorStyle != null)
                ? colorStyle
                : "$Button.background";

        Color finalForegroundColor = (colorStyle != null)
                ? Color.WHITE
                : UIManager.getColor("Label.foreground");

        // 2. Crear el JLabel (el texto)
        JLabel lblValor = new JLabel(String.valueOf(valor));
        lblValor.setForeground(finalForegroundColor);

        // 3. Crear el JPanel que será el FONDO de la PÍLDORA
        JPanel fondoPill = new JPanel(new java.awt.BorderLayout());

        // 💡 CORRECCIÓN CRUCIAL: Forzar la opacidad del JPanel para que pinte el fondo.
        fondoPill.setOpaque(true); // Cambiado a true

        // Aplicamos estilos FlatLaf
        fondoPill.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:12;"
                + "background:" + finalBackgroundColor + ";"
                + "border: 2,8,2,8;");

        // 4. Integrar
        fondoPill.add(lblValor, java.awt.BorderLayout.CENTER);

        // 5. Contenedor de Valor Final
        JPanel valorContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        valorContainer.setOpaque(false); // Este contenedor debe seguir siendo transparente
        valorContainer.add(fondoPill);

        fila.add(valorContainer, BorderLayout.EAST);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        return fila;
    }

    private JPanel crearPillLabel(int valor, String colorStyle) {
        String finalBackgroundColor = (colorStyle != null) ? colorStyle : "$Button.background";
        Color finalForegroundColor = (colorStyle != null) ? Color.WHITE : UIManager.getColor("Label.foreground");

        JLabel lblValor = new JLabel(String.valueOf(valor));
        lblValor.setForeground(finalForegroundColor);
        lblValor.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel fondoPill = new JPanel(new java.awt.BorderLayout());
        fondoPill.setOpaque(true); // ¡CRUCIAL para ver el fondo!

        fondoPill.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:12;"
                + "background:" + finalBackgroundColor + ";"
                + "border: 2,8,2,8;");
        fondoPill.add(lblValor, java.awt.BorderLayout.CENTER);
        fondoPill.setMaximumSize(new Dimension(80, 25)); // Limitar tamaño de la píldora
        fondoPill.setPreferredSize(new Dimension(80, 25)); // Limitar tamaño de la píldora

        // Envuelve la píldora en un panel FlowLayout para que no ocupe todo el ancho
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(fondoPill);
        return wrapper;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        lb = new javax.swing.JLabel();
        panelFilaIndicadores = new javax.swing.JPanel();
        card1 = new com.synapse.ui.components.Card();
        card2 = new com.synapse.ui.components.Card();
        card3 = new com.synapse.ui.components.Card();
        card4 = new com.synapse.ui.components.Card();
        panelFilaResumenEstado = new javax.swing.JPanel();
        panelResumenSistema = new javax.swing.JPanel();
        panelEstadoTareas = new javax.swing.JPanel();

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb.setText("Dashboard");

        javax.swing.GroupLayout panelFilaIndicadoresLayout = new javax.swing.GroupLayout(panelFilaIndicadores);
        panelFilaIndicadores.setLayout(panelFilaIndicadoresLayout);
        panelFilaIndicadoresLayout.setHorizontalGroup(
                panelFilaIndicadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelFilaIndicadoresLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(card1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(card4, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap()));
        panelFilaIndicadoresLayout.setVerticalGroup(
                panelFilaIndicadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelFilaIndicadoresLayout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(panelFilaIndicadoresLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(card1, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
                                        .addComponent(card2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(card3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(card4, javax.swing.GroupLayout.PREFERRED_SIZE, 0,
                                                Short.MAX_VALUE))
                                .addContainerGap(17, Short.MAX_VALUE)));

        panelFilaResumenEstado.setLayout(new java.awt.GridLayout(1, 0, 80, 0));

        panelResumenSistema.setPreferredSize(new java.awt.Dimension(296, 130));

        javax.swing.GroupLayout panelResumenSistemaLayout = new javax.swing.GroupLayout(panelResumenSistema);
        panelResumenSistema.setLayout(panelResumenSistemaLayout);
        panelResumenSistemaLayout.setHorizontalGroup(
                panelResumenSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 429, Short.MAX_VALUE));
        panelResumenSistemaLayout.setVerticalGroup(
                panelResumenSistemaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE));

        panelFilaResumenEstado.add(panelResumenSistema);

        panelEstadoTareas.setPreferredSize(new java.awt.Dimension(296, 170));
        panelEstadoTareas.setLayout(new javax.swing.BoxLayout(panelEstadoTareas, javax.swing.BoxLayout.LINE_AXIS));
        panelFilaResumenEstado.add(panelEstadoTareas);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lb, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panelFilaIndicadores, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(panelFilaResumenEstado, javax.swing.GroupLayout.DEFAULT_SIZE, 939,
                                                Short.MAX_VALUE))
                                .addContainerGap()));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(lb)
                                .addGap(18, 18, 18)
                                .addComponent(panelFilaIndicadores, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(panelFilaResumenEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 219,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(473, 473, 473)));

        jScrollPane1.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 951, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 871,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 16, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.synapse.ui.components.Card card1;
    private com.synapse.ui.components.Card card2;
    private com.synapse.ui.components.Card card3;
    private com.synapse.ui.components.Card card4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lb;
    private javax.swing.JPanel panelEstadoTareas;
    private javax.swing.JPanel panelFilaIndicadores;
    private javax.swing.JPanel panelFilaResumenEstado;
    private javax.swing.JPanel panelResumenSistema;
    // End of variables declaration//GEN-END:variables
}
