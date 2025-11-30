package com.synapse.ui.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JLabel;

/**
 * Un JLabel que muestra una imagen de forma circular. Tiene dos constructores
 * para ser compatible con Menu.java y con cardEquipo.java.
 */
public class CircularImageLabel extends JLabel {

    private BufferedImage masterImage;
    private int diameter;

    /**
     * CONSTRUCTOR VACÍO (para Menu.java) Carga una imagen y tamaño por defecto.
     */
    public CircularImageLabel() {
        this("/icons/users/defaultUser.jpg", 60); // Carga default 60px
    }

    /**
     * CONSTRUCTOR CON PARÁMETROS (para cardEquipo.java)
     *
     * @param imagePath La ruta de la imagen (ej: "/icons/users/fernando.jpg")
     * @param diameter El diámetro deseado
     */
    public CircularImageLabel(String imagePath, int diameter) {
        this.diameter = diameter;
        setPreferredSize(new Dimension(diameter, diameter));
        loadImage(imagePath);
    }

    /**
     * Carga la imagen desde la ruta de recursos y la escala.
     */
    private void loadImage(String imagePath) {
        try {
            URL url = getClass().getResource(imagePath);

            if (url == null) {
                System.err.println("No se encontró el recurso: " + imagePath);
                url = getClass().getResource("/icons/users/defaultUser.jpg"); // Respaldo
            }

            BufferedImage loadedImage = ImageIO.read(url);

            masterImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = masterImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(loadedImage, 0, 0, diameter, diameter, null);
            g2d.dispose();

        } catch (Exception e) {
            e.printStackTrace();
            masterImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        }
    }

    /**
     * Método público para cambiar la imagen después de crear el componente.
     * (Útil para que Menu.java actualice la foto después del login).
     */
    public void setImage(String imagePath) {
        loadImage(imagePath);
        repaint(); // Vuelve a dibujar el componente con la nueva imagen
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (masterImage == null) {
            super.paintComponent(g);
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Ellipse2D.Double clip = new Ellipse2D.Double(0, 0, diameter, diameter);
        g2.setClip(clip);
        g2.drawImage(masterImage, 0, 0, this);

        g2.dispose();
    }
}
