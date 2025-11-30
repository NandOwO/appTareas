package com.synapse.ui.components;

import com.synapse.core.models.Usuario;
import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import net.miginfocom.swing.MigLayout;

public class MemberRenderer extends javax.swing.JPanel implements TableCellRenderer {

    private final CircularImageLabel imageLabel;
    private final JLabel labelNombre;
    private final JLabel labelEmail;

    // --- ¡SOLUCIÓN 1: MEJOR PRÁCTICA! ---
    // Definimos la carpeta y la imagen por defecto en constantes.
    private final String RUTA_BASE_FOTOS = "/icons/users/";
    private final String FOTO_POR_DEFECTO = RUTA_BASE_FOTOS + "defaultUser.jpg";
    // ------------------------------------

    public MemberRenderer() {
        // (Tu layout MigLayout está perfecto)
        setLayout(new MigLayout("insets 5 10 5 10, fill", "[][]", "[][]"));

        imageLabel = new CircularImageLabel(FOTO_POR_DEFECTO, 50); // Carga la de por defecto al inicio

        labelNombre = new JLabel("Nombre de Usuario");
        labelNombre.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "font: bold;");

        labelEmail = new JLabel("email@usuario.com");
        labelEmail.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "foreground: $Label.secondaryForeground;");

        add(imageLabel, "spany 2, width 50!, height 50!");
        add(labelNombre, "wrap, gapy 0");
        add(labelEmail, "gapy 0");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof Usuario) {
            Usuario usuario = (Usuario) value;

            labelNombre.setText(usuario.getNombre());
            labelEmail.setText(usuario.getEmail());

            // --- ¡SOLUCIÓN 2: LÓGICA A PRUEBA DE FALLOS! ---
            String nombreFoto = usuario.getFotoUrl(); // Esto es "fernando.jpg" (o null)

            try {
                if (nombreFoto != null && !nombreFoto.isEmpty()) {
                    // 1. Construimos la ruta completa
                    String rutaCompleta = RUTA_BASE_FOTOS + nombreFoto;
                    imageLabel.setImage(rutaCompleta);
                } else {
                    // 2. Si la BD tiene null, usamos la de por defecto
                    imageLabel.setImage(FOTO_POR_DEFECTO);
                }
            } catch (Exception e) {
                // 3. Si la foto no se encuentra (ej. "pedro.jpg" no existe),
                // usamos la de por defecto para evitar el crasheo.
                System.err.println("Error al cargar imagen: " + e.getMessage());
                imageLabel.setImage(FOTO_POR_DEFECTO);
            }
            // ---------------------------------------------
        }

        // (Tu lógica de selección está perfecta)
        if (isSelected) {
            setBackground(table.getSelectionBackground());
            labelNombre.setForeground(table.getSelectionForeground());
            labelEmail.setForeground(table.getSelectionForeground());
        } else {
            setBackground(table.getBackground());
            labelNombre.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "font: bold;");
            labelEmail.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "foreground: $Label.secondaryForeground;");
        }

        return this;
    }
}
