package com.synapse.ui.views.gerente;

import com.formdev.flatlaf.FlatClientProperties;
import com.synapse.core.models.Equipo;
import com.synapse.core.models.Tarea;
import com.synapse.core.models.Usuario;
import com.synapse.core.services.TareaService;
import com.synapse.core.services.UsuarioService;
import com.synapse.core.services.EquipoService;
import com.synapse.core.services.notifications.EmailService;
import com.synapse.data.dao.EquipoDAO;
import com.synapse.utils.Validator;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.SwingWorker;
import raven.toast.Notifications;

/**
 *
 * @author FERNANDO
 */
public class formCrearTarea extends javax.swing.JPanel {

        private class DatosCombos {
                List<Usuario> empleados;
                List<Equipo> equipos;
        }

        private File[] mArchivosAdjuntos;
        private String nombres_archivos;

        // --- BACKEND INTEGRATION ---
        private final Usuario usuarioLogueado;
        private final TareaService tareaService;
        private final UsuarioService usuarioService;
        private final EquipoService equipoService;
        private List<Usuario> listaEmpleados;
        private List<Equipo> listaEquipos;
        // ---------------------------

        public formCrearTarea(Usuario usuario) {
                this.usuarioLogueado = usuario;
                this.tareaService = new TareaService();
                this.usuarioService = new UsuarioService();
                this.equipoService = new EquipoService();

                initComponents();
                nombres_archivos = "";
                datePicker.setCloseAfterSelected(true);
                datePicker.setEditor(txtFecha);

                datePicker.setDateSelectionAble(
                                localDate -> !localDate.isBefore(java.time.LocalDate.now().plusDays(1)));

                scroll.putClientProperty(FlatClientProperties.STYLE, ""
                                + "arc:25;"
                                + "background:$Table.background");

                contenedorForm.putClientProperty(FlatClientProperties.STYLE, ""
                                + "background:$Table.background");

                contenedorPrincipal.putClientProperty(FlatClientProperties.STYLE, ""
                                + "background:$Table.background");

                panelInformativo.putClientProperty(FlatClientProperties.STYLE, ""
                                + "arc:25;");

                scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, ""
                                + "trackArc:999;"
                                + "trackInsets:3,3,3,3;"
                                + "thumbInsets:3,3,3,3;"
                                + "background:$Panel.background;");

                txtTitulo.putClientProperty(FlatClientProperties.STYLE, ""
                                + "arc:15;"
                                + "borderWidth:0;"
                                + "focusWidth:0;"
                                + "innerFocusWidth:0;"
                                + "margin:5,20,5,20;"
                                + "Background:$Panel.background");

                txtTitulo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ingresa el titulo de la tarea");

                txtDescripcion.putClientProperty(FlatClientProperties.STYLE, ""
                                + "margin:10,15,10,15;"
                                + "Background:$Panel.background");

                ScrollDescripcion.putClientProperty(FlatClientProperties.STYLE, ""
                                + "arc:15;"
                                + "borderWidth:0;"
                                + "focusWidth:0;"
                                + "innerFocusWidth:0;");
                ScrollDescripcion.getViewport().putClientProperty(FlatClientProperties.STYLE,
                                "background:$Panel.background");

                ScrollDescripcion.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                                "Describe Detalladamente la tarea");
                txtDescripcion.setOpaque(false);

                // --- CARGAR DATOS EN COMBOS ---
                cargarDatosEnCombos();

                // --- CONFIGURAR BOTONES DE ADJUNTOS ---
                btnImagen.addActionListener(e -> seleccionarImagen());
        }

        /**
         * Carga los datos de usuarios y equipos en los combos
         */
        private void cargarDatosEnCombos() {
                SwingWorker<DatosCombos, Void> worker = new SwingWorker<DatosCombos, Void>() {
                        @Override
                        protected DatosCombos doInBackground() throws Exception {
                                DatosCombos datos = new DatosCombos();

                                // Obtener solo empleados (no admins ni gerentes)
                                List<Usuario> todosUsuarios = usuarioService.getUsuarios();
                                datos.empleados = new ArrayList<>();
                                for (Usuario usuario : todosUsuarios) {
                                        if ("Empleado".equalsIgnoreCase(usuario.getRol())) {
                                                datos.empleados.add(usuario);
                                        }
                                }

                                // Obtener solo equipos donde el usuario logueado es líder
                                EquipoDAO equipoDAO = new EquipoDAO();
                                datos.equipos = equipoDAO.getEquiposPorLider(usuarioLogueado.getIdUsuario());

                                return datos;
                        }

                        @Override
                        protected void done() {
                                try {
                                        DatosCombos datos = get();
                                        listaEmpleados = datos.empleados;
                                        listaEquipos = datos.equipos;

                                        DefaultComboBoxModel<String> modeloEmpleados = new DefaultComboBoxModel<>();
                                        modeloEmpleados.addElement("-- Seleccione un empleado --");
                                        for (Usuario emp : listaEmpleados) {
                                                modeloEmpleados.addElement(
                                                                emp.getNombre() + " (" + emp.getEmail() + ")");
                                        }
                                        comboEquipoEmpleado.setModel(modeloEmpleados);

                                        // Llenar combo de equipos
                                        DefaultComboBoxModel<String> modeloEquipos = new DefaultComboBoxModel<>();
                                        modeloEquipos.addElement("-- Seleccione un equipo --");
                                        for (Equipo eq : listaEquipos) {
                                                modeloEquipos.addElement(eq.getNombre());
                                        }

                                } catch (Exception e) {
                                        e.printStackTrace();
                                        Notifications.getInstance().show(Notifications.Type.ERROR,
                                                        Notifications.Location.TOP_CENTER,
                                                        "Error al cargar datos: " + e.getMessage());
                                }
                        }
                };
                worker.execute();
        }

        /**
         * Valida los campos del formulario
         */
        private boolean validarCampos() {
                String titulo = txtTitulo.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                String fecha = txtFecha.getText().trim();

                if (!Validator.validarCampoRequerido(titulo)) {
                        Notifications.getInstance().show(Notifications.Type.WARNING,
                                        Notifications.Location.TOP_CENTER,
                                        "El título es obligatorio");
                        txtTitulo.requestFocus();
                        return false;
                }

                if (!Validator.validarCampoRequerido(descripcion)) {
                        Notifications.getInstance().show(Notifications.Type.WARNING,
                                        Notifications.Location.TOP_CENTER,
                                        "La descripción es obligatoria");
                        txtDescripcion.requestFocus();
                        return false;
                }

                if (!Validator.validarCampoRequerido(fecha)) {
                        Notifications.getInstance().show(Notifications.Type.WARNING,
                                        Notifications.Location.TOP_CENTER,
                                        "La fecha límite es obligatoria");
                        txtFecha.requestFocus();
                        return false;
                }

                // Validar que la fecha no sea anterior a hoy
                try {
                        java.time.LocalDate fechaSeleccionada = datePicker.getSelectedDate();
                        java.time.LocalDate hoy = java.time.LocalDate.now();

                        if (fechaSeleccionada != null && fechaSeleccionada.isBefore(hoy)) {
                                Notifications.getInstance().show(Notifications.Type.WARNING,
                                                Notifications.Location.TOP_CENTER,
                                                "La fecha límite no puede ser anterior a hoy");
                                txtFecha.requestFocus();
                                return false;
                        }
                } catch (Exception e) {
                        // Si hay error al parsear la fecha, continuar con otras validaciones
                }

                // Validar que se haya seleccionado un empleado o equipo
                int tipoAsignacion = comboTipoAsignacion.getSelectedIndex();
                if (tipoAsignacion == 0) { // Empleado
                        if (comboEquipoEmpleado.getSelectedIndex() == 0) {
                                Notifications.getInstance().show(Notifications.Type.WARNING,
                                                Notifications.Location.TOP_CENTER,
                                                "Debe seleccionar un empleado");
                                return false;
                        }
                }

                return true;
        }

        /**
         * Guarda la tarea en la base de datos
         */
        private void guardarTarea() {
                if (!validarCampos()) {
                        return;
                }

                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                                // Obtener datos del formulario
                                String titulo = txtTitulo.getText().trim();
                                String descripcion = txtDescripcion.getText().trim();

                                // Convertir LocalDate a Timestamp
                                java.time.LocalDate localDate = datePicker.getSelectedDate();
                                java.sql.Timestamp fechaLimite = java.sql.Timestamp.valueOf(
                                                localDate.atStartOfDay());

                                // Prioridad: 1=Baja, 2=Media, 3=Alta
                                int prioridad = comboPrioridad.getSelectedIndex() + 1;

                                // Estado: 1=Pendiente por defecto
                                int estado = 1;

                                // Crear tarea con Builder
                                Tarea nuevaTarea = new Tarea.Builder(titulo, usuarioLogueado.getIdUsuario())
                                                .descripcion(descripcion)
                                                .fechaLimite(fechaLimite)
                                                .idPrioridad(prioridad)
                                                .idEstado(estado)
                                                .build();

                                // Determinar asignación
                                Integer idUsuarioAsignado = null;
                                Integer idEquipoAsignado = null;

                                String tipoSeleccionado = (String) comboTipoAsignacion.getSelectedItem();
                                if ("Asignar a Empleado Individual".equals(tipoSeleccionado)) {
                                        // Asignar a empleado individual
                                        int indexEmpleado = comboEquipoEmpleado.getSelectedIndex() - 1;
                                        if (indexEmpleado >= 0 && indexEmpleado < listaEmpleados.size()) {
                                                idUsuarioAsignado = listaEmpleados.get(indexEmpleado).getIdUsuario();
                                        }
                                } else if ("Asignar a Equipo".equals(tipoSeleccionado)) {
                                        // Asignar a equipo
                                        int indexEquipo = comboEquipoEmpleado.getSelectedIndex() - 1;
                                        if (indexEquipo >= 0 && indexEquipo < listaEquipos.size()) {
                                                idEquipoAsignado = listaEquipos.get(indexEquipo).getIdEquipo();
                                        }
                                }

                                // Guardar con el servicio
                                return tareaService.crearTareaCompleta(nuevaTarea, idUsuarioAsignado, idEquipoAsignado);
                        }

                        @Override
                        protected void done() {
                                try {
                                        Boolean exito = get();
                                        if (exito) {
                                                // Enviar correo de notificación
                                                enviarNotificacionEmail();

                                                Notifications.getInstance().show(Notifications.Type.SUCCESS,
                                                                Notifications.Location.TOP_CENTER,
                                                                "✅ Tarea creada y notificación enviada");
                                                limpiarFormulario();
                                        } else {
                                                Notifications.getInstance().show(Notifications.Type.ERROR,
                                                                Notifications.Location.TOP_CENTER,
                                                                "❌ Error al crear la tarea");
                                        }
                                } catch (Exception e) {
                                        e.printStackTrace();
                                        Notifications.getInstance().show(Notifications.Type.ERROR,
                                                        Notifications.Location.TOP_CENTER,
                                                        "Error: " + e.getMessage());
                                }
                        }
                };
                worker.execute();
        }

        /**
         * Envía notificación por correo al usuario asignado o a los miembros del equipo
         */
        private void enviarNotificacionEmail() {
                try {
                        String tipoSeleccionado = (String) comboTipoAsignacion.getSelectedItem();

                        // Convertir LocalDate a Timestamp para la tarea
                        java.time.LocalDate localDate = datePicker.getSelectedDate();
                        java.sql.Timestamp fechaLimite = null;
                        if (localDate != null) {
                                fechaLimite = java.sql.Timestamp.valueOf(localDate.atTime(23, 59, 59));
                        }

                        // Crear tarea temporal con datos básicos para el email
                        Tarea tareaTemp = new Tarea.Builder(txtTitulo.getText().trim(),
                                        usuarioLogueado.getIdUsuario())
                                        .descripcion(txtDescripcion.getText().trim())
                                        .fechaLimite(fechaLimite)
                                        .build();

                        // Convertir archivos adjuntos a List (incluye imágenes Y archivos)
                        List<File> adjuntos = new ArrayList<>();
                        if (mArchivosAdjuntos != null) {
                                for (File archivo : mArchivosAdjuntos) {
                                        adjuntos.add(archivo);
                                }
                                System.out.println("Total de adjuntos: " + adjuntos.size());
                        }

                        EmailService emailService = new EmailService();

                        if ("Asignar a Empleado Individual".equals(tipoSeleccionado)) {
                                // Asignación individual
                                int indexEmpleado = comboEquipoEmpleado.getSelectedIndex() - 1;
                                if (indexEmpleado >= 0 && indexEmpleado < listaEmpleados.size()) {
                                        Usuario empleadoAsignado = listaEmpleados.get(indexEmpleado);
                                        emailService.enviarEmailAsignacion(tareaTemp, empleadoAsignado, adjuntos);
                                        System.out.println("Email enviado a: " + empleadoAsignado.getEmail());
                                }
                        } else if ("Asignar a Equipo".equals(tipoSeleccionado)) {
                                // Asignación a equipo - enviar a todos los miembros
                                int indexEquipo = comboEquipoEmpleado.getSelectedIndex() - 1;
                                if (indexEquipo >= 0 && indexEquipo < listaEquipos.size()) {
                                        Equipo equipoSeleccionado = listaEquipos.get(indexEquipo);

                                        // Obtener miembros del equipo
                                        EquipoDAO equipoDAO = new EquipoDAO();
                                        List<Usuario> miembros = equipoDAO
                                                        .getMiembros(equipoSeleccionado.getIdEquipo());

                                        // Enviar email a cada miembro
                                        for (Usuario miembro : miembros) {
                                                emailService.enviarEmailAsignacion(tareaTemp, miembro, adjuntos);
                                                System.out.println("Email enviado a miembro: " + miembro.getEmail());
                                        }

                                        System.out.println("Emails enviados a " + miembros.size()
                                                        + " miembros del equipo " + equipoSeleccionado.getNombre());
                                }
                        }
                } catch (Exception e) {
                        System.err.println("Error al enviar email: " + e.getMessage());
                        e.printStackTrace();
                        // No mostramos error al usuario para no interrumpir el flujo
                }
        }

        /**
         * Permite seleccionar archivos de imagen
         */
        private void seleccionarImagen() {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                                "Imágenes (JPG, PNG, GIF)", "jpg", "jpeg", "png", "gif"));
                fileChooser.setMultiSelectionEnabled(true);

                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        File[] archivos = fileChooser.getSelectedFiles();
                        if (archivos != null && archivos.length > 0) {
                                // Combinar con archivos existentes si los hay
                                if (mArchivosAdjuntos != null) {
                                        File[] nuevosArchivos = new File[mArchivosAdjuntos.length + archivos.length];
                                        System.arraycopy(mArchivosAdjuntos, 0, nuevosArchivos, 0,
                                                        mArchivosAdjuntos.length);
                                        System.arraycopy(archivos, 0, nuevosArchivos, mArchivosAdjuntos.length,
                                                        archivos.length);
                                        mArchivosAdjuntos = nuevosArchivos;
                                } else {
                                        mArchivosAdjuntos = archivos;
                                }

                                // Actualizar label
                                lblImagen.setText(archivos.length + " imagen(es) seleccionada(s)");

                                Notifications.getInstance().show(Notifications.Type.SUCCESS,
                                                Notifications.Location.TOP_CENTER,
                                                "✅ " + archivos.length + " imagen(es) agregada(s)");
                        }
                }
        }

        /**
         * Limpia todos los campos del formulario
         */
        private void limpiarFormulario() {
                txtTitulo.setText("");
                txtDescripcion.setText("");
                txtFecha.setText("");
                comboTipoAsignacion.setSelectedIndex(0);
                comboEquipoEmpleado.setSelectedIndex(0);
                comboPrioridad.setSelectedIndex(0);
                mArchivosAdjuntos = null;
                nombres_archivos = "";
                lblArchivos.setText("...");
                lblImagen.setText("...");
        }

        /**
         * This method is called from within the constructor to initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is always
         * regenerated by the Form Editor.
         */
        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                datePicker = new raven.datetime.component.date.DatePicker();
                scroll = new javax.swing.JScrollPane();
                contenedorPrincipal = new javax.swing.JPanel();
                contenedorForm = new javax.swing.JPanel();
                lblTitulo1 = new javax.swing.JLabel();
                lblTarea = new javax.swing.JLabel();
                txtTitulo = new javax.swing.JTextField();
                lblDescripcion = new javax.swing.JLabel();
                lblAsignacion = new javax.swing.JLabel();
                comboTipoAsignacion = new javax.swing.JComboBox<>();
                ScrollDescripcion = new javax.swing.JScrollPane();
                txtDescripcion = new javax.swing.JTextArea();
                jLabel1 = new javax.swing.JLabel();
                comboEquipoEmpleado = new javax.swing.JComboBox<>();
                jLabel2 = new javax.swing.JLabel();
                jLabel3 = new javax.swing.JLabel();
                comboPrioridad = new javax.swing.JComboBox<>();
                btnImagen = new com.synapse.ui.components.ButtonAction();
                btnArchivos = new com.synapse.ui.components.ButtonAction();
                jLabel4 = new javax.swing.JLabel();
                jLabel5 = new javax.swing.JLabel();
                panelInformativo = new javax.swing.JPanel();
                jLabel6 = new javax.swing.JLabel();
                jLabel7 = new javax.swing.JLabel();
                jLabel8 = new javax.swing.JLabel();
                jLabel9 = new javax.swing.JLabel();
                jLabel10 = new javax.swing.JLabel();
                btnCrearTarea = new com.synapse.ui.components.ButtonAction();
                lblImagen = new javax.swing.JLabel();
                lblArchivos = new javax.swing.JLabel();
                txtFecha = new javax.swing.JFormattedTextField();

                scroll.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                scroll.setToolTipText("");
                scroll.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

                lblTitulo1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
                lblTitulo1.setText("Crear Tareas");

                lblTarea.setText("Titulo de la tarea *");

                lblDescripcion.setText("Descripcion *");

                lblAsignacion.setText("Tipo de Asignacion *");

                comboTipoAsignacion.setModel(new javax.swing.DefaultComboBoxModel<>(
                                new String[] { "Asignar a Empleado Individual", "Asignar a Equipo" }));
                comboTipoAsignacion.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                comboTipoAsignacionActionPerformed(evt);
                        }
                });

                txtDescripcion.setColumns(20);
                txtDescripcion.setLineWrap(true);
                txtDescripcion.setRows(5);
                txtDescripcion.setWrapStyleWord(true);
                ScrollDescripcion.setViewportView(txtDescripcion);

                jLabel1.setText("Asignar a Empleado *");

                comboEquipoEmpleado.setModel(new javax.swing.DefaultComboBoxModel<>(
                                new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

                jLabel2.setText("Fecha Limite *");

                jLabel3.setText("Prioridad");

                comboPrioridad.setModel(
                                new javax.swing.DefaultComboBoxModel<>(new String[] { "Baja", "Media", "Alta" }));

                btnImagen.setBackground(new java.awt.Color(0, 0, 0));
                btnImagen.setForeground(new java.awt.Color(255, 255, 255));
                btnImagen.setText("Agregar Imagen");

                btnArchivos.setBackground(new java.awt.Color(0, 0, 0));
                btnArchivos.setForeground(new java.awt.Color(255, 255, 255));
                btnArchivos.setText("Agregar Archivo");
                btnArchivos.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                btnArchivosActionPerformed(evt);
                        }
                });

                jLabel4.setText("Imágenes");

                jLabel5.setText("Archivos");

                panelInformativo.setBackground(new java.awt.Color(239, 246, 255));

                jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
                jLabel6.setForeground(new java.awt.Color(25, 60, 184));
                jLabel6.setText("Informacion Adicional");

                jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
                jLabel7.setForeground(new java.awt.Color(25, 60, 184));
                jLabel7.setText("• La tarea será asignada inmediatamente al empleado seleccionado");

                jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
                jLabel8.setForeground(new java.awt.Color(25, 60, 184));
                jLabel8.setText("• Se enviará una notificación automática cuando se acerque la fecha límite");

                jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
                jLabel9.setForeground(new java.awt.Color(25, 60, 184));
                jLabel9.setText("• El empleado podrá actualizar el estado de la tarea");

                jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
                jLabel10.setForeground(new java.awt.Color(25, 60, 184));
                jLabel10.setText("• Puedes hacer seguimiento del progreso desde el panel de gestión");

                javax.swing.GroupLayout panelInformativoLayout = new javax.swing.GroupLayout(panelInformativo);
                panelInformativo.setLayout(panelInformativoLayout);
                panelInformativoLayout.setHorizontalGroup(
                                panelInformativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(panelInformativoLayout.createSequentialGroup()
                                                                .addGap(17, 17, 17)
                                                                .addGroup(panelInformativoLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jLabel10,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                449,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel7,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                477,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel6)
                                                                                .addComponent(jLabel8)
                                                                                .addComponent(jLabel9,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                449,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addContainerGap(417, Short.MAX_VALUE)));
                panelInformativoLayout.setVerticalGroup(
                                panelInformativoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(panelInformativoLayout.createSequentialGroup()
                                                                .addGap(14, 14, 14)
                                                                .addComponent(jLabel6)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jLabel7)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jLabel8)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jLabel9)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jLabel10)
                                                                .addContainerGap(16, Short.MAX_VALUE)));

                btnCrearTarea.setBackground(new java.awt.Color(0, 0, 0));
                btnCrearTarea.setForeground(new java.awt.Color(255, 255, 255));
                btnCrearTarea.setText("Crear Tarea");
                btnCrearTarea.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                btnCrearTareaActionPerformed(evt);
                        }
                });

                lblImagen.setText("...");

                lblArchivos.setText("...");

                javax.swing.GroupLayout contenedorFormLayout = new javax.swing.GroupLayout(contenedorForm);
                contenedorForm.setLayout(contenedorFormLayout);
                contenedorFormLayout.setHorizontalGroup(
                                contenedorFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(contenedorFormLayout.createSequentialGroup()
                                                                .addGap(27, 27, 27)
                                                                .addGroup(contenedorFormLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(contenedorFormLayout
                                                                                                .createSequentialGroup()
                                                                                                .addGroup(contenedorFormLayout
                                                                                                                .createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                .addComponent(jLabel4,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                146,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                .addGroup(contenedorFormLayout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addComponent(btnImagen,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                181,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addGap(18, 18, 18)
                                                                                                                                .addComponent(lblImagen,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                88,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                .addGroup(contenedorFormLayout
                                                                                                                                .createSequentialGroup()
                                                                                                                                .addComponent(btnArchivos,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                181,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                .addGap(18, 18, 18)
                                                                                                                                .addComponent(lblArchivos,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                67,
                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                .addComponent(jLabel5,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                129,
                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                contenedorFormLayout
                                                                                                                .createSequentialGroup()
                                                                                                                .addGroup(contenedorFormLayout
                                                                                                                                .createParallelGroup(
                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                                                .addComponent(btnCrearTarea,
                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                .addComponent(panelInformativo,
                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                .addComponent(ScrollDescripcion)
                                                                                                                                .addComponent(txtTitulo)
                                                                                                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                contenedorFormLayout
                                                                                                                                                                .createSequentialGroup()
                                                                                                                                                                .addGroup(contenedorFormLayout
                                                                                                                                                                                .createParallelGroup(
                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                                                                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                                                                contenedorFormLayout
                                                                                                                                                                                                                .createParallelGroup(
                                                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                                                                                                false)
                                                                                                                                                                                                                .addComponent(lblTitulo1,
                                                                                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                                                                                                .addComponent(lblTarea,
                                                                                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                                                                Short.MAX_VALUE))
                                                                                                                                                                                .addComponent(lblDescripcion,
                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                114,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                                                                                                .addGroup(contenedorFormLayout
                                                                                                                                                .createSequentialGroup()
                                                                                                                                                .addGroup(contenedorFormLayout
                                                                                                                                                                .createParallelGroup(
                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                                                false)
                                                                                                                                                                .addComponent(lblAsignacion,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                130,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                .addComponent(jLabel2,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                196,
                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                .addComponent(comboTipoAsignacion,
                                                                                                                                                                                0,
                                                                                                                                                                                450,
                                                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                                                .addComponent(txtFecha))
                                                                                                                                                .addGap(18, 18, 18)
                                                                                                                                                .addGroup(contenedorFormLayout
                                                                                                                                                                .createParallelGroup(
                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                                .addComponent(comboPrioridad,
                                                                                                                                                                                0,
                                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                                                .addComponent(comboEquipoEmpleado,
                                                                                                                                                                                0,
                                                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                                                .addGroup(contenedorFormLayout
                                                                                                                                                                                .createSequentialGroup()
                                                                                                                                                                                .addGroup(contenedorFormLayout
                                                                                                                                                                                                .createParallelGroup(
                                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                                                                .addComponent(jLabel3,
                                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                147,
                                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                                .addComponent(jLabel1,
                                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                                148,
                                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                                                                                .addGap(0, 0, Short.MAX_VALUE)))))
                                                                                                                .addGap(24, 24, 24)))));
                contenedorFormLayout.setVerticalGroup(
                                contenedorFormLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(contenedorFormLayout.createSequentialGroup()
                                                                .addGap(14, 14, 14)
                                                                .addComponent(lblTitulo1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                32,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(lblTarea)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtTitulo,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                37,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(lblDescripcion)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(ScrollDescripcion,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addGroup(contenedorFormLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(lblAsignacion,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                16,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(jLabel1))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(contenedorFormLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(comboTipoAsignacion,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                37,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(comboEquipoEmpleado,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                37,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(contenedorFormLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(jLabel2)
                                                                                .addComponent(jLabel3))
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(contenedorFormLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                false)
                                                                                .addComponent(comboPrioridad,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                37, Short.MAX_VALUE)
                                                                                .addComponent(txtFecha))
                                                                .addGap(24, 24, 24)
                                                                .addComponent(jLabel4,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                16,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(contenedorFormLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(btnImagen,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                36,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(lblImagen,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                43,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel5)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(contenedorFormLayout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                .addComponent(btnArchivos,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                38,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addComponent(lblArchivos,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                52,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addComponent(panelInformativo,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnCrearTarea,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                38,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)));

                javax.swing.GroupLayout contenedorPrincipalLayout = new javax.swing.GroupLayout(contenedorPrincipal);
                contenedorPrincipal.setLayout(contenedorPrincipalLayout);
                contenedorPrincipalLayout.setHorizontalGroup(
                                contenedorPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(contenedorPrincipalLayout.createSequentialGroup()
                                                                .addComponent(contenedorForm,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addGap(105, 105, 105)));
                contenedorPrincipalLayout.setVerticalGroup(
                                contenedorPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(contenedorPrincipalLayout.createSequentialGroup()
                                                                .addComponent(contenedorForm,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addGap(33, 33, 33)));

                scroll.setViewportView(contenedorPrincipal);

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addContainerGap()
                                                                .addComponent(scroll,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                981, Short.MAX_VALUE)
                                                                .addContainerGap()));
                layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(scroll, javax.swing.GroupLayout.Alignment.TRAILING,
                                                                javax.swing.GroupLayout.DEFAULT_SIZE, 865,
                                                                Short.MAX_VALUE));
        }// </editor-fold>//GEN-END:initComponents

        private void btnCrearTareaActionPerformed(java.awt.event.ActionEvent evt) {
                guardarTarea();
        }

        private void comboTipoAsignacionActionPerformed(java.awt.event.ActionEvent evt) {
                String tipoSeleccionado = (String) comboTipoAsignacion.getSelectedItem();

                // Debug
                System.out.println("Tipo seleccionado: " + tipoSeleccionado);
                System.out.println(
                                "Empleados disponibles: " + (listaEmpleados != null ? listaEmpleados.size() : "null"));
                System.out.println("Equipos disponibles: " + (listaEquipos != null ? listaEquipos.size() : "null"));

                // Limpiar combo
                comboEquipoEmpleado.removeAllItems();

                if ("Asignar a Empleado Individual".equals(tipoSeleccionado)) {
                        // Cargar empleados
                        comboEquipoEmpleado.addItem("-- Seleccione un empleado --");
                        if (listaEmpleados != null && !listaEmpleados.isEmpty()) {
                                for (Usuario empleado : listaEmpleados) {
                                        comboEquipoEmpleado.addItem(
                                                        empleado.getNombre() + " (" + empleado.getEmail() + ")");
                                }
                                System.out.println("Cargados " + listaEmpleados.size() + " empleados");
                        } else {
                                System.out.println("Lista de empleados vacía o null");
                        }
                } else if ("Asignar a Equipo".equals(tipoSeleccionado)) {
                        // Cargar equipos
                        comboEquipoEmpleado.addItem("-- Seleccione un equipo --");
                        if (listaEquipos != null && !listaEquipos.isEmpty()) {
                                for (Equipo equipo : listaEquipos) {
                                        comboEquipoEmpleado.addItem(equipo.getNombre());
                                }
                                System.out.println("Cargados " + listaEquipos.size() + " equipos");
                        } else {
                                System.out.println("Lista de equipos vacía o null");
                        }
                }
        }

        private void btnArchivosActionPerformed(java.awt.event.ActionEvent evt) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(true);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (chooser.showOpenDialog(this) != JFileChooser.CANCEL_OPTION) {
                        File[] archivosSeleccionados = chooser.getSelectedFiles();

                        // Combinar con archivos/imágenes existentes
                        if (mArchivosAdjuntos != null && mArchivosAdjuntos.length > 0) {
                                File[] nuevosArchivos = new File[mArchivosAdjuntos.length
                                                + archivosSeleccionados.length];
                                System.arraycopy(mArchivosAdjuntos, 0, nuevosArchivos, 0, mArchivosAdjuntos.length);
                                System.arraycopy(archivosSeleccionados, 0, nuevosArchivos, mArchivosAdjuntos.length,
                                                archivosSeleccionados.length);
                                mArchivosAdjuntos = nuevosArchivos;
                        } else {
                                mArchivosAdjuntos = archivosSeleccionados;
                        }

                        for (File Archivo : archivosSeleccionados) {
                                nombres_archivos += Archivo.getName() + "<br>";
                        }

                        lblArchivos.setText("<html><p>" + nombres_archivos + "<p><html>");

                        System.out.println("Total de archivos adjuntos ahora: " + mArchivosAdjuntos.length);
                }
        }// GEN-LAST:event_btnArchivosActionPerformed

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JScrollPane ScrollDescripcion;
        private com.synapse.ui.components.ButtonAction btnArchivos;
        private com.synapse.ui.components.ButtonAction btnCrearTarea;
        private com.synapse.ui.components.ButtonAction btnImagen;
        private javax.swing.JComboBox<String> comboEquipoEmpleado;
        private javax.swing.JComboBox<String> comboPrioridad;
        private javax.swing.JComboBox<String> comboTipoAsignacion;
        private javax.swing.JPanel contenedorForm;
        private javax.swing.JPanel contenedorPrincipal;
        private raven.datetime.component.date.DatePicker datePicker;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel10;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JLabel jLabel5;
        private javax.swing.JLabel jLabel6;
        private javax.swing.JLabel jLabel7;
        private javax.swing.JLabel jLabel8;
        private javax.swing.JLabel jLabel9;
        private javax.swing.JLabel lblArchivos;
        private javax.swing.JLabel lblAsignacion;
        private javax.swing.JLabel lblDescripcion;
        private javax.swing.JLabel lblImagen;
        private javax.swing.JLabel lblTarea;
        private javax.swing.JLabel lblTitulo1;
        private javax.swing.JPanel panelInformativo;
        private javax.swing.JScrollPane scroll;
        private javax.swing.JTextArea txtDescripcion;
        private javax.swing.JFormattedTextField txtFecha;
        private javax.swing.JTextField txtTitulo;
        // End of variables declaration//GEN-END:variables
}
