package com.synapse.ui.components.menu;

import com.synapse.ui.components.CircularImageLabel;
import com.synapse.ui.lib.LightDarkMode;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import com.synapse.ui.lib.ToolBarAccentColor;
import net.miginfocom.swing.MigLayout;
import com.synapse.core.models.Usuario;
import com.synapse.ui.components.menu.MenuAction;

/**
 *
 * @author Raven
 */
public class Menu extends JPanel {

    private final String menuItems[][] = {
        {"~administrador~"},
        {"Vista General"},//o
        {"Usuarios"},//1
        {"Equipos"},//2
        {"Tareas"},//3
        {"Mi Perfil"},//4
        {"~gerente~"},//
        {"Vista General"},//5
        {"Mis Equipos"},//6
        {"Gestionar Tareas"},//7
        {"Crear Tareas"},//8
        {"Mi Perfil"},//9
        {"~empleado~"},
        {"Vista General"},//10
        {"Mis Tareas"},//11
        {"Mi equipo"},//12
        {"Reportes"},//13
        {"Mi Perfil"},//14
        {"~~"},
        {"~~"},
        {"Logout"}//15
    };

    /**
     * Oculta o muestra los items del menú basados en el rol del usuario.
     * (Versión "A Prueba de Balas" con trim() y toLowerCase())
     */
    public void filtrarMenuPorRol(String rol) {
        if (rol == null) {
            rol = "";
        }

        String rolLimpio = rol.trim().toLowerCase();
        String rolBuscado = "~" + rolLimpio + "~";

        System.out.println("[Menu.java] Filtrando. Rol buscado: '" + rolBuscado + "'");

        boolean mostrarSeccion = false;

        for (Component com : panelMenu.getComponents()) {
            if (com instanceof JLabel) {
                // Es un Título
                JLabel lbTitle = (JLabel) com;

                // --- ¡LA CORRECCIÓN DEL BUG! ---
                // Leemos el nombre completo (~...~) desde la propiedad 'name'
                String tituloCompleto = lbTitle.getName();

                if (tituloCompleto != null && tituloCompleto.startsWith("~") && tituloCompleto.endsWith("~") && !tituloCompleto.equals("~~")) {
                    // Es un título de ROL
                    mostrarSeccion = tituloCompleto.equals(rolBuscado);
                    lbTitle.setVisible(false); // Ocultamos el título (ej. "administrador")
                } else {
                    // Es un separador (ej. "~~")
                    lbTitle.setVisible(mostrarSeccion);
                }

            } else if (com instanceof MenuItem) {
                // Es un Item de Menú
                MenuItem item = (MenuItem) com;
                String primerMenu = item.getMenus()[0].trim().toLowerCase();

                // --- ¡ESTA ES LA CORRECCIÓN! ---
                if (primerMenu.equals("logout")) { // <-- ¡SOLO "logout"!
                    item.setVisible(true); // "Logout" siempre es visible
                } else {
                    // "Mi Perfil" y el resto obedecen a la sección
                    item.setVisible(mostrarSeccion);
                }
            }
        }

        panelMenu.revalidate();
        panelMenu.repaint();
    }

    /**
     * Obtiene el nombre del menú principal (ítem 0) en un índice dado.
     *
     * @param index El índice del MenuItem
     * @return El nombre del menú, o "" si no se encuentra.
     */
    public String getMenuName(int index) {
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                MenuItem item = (MenuItem) com;
                if (item.getMenuIndex() == index) {
                    return item.getMenus()[0];
                }
            }
        }
        return "";
    }

    public boolean isMenuFull() {
        return menuFull;
    }

    public void setMenuFull(boolean menuFull) {
        this.menuFull = menuFull;
        if (menuFull) {
            // Muestra el nombre de usuario almacenado
            profileName.setText(this.currentUserName);
            profileName.setHorizontalAlignment(getComponentOrientation().isLeftToRight() ? JLabel.LEFT : JLabel.RIGHT);
        } else {
            // Oculta el nombre
            profileName.setText("");
            profileName.setHorizontalAlignment(JLabel.CENTER);
        }
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                ((MenuItem) com).setFull(menuFull);
            }
        }
        lightDarkMode.setMenuFull(menuFull);
        toolBarAccentColor.setMenuFull(menuFull);
    }

    private final List<MenuEvent> events = new ArrayList<>();
    private boolean menuFull = true;
    private final String headerName = "";
    private String currentUserName = "";

    protected final boolean hideMenuTitleOnMinimum = true;
    protected final int menuTitleLeftInset = 5;
    protected final int menuTitleVgap = 5;
    protected final int menuMaxWidth = 250;
    protected final int menuMinWidth = 60;
    protected final int headerFullHgap = 5;

    public Menu() {
        init();
    }

    private void init() {
        setLayout(new MenuLayout());
        putClientProperty(FlatClientProperties.STYLE, ""
                + "border:20,2,2,2;"
                + "background:$Menu.background;"
                + "arc:10");

        header = new JPanel(new MigLayout("insets 5 10 5 10", "[center, 50]8[left, grow]"));
        header.setOpaque(false); // Transparente

        profileImage = new CircularImageLabel("/icons/users/defaultUser.jpg", 40); //

        // 2. El Nombre (un JLabel separado)
        profileName = new JLabel(headerName); // headerName es "" por defecto
        profileName.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$Menu.header.font;"
                + "foreground:$Menu.foreground");

        // 3. Añadir los componentes al panel 'header'
        header.add(profileImage, "width 50!, height 50!");
        header.add(profileName, "growx, wrap, al center");
        // --- FIN DE LA CORRECCIÓN ---

        // Menu (Esta parte se queda igual)
        scroll = new JScrollPane();
        panelMenu = new JPanel(new MenuItemLayout(this));
        panelMenu.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5;"
                + "background:$Menu.background");

        scroll.setViewportView(panelMenu);
        scroll.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:null");
        JScrollBar vscroll = scroll.getVerticalScrollBar();
        vscroll.setUnitIncrement(10);
        vscroll.putClientProperty(FlatClientProperties.STYLE, ""
                + "width:$Menu.scroll.width;"
                + "trackInsets:$Menu.scroll.trackInsets;"
                + "thumbInsets:$Menu.scroll.thumbInsets;"
                + "background:$Menu.ScrollBar.background;"
                + "thumb:$Menu.ScrollBar.thumb");
        createMenu();
        lightDarkMode = new LightDarkMode();
        toolBarAccentColor = new ToolBarAccentColor(this);
        toolBarAccentColor.setVisible(FlatUIUtils.getUIBoolean("AccentControl.show", false));

        add(header);
        add(scroll);
        add(lightDarkMode);
        add(toolBarAccentColor);
    }

    public void updateUserInfo(String name, String photoUrl) {
        this.currentUserName = name;

        if (isMenuFull()) {
            profileName.setText(name);
        }

        try {
            if (photoUrl != null && !photoUrl.isEmpty()) {
                System.out.println("Intentando cargar la imagen desde: " + photoUrl);

                profileImage.setImage(photoUrl);
            } else {
                profileImage.setImage("/icons/users/defaultUser.jpg");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar la foto de perfil desde el recurso: " + e.getMessage());
            // header.setIcon(new ImageIcon(getClass().getResource("/icons/users/defaultUser.jpg"))); // <-- LÍNEA ANTIGUA (BORRADA)
            profileImage.setImage("/icons/users/defaultUser.jpg"); // <-- LÍNEA NUEVA
        }
    }

    public void setUsuario(Usuario user) {
        if (user == null) {
            updateUserInfo("Usuario Invitado", "/icons/users/defaultUser.jpg");
            return;
        }
        updateUserInfo(user.getNombre(), user.getFotoUrl());
    }

    private void createMenu() {
        int index = 0;
        for (int i = 0; i < menuItems.length; i++) {
            String menuName = menuItems[i][0];
            if (menuName.startsWith("~") && menuName.endsWith("~")) {
                panelMenu.add(createTitle(menuName));
            } else {
                MenuItem menuItem = new MenuItem(this, menuItems[i], index++, events);
                panelMenu.add(menuItem);
            }
        }
    }

    private JLabel createTitle(String title) {
        String menuName = title.substring(1, title.length() - 1);
        JLabel lbTitle = new JLabel(menuName);
        lbTitle.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:$Menu.label.font;"
                + "foreground:$Menu.title.foreground");
        lbTitle.setName(title);
        return lbTitle;
    }

    public void setSelectedMenu(int index, int subIndex) {
        runEvent(index, subIndex);
    }

    protected void setSelected(int index, int subIndex) {
        int size = panelMenu.getComponentCount();
        for (int i = 0; i < size; i++) {
            Component com = panelMenu.getComponent(i);
            if (com instanceof MenuItem) {
                MenuItem item = (MenuItem) com;
                if (item.getMenuIndex() == index) {
                    item.setSelectedIndex(subIndex);
                } else {
                    item.setSelectedIndex(-1);
                }
            }
        }
    }

    protected void runEvent(int index, int subIndex) {
        MenuAction menuAction = new MenuAction();
        for (MenuEvent event : events) {
            event.menuSelected(index, subIndex, menuAction);
        }
        if (!menuAction.isCancel()) {
            setSelected(index, subIndex);
        }
    }

    public void addMenuEvent(MenuEvent event) {
        events.add(event);
    }

    public void hideMenuItem() {
        for (Component com : panelMenu.getComponents()) {
            if (com instanceof MenuItem) {
                ((MenuItem) com).hideMenuItem();
            }
        }
        revalidate();
    }

    public boolean isHideMenuTitleOnMinimum() {
        return hideMenuTitleOnMinimum;
    }

    public int getMenuTitleLeftInset() {
        return menuTitleLeftInset;
    }

    public int getMenuTitleVgap() {
        return menuTitleVgap;
    }

    public int getMenuMaxWidth() {
        return menuMaxWidth;
    }

    public int getMenuMinWidth() {
        return menuMinWidth;
    }

    private JPanel header; // <-- LÍNEA NUEVA (Ahora es un JPanel)
    private CircularImageLabel profileImage; // <-- LÍNEA NUEVA
    private JLabel profileName;

    private JScrollPane scroll;
    private JPanel panelMenu;
    private LightDarkMode lightDarkMode;
    private ToolBarAccentColor toolBarAccentColor;

    private class MenuLayout implements LayoutManager {

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
                Insets insets = parent.getInsets();
                int x = insets.left;
                int y = insets.top;
                int gap = UIScale.scale(5);
                int sheaderFullHgap = UIScale.scale(headerFullHgap);
                int width = parent.getWidth() - (insets.left + insets.right);
                int height = parent.getHeight() - (insets.top + insets.bottom);
                int iconWidth = width;
                int iconHeight = header.getPreferredSize().height;
                int hgap = menuFull ? sheaderFullHgap : 0;
                int accentColorHeight = 0;
                if (toolBarAccentColor.isVisible()) {
                    accentColorHeight = toolBarAccentColor.getPreferredSize().height + gap;
                }

                header.setBounds(x + hgap, y, iconWidth - (hgap * 2), iconHeight);
                int ldgap = UIScale.scale(10);
                int ldWidth = width - ldgap * 2;
                int ldHeight = lightDarkMode.getPreferredSize().height;
                int ldx = x + ldgap;
                int ldy = y + height - ldHeight - ldgap - accentColorHeight;

                int menux = x;
                int menuy = y + iconHeight + gap;
                int menuWidth = width;
                int menuHeight = height - (iconHeight + gap) - (ldHeight + ldgap * 2) - (accentColorHeight);
                scroll.setBounds(menux, menuy, menuWidth, menuHeight);

                lightDarkMode.setBounds(ldx, ldy, ldWidth, ldHeight);

                if (toolBarAccentColor.isVisible()) {
                    int tbheight = toolBarAccentColor.getPreferredSize().height;
                    int tbwidth = Math.min(toolBarAccentColor.getPreferredSize().width, ldWidth);
                    int tby = y + height - tbheight - ldgap;
                    int tbx = ldx + ((ldWidth - tbwidth) / 2);
                    toolBarAccentColor.setBounds(tbx, tby, tbwidth, tbheight);
                }
            }
        }
    }
}
