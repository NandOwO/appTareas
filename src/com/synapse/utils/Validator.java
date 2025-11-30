package com.synapse.utils;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * Utilidad para validaciones centralizadas
 * Proporciona métodos de validación reutilizables
 * 
 * @author FERNANDO
 */
public class Validator {

    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    /**
     * Valida formato de email
     */
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valida que una fecha no sea nula y sea futura
     */
    public static boolean validarFechaFutura(Date fecha) {
        if (fecha == null) {
            return false;
        }
        return fecha.after(new Date());
    }

    /**
     * Valida que una fecha no sea nula
     */
    public static boolean validarFecha(Date fecha) {
        return fecha != null;
    }

    /**
     * Valida que un campo no esté vacío
     */
    public static boolean validarCampoRequerido(String campo) {
        return campo != null && !campo.trim().isEmpty();
    }

    /**
     * Valida que un campo tenga una longitud mínima
     */
    public static boolean validarLongitudMinima(String campo, int longitudMinima) {
        if (campo == null) {
            return false;
        }
        return campo.trim().length() >= longitudMinima;
    }

    /**
     * Valida que un campo tenga una longitud máxima
     */
    public static boolean validarLongitudMaxima(String campo, int longitudMaxima) {
        if (campo == null) {
            return true; // null es válido para longitud máxima
        }
        return campo.trim().length() <= longitudMaxima;
    }

    /**
     * Valida tamaño de archivo en bytes
     */
    public static boolean validarTamañoArchivo(long bytes, long maxBytes) {
        return bytes > 0 && bytes <= maxBytes;
    }

    /**
     * Valida formato de teléfono (10 dígitos)
     */
    public static boolean validarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(telefono.trim()).matches();
    }

    /**
     * Valida que un número sea positivo
     */
    public static boolean validarNumeroPositivo(int numero) {
        return numero > 0;
    }

    /**
     * Valida que un número esté en un rango
     */
    public static boolean validarRango(int numero, int min, int max) {
        return numero >= min && numero <= max;
    }

    /**
     * Valida formato de contraseña
     * Mínimo 6 caracteres, al menos una letra y un número
     */
    public static boolean validarPassword(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }

        boolean tieneLetra = password.matches(".*[a-zA-Z].*");
        boolean tieneNumero = password.matches(".*[0-9].*");

        return tieneLetra && tieneNumero;
    }

    /**
     * Valida extensión de archivo
     */
    public static boolean validarExtensionArchivo(String nombreArchivo, String[] extensionesPermitidas) {
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            return false;
        }

        String extension = obtenerExtension(nombreArchivo);
        if (extension == null) {
            return false;
        }

        for (String ext : extensionesPermitidas) {
            if (extension.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene la extensión de un archivo
     */
    public static String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            return null;
        }

        int lastDot = nombreArchivo.lastIndexOf('.');
        if (lastDot == -1 || lastDot == nombreArchivo.length() - 1) {
            return null;
        }

        return nombreArchivo.substring(lastDot + 1);
    }

    /**
     * Valida que un ID sea válido (mayor a 0)
     */
    public static boolean validarId(int id) {
        return id > 0;
    }

    /**
     * Sanitiza un string removiendo caracteres especiales
     */
    public static String sanitizarString(String input) {
        if (input == null) {
            return null;
        }
        // Remover caracteres peligrosos para SQL injection
        return input.replaceAll("[';\"\\\\]", "");
    }

    /**
     * Valida que dos strings sean iguales
     */
    public static boolean validarIgualdad(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }
}
