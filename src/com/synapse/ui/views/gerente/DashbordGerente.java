package com.synapse.ui.views.gerente;

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
public class DashbordGerente extends javax.swing.JPanel {

    private final com.synapse.core.models.Usuario usuarioLogueado;
    private final com.synapse.data.dao.TareaDAO tareaDAO;

    // Credenciales para los envios de correo
    public DashbordGerente(com.synapse.core.models.Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.tareaDAO = new com.synapse.data.dao.TareaDAO();
        initComponents();

        panelFilaIndicadores.setLayout(new GridLayout(1, 4, 30, 0));
        panelResumenSistema.setLayout(new javax.swing.BoxLayout(panelResumenSistema, javax.swing.BoxLayout.Y_AXIS));
        panelEstadoTareas.setLayout(new javax.swing.BoxLayout(panelEstadoTareas, javax.swing.BoxLayout.Y_AXIS));
        cargarDatosDashboard();
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
        panelFilaExportar.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background;"
                + "border:15,15,15,15");
        crearPanelExportar();
    }

    private void cargarDatosDashboard() {
        int iconSize = 24;
        ImageIcon iconTareas = new FlatSVGIcon("apptareas/icon/chart-line-green.svg", iconSize, iconSize);
        ImageIcon iconPendientes = new FlatSVGIcon("apptareas/icon/box-orange.svg", iconSize, iconSize);
        ImageIcon iconEquipos = new FlatSVGIcon("apptareas/icon/user.svg", iconSize, iconSize);
        ImageIcon iconVencidas = new FlatSVGIcon("apptareas/icon/alert-triangle-red.svg", iconSize, iconSize);

        javax.swing.SwingWorker<DashboardData, Void> worker = new javax.swing.SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() throws Exception {
                java.util.List<com.synapse.core.models.Tarea> tareas = tareaDAO
                        .getTareasPorGerente(usuarioLogueado.getIdUsuario());

                int total = tareas.size();
                int pendientes = 0, enProgreso = 0, completadas = 0, vencidas = 0;
                java.time.LocalDate hoy = java.time.LocalDate.now();

                for (com.synapse.core.models.Tarea t : tareas) {
                    if (t.getIdEstado() == 1)
                        pendientes++;
                    else if (t.getIdEstado() == 2)
                        enProgreso++;
                    else if (t.getIdEstado() == 3)
                        completadas++;

                    if (t.getFechaLimite() != null && t.getIdEstado() != 3) {
                        java.time.LocalDate fechaLimite = t.getFechaLimite().toLocalDateTime().toLocalDate();
                        if (fechaLimite.isBefore(hoy))
                            vencidas++;
                    }
                }
                return new DashboardData(total, pendientes, enProgreso, completadas, vencidas);
            }

            @Override
            protected void done() {
                try {
                    DashboardData data = get();
                    card1.setTitle("Tareas Gestionadas");
                    card1.setValue(data.total);
                    card1.setIcon(iconTareas);

                    card2.setTitle("Pendientes");
                    card2.setValue(data.pendientes);
                    card2.setIcon(iconPendientes);

                    card3.setTitle("En Progreso");
                    card3.setValue(data.enProgreso);
                    card3.setIcon(iconEquipos);

                    card4.setTitle("Vencidas");
                    card4.setValue(data.vencidas);
                    card4.setIcon(iconVencidas);

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

        JLabel titleResumen = new JLabel("Mis Tareas Gestionadas");
        titleResumen.setFont(titleResumen.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        titleResumen.setAlignmentX(LEFT_ALIGNMENT);
        titleResumen.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelResumenSistema.add(titleResumen);

        panelResumenSistema.add(crearFilaResumen("Total:", data.total, null));
        panelResumenSistema.add(crearFilaResumen("Pendientes:", data.pendientes, "#FF9800"));
        panelResumenSistema.add(crearFilaResumen("En Progreso:", data.enProgreso, "#2196F3"));
        panelResumenSistema.add(crearFilaResumen("Completadas:", data.completadas, "#4CAF50"));

        JLabel titleEstado = new JLabel("Estado de Tareas");
        titleEstado.setFont(titleEstado.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        titleEstado.setAlignmentX(LEFT_ALIGNMENT);
        titleEstado.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelEstadoTareas.add(titleEstado);

        panelEstadoTareas.add(crearFilaResumen("Completadas:", data.completadas, "#7B68EE"));
        panelEstadoTareas.add(crearFilaResumen("En Progreso:", data.enProgreso, null));
        panelEstadoTareas.add(crearFilaResumen("Pendientes:", data.pendientes, null));
        panelEstadoTareas.add(crearFilaResumen("Vencidas:", data.vencidas, "#FF5252"));

        panelResumenSistema.revalidate();
        panelResumenSistema.repaint();
        panelEstadoTareas.revalidate();
        panelEstadoTareas.repaint();
    }

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

    private void crearPanelExportar() {

        // --- Configuración e Inicialización ---
        panelFilaExportar.setLayout(new BorderLayout(0, 15));
        panelFilaExportar.putClientProperty(FlatClientProperties.STYLE,
                "arc:25; background:$Table.background; border:15,15,15,15");

        // 1. TÍTULO PRINCIPAL (NORTE)
        JPanel panelTituloPrincipal = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelTituloPrincipal.setOpaque(false);

        JLabel lblTituloPrincipal = new JLabel("Exportar y Notificar");
        lblTituloPrincipal.setFont(lblTituloPrincipal.getFont().deriveFont(Font.BOLD, 16f));

        // Icono dinámico (se pintará con el foreground del tema: oscuro en claro, claro
        // en oscuro)
        FlatSVGIcon iconPrincipal = new FlatSVGIcon("apptareas/icon/download.svg", 18, 18);
        iconPrincipal.setColorFilter(new ColorFilter(color -> UIManager.getColor("Label.foreground")));

        lblTituloPrincipal.setIcon(iconPrincipal);
        lblTituloPrincipal.setIconTextGap(8);
        panelTituloPrincipal.add(lblTituloPrincipal);
        panelFilaExportar.add(panelTituloPrincipal, BorderLayout.NORTH);

        // --- 2. SECCIÓN CENTRAL: Filtros, Formato y Resumen de Tareas ---
        JPanel panelContenidoCentral = new JPanel();
        panelContenidoCentral.setLayout(new BoxLayout(panelContenidoCentral, BoxLayout.Y_AXIS));
        panelContenidoCentral.setOpaque(false);

        // 2a. Subsección: Filtros de Exportación y Formato (Layout horizontal)
        JPanel panelFiltrosYFormato = new JPanel(new GridLayout(1, 2, 20, 0));
        panelFiltrosYFormato.setOpaque(false);

        // Panel Izquierdo: "Filtros de Exportación" y Checkboxes
        JPanel panelFiltros = new JPanel();
        panelFiltros.setLayout(new BoxLayout(panelFiltros, BoxLayout.Y_AXIS));
        panelFiltros.setOpaque(false);
        JLabel lblFiltros = new JLabel("Filtros de Exportación");
        lblFiltros.setFont(lblFiltros.getFont().deriveFont(Font.BOLD, 12f));
        panelFiltros.add(lblFiltros);
        panelFiltros.add(Box.createRigidArea(new Dimension(0, 5)));
        JCheckBox chkCompletadas = new JCheckBox("Incluir tareas completadas");
        JCheckBox chkArchivadas = new JCheckBox("Incluir tareas archivadas");
        chkCompletadas.setOpaque(false);
        chkArchivadas.setOpaque(false);
        panelFiltros.add(chkCompletadas);
        panelFiltros.add(chkArchivadas);
        panelFiltrosYFormato.add(panelFiltros);

        JPanel panelFormato = new JPanel();
        panelFormato.setLayout(new BoxLayout(panelFormato, BoxLayout.Y_AXIS));
        panelFormato.setOpaque(false);
        JLabel lblFormato = new JLabel("Formato de Exportación");
        lblFormato.setFont(lblFormato.getFont().deriveFont(Font.BOLD, 12f));
        panelFormato.add(lblFormato);
        panelFormato.add(Box.createRigidArea(new Dimension(0, 5)));
        JComboBox<String> comboFormato = new JComboBox<>();

        comboFormato.addItem("CSV (Excel)");
        comboFormato.addItem("JSON");
        comboFormato.addItem("ICS (Calendario)");

        comboFormato.putClientProperty(FlatClientProperties.STYLE, "arc:25");
        panelFormato.add(comboFormato);
        panelFiltrosYFormato.add(panelFormato);

        panelContenidoCentral.add(panelFiltrosYFormato);
        panelContenidoCentral.add(Box.createRigidArea(new Dimension(0, 20)));

        // 2b. Subsección: Resumen de Tareas (4 columnas de conteo)
        JPanel panelResumenTareas = new JPanel(new GridLayout(2, 4, 15, 5)); // 2 filas, 4 cols, espacio
        panelResumenTareas.setOpaque(false);
        JLabel lblTituloResumen = new JLabel("Resumen");
        lblTituloResumen.setFont(lblTituloResumen.getFont().deriveFont(Font.BOLD, 12f));
        lblTituloResumen.setAlignmentX(LEFT_ALIGNMENT);
        panelContenidoCentral.add(lblTituloResumen);
        panelContenidoCentral.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio

        panelResumenTareas.add(new JLabel("Total tareas:"));
        panelResumenTareas.add(new JLabel("Pendientes:"));
        panelResumenTareas.add(new JLabel("En progreso:"));
        panelResumenTareas.add(new JLabel("Completadas:"));

        // Valores de las métricas (segunda fila de GridLayout con píldoras)
        // Reutilizamos crearFilaResumen, pero solo la lógica de la píldora.
        // Aquí ajustamos la llamada para que solo devuelva la píldora con el número.
        panelResumenTareas.add(crearPillLabel(3, null)); // Total Tareas (neutra)
        panelResumenTareas.add(crearPillLabel(2, "#FFC107")); // Pendientes (amarillo/naranja)
        panelResumenTareas.add(crearPillLabel(1, "#17A2B8")); // En progreso (azul)
        panelResumenTareas.add(crearPillLabel(0, "#28A745")); // Completadas (verde)

        panelContenidoCentral.add(panelResumenTareas);

        panelFilaExportar.add(panelContenidoCentral, BorderLayout.CENTER);

        // 3. SECCIÓN SUR: Botones y Descripción (SOUTH)
        JPanel panelSur = new JPanel();
        panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.Y_AXIS));
        panelSur.setOpaque(false);

        // 3a. Fila de Botones
        JPanel panelBotonesAccion = new JPanel(new GridLayout(1, 4, 10, 0));
        panelBotonesAccion.setOpaque(false);

        // Aseguramos que el panel se alinee a la izquierda dentro del BoxLayout
        panelBotonesAccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Botones
        JButton btnExportar = new JButton("Exportar ICS");
        JButton btnCalendario = new JButton("A Calendario");
        JButton btnImprimir = new JButton("Imprimir");
        JButton btnNotificar = new JButton("Notificar");

        // --- ESTILOS DE BOTONES ---
        // Estilo Botón Principal (Exportar ICS)
        FlatSVGIcon iconExportar = new FlatSVGIcon("apptareas/icon/download.svg", 16, 16);
        iconExportar.setColorFilter(new ColorFilter(color -> Color.WHITE)); // Forzar blanco
        btnExportar.setIcon(iconExportar);
        btnExportar.putClientProperty("JButton.buttonType", "roundRect"); // CORRECCIÓN CLAVE
        btnExportar.putClientProperty(FlatClientProperties.STYLE,
                "background: #DC3545; foreground: #FFFFFF; arc:999"); // Rojo y Blanco

        // Estilo Botones Secundarios (Ghost/Contorno)
        // Icono adaptable (oscuro en claro, claro en oscuro)
        ColorFilter iconFilterSecondary = new ColorFilter(color -> UIManager.getColor("Button.foreground"));

        // Aplicar estilos y corrección de "JButton.buttonType"
        JButton[] secondaryButtons = { btnCalendario, btnImprimir, btnNotificar };
        String styleSecondary = "background: $Button.secondary.background; foreground: $Button.secondary.foreground; arc:999";

        FlatSVGIcon iconCalendar = new FlatSVGIcon("apptareas/icon/calendar.svg", 16, 16);
        iconCalendar.setColorFilter(iconFilterSecondary);
        btnCalendario.setIcon(iconCalendar);

        FlatSVGIcon iconPrinter = new FlatSVGIcon("apptareas/icon/printer.svg", 16, 16);
        iconPrinter.setColorFilter(iconFilterSecondary);
        btnImprimir.setIcon(iconPrinter);

        FlatSVGIcon iconBell = new FlatSVGIcon("apptareas/icon/bell.svg", 16, 16);
        iconBell.setColorFilter(iconFilterSecondary);
        btnNotificar.setIcon(iconBell);

        for (JButton btn : secondaryButtons) {
            btn.putClientProperty("JButton.buttonType", "roundRect");
            btn.putClientProperty(FlatClientProperties.STYLE, styleSecondary);
        }

        panelBotonesAccion.add(btnExportar);
        panelBotonesAccion.add(btnCalendario);
        panelBotonesAccion.add(btnImprimir);
        panelBotonesAccion.add(btnNotificar);
        panelSur.add(panelBotonesAccion);
        panelSur.add(Box.createRigidArea(new Dimension(0, 15)));

        // 3b. Descripción de los Formatos (Lista de Puntos)
        JPanel panelDescripciones = new JPanel();
        panelDescripciones.setLayout(new BoxLayout(panelDescripciones, BoxLayout.Y_AXIS));
        panelDescripciones.setOpaque(false);

        // 💡 CORRECCIÓN DE ALINEACIÓN: Alineación forzada a la izquierda
        panelDescripciones.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Contenido de las notas
        panelDescripciones.add(new JLabel("• CSV: Compatible con Excel y hojas de cálculo"));
        panelDescripciones.add(new JLabel("• JSON: Formato de datos estructurado para desarrolladores"));
        panelDescripciones.add(new JLabel("• ICS: Compatible con Google Calendar, Outlook, etc."));
        panelDescripciones.add(new JLabel("• Imprimir: Genera un reporte en PDF listo para imprimir"));
        panelDescripciones.add(new JLabel("• Notificar: Envía resumen por correo electrónico"));

        panelSur.add(panelDescripciones);

        panelFilaExportar.add(panelSur, BorderLayout.SOUTH);

        panelFilaExportar.revalidate();
        panelFilaExportar.repaint();
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
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btnCrear = new javax.swing.JButton();
        panelFilaIndicadores = new javax.swing.JPanel();
        card1 = new com.synapse.ui.components.Card();
        card2 = new com.synapse.ui.components.Card();
        card3 = new com.synapse.ui.components.Card();
        card4 = new com.synapse.ui.components.Card();
        panelFilaResumenEstado = new javax.swing.JPanel();
        panelResumenSistema = new javax.swing.JPanel();
        panelEstadoTareas = new javax.swing.JPanel();
        panelFilaExportar = new javax.swing.JPanel();

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb.setText("Dashboard");

        jButton1.setText("Success Noti");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Error Noti");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Warning NOTI");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Info Noti");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        btnCrear.setText("Crear");
        btnCrear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrearActionPerformed(evt);
            }
        });

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

        javax.swing.GroupLayout panelFilaExportarLayout = new javax.swing.GroupLayout(panelFilaExportar);
        panelFilaExportar.setLayout(panelFilaExportarLayout);
        panelFilaExportarLayout.setHorizontalGroup(
                panelFilaExportarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 0, Short.MAX_VALUE));
        panelFilaExportarLayout.setVerticalGroup(
                panelFilaExportarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 293, Short.MAX_VALUE));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout
                                                .createSequentialGroup()
                                                .addGap(4, 4, 4)
                                                .addGroup(jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lb, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(panelFilaIndicadores,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(panelFilaResumenEstado,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                jPanel1Layout.createSequentialGroup()
                                                        .addContainerGap(12, Short.MAX_VALUE)
                                                        .addComponent(btnCrear, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(288, 288, 288)
                                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(36, 36, 36)
                                                        .addComponent(jButton3)
                                                        .addGap(40, 40, 40)
                                                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                106, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(panelFilaExportar, javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                                .addGap(18, 18, 18)
                                .addComponent(panelFilaExportar, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(109, 109, 109)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnCrear, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jButton2)
                                                        .addComponent(jButton1))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                jPanel1Layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jButton4)
                                                        .addComponent(jButton3)))
                                .addContainerGap()));

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

    private void btnCrearActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCrearActionPerformed

    }// GEN-LAST:event_btnCrearActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        Notifications.getInstance().show(Notifications.Type.INFO, Notifications.Location.TOP_RIGHT,
                "Se le ha asigando una tarea");
    }// GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        Notifications.getInstance().show(Notifications.Type.WARNING, Notifications.Location.TOP_RIGHT,
                "Esta seguro que desea cambiar la contraseña");
    }// GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed

        Notifications.getInstance().show(Notifications.Type.ERROR, Notifications.Location.TOP_RIGHT,
                "Error cargando a la base de datos");
    }// GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        Notifications.getInstance().show(Notifications.Type.SUCCESS, Notifications.Location.TOP_RIGHT,
                "Datos cargados correctamente");
    }// GEN-LAST:event_jButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCrear;
    private com.synapse.ui.components.Card card1;
    private com.synapse.ui.components.Card card2;
    private com.synapse.ui.components.Card card3;
    private com.synapse.ui.components.Card card4;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lb;
    private javax.swing.JPanel panelEstadoTareas;
    private javax.swing.JPanel panelFilaExportar;
    private javax.swing.JPanel panelFilaIndicadores;
    private javax.swing.JPanel panelFilaResumenEstado;
    private javax.swing.JPanel panelResumenSistema;
    // End of variables declaration//GEN-END:variables
}
