package com.synapse.ui.views.shared;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.synapse.Application;
import com.synapse.ui.views.admin.DashbordAdmin;
import com.synapse.ui.views.admin.formEquipos;
import com.synapse.ui.views.admin.formUsuarios;
import com.synapse.ui.views.empleado.DashbordEmpleado;
import com.synapse.ui.views.gerente.DashbordGerente;
import com.synapse.ui.views.gerente.formCrearTarea;
import com.synapse.ui.components.menu.Menu;
import com.synapse.ui.components.menu.MenuAction;
import com.synapse.core.models.Usuario;
import com.synapse.core.services.ViewFactory;
import com.synapse.ui.views.empleado.formMiEquipo;
import com.synapse.ui.views.empleado.formMisTareas;
import com.synapse.ui.views.empleado.formReportes;
import com.synapse.ui.views.gerente.formMisEquipos;

/**
 *
 * @author Raven
 */
public class MainForm extends JLayeredPane {

    private Usuario usuarioLogueado;

    public MainForm() {
        init();
    }

    private void init() {
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setLayout(new MainFormLayout());
        menu = new Menu();
        panelBody = new JPanel(new BorderLayout());
        initMenuArrowIcon();
        menuButton.putClientProperty(FlatClientProperties.STYLE, ""
                + "background:$Menu.button.background;"
                + "arc:999;"
                + "focusWidth:0;"
                + "borderWidth:0");
        menuButton.addActionListener((ActionEvent e) -> {
            setMenuFull(!menu.isMenuFull());
        });
        setLayer(menuButton, JLayeredPane.POPUP_LAYER);
        add(menuButton);
        add(menu);
        add(panelBody);
    }

    public void updateMenuInfo(Usuario usuario) {
        this.usuarioLogueado = usuario;

        // 1. Actualizar la foto y nombre en el menú (como ya lo hacías)
        if (usuario != null) {
            menu.setUsuario(usuario); // (Tu Menu.java ya tiene este método)
        }

        // --- INICIO DE INTEGRACIÓN (Factory y Menú) ---
        // 2. Filtrar el menú lateral según el rol del usuario
        // (Necesitaremos añadir 'filtrarMenuPorRol' a Menu.java)
        menu.filtrarMenuPorRol(usuario.getRol());

        // 3. Cargar el dashboard inicial usando el Patrón Factory
        cargarDashboardInicial(usuario);

        // 4. Inicializar los eventos del MENÚ FILTRADO
        initMenuEvent(usuario.getRol());

        // 5. Verificar y notificar sobre tareas pendientes
        verificarTareasPendientes(usuario);

        // --- FIN DE INTEGRACIÓN ---
    }

    /**
     * Verifica si el usuario tiene tareas asignadas y muestra notificación
     */
    private void verificarTareasPendientes(Usuario usuario) {
        javax.swing.SwingWorker<Integer, Void> worker = new javax.swing.SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() throws Exception {
                com.synapse.data.dao.TareaDAO tareaDAO = new com.synapse.data.dao.TareaDAO();
                java.util.List<com.synapse.core.models.Tarea> tareas = tareaDAO
                        .getTareasPorUsuario(usuario.getIdUsuario());

                // Contar tareas pendientes (estado = 1)
                int pendientes = 0;
                for (com.synapse.core.models.Tarea t : tareas) {
                    if (t.getIdEstado() == 1) { // 1 = Pendiente
                        pendientes++;
                    }
                }
                return pendientes;
            }

            @Override
            protected void done() {
                try {
                    Integer pendientes = get();
                    if (pendientes > 0) {
                        String mensaje = pendientes == 1
                                ? "Tienes 1 tarea pendiente\nRevisa tu correo para más detalles"
                                : "Tienes " + pendientes + " tareas pendientes\nRevisa tu correo para más detalles";

                        raven.toast.Notifications.getInstance().show(
                                raven.toast.Notifications.Type.INFO,
                                raven.toast.Notifications.Location.TOP_CENTER,
                                mensaje);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void cargarDashboardInicial(Usuario usuario) {
        // 1. Pedimos a la fábrica que cree el dashboard
        JPanel dashboard = ViewFactory.crearDashboard(usuario);
        showForm(dashboard);
    }

    @Override
    public void applyComponentOrientation(ComponentOrientation o) {
        super.applyComponentOrientation(o);
        initMenuArrowIcon();
    }

    private void initMenuArrowIcon() {
        if (menuButton == null) {
            menuButton = new JButton();
        }
        String icon = (getComponentOrientation().isLeftToRight()) ? "menu_left.svg" : "menu_right.svg";
        menuButton.setIcon(new FlatSVGIcon("icons/" + icon, 0.8f));
    }

    /**
     * Modificado para ser dinámico y basarse en el rol y el texto del menú.
     */
    private void initMenuEvent(String rol) {
        menu.addMenuEvent((int index, int subIndex, MenuAction action) -> {

            String menuSeleccionado = menu.getMenuName(index);

            if (menuSeleccionado.equals("Mi Perfil")) {
                Application.showForm(new formPerfil(usuarioLogueado));
                return;
            }
            if (menuSeleccionado.equals("Logout")) {
                Application.logout();
                return;
            }

            // Navegación Específica por Rol
            switch (rol) {
                case "administrador":
                    navegarAdmin(menuSeleccionado);
                    break;
                case "gerente":
                    navegarGerente(menuSeleccionado);
                    break;
                case "empleado":
                    navegarEmpleado(menuSeleccionado);
                    break;
            }
        });
    }

    // --- Métodos de navegación por rol ---
    private void navegarAdmin(String menuNombre) {
        if (menuNombre.equals("Vista General")) {
            Application.showForm(new DashbordAdmin(usuarioLogueado));
        } else if (menuNombre.equals("Usuarios")) {
            Application.showForm(new formUsuarios());
        } else if (menuNombre.equals("Equipos")) {
            Application.showForm(new formEquipos(usuarioLogueado));
        } else if (menuNombre.equals("Tareas")) {
            Application.showForm(new formTareas(usuarioLogueado));
        }
    }

    private void navegarGerente(String menuNombre) {
        if (menuNombre.equals("Vista General")) {
            Application.showForm(new DashbordGerente(usuarioLogueado));
        } else if (menuNombre.equals("Crear Tareas")) {
            // --- CORRECCIÓN AQUÍ ---
            Application.showForm(new formCrearTarea(usuarioLogueado));
            // --- FIN CORRECCIÓN ---
        } else if (menuNombre.equals("Mis Equipos")) {
            Application.showForm(new formMisEquipos(usuarioLogueado));
        } else if (menuNombre.equals("Gestionar Tareas")) {
            Application.showForm(new formTareas(usuarioLogueado));
        }
    }

    private void navegarEmpleado(String menuNombre) {
        if (menuNombre.equalsIgnoreCase("Vista General")) {
            Application.showForm(new DashbordEmpleado(usuarioLogueado));
        } else if (menuNombre.equalsIgnoreCase("Mis Tareas")) {
            Application.showForm(new formMisTareas(usuarioLogueado));
        } else if (menuNombre.equalsIgnoreCase("Mi equipo")) {
            Application.showForm(new formMiEquipo(usuarioLogueado));
        } else if (menuNombre.equalsIgnoreCase("Reportes")) {
            // --- ¡AJUSTE AQUÍ! ---
            // Le pasamos el usuario logueado al formulario
            Application.showForm(new formReportes(usuarioLogueado));
            // --- FIN AJUSTE ---
        }
    }

    private void setMenuFull(boolean full) {
        String icon;
        if (getComponentOrientation().isLeftToRight()) {
            icon = (full) ? "menu_left.svg" : "menu_right.svg";
        } else {
            icon = (full) ? "menu_right.svg" : "menu_left.svg";
        }
        menuButton.setIcon(new FlatSVGIcon("icons/" + icon, 0.8f));
        menu.setMenuFull(full);
        revalidate();
    }

    public void hideMenu() {
        menu.hideMenuItem();
    }

    public void showForm(Component component) {
        panelBody.removeAll();
        panelBody.add(component);
        panelBody.repaint();
        panelBody.revalidate();
    }

    public void setSelectedMenu(int index, int subIndex) {
        menu.setSelectedMenu(index, subIndex);
    }

    private Menu menu;
    private JPanel panelBody;
    private JButton menuButton;

    private class MainFormLayout implements LayoutManager {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(5, 5);
            }
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            synchronized (parent.getTreeLock()) {
                return new Dimension(0, 0);
            }
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = UIScale.scale(parent.getInsets());
                int x = insets.left;
                int y = insets.top;
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
                int menuX = ltr ? x : x + width - menuWidth;
                menu.setBounds(menuX, y, menuWidth, height);
                int menuButtonWidth = menuButton.getPreferredSize().width;
                int menuButtonHeight = menuButton.getPreferredSize().height;
                int menubX;
                if (ltr) {
                    menubX = (int) (x + menuWidth - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
                } else {
                    menubX = (int) (menuX - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.7f)));
                }
                menuButton.setBounds(menubX, UIScale.scale(30), menuButtonWidth, menuButtonHeight);
                int gap = UIScale.scale(5);
                int bodyWidth = width - menuWidth - gap;
                int bodyHeight = height;
                int bodyx = ltr ? (x + menuWidth + gap) : x;
                int bodyy = y;
                panelBody.setBounds(bodyx, bodyy, bodyWidth, bodyHeight);
            }
        }
    }
}
