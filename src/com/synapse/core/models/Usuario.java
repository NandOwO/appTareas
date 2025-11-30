package com.synapse.core.models;

import java.util.List;

public class Usuario {

    private int idUsuario;
    private String nombre;
    private String email;
    private String fotoUrl;
    private boolean activo;
    private String codigoEmpleado;

    // Campos de JOIN (desde credenciales y roles)
    private int idRol;
    private String rol;

    // Campos de JOIN (desde equipo_miembros)
    private List<Equipo> equipos;

    public Usuario() {
    }

    // Constructor completo (útil para el DAO)
    public Usuario(int idUsuario, String nombre, String email, String fotoUrl, boolean activo, String codigoEmpleado, int idRol, String rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.fotoUrl = fotoUrl;
        this.activo = activo;
        this.codigoEmpleado = codigoEmpleado;
        this.idRol = idRol;
        this.rol = rol;
    }

    // --- Getters y Setters ---
    
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getCodigoEmpleado() {
        return codigoEmpleado;
    }

    public void setCodigoEmpleado(String codigoEmpleado) {
        this.codigoEmpleado = codigoEmpleado;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<Equipo> equipos) {
        this.equipos = equipos;
    }
    
    // Override toString() para JComboBox/JList si es necesario
    @Override
    public String toString() {
        return nombre; 
    }
}