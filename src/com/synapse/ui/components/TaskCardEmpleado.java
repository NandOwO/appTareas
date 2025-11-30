package com.synapse.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.synapse.core.models.Tarea;
import com.synapse.core.models.Usuario;
import com.synapse.data.dao.TareaDAO;
import com.synapse.data.dao.UsuarioDAO;
import com.synapse.core.services.notifications.EmailService;
import com.synapse.core.services.notifications.EmailTemplates;
import com.synapse.ui.views.empleado.VerTareaDialog;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Frame;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import java.util.List;

public class TaskCardEmpleado extends JPanel {

    private final Tarea tarea;
    private final CardLayout buttonCardLayout;
    private final JPanel panelBotonesWrapper;
    private final TareaDAO tareaDAO;
    private final PrioridadPillRenderer pillPrioridad;
    private final TaskStatusPillRenderer pillEstado;
    private final JPanel panelPillEstado;

    public TaskCardEmpleado(Tarea tarea, TareaDAO tareaDAO) {
        this.tarea = tarea;
        this.tareaDAO = tareaDAO;
        this.pillPrioridad = new PrioridadPillRenderer();
        this.pillEstado = new TaskStatusPillRenderer();

        // 1. Configuración de la Tarjeta Principal
        setLayout(new BorderLayout(15, 5));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        putClientProperty(FlatClientProperties.STYLE, "arc: 20; background: $Panel.background;");

        // 2. Panel de Información (Izquierda)
        JPanel infoPanel = new JPanel(new MigLayout("wrap 2, fillx", "[100!][grow]"));
        infoPanel.setOpaque(false);

        JLabel lblNombreTarea = new JLabel(tarea.getTitulo());
        lblNombreTarea.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");

        String fecha = (tarea.getFechaLimite() != null) ? tarea.getFechaLimite().toString() : "Sin fecha";
        JLabel lblFecha = new JLabel(fecha);

        infoPanel.add(lblNombreTarea, "span 2, growx, gapbottom 5");
        infoPanel.add(new JLabel("Fecha Límite:"));
        infoPanel.add(lblFecha);
        infoPanel.add(new JLabel("Prioridad:"));
        infoPanel
                .add(pillPrioridad.getTableCellRendererComponent(null, tarea.getNombrePrioridad(), false, false, 0, 0));
        infoPanel.add(new JLabel("Estado:"));

        panelPillEstado = new JPanel(new MigLayout("insets 0"));
        panelPillEstado.setOpaque(false);
        panelPillEstado
                .add(pillEstado.getTableCellRendererComponent(null, tarea.getNombreEstado(), false, false, 0, 0));
        infoPanel.add(panelPillEstado);

        // 3. Panel de Botones (Derecha)
        buttonCardLayout = new CardLayout();
        panelBotonesWrapper = new JPanel(buttonCardLayout);
        panelBotonesWrapper.setOpaque(false);

        panelBotonesWrapper.add(createPanelPendiente(), "PENDIENTE");
        panelBotonesWrapper.add(createPanelEnProgreso(), "EN_PROGRESO");
        panelBotonesWrapper.add(createPanelCompletada(), "COMPLETADA");
        panelBotonesWrapper.add(createPanelPausada(), "PAUSADA");

        // 4. Ensamblaje
        add(infoPanel, BorderLayout.CENTER);
        add(panelBotonesWrapper, BorderLayout.EAST);

        // 5. Mostrar la tarjeta de botones correcta al inicio
        actualizarEstadoVisual(false);
    }

    private JPanel createPanelPendiente() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0"));
        panel.setOpaque(false);

        ButtonAction btnVer = new ButtonAction();
        btnVer.setText("Ver Tarea");
        btnVer.addActionListener(e -> verTarea());

        ButtonAction btnIniciar = new ButtonAction();
        btnIniciar.setText("Iniciar Tarea");
        btnIniciar.putClientProperty(FlatClientProperties.STYLE,
                "background: $Component.successColor; foreground: $Component.successColor.foreground;");
        btnIniciar.addActionListener(e -> iniciarTarea());

        panel.add(btnVer, "growx");
        panel.add(btnIniciar, "growx, gaptop 5");
        return panel;
    }

    private JPanel createPanelEnProgreso() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0"));
        panel.setOpaque(false);

        ButtonAction btnVer = new ButtonAction();
        btnVer.setText("Ver Tarea");
        btnVer.addActionListener(e -> verTarea());

        ButtonAction btnCompletar = new ButtonAction();
        btnCompletar.setText("Completar");
        btnCompletar.putClientProperty(FlatClientProperties.STYLE,
                "background: $Component.successColor; foreground: $Component.successColor.foreground;");
        btnCompletar.addActionListener(e -> completarTarea());

        ButtonAction btnPausar = new ButtonAction();
        btnPausar.setText("Pausar Tarea");
        btnPausar.putClientProperty(FlatClientProperties.STYLE,
                "background: $Component.warningColor; foreground: $Component.warningColor.foreground;");
        btnPausar.addActionListener(e -> pausarTarea());

        panel.add(btnVer, "growx");
        panel.add(btnCompletar, "growx, gaptop 5");
        panel.add(btnPausar, "growx, gaptop 5");
        return panel;
    }

    private JPanel createPanelCompletada() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, al center center, insets 0"));
        panel.setOpaque(false);

        JLabel lblCompletada = new JLabel("¡Completada!");
        lblCompletada.putClientProperty(FlatClientProperties.STYLE,
                "font: bold +1; foreground: $Component.successColor;");

        ButtonAction btnVer = new ButtonAction();
        btnVer.setText("Ver Tarea");
        btnVer.addActionListener(e -> verTarea());

        panel.add(lblCompletada, "center, gapbottom 5");
        panel.add(btnVer, "growx");
        return panel;
    }

    private JPanel createPanelPausada() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0"));
        panel.setOpaque(false);

        ButtonAction btnVer = new ButtonAction();
        btnVer.setText("Ver Tarea");
        btnVer.addActionListener(e -> verTarea());

        ButtonAction btnReanudar = new ButtonAction();
        btnReanudar.setText("Reanudar Tarea");
        btnReanudar.putClientProperty(FlatClientProperties.STYLE,
                "background: $Component.accentColor; foreground: $Component.accentColor.foreground;");
        btnReanudar.addActionListener(e -> iniciarTarea());

        panel.add(btnVer, "growx");
        panel.add(btnReanudar, "growx, gaptop 5");
        return panel;
    }

    private void verTarea() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        VerTareaDialog dialog = new VerTareaDialog((JFrame) parent, true, tarea);
        dialog.setVisible(true);
    }

    private void iniciarTarea() {
        boolean exito = tareaDAO.actualizarEstadoTarea(tarea.getIdTarea(), 2);
        if (exito) {
            tarea.setNombreEstado("en_progreso");
            actualizarEstadoVisual(true);
        } else {
            JOptionPane.showMessageDialog(this, "Error al iniciar la tarea.", "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completarTarea() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Marcar tarea como completada?", "Completar Tarea",
                JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        boolean exito = tareaDAO.actualizarEstadoTarea(tarea.getIdTarea(), 3);
        if (exito) {
            tarea.setNombreEstado("completada");
            actualizarEstadoVisual(true);
            enviarNotificacionCompletada();
        } else {
            JOptionPane.showMessageDialog(this, "Error al completar la tarea.", "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pausarTarea() {
        boolean exito = tareaDAO.actualizarEstadoTarea(tarea.getIdTarea(), 4);
        if (exito) {
            tarea.setNombreEstado("pausada");
            actualizarEstadoVisual(true);
        } else {
            JOptionPane.showMessageDialog(this, "Error al pausar la tarea.", "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEstadoVisual(boolean actualizar) {
        panelPillEstado.removeAll();
        panelPillEstado
                .add(pillEstado.getTableCellRendererComponent(null, tarea.getNombreEstado(), false, false, 0, 0));

        switch (tarea.getNombreEstado().toLowerCase()) {
            case "en_progreso":
                buttonCardLayout.show(panelBotonesWrapper, "EN_PROGRESO");
                break;
            case "completada":
                buttonCardLayout.show(panelBotonesWrapper, "COMPLETADA");
                break;
            case "pausada":
                buttonCardLayout.show(panelBotonesWrapper, "PAUSADA");
                break;
            case "pendiente":
            default:
                buttonCardLayout.show(panelBotonesWrapper, "PENDIENTE");
                break;
        }

        if (actualizar) {
            panelPillEstado.revalidate();
            panelPillEstado.repaint();
        }
    }

    private void enviarNotificacionCompletada() {
        System.out.println("=== INICIANDO ENVÍO DE EMAIL ===");
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            TareaDAO tareaDAO = new TareaDAO();

            System.out.println("ID Creador: " + tarea.getCreadaPor());
            Usuario creador = usuarioDAO.getUsuarioPorId(tarea.getCreadaPor());
            System.out.println("Creador encontrado: " + (creador != null ? creador.getNombre() : "NULL"));

            // Obtener usuarios asignados a la tarea
            List<Usuario> usuariosAsignados = tareaDAO.getUsuariosAsignadosPorTarea(tarea.getIdTarea());
            String nombreEmpleado = (usuariosAsignados != null && !usuariosAsignados.isEmpty())
                    ? usuariosAsignados.get(0).getNombre()
                    : "Un empleado";
            System.out.println("Nombre empleado: " + nombreEmpleado);

            if (creador != null && creador.getEmail() != null) {
                System.out.println("✅ Todas las condiciones OK, enviando email...");
                EmailService emailService = new EmailService();
                String asunto = "Tarea Completada: " + tarea.getTitulo();
                String mensaje = EmailTemplates.getTemplateTareaCompletadaPorEmpleado(
                        creador.getNombre(),
                        nombreEmpleado,
                        tarea.getTitulo());
                emailService.sendEmail(creador.getEmail(), asunto, mensaje, null);
                System.out.println("✅ Email enviado a: " + creador.getEmail());
            } else {
                System.out.println("❌ Condiciones NO cumplidas:");
                System.out.println("  - Creador null? " + (creador == null));
                System.out.println("  - Email null? " + (creador != null && creador.getEmail() == null));
            }
        } catch (Exception e) {
            System.err.println("❌ ERROR al enviar notificación: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
