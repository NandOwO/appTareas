
package com.synapse.ui.components;

/**
 *
 * @author FERNANDO
 */

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class Card extends JPanel {

    private JLabel lbTitle;
    private JLabel lbValue;
    private JLabel lbIcon;

    // CONSTRUCTOR VACÍO para el editor de NetBeans
    public Card() {
        this("", 0, null);
        setBorder(null);
    }
    
    // CONSTRUCTOR PRINCIPAL con argumentos
    public Card(String title, int value, ImageIcon icon) {
        initComponents();
        setTitle(title);
        setValue(value);
        setIcon(icon);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());

        putClientProperty(FlatClientProperties.STYLE, ""
                + "arc:20;"
                + "border:8,10,8,10;"
                + "background: $Panel.background;");

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());
        textPanel.setOpaque(false);

        lbTitle = new JLabel();
        lbTitle.setFont(lbTitle.getFont().deriveFont(Font.PLAIN, 12f));
        textPanel.add(lbTitle, BorderLayout.NORTH);

        lbValue = new JLabel();
        lbValue.setFont(lbValue.getFont().deriveFont(Font.BOLD, 22f));
        textPanel.add(lbValue, BorderLayout.CENTER);

        add(textPanel, BorderLayout.CENTER);

        lbIcon = new JLabel();
        lbIcon.setBorder(new EmptyBorder(0, 10, 0, 0));
        lbIcon.setHorizontalAlignment(SwingConstants.RIGHT);
        lbIcon.setVerticalAlignment(SwingConstants.CENTER);
        
        add(lbIcon, BorderLayout.EAST);
    }
    
    public String getTitle() {
        return lbTitle.getText();
    }
    
    public void setTitle(String title) {
        lbTitle.setText(title);
    }
    
    public int getValue() {
        try {
            return Integer.parseInt(lbValue.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    public void setValue(int value) {
        lbValue.setText(String.valueOf(value));
    }
    
    public ImageIcon getIcon() {
        return (ImageIcon) lbIcon.getIcon();
    }
    
    public void setIcon(ImageIcon icon) {
        lbIcon.setIcon(icon);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(220, 80);
    }
}