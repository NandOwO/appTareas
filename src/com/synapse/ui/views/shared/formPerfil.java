package com.synapse.ui.views.shared;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.Icon;

/**
 *
 * @author FERNANDO
 */
public class formPerfil extends javax.swing.JPanel {

    private final Icon iconoMostrar;
    private final Icon iconoOcultar;
    private final Icon iconoGuardar;
    private final char echoCharPorDefecto;
    private com.synapse.core.models.Usuario usuarioLogueado;
    private com.synapse.data.dao.UsuarioDAO usuarioDAO;

    public formPerfil(com.synapse.core.models.Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.usuarioDAO = new com.synapse.data.dao.UsuarioDAO();

        iconoMostrar = new FlatSVGIcon("icons/general/verContraseña.svg", 18, 18);
        iconoOcultar = new FlatSVGIcon("icons/general/noVerContraseña.svg", 18, 18);
        iconoGuardar = new FlatSVGIcon("icons/general/guardar.svg", 18, 18);

        initComponents();

        panelContenedor.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "background:$Table.background");
        panelPerfil.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:25;"
                + "Background:$Panel.background");
        lblTitulo.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:bold +5;");
        txtNombre.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:5,20,5,20;"
                + "Background:$Panel.background");

        txtEmail.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:5,20,5,20;"
                + "Background:$Panel.background");

        btnMostrar.setIcon(iconoMostrar);

        txtNombre.setEditable(false);
        txtNombre.setFocusable(false);
        txtEmail.setEditable(false);
        txtEmail.setFocusable(false);

        panelContraseñas.setVisible(false);
        btnGuardar.setVisible(false);
        btnCancelar.setVisible(false);
        this.echoCharPorDefecto = txtContraseña.getEchoChar();

        btnEditarPerfil.addActionListener((e) -> {
            activarEdicion();
        });

        btnCancelar.addActionListener((e) -> {
            ocultarEdicion();
        });

        btnGuardar.addActionListener((e) -> {
            guardarCambios();
        });

        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        txtNombre.setText(usuarioLogueado.getNombre());
        txtEmail.setText(usuarioLogueado.getEmail());
        lblNombre.setText(usuarioLogueado.getNombre());
        lblEmail.setText(usuarioLogueado.getEmail());
        lblID.setText(String.valueOf(usuarioLogueado.getIdUsuario()));
        lblEstado.setText(usuarioLogueado.isActivo() ? "Activada" : "Desactivada");

        if (usuarioLogueado.getFotoUrl() != null && !usuarioLogueado.getFotoUrl().isEmpty()) {
            circularImageLabel1.setImage("/icons/users/" + usuarioLogueado.getFotoUrl());
        }
    }

    private void guardarCambios() {
        String nuevoNombre = txtNombre.getText().trim();
        String nuevoEmail = txtEmail.getText().trim();
        String contraseñaActual = new String(txtContraseña.getPassword());
        String nuevaContraseña = new String(txtNueva.getPassword());
        String confirmacion = new String(txtConfirmacion.getPassword());

        if (nuevoNombre.isEmpty() || nuevoEmail.isEmpty()) {
            raven.toast.Notifications.getInstance().show(
                    raven.toast.Notifications.Type.ERROR,
                    raven.toast.Notifications.Location.TOP_CENTER,
                    "Nombre y email son obligatorios");
            return;
        }

        if (!nuevaContraseña.isEmpty()) {
            if (!nuevaContraseña.equals(confirmacion)) {
                raven.toast.Notifications.getInstance().show(
                        raven.toast.Notifications.Type.ERROR,
                        raven.toast.Notifications.Location.TOP_CENTER,
                        "Las contraseñas no coinciden");
                return;
            }

            if (contraseñaActual.isEmpty()) {
                raven.toast.Notifications.getInstance().show(
                        raven.toast.Notifications.Type.ERROR,
                        raven.toast.Notifications.Location.TOP_CENTER,
                        "Debes ingresar tu contraseña actual");
                return;
            }

            usuarioLogueado.setNombre(nuevoNombre);
            usuarioLogueado.setEmail(nuevoEmail);
            boolean exito = usuarioDAO.actualizarUsuarioConPassword(usuarioLogueado, nuevaContraseña, contraseñaActual);

            if (exito) {
                raven.toast.Notifications.getInstance().show(
                        raven.toast.Notifications.Type.SUCCESS,
                        raven.toast.Notifications.Location.TOP_CENTER,
                        "Perfil y contraseña actualizados");
                cargarDatosUsuario();
                ocultarEdicion();
            } else {
                raven.toast.Notifications.getInstance().show(
                        raven.toast.Notifications.Type.ERROR,
                        raven.toast.Notifications.Location.TOP_CENTER,
                        "Error: Contraseña actual incorrecta");
            }
        } else {
            usuarioLogueado.setNombre(nuevoNombre);
            usuarioLogueado.setEmail(nuevoEmail);
            boolean exito = usuarioDAO.actualizarUsuario(usuarioLogueado);

            if (exito) {
                raven.toast.Notifications.getInstance().show(
                        raven.toast.Notifications.Type.SUCCESS,
                        raven.toast.Notifications.Location.TOP_CENTER,
                        "Perfil actualizado correctamente");
                cargarDatosUsuario();
                ocultarEdicion();
            } else {
                raven.toast.Notifications.getInstance().show(
                        raven.toast.Notifications.Type.ERROR,
                        raven.toast.Notifications.Location.TOP_CENTER,
                        "Error al actualizar perfil");
            }
        }
    }

    private void activarEdicion() {

        panelContraseñas.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Table.background");
        txtContraseña.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Contraseña Actual");
        txtContraseña.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:5,20,5,20;"
                + "Background:$Panel.background");
        txtNueva.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nueva contraseña");
        txtNueva.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:5,20,5,20;"
                + "Background:$Panel.background");
        txtConfirmacion.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirmar contraseña");
        txtConfirmacion.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "borderWidth:0;"
                + "focusWidth:0;"
                + "innerFocusWidth:0;"
                + "margin:5,20,5,20;"
                + "Background:$Panel.background");

        txtNombre.setEditable(true);
        txtNombre.setFocusable(true);
        txtEmail.setEditable(true);
        txtEmail.setFocusable(true);

        panelContraseñas.setVisible(true);
        btnGuardar.setVisible(true);
        btnGuardar.setIcon(iconoGuardar);
        btnCancelar.setVisible(true);

        btnEditarPerfil.setVisible(false);

        btnMostrar.setVisible(true);

        btnMostrar.addActionListener((e) -> {
            if (txtNueva.getEchoChar() != 0) {
                txtContraseña.setEchoChar((char) 0);
                txtNueva.setEchoChar((char) 0);
                txtConfirmacion.setEchoChar((char) 0);
                btnMostrar.setText("Ocultar");
                btnMostrar.setIcon(iconoOcultar);
            } else {
                txtContraseña.setEchoChar(echoCharPorDefecto);
                txtNueva.setEchoChar(echoCharPorDefecto);
                txtConfirmacion.setEchoChar(echoCharPorDefecto);
                btnMostrar.setText("Mostrar");
                btnMostrar.setIcon(iconoMostrar);
            }
        });
        revalidate();
        repaint();
    }

    private void ocultarEdicion() {
        txtNombre.setEditable(false);
        txtNombre.setFocusable(false);
        txtEmail.setEditable(false);
        txtEmail.setFocusable(false);

        panelContraseñas.setVisible(false);
        btnGuardar.setVisible(false);
        btnCancelar.setVisible(false);

        btnEditarPerfil.setVisible(true);

        txtContraseña.setText("");
        txtNueva.setText("");
        txtConfirmacion.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelContenedor = new javax.swing.JPanel();
        panelPerfil = new javax.swing.JPanel();
        circularImageLabel1 = new com.synapse.ui.components.CircularImageLabel();
        lblNombre = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblTitulo = new javax.swing.JLabel();
        btnEditarPerfil = new com.synapse.ui.components.ButtonAction();
        lblInformacion = new javax.swing.JLabel();
        lblDatos = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblID = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        panelContraseñas = new javax.swing.JPanel();
        txtNueva = new javax.swing.JPasswordField();
        txtContraseña = new javax.swing.JPasswordField();
        txtConfirmacion = new javax.swing.JPasswordField();
        lblcontraseña = new javax.swing.JLabel();
        lblNueva = new javax.swing.JLabel();
        lblConfirmacion = new javax.swing.JLabel();
        btnMostrar = new com.synapse.ui.components.ButtonAction();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnGuardar = new com.synapse.ui.components.ButtonAction();
        btnCancelar = new com.synapse.ui.components.ButtonAction();

        circularImageLabel1.setText("circularImageLabel1");

        lblNombre.setText("jLabel1");

        lblEmail.setText("jLabel1");

        javax.swing.GroupLayout panelPerfilLayout = new javax.swing.GroupLayout(panelPerfil);
        panelPerfil.setLayout(panelPerfilLayout);
        panelPerfilLayout.setHorizontalGroup(
                panelPerfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelPerfilLayout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(circularImageLabel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addGroup(
                                        panelPerfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(lblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 215,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 215,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(55, Short.MAX_VALUE)));
        panelPerfilLayout.setVerticalGroup(
                panelPerfilLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelPerfilLayout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(panelPerfilLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelPerfilLayout.createSequentialGroup()
                                                .addComponent(lblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 26,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(circularImageLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 101,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(22, Short.MAX_VALUE)));

        lblTitulo.setText("Mi Perfil");

        btnEditarPerfil.setBackground(new java.awt.Color(0, 0, 0));
        btnEditarPerfil.setForeground(new java.awt.Color(255, 255, 255));
        btnEditarPerfil.setText("Editar Perfil");
        btnEditarPerfil.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        lblInformacion.setText("Informacion Personal");

        lblDatos.setText("Nombre Completo *");

        jLabel1.setText("Correo electronico *");

        jLabel5.setText("Informacion del Sistema");

        jLabel6.setText("ID Usuario: ");

        lblID.setText("1");

        jLabel7.setText("Estado de la cuenta: ");

        lblEstado.setText("Activada");

        txtContraseña.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContraseñaActionPerformed(evt);
            }
        });

        lblcontraseña.setText("Contraseña Actual ");

        lblNueva.setText("Nueva Contraseña ");

        lblConfirmacion.setText("Confirmar Nueva ");

        btnMostrar.setText("Mostrar");
        btnMostrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jLabel2.setText("Cambiar Contraseña");

        jLabel3.setText("Deja estos campos vacios si no deseas cambiar tu contraseña");

        javax.swing.GroupLayout panelContraseñasLayout = new javax.swing.GroupLayout(panelContraseñas);
        panelContraseñas.setLayout(panelContraseñasLayout);
        panelContraseñasLayout.setHorizontalGroup(
                panelContraseñasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelContraseñasLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addGroup(panelContraseñasLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelContraseñasLayout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(panelContraseñasLayout.createSequentialGroup()
                                                .addGroup(panelContraseñasLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(panelContraseñasLayout.createSequentialGroup()
                                                                .addComponent(jLabel2)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(btnMostrar,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(panelContraseñasLayout.createSequentialGroup()
                                                                .addGroup(panelContraseñasLayout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(panelContraseñasLayout
                                                                                .createSequentialGroup()
                                                                                .addComponent(txtContraseña)
                                                                                .addGap(28, 28, 28))
                                                                        .addGroup(panelContraseñasLayout
                                                                                .createSequentialGroup()
                                                                                .addComponent(lblcontraseña,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addGap(26, 26, 26)))
                                                                .addGroup(panelContraseñasLayout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(panelContraseñasLayout
                                                                                .createSequentialGroup()
                                                                                .addComponent(lblNueva,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                        Short.MAX_VALUE)
                                                                                .addGap(29, 29, 29))
                                                                        .addGroup(panelContraseñasLayout
                                                                                .createSequentialGroup()
                                                                                .addComponent(txtNueva)
                                                                                .addGap(18, 18, 18)))
                                                                .addGroup(panelContraseñasLayout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(lblConfirmacion,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                127,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(txtConfirmacion))))
                                                .addGap(23, 23, 23)))));
        panelContraseñasLayout.setVerticalGroup(
                panelContraseñasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelContraseñasLayout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(panelContraseñasLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnMostrar, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2))
                                .addGap(5, 5, 5)
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addGroup(panelContraseñasLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblcontraseña)
                                        .addComponent(lblNueva)
                                        .addComponent(lblConfirmacion))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelContraseñasLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtContraseña, javax.swing.GroupLayout.DEFAULT_SIZE, 31,
                                                Short.MAX_VALUE)
                                        .addComponent(txtNueva, javax.swing.GroupLayout.DEFAULT_SIZE, 31,
                                                Short.MAX_VALUE)
                                        .addComponent(txtConfirmacion, javax.swing.GroupLayout.DEFAULT_SIZE, 31,
                                                Short.MAX_VALUE))
                                .addGap(15, 15, 15)));

        btnGuardar.setBackground(new java.awt.Color(0, 0, 0));
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setText("Guardar Cambios");
        btnGuardar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        btnCancelar.setText("Cancelar");
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        javax.swing.GroupLayout panelContenedorLayout = new javax.swing.GroupLayout(panelContenedor);
        panelContenedor.setLayout(panelContenedorLayout);
        panelContenedorLayout.setHorizontalGroup(
                panelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelContenedorLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(panelContenedorLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelContenedorLayout
                                                .createSequentialGroup()
                                                .addGroup(panelContenedorLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(panelContenedorLayout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.TRAILING,
                                                                        false)
                                                                .addComponent(lblDatos,
                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(lblInformacion,
                                                                        javax.swing.GroupLayout.Alignment.LEADING,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE))
                                                        .addComponent(txtNombre))
                                                .addGap(37, 37, 37)
                                                .addGroup(panelContenedorLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtEmail)
                                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addComponent(panelPerfil, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelContenedorLayout
                                                .createSequentialGroup()
                                                .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 231,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnEditarPerfil, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(28, 28, 28))
                        .addGroup(panelContenedorLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(panelContraseñas, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(panelContenedorLayout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addGroup(panelContenedorLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelContenedorLayout.createSequentialGroup()
                                                .addGroup(panelContenedorLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblID)
                                                        .addComponent(jLabel6))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(panelContenedorLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel7)
                                                        .addComponent(lblEstado, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(110, 110, 110))
                                        .addGroup(panelContenedorLayout.createSequentialGroup()
                                                .addGroup(panelContenedorLayout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel5)
                                                        .addGroup(panelContenedorLayout.createSequentialGroup()
                                                                .addComponent(btnGuardar,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnCancelar,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(0, 0, Short.MAX_VALUE)))));
        panelContenedorLayout.setVerticalGroup(
                panelContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(panelContenedorLayout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(panelContenedorLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnEditarPerfil, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(panelPerfil, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblInformacion)
                                .addGap(18, 18, 18)
                                .addGroup(panelContenedorLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblDatos)
                                        .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panelContenedorLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(panelContraseñas, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panelContenedorLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(panelContenedorLayout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(lblID))
                                        .addGroup(panelContenedorLayout.createSequentialGroup()
                                                .addComponent(jLabel7)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(lblEstado)))
                                .addGap(33, 33, 33)
                                .addGroup(panelContenedorLayout
                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24)));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(46, 46, 46)
                                .addComponent(panelContenedor, javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(46, 46, 46)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(panelContenedor, javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    }// </editor-fold>//GEN-END:initComponents

    private void txtContraseñaActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtContraseñaActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_txtContraseñaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.synapse.ui.components.ButtonAction btnCancelar;
    private com.synapse.ui.components.ButtonAction btnEditarPerfil;
    private com.synapse.ui.components.ButtonAction btnGuardar;
    private com.synapse.ui.components.ButtonAction btnMostrar;
    private com.synapse.ui.components.CircularImageLabel circularImageLabel1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lblConfirmacion;
    private javax.swing.JLabel lblDatos;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblInformacion;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNueva;
    private javax.swing.JLabel lblTitulo;
    private javax.swing.JLabel lblcontraseña;
    private javax.swing.JPanel panelContenedor;
    private javax.swing.JPanel panelContraseñas;
    private javax.swing.JPanel panelPerfil;
    private javax.swing.JPasswordField txtConfirmacion;
    private javax.swing.JPasswordField txtContraseña;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JPasswordField txtNueva;
    // End of variables declaration//GEN-END:variables
}
