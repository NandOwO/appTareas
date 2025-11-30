package com.synapse.ui.components;

import com.synapse.core.models.Usuario;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * @author @Fernando
 */
public class UsuarioRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        // Llama al método padre para que maneje los colores de selección, etc.
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        // Si el objeto es un Usuario...
        if (value instanceof Usuario) {
            Usuario usuario = (Usuario) value;
            // ...pon su nombre como texto.
            setText(usuario.getNombre());
        }
        
        return this;
    }
}