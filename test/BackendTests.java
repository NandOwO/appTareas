package com.synapse.test;

import com.synapse.core.models.*;
import com.synapse.core.services.*;
import com.synapse.data.dao.*;
import com.synapse.utils.PasswordHasher;
import com.synapse.utils.Validator;
import java.util.*;

/**
 * Clase de pruebas del backend
 * Ejecuta pruebas automatizadas de todos los componentes
 * 
 * @author FERNANDO
 */
public class BackendTests {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║   PRUEBAS DEL BACKEND - Sistema de Gestión de Tareas  ║");
        System.out.println("╚════════════════════════════════════════════════════════╝\n");

        // Pruebas de DAOs
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│      PRUEBAS DE DAOs                │");
        System.out.println("└─────────────────────────────────────┘");
        testTareaDAO();
        testUsuarioDAO();
        testEquipoDAO();
        testNotificacionDAO();

        // Pruebas de Servicios
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│    PRUEBAS DE SERVICIOS (Facade)    │");
        System.out.println("└─────────────────────────────────────┘");
        testTareaService();
        testUsuarioService();
        testEquipoService();

        // Pruebas de Utilidades
        System.out.println("\n┌─────────────────────────────────────┐");
        System.out.println("│     PRUEBAS DE UTILIDADES           │");
        System.out.println("└─────────────────────────────────────┘");
        testPasswordHasher();
        testValidator();

        // Resumen
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║                    RESUMEN                             ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.printf("║  ✅ Pruebas Exitosas:  %-30d  ║%n", testsPassed);
        System.out.printf("║  ❌ Pruebas Fallidas:  %-30d  ║%n", testsFailed);
        System.out.printf("║  📊 Total:             %-30d  ║%n", testsPassed + testsFailed);
        System.out.println("╚════════════════════════════════════════════════════════╝");

        if (testsFailed == 0) {
            System.out.println("\n🎉 ¡TODAS LAS PRUEBAS PASARON EXITOSAMENTE!");
        } else {
            System.out.println("\n⚠️  Algunas pruebas fallaron. Revisa los errores arriba.");
        }
    }

    // ========== PRUEBAS DE DAOs ==========

    private static void testTareaDAO() {
        System.out.println("\n📝 TareaDAO:");
        TareaDAO dao = new TareaDAO();

        try {
            // Test 1: Obtener tarea por ID
            Tarea tarea = dao.getTareaPorId(1);
            assertNotNull("getTareaPorId()", tarea);

            // Test 2: Buscar tareas
            List<Tarea> tareas = dao.buscarTareas("módulo");
            assertNotNull("buscarTareas()", tareas);

            // Test 3: Obtener tareas por usuario
            List<Tarea> tareasPorUsuario = dao.getTareasPorUsuario(1);
            assertNotNull("getTareasPorUsuario()", tareasPorUsuario);

            // Test 4: Obtener usuarios asignados
            List<Usuario> usuarios = dao.getUsuariosAsignadosPorTarea(1);
            assertNotNull("getUsuariosAsignadosPorTarea()", usuarios);

        } catch (Exception e) {
            System.out.println("   ❌ Error en TareaDAO: " + e.getMessage());
            testsFailed++;
        }
    }

    private static void testUsuarioDAO() {
        System.out.println("\n👤 UsuarioDAO:");
        UsuarioDAO dao = new UsuarioDAO();

        try {
            // Test 1: Login
            Usuario usuario = dao.login("admin@synapse.com", "test123");
            assertNotNull("login()", usuario);

            // Test 2: Obtener por email
            Usuario usuarioPorEmail = dao.getUsuarioPorEmail("carlos.lopez@synapse.com");
            assertNotNull("getUsuarioPorEmail()", usuarioPorEmail);

            // Test 3: Buscar usuarios
            List<Usuario> usuarios = dao.buscarUsuarios("Lopez");
            assertNotNull("buscarUsuarios()", usuarios);

            // Test 4: Obtener todos los usuarios
            List<Usuario> todosUsuarios = dao.getUsuarios();
            assertTrue("getUsuarios() > 0", todosUsuarios != null && todosUsuarios.size() > 0);

        } catch (Exception e) {
            System.out.println("   ❌ Error en UsuarioDAO: " + e.getMessage());
            testsFailed++;
        }
    }

    private static void testEquipoDAO() {
        System.out.println("\n👥 EquipoDAO:");
        EquipoDAO dao = new EquipoDAO();

        try {
            // Test 1: Obtener todos los equipos
            List<Equipo> equipos = dao.getEquipos();
            assertTrue("getEquipos() > 0", equipos != null && equipos.size() > 0);

            if (!equipos.isEmpty()) {
                int idEquipo = equipos.get(0).getIdEquipo();

                // Test 2: Obtener equipo por ID
                Equipo equipo = dao.getEquipoPorId(idEquipo);
                assertNotNull("getEquipoPorId()", equipo);

                // Test 3: Obtener miembros
                List<Usuario> miembros = dao.getMiembros(idEquipo);
                assertNotNull("getMiembros()", miembros);

                // Test 4: Verificar membresía
                if (!miembros.isEmpty()) {
                    boolean esMiembro = dao.esMiembro(idEquipo, miembros.get(0).getIdUsuario());
                    assertTrue("esMiembro()", esMiembro);
                }
            }

        } catch (Exception e) {
            System.out.println("   ❌ Error en EquipoDAO: " + e.getMessage());
            testsFailed++;
        }
    }

    private static void testNotificacionDAO() {
        System.out.println("\n🔔 NotificacionDAO:");
        NotificacionDAO dao = new NotificacionDAO();

        try {
            // Test 1: Obtener notificaciones por usuario
            List<Notificacion> notificaciones = dao.getNotificacionesPorUsuario(1);
            assertNotNull("getNotificacionesPorUsuario()", notificaciones);

            // Test 2: Obtener no leídas
            List<Notificacion> noLeidas = dao.getNotificacionesNoLeidas(1);
            assertNotNull("getNotificacionesNoLeidas()", noLeidas);

            // Test 3: Contar no leídas
            int count = dao.getNumeroNotificacionesNoLeidas(1);
            assertTrue("getNumeroNotificacionesNoLeidas() >= 0", count >= 0);

        } catch (Exception e) {
            System.out.println("   ❌ Error en NotificacionDAO: " + e.getMessage());
            testsFailed++;
        }
    }

    // ========== PRUEBAS DE SERVICIOS ==========

    private static void testTareaService() {
        System.out.println("\n📋 TareaService:");
        TareaService service = new TareaService();

        try {
            // Test 1: Obtener tareas por usuario
            List<Tarea> tareas = service.getTareasPorUsuario(1);
            assertNotNull("getTareasPorUsuario()", tareas);

            // Test 2: Buscar tareas
            List<Tarea> tareasEncontradas = service.buscarTareas("módulo");
            assertNotNull("buscarTareas()", tareasEncontradas);

            // Test 3: Obtener tarea por ID
            Tarea tarea = service.getTareaPorId(1);
            assertNotNull("getTareaPorId()", tarea);

            System.out.println("   ℹ️  Nota: Pruebas de creación/modificación omitidas para no alterar datos");

        } catch (Exception e) {
            System.out.println("   ❌ Error en TareaService: " + e.getMessage());
            testsFailed++;
        }
    }

    private static void testUsuarioService() {
        System.out.println("\n👤 UsuarioService:");
        UsuarioService service = new UsuarioService();

        try {
            // Test 1: Login
            Usuario usuario = service.login("admin@synapse.com", "test123");
            assertNotNull("login()", usuario);

            // Test 2: Buscar usuarios
            List<Usuario> usuarios = service.buscarUsuarios("garcia");
            assertNotNull("buscarUsuarios()", usuarios);

            // Test 3: Obtener todos
            List<Usuario> todosUsuarios = service.getUsuarios();
            assertTrue("getUsuarios() > 0", todosUsuarios != null && todosUsuarios.size() > 0);

            System.out.println("   ℹ️  Nota: Pruebas de creación/modificación omitidas para no alterar datos");

        } catch (Exception e) {
            System.out.println("   ❌ Error en UsuarioService: " + e.getMessage());
            testsFailed++;
        }
    }

    private static void testEquipoService() {
        System.out.println("\n👥 EquipoService:");
        EquipoService service = new EquipoService();

        try {
            // Test 1: Obtener todos los equipos
            List<Equipo> equipos = service.getEquipos();
            assertTrue("getEquipos() > 0", equipos != null && equipos.size() > 0);

            if (!equipos.isEmpty()) {
                int idEquipo = equipos.get(0).getIdEquipo();

                // Test 2: Obtener equipo por ID
                Equipo equipo = service.getEquipoPorId(idEquipo);
                assertNotNull("getEquipoPorId()", equipo);

                // Test 3: Obtener miembros
                List<Usuario> miembros = service.getMiembros(idEquipo);
                assertNotNull("getMiembros()", miembros);
            }

            System.out.println("   ℹ️  Nota: Pruebas de creación/modificación omitidas para no alterar datos");

        } catch (Exception e) {
            System.out.println("   ❌ Error en EquipoService: " + e.getMessage());
            testsFailed++;
        }
    }

    // ========== PRUEBAS DE UTILIDADES ==========

    private static void testPasswordHasher() {
        System.out.println("\n🔐 PasswordHasher:");

        try {
            // Test 1: Hash de contraseña
            String password = "test123";
            String hash = PasswordHasher.hashPassword(password);
            assertNotNull("hashPassword()", hash);
            assertTrue("Hash length > 50", hash.length() > 50);

            // Test 2: Verificar contraseña
            boolean isValid = PasswordHasher.verifyPassword(password, hash);
            assertTrue("verifyPassword() correcta", isValid);

            // Test 3: Verificar contraseña incorrecta
            boolean isInvalid = PasswordHasher.verifyPassword("wrong", hash);
            assertTrue("verifyPassword() incorrecta", !isInvalid);

        } catch (Exception e) {
            System.out.println("   ❌ Error en PasswordHasher: " + e.getMessage());
            testsFailed++;
        }
    }

    private static void testValidator() {
        System.out.println("\n✔️  Validator:");

        try {
            // Test 1: Validar email
            boolean emailValido = Validator.validarEmail("test@example.com");
            assertTrue("validarEmail() válido", emailValido);

            boolean emailInvalido = Validator.validarEmail("invalid-email");
            assertTrue("validarEmail() inválido", !emailInvalido);

            // Test 2: Validar password
            boolean passValida = Validator.validarPassword("test123");
            assertTrue("validarPassword() válida", passValida);

            boolean passInvalida = Validator.validarPassword("123");
            assertTrue("validarPassword() inválida", !passInvalida);

            // Test 3: Validar campo requerido
            boolean campoValido = Validator.validarCampoRequerido("texto");
            assertTrue("validarCampoRequerido() válido", campoValido);

            boolean campoInvalido = Validator.validarCampoRequerido("");
            assertTrue("validarCampoRequerido() inválido", !campoInvalido);

        } catch (Exception e) {
            System.out.println("   ❌ Error en Validator: " + e.getMessage());
            testsFailed++;
        }
    }

    // ========== MÉTODOS DE UTILIDAD ==========

    private static void assertNotNull(String testName, Object obj) {
        if (obj != null) {
            System.out.println("   ✅ " + testName + " - PASS");
            testsPassed++;
        } else {
            System.out.println("   ❌ " + testName + " - FAIL (null)");
            testsFailed++;
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("   ✅ " + testName + " - PASS");
            testsPassed++;
        } else {
            System.out.println("   ❌ " + testName + " - FAIL");
            testsFailed++;
        }
    }
}
