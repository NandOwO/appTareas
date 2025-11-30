package com.synapse.ui.views.admin;

import com.synapse.core.models.Usuario;
import com.synapse.data.dao.UsuarioDAO;
import com.synapse.utils.PasswordBuilder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.awt.Frame;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import raven.toast.Notifications;

/**
 *
 * @author FERNANDO
 */
public class CrearUsuarioDialog extends javax.swing.JDialog {

        private static String emailFrom = "system.momentum.noreply@gmail.com";
        private static String passwordFrom = "qyyfqwcvkblygnes";
        private String emailTo;
        private String Subject;
        private String Content;
        private String usuario;
        private String contraseñaGenerada;

        private Properties properties;
        private Session mSession;
        private MimeMessage mCorreo;
        private final UsuarioDAO usuarioDAO;
        private boolean exitoso = false;

        public CrearUsuarioDialog(Frame parent) {
                super(parent, "Crear Nuevo Usuario", true);
                this.usuarioDAO = new UsuarioDAO();
                initComponents();
                properties = new Properties();
                setLocationRelativeTo(parent);
                setResizable(false);
        }

        public boolean isExitoso() {
                return exitoso;
        }

        private void createEmail() {
                emailTo = txtEmail.getText().trim();
                Subject = "Credenciales de acceso a Synapse";
                usuario = txtNombre.getText().toUpperCase();

                String contraseñaAEnviar = generarContrasenaParaElUsuario();

                Content = "<!DOCTYPE html>"
                                + "<html lang=\"es\">"
                                + "<head>"
                                + "    <meta charset=\"UTF-8\">"
                                + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                                + "    <title>Credenciales de Acceso a Synapse</title>"
                                + "<style>\n"
                                + "        body {\n"
                                + "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Arial, sans-serif;\n"
                                + "            background: #f5f5f5;\n"
                                + "            padding: 40px 20px;\n"
                                + "            margin: 0;\n"
                                + "        }\n"
                                + "        .template-selector {\n"
                                + "            max-width: 800px;\n"
                                + "            margin: 0 auto 40px;\n"
                                + "            background: white;\n"
                                + "            padding: 30px;\n"
                                + "            border-radius: 12px;\n"
                                + "            box-shadow: 0 4px 20px rgba(0,0,0,0.1);\n"
                                + "        }\n"
                                + "        .template-selector h1 {\n"
                                + "            margin: 0 0 20px;\n"
                                + "            font-size: 28px;\n"
                                + "            font-weight: 900;\n"
                                + "        }\n"
                                + "        .template-buttons {\n"
                                + "            display: flex;\n"
                                + "            gap: 10px;\n"
                                + "            flex-wrap: wrap;\n"
                                + "        }\n"
                                + "        .template-btn {\n"
                                + "            padding: 12px 24px;\n"
                                + "            background: #000;\n"
                                + "            color: white;\n"
                                + "            border: none;\n"
                                + "            border-radius: 8px;\n"
                                + "            cursor: pointer;\n"
                                + "            font-weight: 600;\n"
                                + "            transition: all 0.3s;\n"
                                + "        }\n"
                                + "        .template-btn:hover {\n"
                                + "            background: #FF6B35;\n"
                                + "            transform: translateY(-2px);\n"
                                + "        }\n"
                                + "        .template-btn.active {\n"
                                + "            background: #FF6B35;\n"
                                + "        }\n"
                                + "        .preview-container {\n"
                                + "            max-width: 800px;\n"
                                + "            margin: 0 auto;\n"
                                + "            background: white;\n"
                                + "            padding: 20px;\n"
                                + "            border-radius: 12px;\n"
                                + "            box-shadow: 0 4px 20px rgba(0,0,0,0.1);\n"
                                + "            position: relative;\n"
                                + "            min-height: 600px;\n"
                                + "        }\n"
                                + "        .template-preview {\n"
                                + "            display: none;\n"
                                + "            animation: fadeIn 0.3s ease-in-out;\n"
                                + "        }\n"
                                + "        .template-preview.active {\n"
                                + "            display: block;\n"
                                + "        }\n"
                                + "        @keyframes fadeIn {\n"
                                + "            from { opacity: 0; transform: translateY(10px); }\n"
                                + "            to { opacity: 1; transform: translateY(0); }\n"
                                + "        }\n"
                                + "    </style>"
                                + "</head>"
                                + "<body>"
                                + "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #f5f5f5; padding: 40px 0;\">\n"
                                + "                <tr>\n"
                                + "                    <td align=\"center\">\n"
                                + "                        <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.08);\">\n"
                                + "                            <!-- Header with gradient -->\n"
                                + "                            <tr>\n"
                                + "                                <td style=\"background: linear-gradient(135deg, #000000 0%, #262626 100%); padding: 40px 40px 60px; text-align: center;\">\n"
                                + "                                    <h1 style=\"margin: 0; color: #ffffff; font-size: 32px; font-weight: 900; letter-spacing: -1px;\">Synapse</h1>\n"
                                + "                                    <p style=\"margin: 10px 0 0; color: rgba(255,255,255,0.8); font-size: 14px;\">Gestión de tareas</p>\n"
                                + "                                </td>\n"
                                + "                            </tr>\n"
                                + "                            \n"
                                + "                            <!-- Content -->\n"
                                + "                            <tr>\n"
                                + "                                <td style=\"padding: 0 40px 40px;\">\n"
                                + "                                    <!-- Icon -->\n"
                                + "                                    <div style=\"margin-top: -30px; text-align: center;\">\n"
                                + "                                        <div style=\"display: inline-block; width: 60px; height: 60px; background: #FF6B35; border-radius: 50%; box-shadow: 0 8px 20px rgba(255,107,53,0.3); line-height: 60px; font-size: 28px;\">\n"
                                + "                                            👋\n"
                                + "                                        </div>\n"
                                + "                                    </div>\n"
                                + "                                    \n"
                                + "                                    <h2 style=\"margin: 30px 0 15px; color: #000000; font-size: 28px; font-weight: 900; text-align: center; letter-spacing: -1px;\">¡Bienvenido a Synapse!</h2>\n"
                                + "                                    \n"
                                + "                                    <p style=\"margin: 0 0 25px; color: #525252; font-size: 16px; line-height: 1.6; text-align: center;\">\n"
                                + "                                        Hola <strong>" + usuario
                                + "</strong>, tu cuenta ha sido creada exitosamente. Usa las credenciales a continuación para acceder.\n"
                                + "                                    </p>\n"
                                + "                                    \n"
                                + "                                    <!-- Credentials Box -->\n"
                                + "                                    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #fafafa; border: 2px solid #e5e5e5; border-radius: 12px; margin: 30px 0;\">\n"
                                + "                                        <tr>\n"
                                + "                                            <td style=\"padding: 30px;\">\n"
                                + "                                                <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">\n"
                                + "                                                    <tr>\n"
                                + "                                                        <td style=\"padding-bottom: 15px;\">\n"
                                + "                                                            <p style=\"margin: 0 0 5px; color: #737373; font-size: 13px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px;\">Email</p>\n"
                                + "                                                            <p style=\"margin: 0; color: #000000; font-size: 16px; font-weight: 600;\">"
                                + emailTo + "</p>\n"
                                + "                                                        </td>\n"
                                + "                                                    </tr>\n"
                                + "                                                    <tr>\n"
                                + "                                                        <td style=\"border-top: 1px solid #e5e5e5; padding-top: 15px;\">\n"
                                + "                                                            <p style=\"margin: 0 0 5px; color: #737373; font-size: 13px; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px;\">Contraseña Temporal</p>\n"
                                + "                                                            <p style=\"margin: 0; color: #000000; font-size: 18px; font-weight: 700; font-family: 'Courier New', monospace; letter-spacing: 2px;\">"
                                + contraseñaAEnviar + "</p>\n"
                                + "                                                        </td>\n"
                                + "                                                    </tr>\n"
                                + "                                                </table>\n"
                                + "                                            </td>\n"
                                + "                                        </tr>\n"
                                + "                                    </table>\n"
                                + "                                    \n"
                                + "                                    <!-- Warning Box -->\n"
                                + "                                    <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color: #fff7ed; border-left: 4px solid #FF6B35; border-radius: 8px; margin: 20px 0;\">\n"
                                + "                                        <tr>\n"
                                + "                                            <td style=\"padding: 15px 20px;\">\n"
                                + "                                                <p style=\"margin: 0; color: #9a3412; font-size: 14px; line-height: 1.5;\">\n"
                                + "                                                    <strong>⚠️ Importante:</strong> Esta es una contraseña temporal. Te recomendamos cambiarla después de tu primer inicio de sesión.\n"
                                + "                                                </p>\n"
                                + "                                            </td>\n"
                                + "                                        </tr>\n"
                                + "                                    </table>\n"
                                + "                                    \n"
                                + "\n"
                                + "                                    \n"
                                + "                                    <p style=\"margin: 25px 0 0; color: #a3a3a3; font-size: 13px; line-height: 1.6; text-align: center;\">\n"
                                + "                                        Si no solicitaste esta cuenta, puedes ignorar este mensaje.\n"
                                + "                                    </p>\n"
                                + "                                </td>\n"
                                + "                            </tr>\n"
                                + "                            \n"
                                + "                            <!-- Footer -->\n"
                                + "                            <tr>\n"
                                + "                                <td style=\"background-color: #fafafa; padding: 30px 40px; text-align: center; border-top: 1px solid #e5e5e5;\">\n"
                                + "                                    <p style=\"margin: 0 0 10px; color: #737373; font-size: 13px;\">\n"
                                + "                                        ¿Necesitas ayuda? <a href=\"mailto:soporte@synapse.com\" style=\"color: #FF6B35; text-decoration: none; font-weight: 600;\">Contáctanos</a>\n"
                                + "                                    </p>\n"
                                + "                                    <p style=\"margin: 0; color: #a3a3a3; font-size: 12px;\">\n"
                                + "                                        © 2025 Synapse • Gestión de tareas inteligente\n"
                                + "                                    </p>\n"
                                + "                                </td>\n"
                                + "                            </tr>\n"
                                + "                        </table>\n"
                                + "                    </td>\n"
                                + "                </tr>\n"
                                + "            </table>"
                                + ""
                                + ""
                                + "</body>"
                                + "</html>";

                // Simple Mail Protocol;
                properties.put("mail.smtp.host", "smtp.gmail.com");
                properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
                properties.setProperty("mail.smtp.starttls.enable", "true");
                properties.setProperty("mail.smtp.port", "587");
                properties.setProperty("mail.smtp.user", emailFrom);
                properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
                properties.setProperty("mail.smtp.auth", "true");

                mSession = Session.getDefaultInstance(properties);

                try {
                        mCorreo = new MimeMessage(mSession);
                        mCorreo.setFrom(new InternetAddress(emailFrom));
                        mCorreo.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
                        mCorreo.setSubject(Subject);
                        mCorreo.setText(Content, "UTF-8", "html");
                } catch (AddressException ex) {
                        Logger.getLogger(CrearUsuarioDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MessagingException ex) {
                        Logger.getLogger(CrearUsuarioDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

        }

        private String generarContrasenaParaElUsuario() {
                PasswordBuilder builder = new PasswordBuilder();
                contraseñaGenerada = builder
                                .setLength(12)
                                .useSymbols(true)
                                .build()
                                .getValue();
                return contraseñaGenerada;
        }

        private void sendEmail() {
                try {
                        Transport mTransport = mSession.getTransport("smtp");
                        mTransport.connect(emailFrom, passwordFrom);
                        mTransport.sendMessage(mCorreo, mCorreo.getRecipients(Message.RecipientType.TO));
                        mTransport.close();

                        Notifications.getInstance().show(Notifications.Type.SUCCESS.INFO,
                                        Notifications.Location.TOP_RIGHT,
                                        "Se envio correo con credenciales");
                } catch (NoSuchProviderException ex) {
                        Logger.getLogger(CrearUsuarioDialog.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MessagingException ex) {
                        Logger.getLogger(CrearUsuarioDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

        }

        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jLabel1 = new javax.swing.JLabel();
                jLabel2 = new javax.swing.JLabel();
                jLabel3 = new javax.swing.JLabel();
                txtNombre = new javax.swing.JTextField();
                txtEmail = new javax.swing.JTextField();
                comboRol = new javax.swing.JComboBox<>();
                btnCrearUsuario = new com.synapse.ui.components.ButtonAction();

                setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

                jLabel1.setText("Nombre Completo *");

                jLabel2.setText("Correo Electronico *");

                jLabel3.setText("Rol");

                comboRol.setModel(new javax.swing.DefaultComboBoxModel<>(
                                new String[] { "Empleado", "Gerente", "Administrador", " " }));

                btnCrearUsuario.setBackground(new java.awt.Color(0, 0, 0));
                btnCrearUsuario.setForeground(new java.awt.Color(255, 255, 255));
                btnCrearUsuario.setText("Crear Usuario");
                btnCrearUsuario.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                btnCrearUsuarioActionPerformed(evt);
                        }
                });

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addGap(15, 15, 15)
                                                                .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(jLabel2)
                                                                                                .addContainerGap(
                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE))
                                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
                                                                                                layout.createSequentialGroup()
                                                                                                                .addGroup(layout.createParallelGroup(
                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                                                .addComponent(txtEmail,
                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                .addComponent(btnCrearUsuario,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                .addComponent(comboRol,
                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                0,
                                                                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                                                Short.MAX_VALUE)
                                                                                                                                .addComponent(txtNombre,
                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                layout.createSequentialGroup()
                                                                                                                                                                .addGroup(layout.createParallelGroup(
                                                                                                                                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                                                                                                                .addComponent(jLabel3,
                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                                                                                                161,
                                                                                                                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                                                                .addComponent(jLabel1,
                                                                                                                                                                                                javax.swing.GroupLayout.Alignment.LEADING))
                                                                                                                                                                .addGap(0, 185, Short.MAX_VALUE)))
                                                                                                                .addGap(17, 17, 17)))));
                layout.setVerticalGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                                .addGap(23, 23, 23)
                                                                .addComponent(jLabel1,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                24,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtNombre,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                33,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel2,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                29,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtEmail,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                33,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                .addComponent(jLabel3,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                25,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(comboRol,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                33,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnCrearUsuario,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                37,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(21, 21, 21)));

                pack();
        }// </editor-fold>//GEN-END:initComponents

        private void btnCrearUsuarioActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnCrearUsuarioActionPerformed
                // Validaciones
                String nombre = txtNombre.getText().trim();
                String email = txtEmail.getText().trim();

                if (nombre.isEmpty() || email.isEmpty()) {
                        Notifications.getInstance().show(Notifications.Type.WARNING,
                                        Notifications.Location.TOP_CENTER,
                                        "Nombre y email son obligatorios");
                        return;
                }

                // Validar formato de email
                if (!esEmailValido(email)) {
                        Notifications.getInstance().show(Notifications.Type.WARNING,
                                        Notifications.Location.TOP_CENTER,
                                        "Por favor ingresa un email válido");
                        return;
                }

                int[] mapeoRoles = { 3, 2, 1 }; // Empleado=3, Gerente=2, Admin=1
                int idRol = mapeoRoles[comboRol.getSelectedIndex()];

                // Generar contraseña
                createEmail();

                // Crear objeto Usuario
                Usuario nuevoUsuario = new Usuario();
                nuevoUsuario.setNombre(nombre);
                nuevoUsuario.setEmail(email);
                nuevoUsuario.setActivo(true);

                // Guardar usuario en BD
                boolean guardado = usuarioDAO.crearUsuario(nuevoUsuario, contraseñaGenerada, idRol);

                if (guardado) {
                        // Enviar email
                        sendEmail();
                        exitoso = true;
                        Notifications.getInstance().show(Notifications.Type.SUCCESS,
                                        Notifications.Location.TOP_CENTER,
                                        "Usuario creado exitosamente");
                        this.dispose();
                } else {
                        Notifications.getInstance().show(Notifications.Type.ERROR,
                                        Notifications.Location.TOP_CENTER,
                                        "Error al crear usuario");
                }
        }

        /**
         * Valida si un email tiene formato correcto
         */
        private boolean esEmailValido(String email) {
                // Regex simple para validar email
                String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
                return email.matches(emailRegex);
        }

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private com.synapse.ui.components.ButtonAction btnCrearUsuario;
        private javax.swing.JComboBox<String> comboRol;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JTextField txtEmail;
        private javax.swing.JTextField txtNombre;
        // End of variables declaration//GEN-END:variables
}
