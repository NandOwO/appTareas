package com.synapse.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.synapse.core.models.Notificacion;
import com.synapse.core.models.Usuario;
import com.synapse.data.dao.NotificacionDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author FERNANDO
 */
public class NotificationBadge extends JPanel {

    private final Usuario usuarioLogueado;
    private final NotificacionDAO notificacionDAO;
    private final JLabel lblBadge;
    private final JLabel lblIcono;
    private final JPanel panelDropdown;
    private boolean isDropdownVisible = false;
    private int notificacionesNoLeidas = 0;

    public NotificationBadge(Usuario usuario) {
        this.usuarioLogueado = usuario;
        this.notificacionDAO = new NotificacionDAO();

        setLayout(new BorderLayout());
        setOpaque(false);

        // Panel principal con icono y badge
        JPanel panelPrincipal = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelPrincipal.setOpaque(false);

        // Icono de campana
        lblIcono = new JLabel("");
        lblIcono.setFont(lblIcono.getFont().deriveFont(20f));
        lblIcono.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblIcono.setToolTipText("Notificaciones");

        // Badge con número
        lblBadge = new JLabel("0");
        lblBadge.setFont(lblBadge.getFont().deriveFont(10f));
        lblBadge.setForeground(Color.WHITE);
        lblBadge.setBackground(new Color(220, 53, 69)); // Rojo
        lblBadge.setOpaque(true);
        lblBadge.setHorizontalAlignment(SwingConstants.CENTER);
        lblBadge.setPreferredSize(new Dimension(18, 18));
        lblBadge.putClientProperty(FlatClientProperties.STYLE, "arc:999");
        lblBadge.setVisible(false);

        panelPrincipal.add(lblIcono);
        panelPrincipal.add(lblBadge);

        // Panel dropdown (inicialmente oculto)
        panelDropdown = new JPanel(new BorderLayout());
        panelDropdown.setPreferredSize(new Dimension(350, 400));
        panelDropdown.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        panelDropdown.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:15;"
                + "background:$Panel.background");
        panelDropdown.setVisible(false);

        add(panelPrincipal, BorderLayout.NORTH);
        add(panelDropdown, BorderLayout.CENTER);

        // Click en icono para toggle dropdown
        lblIcono.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleDropdown();
            }
        });

        // Cargar notificaciones
        cargarNotificaciones();
    }

    /**
     * Alterna la visibilidad del dropdown
     */
    private void toggleDropdown() {
        isDropdownVisible = !isDropdownVisible;
        panelDropdown.setVisible(isDropdownVisible);

        if (isDropdownVisible) {
            cargarNotificaciones();
        }

        revalidate();
        repaint();
    }

    /**
     * Carga las notificaciones del usuario
     */
    private void cargarNotificaciones() {
        SwingWorker<List<Notificacion>, Void> worker = new SwingWorker<List<Notificacion>, Void>() {
            @Override
            protected List<Notificacion> doInBackground() throws Exception {
                return notificacionDAO.getNotificacionesPorUsuario(usuarioLogueado.getIdUsuario());
            }

            @Override
            protected void done() {
                try {
                    List<Notificacion> notificaciones = get();
                    actualizarBadge(notificaciones);
                    actualizarDropdown(notificaciones);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    /**
     * Actualiza el badge con el número de notificaciones no leídas
     */
    private void actualizarBadge(List<Notificacion> notificaciones) {
        notificacionesNoLeidas = 0;
        for (Notificacion n : notificaciones) {
            if (!n.isLeida()) {
                notificacionesNoLeidas++;
            }
        }

        if (notificacionesNoLeidas > 0) {
            lblBadge.setText(String.valueOf(notificacionesNoLeidas));
            lblBadge.setVisible(true);
        } else {
            lblBadge.setVisible(false);
        }
    }

    /**
     * Actualiza el panel dropdown con la lista de notificaciones
     */
    private void actualizarDropdown(List<Notificacion> notificaciones) {
        panelDropdown.removeAll();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        header.setOpaque(false);

        JLabel lblTitulo = new JLabel("Notificaciones");
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(16f).deriveFont(java.awt.Font.BOLD));
        header.add(lblTitulo, BorderLayout.WEST);

        if (notificacionesNoLeidas > 0) {
            JLabel lblCount = new JLabel(notificacionesNoLeidas + " nuevas");
            lblCount.setForeground(new Color(220, 53, 69));
            header.add(lblCount, BorderLayout.EAST);
        }

        panelDropdown.add(header, BorderLayout.NORTH);

        // Lista de notificaciones
        JPanel listaPanel = new JPanel();
        listaPanel.setLayout(new BoxLayout(listaPanel, BoxLayout.Y_AXIS));
        listaPanel.setOpaque(false);

        if (notificaciones.isEmpty()) {
            JLabel lblVacio = new JLabel("No hay notificaciones");
            lblVacio.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
            lblVacio.setForeground(Color.GRAY);
            listaPanel.add(lblVacio);
        } else {
            for (Notificacion notif : notificaciones) {
                listaPanel.add(crearItemNotificacion(notif));
                listaPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        JScrollPane scroll = new JScrollPane(listaPanel);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        panelDropdown.add(scroll, BorderLayout.CENTER);

        panelDropdown.revalidate();
        panelDropdown.repaint();
    }

    /**
     * Crea un item de notificación
     */
    private JPanel crearItemNotificacion(Notificacion notif) {
        JPanel item = new JPanel(new MigLayout("fillx, insets 10", "[grow][]"));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Fondo diferente si no está leída
        if (!notif.isLeida()) {
            item.setBackground(new Color(240, 248, 255)); // Azul claro
        } else {
            item.setOpaque(false);
        }

        // Mensaje
        JLabel lblMensaje = new JLabel("<html>" + notif.getMensaje() + "</html>");
        lblMensaje.setFont(lblMensaje.getFont().deriveFont(12f));
        if (!notif.isLeida()) {
            lblMensaje.setFont(lblMensaje.getFont().deriveFont(java.awt.Font.BOLD));
        }

        // Fecha
        JLabel lblFecha = new JLabel(notif.getFecha().toString().substring(0, 16));
        lblFecha.setFont(lblFecha.getFont().deriveFont(10f));
        lblFecha.setForeground(Color.GRAY);

        item.add(lblMensaje, "wrap");
        item.add(lblFecha, "");

        // Click para marcar como leída
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!notif.isLeida()) {
                    marcarComoLeida(notif.getIdNotificacion());
                }
            }
        });

        return item;
    }

    /**
     * Marca una notificación como leída
     */
    private void marcarComoLeida(int idNotificacion) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return notificacionDAO.marcarComoLeida(idNotificacion);
            }

            @Override
            protected void done() {
                try {
                    Boolean exito = get();
                    if (exito) {
                        cargarNotificaciones(); // Recargar
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    /**
     * Método público para refrescar notificaciones
     */
    public void refresh() {
        cargarNotificaciones();
    }
}
