package com.synapse.ui.views.login;

import com.twelvemonkeys.image.ImageUtil;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import com.synapse.ui.lib.raven.FancyBorderRadius;
import com.synapse.ui.lib.shadow.ShadowRenderer;

/**
 *
 * @author FERNANDO
 */
public class Background extends JComponent {

    private Icon image;
    private BufferedImage bufferedImage;
    private Component blur;
    private Object Rectangle2D;

    public Component getBlur() {
        return blur;
    }

    public void setBlur(Component blur) {
        this.blur = blur;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createImage();
                repaint();
            }
        });
    }

    // En: com/synapse/ui/views/login/Background.java
    public Background() {
        // Hacemos el componente "seguro para el diseñador"
        try {
            // 1. Intentamos obtener la URL de la imagen
            java.net.URL imgUrl = getClass().getResource("/images/synapse.png");

            // 2. Comprobamos si la encontró
            if (imgUrl != null) {
                image = new ImageIcon(imgUrl);
            } else {
                // 3. Si no la encuentra, no falla. Solo avisa en la consola.
                System.err.println("ADVERTENCIA (Diseñador): No se pudo cargar /images/img_2.jpg");
                // Opcional: Pon un color de fondo para que no sea invisible en el diseñador
                setBackground(java.awt.Color.GRAY);
            }
        } catch (Exception e) {
            // 4. Captura cualquier otro error
            System.err.println("Error cargando Background: " + e.getMessage());
            setBackground(java.awt.Color.RED); // Pon un fondo rojo si algo sale muy mal
        }
    }

    private void createImage() {
        if (image != null) {
            int width = getWidth();
            int height = getHeight();
            if (width > 0 && height > 0) {
                bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = bufferedImage.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                Rectangle rec = getAutoSize(image);
                g2.drawImage(((ImageIcon) image).getImage(), rec.x, rec.y, rec.width, rec.height, null);
                if (blur != null) {
                    createBlurImage(g2);
                }
                g2.dispose();
            }   
        }
    }

    private void createBlurImage(Graphics2D g) {
        int x = blur.getX();
        int y = blur.getY();
        int width = blur.getWidth();
        int height = blur.getHeight();
        int shadow = 8;

        if (width > 0 && height > 0) {
            // Paso 1: Crear una imagen temporal y un pincel para dibujar en ella.
            BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = tempImage.createGraphics();

            // Configurar los hints para un dibujo de alta calidad
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // Paso 2: Crear la forma de la "caja" y usarla como área de recorte.
            // Todo lo que se dibuje después solo será visible dentro de esta forma.
            Shape shape = new FancyBorderRadius(width, height, "10% 60% 10% 59% / 60% 10% 38% 10%").getShape();
            g2.setClip(shape);

            // Paso 3: Obtener la sub-imagen del fondo para aplicar los efectos.
            BufferedImage subImage = bufferedImage.getSubimage(x, y, width, height);

            // Paso 4: Aplicar el desenfoque y dibujar la imagen borrosa dentro de la forma.
            BufferedImage blurredSubImage = ImageUtil.blur(subImage, 30f);
            g2.drawImage(blurredSubImage, 0, 0, null);

            g2.dispose(); // Liberar los recursos del pincel temporal.

            // Paso 5: Ahora que tenemos la imagen desenfocada con la forma, creamos la sombra
            BufferedImage shadowImage = new ShadowRenderer(shadow, 0.3f, new Color(0, 0, 0)).createShadow(tempImage);

            // Paso 6: Dibujar la sombra y la imagen final sobre el fondo principal.
            // Dibujamos la sombra primero, con un desplazamiento, y luego la imagen final encima.
            g.drawImage(shadowImage, x, y, null);
            g.drawImage(tempImage, x, y, null);
        }
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        if (bufferedImage != null) {
            grphcs.drawImage(bufferedImage, 0, 0, null);
        }
        super.paintComponent(grphcs);
    }

    @Override
    public void setBounds(int i, int i1, int i2, int i3) {
        super.setBounds(i, i1, i2, i3);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createImage();
                repaint();
            }
        });
    }

    private Rectangle getAutoSize(Icon image) {
        int w = getWidth();
        int h = getHeight();
        int iw = image.getIconWidth();
        int ih = image.getIconHeight();
        double xScale = (double) w / iw;
        double yScale = (double) h / ih;
        double scale = Math.max(xScale, yScale);
        int width = (int) (scale * iw);
        int height = (int) (scale * ih);
        if (width < 1) {
            width = 1;
        }
        if (height < 1) {
            height = 1;
        }
        int x = (w - width) / 2;
        int y = (h - height) / 2;
        return new Rectangle(new Point(x, y), new Dimension(width, height));
    }

}
