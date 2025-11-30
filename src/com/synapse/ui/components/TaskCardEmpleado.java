package com.synapse.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.synapse.core.models.Tarea;
// --- Imports Nuevos ---
import com.synapse.data.dao.TareaDAO;
import com.synapse.ui.views.empleado.VerTareaDialog;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Frame;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class TaskCardEmpleado extends JPanel {

    private final Tarea tarea;
    private final CardLayout buttonCardLayout;
    private final JPanel panelBotonesWrapper;

    // --- NUEVO ---
    private final TareaDAO tareaDAO; // El DAO para hacer cambios
    // -----------

    private final PrioridadPillRenderer pillPrioridad;
    private final TaskStatusPillRenderer pillEstado;
    private final JPanel panelPillEstado;

    // --- CONSTRUCTOR CORREGIDO ---
    public TaskCardEmpleado(Tarea tarea, TareaDAO tareaDAO) {
        this.tarea = tarea;
        this.tareaDAO = tareaDAO; // Guardamos el DAO
        // -------------------------

        this.pillPrioridad = new PrioridadPillRenderer();
        this.pillEstado = new TaskStatusPillRenderer();

        // 1. Configuración de la Tarjeta Principal
        setLayout(new BorderLayout(15, 5));
        setBorder(new EmptyBorder(15, 20, 15, 20));
        putClientProperty(FlatClientProperties.STYLE, "arc: 20; background: $Panel.background;");

        // 2. Panel de Información (Izquierda)
        JPanel infoPanel = new JPanel(new MigLayout("wrap 2, fillx", "[100!][grow]"));
        infoPanel.setOpaque(false);

        // --- CORRECCIÓN DE CAMPOS (para coincidir con el modelo SQL) ---
        JLabel lblNombreTarea = new JLabel(tarea.getTitulo()); // getTitulo()
        lblNombreTarea.putClientProperty(FlatClientProperties.STYLE, "font: bold +2");

        String fecha = (tarea.getFechaLimite() != null) ? tarea.getFechaLimite().toString() : "Sin fecha";
        JLabel lblFecha = new JLabel(fecha);
        // -----------------------------------------------------------

        infoPanel.add(lblNombreTarea, "span 2, growx, gapbottom 5");
        infoPanel.add(new JLabel("Fecha Límite:"));
        infoPanel.add(lblFecha);
        infoPanel.add(new JLabel("Prioridad:"));

        // --- CORRECCIÓN DE CAMPOS ---
        infoPanel.add(pillPrioridad.getTableCellRendererComponent(null, tarea.getNombrePrioridad(), false, false, 0, 0)); // getNombrePrioridad()
        infoPanel.add(new JLabel("Estado:"));

        panelPillEstado = new JPanel(new MigLayout("insets 0"));
        panelPillEstado.setOpaque(false);
        panelPillEstado.add(pillEstado.getTableCellRendererComponent(null, tarea.getNombreEstado(), false, false, 0, 0)); // getNombreEstado()
        infoPanel.add(panelPillEstado);
        // ------------------------------

        // 3. Panel de Botones (Derecha)
        buttonCardLayout = new CardLayout();
        panelBotonesWrapper = new JPanel(buttonCardLayout);
        panelBotonesWrapper.setOpaque(false);

        panelBotonesWrapper.add(createPanelPendiente(), "PENDIENTE");
        panelBotonesWrapper.add(createPanelEnProgreso(), "EN_PROGRESO");
        panelBotonesWrapper.add(createPanelCompletada(), "COMPLETADA");

        // 4. Ensamblaje
        add(infoPanel, BorderLayout.CENTER);
        add(panelBotonesWrapper, BorderLayout.EAST);

        // 5. Mostrar la tarjeta de botones correcta al inicio
        actualizarEstadoVisual(false);
    }

    // --- (createPanelPendiente, createPanelEnProgreso, createPanelCompletada se quedan igual) ---
    // ... (Tu código para esos 3 métodos) ...
    private JPanel createPanelPendiente() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 0"));
        panel.setOpaque(false);

        ButtonAction btnVer = new ButtonAction();
        btnVer.setText("Ver Tarea");
        btnVer.addActionListener(e -> verTarea());

        ButtonAction btnIniciar = new ButtonAction();
        btnIniciar.setText("Iniciar Tarea");
        btnIniciar.putClientProperty(FlatClientProperties.STYLE, "background: $Component.successColor; foreground: $Component.successColor.foreground;");
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
        btnCompletar.putClientProperty(FlatClientProperties.STYLE, "background: $Component.successColor; foreground: $Component.successColor.foreground;");
        btnCompletar.addActionListener(e -> completarTarea());

        ButtonAction btnPausar = new ButtonAction();
        btnPausar.setText("Pausar Tarea");
        btnPausar.putClientProperty(FlatClientProperties.STYLE, "background: $Component.warningColor; foreground: $Component.warningColor.foreground;");
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
        lblCompletada.putClientProperty(FlatClientProperties.STYLE, "font: bold +1; foreground: $Component.successColor;");

        ButtonAction btnVer = new ButtonAction();
        btnVer.setText("Ver Tarea");
        btnVer.addActionListener(e -> verTarea());

        panel.add(lblCompletada, "center, gapbottom 5");
        panel.add(btnVer, "growx");
        return panel;
    }

    // --- ACCIONES DE LOS BOTONES (CONECTADAS AL DAO) ---
    private void verTarea() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        VerTareaDialog dialog = new VerTareaDialog((JFrame) parent, true, tarea);
        dialog.setVisible(true);
    }

    private void iniciarTarea() {
        // (Asumimos ID 2 = "En progreso")
        boolean exito = tareaDAO.actualizarEstadoTarea(tarea.getIdTarea(), 2);
        if (exito) {
            tarea.setNombreEstado("En progreso"); // Actualizamos el objeto local
            actualizarEstadoVisual(true); // Actualizamos la UI
        } else {
            JOptionPane.showMessageDialog(this, "Error al iniciar la tarea.", "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completarTarea() {
        // (Lógica de adjuntar archivos... se queda igual)
        int opcion = JOptionPane.showConfirmDialog(this, "¿Deseas adjuntar archivos?", "Completar Tarea", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            // ... tu lógica de JFileChooser ...
        }

        // (Asumimos ID 3 = "Completada")
        boolean exito = tareaDAO.actualizarEstadoTarea(tarea.getIdTarea(), 3);
        if (exito) {
            tarea.setNombreEstado("Completada");
            actualizarEstadoVisual(true);
        } else {
            JOptionPane.showMessageDialog(this, "Error al completar la tarea.", "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pausarTarea() {
        // (Asumimos ID 1 = "Pendiente")
        boolean exito = tareaDAO.actualizarEstadoTarea(tarea.getIdTarea(), 1);
        if (exito) {
            tarea.setNombreEstado("Pendiente");
            actualizarEstadoVisual(true);
        } else {
            JOptionPane.showMessageDialog(this, "Error al pausar la tarea.", "Error DB", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Actualiza la píldora de estado y la tarjeta de botones
     */
    private void actualizarEstadoVisual(boolean actualizar) {
        // 1. Actualizar la píldora de estado
        panelPillEstado.removeAll();
        panelPillEstado.add(pillEstado.getTableCellRendererComponent(null, tarea.getNombreEstado(), false, false, 0, 0)); // getNombreEstado()

        // 2. Actualizar la tarjeta de botones
        // --- CORRECCIÓN DE CAMPO ---
        switch (tarea.getNombreEstado().toLowerCase()) { // getNombreEstado()
            case "en progreso":
                buttonCardLayout.show(panelBotonesWrapper, "EN_PROGRESO");
                break;
            case "completada":
                buttonCardLayout.show(panelBotonesWrapper, "COMPLETADA");
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
}
