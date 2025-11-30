package com.synapse.ui.components;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.SwingUtilities;
import java.awt.Frame;
import javax.swing.table.DefaultTableModel;
import com.synapse.core.models.Usuario;
import com.synapse.ui.views.admin.EditarUsuario;

/**
 * Edita la celda mostrando el mismo botón de icono redondo.
 */
public class BotonEditar extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private final JPanel panel;
    private final JButton actionButton;
    private int currentRow;
    private JTable table;

    public BotonEditar() {
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.setOpaque(false);
        actionButton = new JButton();
        actionButton.setIcon(new FlatSVGIcon("icons/general/crear.svg"));
        actionButton.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 999;"
                + "margin: 5,5,5,5;"
                + "background: null;"
                + "borderColor: null;");
        actionButton.setBackground(null);
        actionButton.setFocusable(false);
        actionButton.setBorderPainted(false);
        actionButton.addActionListener(this);
        panel.add(actionButton);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {

        this.currentRow = row;
        this.table = table;

        panel.setOpaque(true);
        panel.setBackground(table.getSelectionBackground());

        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "Editar";
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            DefaultTableModel model = (DefaultTableModel) this.table.getModel();
            int modelRow = this.table.convertRowIndexToModel(currentRow);
            Object usuarioObj = model.getValueAt(modelRow, 0);

            if (usuarioObj instanceof Usuario) {
                Usuario usuarioAEditar = (Usuario) usuarioObj;
                Frame parent = (Frame) SwingUtilities.getWindowAncestor(this.table);

                // Abrir dialog de edición
                com.synapse.ui.views.admin.EditarUsuario dialog = new EditarUsuario(parent, usuarioAEditar);
                dialog.setVisible(true);

                // Si se guardaron cambios, actualizar la fila
                if (dialog.isGuardado()) {
                    model.setValueAt(usuarioAEditar, modelRow, 0);
                    model.setValueAt(usuarioAEditar.getRol(), modelRow, 1);
                    model.fireTableRowsUpdated(modelRow, modelRow);
                }
            } else {
                System.err.println("Error: El objeto en la fila " + modelRow + " no es un Usuario.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        fireEditingStopped();
    }

    @Override
    public boolean isCellEditable(java.util.EventObject anEvent) {
        return true;
    }
}
