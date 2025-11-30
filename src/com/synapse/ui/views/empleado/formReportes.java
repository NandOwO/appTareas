package com.synapse.ui.views.empleado;

import com.formdev.flatlaf.FlatClientProperties;
import com.synapse.core.models.Tarea;
import com.synapse.core.models.Usuario;
import com.synapse.core.services.reports.ExcelStrategy;
import com.synapse.core.services.reports.IReporteStrategy;
import com.synapse.core.services.reports.PdfStrategy;
import com.synapse.core.services.reports.IcsStrategy;
import com.synapse.data.dao.TareaDAO;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import net.miginfocom.swing.MigLayout;

/**
 *
 * @author FERNANDO
 */
public class formReportes extends javax.swing.JPanel {

    private ButtonGroup formatoGroup;
    private final Usuario usuarioLogueado;
    private final TareaDAO tareaDAO;

    // --- NUEVA VARIABLE ---
    private javax.swing.JRadioButton radioICS; // Necesitamos añadir esto

    public formReportes(Usuario usuario) {
        // --- CONSTRUCTOR CORREGIDO ---
        this.usuarioLogueado = usuario;
        this.tareaDAO = new TareaDAO();

        initComponents();
        initPersonalizado();
        btnGenerar.addActionListener(e -> generarReporteConSwingWorker());
        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    private void initPersonalizado() {
        lblTitulo.putClientProperty(FlatClientProperties.STYLE, "font: bold +5");

        // --- 1. CONFIGURAR SUB-PANELES ---
        panelFechas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        txtFechaDesde.setPreferredSize(new Dimension(150, 22));
        txtFechaHasta.setPreferredSize(new Dimension(150, 22));
        panelFechas.add(txtFechaDesde);
        panelFechas.add(jLabel1);
        panelFechas.add(txtFechaHasta);

        // --- CORRECCIÓN DE STRATEGY ---
        // Añadimos el radio button de .ICS
        radioICS = new javax.swing.JRadioButton("iCalendar (.ics)");
        panelFormatoRadio.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelFormatoRadio.add(radioPDF);
        panelFormatoRadio.add(radioExcel);
        panelFormatoRadio.add(radioICS); // <-- AÑADIDO
        // ------------------------------

        panelBotones.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnGenerar);

        // --- 2. CONFIGURAR PANEL PRINCIPAL ---
        panelFormulario.setLayout(new MigLayout("wrap 2, fillx, insets 20", "[right, 100!][grow, fill]"));
        panelFormulario.add(lblTituloReportes, "span 2, growx");
        panelFormulario.add(jSeparator1, "span 2, growx, gaptop 5, gapbottom 10");
        panelFormulario.add(lblTipoReporte, "gaptop 5");
        panelFormulario.add(cmbTipoReporte, "growx, gaptop 5");
        panelFormulario.add(lblRangoFechas, "gaptop 5");
        panelFormulario.add(panelFechas, "growx, gaptop 5");
        panelFormulario.add(lblFormato, "gaptop 5");
        panelFormulario.add(panelFormatoRadio, "growx, gaptop 5");
        panelFormulario.add(jSeparator2, "span 2, growx, gaptop 10");
        panelFormulario.add(panelBotones, "span 2, growx");

        // --- 3. LÓGICA DE COMPONENTES ---
        formatoGroup = new ButtonGroup();
        formatoGroup.add(radioPDF);
        formatoGroup.add(radioExcel);
        formatoGroup.add(radioICS); // <-- AÑADIDO
        radioPDF.setSelected(true);

        // --- CORRECCIÓN DE STRATEGY ---
        // Quitamos .ics de aquí
        cmbTipoReporte.removeAllItems();
        cmbTipoReporte.addItem("Mis Tareas (Resumen)");
        cmbTipoReporte.addItem("Mis Tareas (Detallado)");
        cmbTipoReporte.addItem("Tareas de Equipo");
        // ------------------------------

        // Limpiamos textos
        lblTituloReportes.setText("Reporte de Tareas");
        lblTipoReporte.setText("Tipo de Reporte:");
        lblRangoFechas.setText("Rango de Fechas:");
        lblFormato.setText("Formato:");
        lblTitulo.setText("Generar Reportes");

        radioPDF.setText("PDF");
        radioExcel.setText("Excel");

        txtFechaDesde.setText("");
        txtFechaHasta.setText("");
        txtFechaDesde.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "YYYY-MM-DD");
        txtFechaHasta.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "YYYY-MM-DD");

        // --- 4. ACTION LISTENERS ---
        // ¡Este es el único listener que necesitamos!
        btnGenerar.addActionListener(e -> ejecutarPatronStrategy());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        panelFormulario.revalidate();
        panelFormulario.repaint();
    }

    private void generarReporteConSwingWorker() {
        // 1. Obtenemos la estrategia (Esto es rápido)
        IReporteStrategy estrategiaSeleccionada;
        if (radioPDF.isSelected()) {
            estrategiaSeleccionada = new PdfStrategy();
        } else if (radioExcel.isSelected()) {
            estrategiaSeleccionada = new ExcelStrategy();
        } else if (radioICS.isSelected()) {
            estrategiaSeleccionada = new IcsStrategy();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un formato.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Obtenemos los filtros (Esto es rápido)
        String tipoReporte = cmbTipoReporte.getSelectedItem().toString();
        Date fechaDesde = parsearFecha(txtFechaDesde.getText());
        Date fechaHasta = parsearFecha(txtFechaHasta.getText());

        // (Opcional: Poner el botón en "Generando...")
        btnGenerar.setEnabled(false);
        btnGenerar.setText("Generando...");

        // 3. Creamos el SwingWorker
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                // 4. ESTO SE EJECUTA EN OTRO HILO

                // 4a. Búsqueda LENTA en BD
                List<Tarea> tareasParaReporte = tareaDAO.getTareasPorUsuario(usuarioLogueado.getIdUsuario(), fechaDesde, fechaHasta);

                if (tareasParaReporte.isEmpty()) {
                    // Lanzamos una excepción para que 'done()' la atrape
                    throw new Exception("No se encontraron tareas con esos filtros.");
                }

                // 4b. Generación LENTA de archivo
                return estrategiaSeleccionada.generar(tareasParaReporte);
            }

            @Override
            protected void done() {
                // 5. ESTO SE EJECUTA DE VUELTA EN EL HILO DE UI
                try {
                    boolean exito = get(); // Obtenemos el resultado (true/false)
                    if (exito) {
                        // (La estrategia ya mostró su mensaje)
                    } else {
                        JOptionPane.showMessageDialog(formReportes.this, "La exportación fue cancelada.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    // Si 'get()' lanza una excepción (como la que pusimos)
                    JOptionPane.showMessageDialog(formReportes.this,
                            e.getMessage(),
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                }

                // (Volvemos a activar el botón)
                btnGenerar.setEnabled(true);
                btnGenerar.setText("Generar Reporte");
            }
        };

        // 6. Iniciamos el "asistente"
        worker.execute();
    }

    /**
     * ¡AQUÍ ESTÁ EL PATRÓN STRATEGY! El botón "Generar" llama a este método,
     * que actúa como el "Contexto".
     */
    private void ejecutarPatronStrategy() {
        // --- 1. Seleccionar la Estrategia ---
        IReporteStrategy estrategiaSeleccionada;

        if (radioPDF.isSelected()) {
            estrategiaSeleccionada = new PdfStrategy();
        } else if (radioExcel.isSelected()) {
            estrategiaSeleccionada = new ExcelStrategy();
        } else if (radioICS.isSelected()) {
            estrategiaSeleccionada = new IcsStrategy();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un formato.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- 2. Recolectar Datos (Filtros) ---
        String tipoReporte = cmbTipoReporte.getSelectedItem().toString();
        Date fechaDesde = parsearFecha(txtFechaDesde.getText());
        Date fechaHasta = parsearFecha(txtFechaHasta.getText());

        // --- 3. Obtener Tareas (según filtros) ---
        // (Por ahora, todos los reportes usan las tareas del usuario)
        // (Aquí podrías añadir lógica: si tipoReporte == "Tareas de Equipo", llamar a otro DAO)
        List<Tarea> tareasParaReporte = tareaDAO.getTareasPorUsuario(usuarioLogueado.getIdUsuario(), fechaDesde, fechaHasta);

        if (tareasParaReporte.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron tareas con esos filtros.", "Sin Resultados", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // --- 4. Ejecutar la Estrategia ---
        try {
            boolean exito = estrategiaSeleccionada.generar(tareasParaReporte);
            if (exito) {
                // El mensaje de éxito lo da la propia estrategia
            } else {
                JOptionPane.showMessageDialog(this, "La exportación fue cancelada por el usuario.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Helper para convertir el texto del JTextField a un objeto Date
     */
    private Date parsearFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            return null;
        }
        try {
            // (Ajusta el formato si es necesario)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse(fechaStr.trim());
        } catch (ParseException e) {
            System.err.println("Formato de fecha inválido: " + fechaStr);
            return null; // Opcional: mostrar error al usuario
        }
    }

    /**
     * Limpia todos los campos del formulario
     */
    private void limpiarCampos() {
        cmbTipoReporte.setSelectedIndex(0);
        txtFechaDesde.setText("");
        txtFechaHasta.setText("");
        radioPDF.setSelected(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTituloReportes = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblTipoReporte = new javax.swing.JLabel();
        cmbTipoReporte = new javax.swing.JComboBox<>();
        lblRangoFechas = new javax.swing.JLabel();
        panelFechas = new javax.swing.JPanel();
        txtFechaDesde = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtFechaHasta = new javax.swing.JTextField();
        lblFormato = new javax.swing.JLabel();
        panelFormatoRadio = new javax.swing.JPanel();
        radioPDF = new javax.swing.JRadioButton();
        radioExcel = new javax.swing.JRadioButton();
        jSeparator2 = new javax.swing.JSeparator();
        panelBotones = new javax.swing.JPanel();
        btnLimpiar = new javax.swing.JButton();
        btnGenerar = new com.synapse.ui.components.ButtonAction();
        panelHeader = new javax.swing.JPanel();
        lblTitulo = new javax.swing.JLabel();
        panelFormulario = new javax.swing.JPanel();

        lblTituloReportes.setText("jLabel1");

        lblTipoReporte.setText("jLabel1");

        cmbTipoReporte.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblRangoFechas.setText("jLabel1");

        javax.swing.GroupLayout panelFechasLayout = new javax.swing.GroupLayout(panelFechas);
        panelFechas.setLayout(panelFechasLayout);
        panelFechasLayout.setHorizontalGroup(
            panelFechasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 638, Short.MAX_VALUE)
        );
        panelFechasLayout.setVerticalGroup(
            panelFechasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        txtFechaDesde.setText("jTextField1");

        jLabel1.setText("-");

        txtFechaHasta.setText("jTextField2");

        lblFormato.setText("jLabel2");

        javax.swing.GroupLayout panelFormatoRadioLayout = new javax.swing.GroupLayout(panelFormatoRadio);
        panelFormatoRadio.setLayout(panelFormatoRadioLayout);
        panelFormatoRadioLayout.setHorizontalGroup(
            panelFormatoRadioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 638, Short.MAX_VALUE)
        );
        panelFormatoRadioLayout.setVerticalGroup(
            panelFormatoRadioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        radioPDF.setText("jRadioButton1");

        radioExcel.setText("jRadioButton2");

        javax.swing.GroupLayout panelBotonesLayout = new javax.swing.GroupLayout(panelBotones);
        panelBotones.setLayout(panelBotonesLayout);
        panelBotonesLayout.setHorizontalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 638, Short.MAX_VALUE)
        );
        panelBotonesLayout.setVerticalGroup(
            panelBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 102, Short.MAX_VALUE)
        );

        btnLimpiar.setText("jButton1");

        btnGenerar.setText("buttonAction1");

        setLayout(new java.awt.BorderLayout());

        lblTitulo.setText("jLabel1");

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(lblTitulo)
                .addContainerGap(700, Short.MAX_VALUE))
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(lblTitulo)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        add(panelHeader, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout panelFormularioLayout = new javax.swing.GroupLayout(panelFormulario);
        panelFormulario.setLayout(panelFormularioLayout);
        panelFormularioLayout.setHorizontalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 782, Short.MAX_VALUE)
        );
        panelFormularioLayout.setVerticalGroup(
            panelFormularioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 650, Short.MAX_VALUE)
        );

        add(panelFormulario, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.synapse.ui.components.ButtonAction btnGenerar;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JComboBox<String> cmbTipoReporte;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblFormato;
    private javax.swing.JLabel lblRangoFechas;
    private javax.swing.JLabel lblTipoReporte;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblTituloReportes;
    private javax.swing.JPanel panelBotones;
    private javax.swing.JPanel panelFechas;
    private javax.swing.JPanel panelFormatoRadio;
    private javax.swing.JPanel panelFormulario;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JRadioButton radioExcel;
    private javax.swing.JRadioButton radioPDF;
    private javax.swing.JTextField txtFechaDesde;
    private javax.swing.JTextField txtFechaHasta;
    // End of variables declaration//GEN-END:variables
}
