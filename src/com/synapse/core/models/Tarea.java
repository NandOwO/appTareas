package com.synapse.core.models;

import java.sql.Timestamp;
import java.util.List;

public class Tarea {

    // Campos directos de la tabla 'tareas'
    private int idTarea;
    private String titulo;
    private String descripcion;
    private Timestamp fechaCreacion;
    private Timestamp fechaLimite;
    private int creadaPor; // id_usuario
    private int idEstado;
    private int idPrioridad;

    // Campos de JOIN (para mostrar en la UI)
    private String nombreEstado;
    private String nombrePrioridad;
    private Usuario creador;
    
    private String nombreAsignado;
    
    // Campos de JOIN (Asignaciones)
    private List<Usuario> usuariosAsignados;
    private List<Equipo> equiposAsignados;
    private List<Adjunto> adjuntos; // Necesitarás un modelo Adjunto.java

    // Constructor privado para el Patrón Builder
    private Tarea(Builder builder) {
        this.idTarea = builder.idTarea;
        this.titulo = builder.titulo;
        this.descripcion = builder.descripcion;
        this.fechaCreacion = builder.fechaCreacion;
        this.fechaLimite = builder.fechaLimite;
        this.creadaPor = builder.creadaPor;
        this.idEstado = builder.idEstado;
        this.idPrioridad = builder.idPrioridad;
    }

    // Constructor vacío para el DAO
    public Tarea() {
    }

    // --- Getters y Setters (Necesarios para que el DAO hidrate el objeto) ---
    public int getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(int idTarea) {
        this.idTarea = idTarea;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Timestamp getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Timestamp fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public int getCreadaPor() {
        return creadaPor;
    }

    public void setCreadaPor(int creadaPor) {
        this.creadaPor = creadaPor;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public int getIdPrioridad() {
        return idPrioridad;
    }

    public void setIdPrioridad(int idPrioridad) {
        this.idPrioridad = idPrioridad;
    }

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public String getNombrePrioridad() {
        return nombrePrioridad;
    }

    public void setNombrePrioridad(String nombrePrioridad) {
        this.nombrePrioridad = nombrePrioridad;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public List<Usuario> getUsuariosAsignados() {
        return usuariosAsignados;
    }

    public void setUsuariosAsignados(List<Usuario> usuariosAsignados) {
        this.usuariosAsignados = usuariosAsignados;
    }

    public List<Equipo> getEquiposAsignados() {
        return equiposAsignados;
    }

    public void setEquiposAsignados(List<Equipo> equiposAsignados) {
        this.equiposAsignados = equiposAsignados;
    }

    public List<Adjunto> getAdjuntos() {
        return adjuntos;
    }

    public void setAdjuntos(List<Adjunto> adjuntos) {
        this.adjuntos = adjuntos;
    }

    public String getNombreAsignado() {
        return nombreAsignado;
    }

    public void setNombreAsignado(String nombreAsignado) {
        this.nombreAsignado = nombreAsignado;
    }
    
    // --- PATRÓN BUILDER (Constructor) ---
    public static class Builder {

        // Requeridos
        private final String titulo;
        private final int creadaPor;

        // Opcionales (con valores por defecto)
        private int idTarea = 0;
        private String descripcion = "";
        private Timestamp fechaCreacion = new Timestamp(System.currentTimeMillis());
        private Timestamp fechaLimite = null;
        private int idEstado = 1; // Asumimos 1 = "Pendiente"
        private int idPrioridad = 1; // Asumimos 1 = "Baja"

        // Constructor para campos requeridos
        public Builder(String titulo, int creadaPor) {
            this.titulo = titulo;
            this.creadaPor = creadaPor;
        }

        public Builder idTarea(int id) {
            this.idTarea = id;
            return this;
        }

        public Builder descripcion(String desc) {
            this.descripcion = desc;
            return this;
        }

        public Builder fechaCreacion(Timestamp fecha) {
            this.fechaCreacion = fecha;
            return this;
        }

        public Builder fechaLimite(Timestamp fecha) {
            this.fechaLimite = fecha;
            return this;
        }

        public Builder idEstado(int id) {
            this.idEstado = id;
            return this;
        }

        public Builder idPrioridad(int id) {
            this.idPrioridad = id;
            return this;
        }

        // Método final para construir el objeto Tarea
        public Tarea build() {
            return new Tarea(this);
        }
    }
}
