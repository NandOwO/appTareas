package com.synapse.core.services;

import com.synapse.core.models.Usuario;
import com.synapse.ui.views.admin.DashbordAdmin;
import com.synapse.ui.views.empleado.DashbordEmpleado;
import com.synapse.ui.views.gerente.DashbordGerente;
import javax.swing.JPanel;

/**
 * 
 * @author FERNANDO
 */
public class ViewFactory {

    public static JPanel crearDashboard(Usuario usuario) {


        String rol = usuario.getRol();

        if (rol == null) {
            System.err.println("El usuario no tiene un rol asignado. Cargando vista por defecto.");
            return new DashbordEmpleado(usuario); 
        }

        switch (rol) {
            case "administrador":
                return new DashbordAdmin(usuario);
            case "gerente":
                return new DashbordGerente(usuario);
            case "empleado":
                return new DashbordEmpleado(usuario);
            default:
                System.err.println("Rol desconocido: " + rol + ". Cargando vista por defecto.");
                return new DashbordEmpleado(usuario);
        }
    }
}