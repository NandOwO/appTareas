package com.synapse.core.services.notifications;

/**
 * Templates HTML para emails
 * Proporciona plantillas profesionales para diferentes tipos de notificaciones
 * 
 * @author FERNANDO
 */
public class EmailTemplates {

        /**
         * Template para asignación de tarea
         */
        public static String getTemplateAsignacionTarea(String nombreUsuario, String tituloTarea,
                        String descripcion, String fechaLimite,
                        boolean tieneAdjuntos,
                        String listaAdjuntos) {
                String adjuntosHtml = "";
                if (tieneAdjuntos) {
                        adjuntosHtml = String.format(
                                        "<div style='background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0;'>"
                                                        +
                                                        "<h3 style='color: #856404; margin-top: 0;'>📎 Archivos Adjuntos</h3>"
                                                        +
                                                        "<p style='color: #856404; margin: 0;'>%s</p>" +
                                                        "<p style='color: #856404; margin: 5px 0 0 0; font-size: 12px;'>"
                                                        +
                                                        "<em>Los archivos han sido adjuntados a este correo</em></p>" +
                                                        "</div>",
                                        listaAdjuntos);
                }

                return String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head><meta charset='UTF-8'></head>" +
                                                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                                                +
                                                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;'>"
                                                +
                                                "<div style='background-color: #007bff; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0;'>"
                                                +
                                                "<h1 style='margin: 0;'>📋 Nueva Tarea Asignada</h1>" +
                                                "</div>" +
                                                "<div style='background-color: white; padding: 30px; border-radius: 0 0 5px 5px;'>"
                                                +
                                                "<p style='font-size: 16px;'>Hola <strong>%s</strong>,</p>" +
                                                "<p>Se te ha asignado una nueva tarea:</p>" +
                                                "<div style='background-color: #e9ecef; padding: 20px; border-radius: 5px; margin: 20px 0;'>"
                                                +
                                                "<h2 style='color: #007bff; margin-top: 0;'>%s</h2>" +
                                                "<p><strong>Descripción:</strong> %s</p>" +
                                                "<p><strong>Fecha Límite:</strong> <span style='color: #dc3545;'>%s</span></p>"
                                                +
                                                "</div>" +
                                                "%s" +
                                                "<p style='margin-top: 30px;'>Por favor, revisa los detalles y la prioridad en el sistema.</p>"
                                                +
                                                "<p style='color: #6c757d; font-size: 14px; margin-top: 30px; border-top: 1px solid #dee2e6; padding-top: 20px;'>"
                                                +
                                                "Este es un mensaje automático del Sistema de Gestión de Tareas Synapse.<br>"
                                                +
                                                "Por favor, no respondas a este correo." +
                                                "</p>" +
                                                "</div>" +
                                                "</div>" +
                                                "</body>" +
                                                "</html>",
                                nombreUsuario, tituloTarea, descripcion, fechaLimite, adjuntosHtml);
        }

        /**
         * Template para vencimiento próximo
         */
        public static String getTemplateVencimientoProximo(String nombreUsuario, String tituloTarea,
                        String fechaLimite, int horasRestantes) {
                return String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head><meta charset='UTF-8'></head>" +
                                                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                                                +
                                                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;'>"
                                                +
                                                "<div style='background-color: #dc3545; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0;'>"
                                                +
                                                "<h1 style='margin: 0;'>⚠️ Tarea Próxima a Vencer</h1>" +
                                                "</div>" +
                                                "<div style='background-color: white; padding: 30px; border-radius: 0 0 5px 5px;'>"
                                                +
                                                "<p style='font-size: 16px;'>Hola <strong>%s</strong>,</p>" +
                                                "<p style='color: #dc3545; font-size: 18px;'><strong>¡Atención!</strong> Una de tus tareas está próxima a vencer.</p>"
                                                +
                                                "<div style='background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 20px; margin: 20px 0;'>"
                                                +
                                                "<h2 style='color: #856404; margin-top: 0;'>%s</h2>" +
                                                "<p><strong>Fecha Límite:</strong> %s</p>" +
                                                "<p><strong>Tiempo Restante:</strong> <span style='color: #dc3545; font-size: 20px;'>%d horas</span></p>"
                                                +
                                                "</div>" +
                                                "<p style='margin-top: 30px;'>Te recomendamos priorizar esta tarea para completarla a tiempo.</p>"
                                                +
                                                "<p style='color: #6c757d; font-size: 14px; margin-top: 30px; border-top: 1px solid #dee2e6; padding-top: 20px;'>"
                                                +
                                                "Este es un mensaje automático del Sistema de Gestión de Tareas Synapse."
                                                +
                                                "</p>" +
                                                "</div>" +
                                                "</div>" +
                                                "</body>" +
                                                "</html>",
                                nombreUsuario, tituloTarea, fechaLimite, horasRestantes);
        }

        /**
         * Template para cambio de estado
         */
        public static String getTemplateCambioEstado(String nombreUsuario, String tituloTarea,
                        String nuevoEstado) {
                String colorEstado = nuevoEstado.equalsIgnoreCase("completada") ? "#28a745"
                                : nuevoEstado.equalsIgnoreCase("en progreso") ? "#007bff" : "#6c757d";

                return String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head><meta charset='UTF-8'></head>" +
                                                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                                                +
                                                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;'>"
                                                +
                                                "<div style='background-color: %s; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0;'>"
                                                +
                                                "<h1 style='margin: 0;'>🔄 Cambio de Estado</h1>" +
                                                "</div>" +
                                                "<div style='background-color: white; padding: 30px; border-radius: 0 0 5px 5px;'>"
                                                +
                                                "<p style='font-size: 16px;'>Hola <strong>%s</strong>,</p>" +
                                                "<p>Una tarea ha cambiado de estado:</p>" +
                                                "<div style='background-color: #e9ecef; padding: 20px; border-radius: 5px; margin: 20px 0;'>"
                                                +
                                                "<h2 style='color: #007bff; margin-top: 0;'>%s</h2>" +
                                                "<p><strong>Nuevo Estado:</strong> <span style='background-color: %s; color: white; padding: 5px 15px; border-radius: 3px; font-size: 16px;'>%s</span></p>"
                                                +
                                                "</div>" +
                                                "<p style='color: #6c757d; font-size: 14px; margin-top: 30px; border-top: 1px solid #dee2e6; padding-top: 20px;'>"
                                                +
                                                "Este es un mensaje automático del Sistema de Gestión de Tareas Synapse."
                                                +
                                                "</p>" +
                                                "</div>" +
                                                "</div>" +
                                                "</body>" +
                                                "</html>",
                                colorEstado, nombreUsuario, tituloTarea, colorEstado, nuevoEstado);
        }

        /**
         * Template para tarea completada
         */
        public static String getTemplateTareaCompletada(String nombreUsuario, String tituloTarea) {
                return String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head><meta charset='UTF-8'></head>" +
                                                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                                                +
                                                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;'>"
                                                +
                                                "<div style='background-color: #28a745; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0;'>"
                                                +
                                                "<h1 style='margin: 0;'>✅ Tarea Completada</h1>" +
                                                "</div>" +
                                                "<div style='background-color: white; padding: 30px; border-radius: 0 0 5px 5px;'>"
                                                +
                                                "<p style='font-size: 16px;'>Hola <strong>%s</strong>,</p>" +
                                                "<p style='font-size: 18px; color: #28a745;'><strong>¡Felicidades!</strong> Has completado una tarea.</p>"
                                                +
                                                "<div style='background-color: #d4edda; border-left: 4px solid #28a745; padding: 20px; margin: 20px 0;'>"
                                                +
                                                "<h2 style='color: #155724; margin-top: 0;'>%s</h2>" +
                                                "</div>" +
                                                "<p style='margin-top: 30px;'>¡Excelente trabajo! Sigue así.</p>" +
                                                "<p style='color: #6c757d; font-size: 14px; margin-top: 30px; border-top: 1px solid #dee2e6; padding-top: 20px;'>"
                                                +
                                                "Este es un mensaje automático del Sistema de Gestión de Tareas Synapse."
                                                +
                                                "</p>" +
                                                "</div>" +
                                                "</div>" +
                                                "</body>" +
                                                "</html>",
                                nombreUsuario, tituloTarea);
        }

        /**
         * Template para notificar al gerente que un empleado completó una tarea
         */
        public static String getTemplateTareaCompletadaPorEmpleado(String nombreGerente, String nombreEmpleado,
                        String tituloTarea) {
                return String.format(
                                "<!DOCTYPE html>" +
                                                "<html>" +
                                                "<head><meta charset='UTF-8'></head>" +
                                                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                                                +
                                                "<div style='max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f8f9fa;'>"
                                                +
                                                "<div style='background-color: #28a745; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0;'>"
                                                +
                                                "<h1 style='margin: 0;'>✅ Tarea Completada</h1>" +
                                                "</div>" +
                                                "<div style='background-color: white; padding: 30px; border-radius: 0 0 5px 5px;'>"
                                                +
                                                "<p style='font-size: 16px;'>Hola <strong>%s</strong>,</p>" +
                                                "<p style='font-size: 18px; color: #28a745;'><strong>¡Buenas noticias!</strong> Un empleado ha completado una tarea.</p>"
                                                +
                                                "<div style='background-color: #d4edda; border-left: 4px solid #28a745; padding: 20px; margin: 20px 0;'>"
                                                +
                                                "<h2 style='color: #155724; margin-top: 0;'>%s</h2>" +
                                                "<p style='margin: 10px 0 0 0; color: #155724;'><strong>Completada por:</strong> %s</p>"
                                                +
                                                "</div>" +
                                                "<p style='margin-top: 30px;'>Puedes revisar los detalles de la tarea en el sistema.</p>"
                                                +
                                                "<p style='color: #6c757d; font-size: 14px; margin-top: 30px; border-top: 1px solid #dee2e6; padding-top: 20px;'>"
                                                +
                                                "Este es un mensaje automático del Sistema de Gestión de Tareas Synapse."
                                                +
                                                "</p>" +
                                                "</div>" +
                                                "</div>" +
                                                "</body>" +
                                                "</html>",
                                nombreGerente, tituloTarea, nombreEmpleado);
        }
}
