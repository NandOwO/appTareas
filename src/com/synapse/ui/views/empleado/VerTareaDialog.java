package com.synapse.ui.views.empleado;

import com.formdev.flatlaf.FlatClientProperties;
import com.synapse.core.models.Adjunto;
import com.synapse.core.models.Equipo;
import com.synapse.core.models.Tarea;
import com.synapse.core.models.Usuario;
import com.synapse.ui.components.PrioridadPillRenderer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class VerTareaDialog extends JDialog {

    public VerTareaDialog(JFrame parent, boolean modal, Tarea tarea) {
        super(parent, modal);

        setTitle("Detalles de la Tarea");
        setLayout(new BorderLayout(0, 10));

        // Cargar asignaciones desde la base de datos
        cargarDatosTarea(tarea);

        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx", "[100!][grow, fill]"));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Título
        JLabel lblTitulo = new JLabel(tarea.getTitulo());
        lblTitulo.putClientProperty(FlatClientProperties.STYLE, "font: bold +3");
        panel.add(lblTitulo, "span 2, growx, gapbottom 10");

        // Descripción
        panel.add(new JLabel("Descripción:"));
        JTextArea txtDesc = new JTextArea(tarea.getDescripcion());
        txtDesc.setEditable(false);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setOpaque(false);
        txtDesc.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.secondaryForeground;");
        panel.add(txtDesc, "growx, h 60!");

        // Prioridad
        panel.add(new JLabel("Prioridad:"));
        PrioridadPillRenderer pillPrioridad = new PrioridadPillRenderer();
        panel.add(pillPrioridad.getTableCellRendererComponent(null, tarea.getNombrePrioridad(), false, false, 0, 0),
                "left");

        // Fecha Límite
        panel.add(new JLabel("Vence:"));
        String fecha = (tarea.getFechaLimite() != null) ? tarea.getFechaLimite().toString() : "N/A";
        panel.add(new JLabel(fecha));

        // Asignado por
        String gerente = (tarea.getCreador() != null) ? tarea.getCreador().getNombre() : "N/A";
        panel.add(new JLabel("Asignado por:"));
        panel.add(new JLabel(gerente));

        // Asignado a
        panel.add(new JLabel("Asignado a:"));
        String asignadoDisplay = "N/A";
        if (tarea.getUsuariosAsignados() != null && !tarea.getUsuariosAsignados().isEmpty()) {
            asignadoDisplay = tarea.getUsuariosAsignados().get(0).getNombre();
        } else if (tarea.getEquiposAsignados() != null && !tarea.getEquiposAsignados().isEmpty()) {
            asignadoDisplay = "Equipo: " + tarea.getEquiposAsignados().get(0).getNombre();
        }
        panel.add(new JLabel(asignadoDisplay));

        // Archivos adjuntos
        DefaultListModel<String> fileModel = new DefaultListModel<>();
        List<Adjunto> adjuntos = tarea.getAdjuntos();

        if (adjuntos == null || adjuntos.isEmpty()) {
            fileModel.addElement("📎 Los archivos adjuntos fueron enviados por correo");
        } else {
            fileModel.addElement("📎 Archivos adjuntos (enviados por correo):");
            for (Adjunto adjunto : adjuntos) {
                fileModel.addElement("  • " + adjunto.getNombreArchivo());
            }
        }

        JList<String> listArchivos = new JList<>(fileModel);
        listArchivos.setEnabled(false);
        JScrollPane scrollArchivos = new JScrollPane(listArchivos);
        scrollArchivos.setPreferredSize(new Dimension(100, 80));
        scrollArchivos.setBorder(BorderFactory.createTitledBorder("Archivos Adjuntos"));

        panel.add(scrollArchivos, "span 2, growx, gaptop 10");

        add(panel, BorderLayout.CENTER);

        setSize(450, 500);
        setLocationRelativeTo(parent);
    }

    private void cargarDatosTarea(Tarea tarea) {
        try {
            // Cargar asignaciones
            com.synapse.data.dao.TareaDAO tareaDAO = new com.synapse.data.dao.TareaDAO();
            List<Usuario> usuariosAsignados = tareaDAO.getUsuariosAsignadosPorTarea(tarea.getIdTarea());
            List<Equipo> equiposAsignados = tareaDAO.getEquiposAsignadosPorTarea(tarea.getIdTarea());

            tarea.setUsuariosAsignados(usuariosAsignados);
            tarea.setEquiposAsignados(equiposAsignados);

            // Nota: Los adjuntos no se guardan en BD, solo se envían por email
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al cargar datos de la tarea: " + e.getMessage());
        }
    }
}
