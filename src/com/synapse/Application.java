package com.synapse;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.synapse.ui.views.login.FormLogin;
import com.synapse.ui.views.shared.MainForm;
import com.synapse.core.models.Usuario;
import com.synapse.core.services.notifications.EmailService;
import com.synapse.core.services.notifications.NotificationService;
import com.synapse.core.services.notifications.TaskScheduler;
import raven.toast.Notifications;

/**
 *
 * @author Raven
 */
public class Application extends javax.swing.JFrame {

    private static Application app;
    private final MainForm mainForm;
    private final FormLogin loginForm;

    public Application() {
        initComponents();
        setSize(new Dimension(1286, 768));
        setLocationRelativeTo(null);

        mainForm = new MainForm();
        loginForm = new FormLogin(mainForm);

        Notifications.getInstance().setJFrame(this);
        setContentPane(loginForm);
        getRootPane().putClientProperty(FlatClientProperties.FULL_WINDOW_CONTENT, true);
        Notifications.getInstance().setJFrame(this);
    }

    public static void iniciarSesionUsuario(Usuario usuario) {
        FlatAnimatedLafChange.showSnapshot();
        app.setContentPane(app.mainForm);
        app.mainForm.applyComponentOrientation(app.getComponentOrientation());
        setSelectedMenu(0, 0);
        app.mainForm.hideMenu();
        SwingUtilities.updateComponentTreeUI(app.mainForm);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();

        // Aquí es donde actualizas el menú con la información del usuario
        app.mainForm.updateMenuInfo(usuario);
    }

    public static void showForm(Component component) {
        component.applyComponentOrientation(app.getComponentOrientation());
        app.mainForm.showForm(component);
    }

    public void login() {
        FlatAnimatedLafChange.showSnapshot();
        setContentPane(this.mainForm);
        this.mainForm.applyComponentOrientation(getComponentOrientation());
        setSelectedMenu(0, 0);
        this.mainForm.hideMenu();
        SwingUtilities.updateComponentTreeUI(this.mainForm);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void logout() {
        FlatAnimatedLafChange.showSnapshot();
        app.setContentPane(app.loginForm);
        app.loginForm.applyComponentOrientation(app.getComponentOrientation());
        app.loginForm.limpiarPassword(); // Limpia solo la contraseña, mantiene el email
        SwingUtilities.updateComponentTreeUI(app.loginForm);
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    public static void setSelectedMenu(int index, int subIndex) {
        app.mainForm.setSelectedMenu(index, subIndex);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 719, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 521, Short.MAX_VALUE));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {

        TaskScheduler scheduler = new TaskScheduler();

        // Registrar los observadores
        scheduler.addObserver(new NotificationService());
        scheduler.addObserver(new EmailService());

        // Iniciar el hilo
        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.setDaemon(true); // Para que no impida que la JVM se cierre
        schedulerThread.start();

        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatMacDarkLaf.setup();
        java.awt.EventQueue.invokeLater(() -> {
            app = new Application();
            // app.applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            app.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
