# 📖 Manual de Usuario - Sistema Synapse

## Índice

1. [Introducción](#1-introducción)
2. [Inicio Rápido](#2-inicio-rápido)
3. [Roles y Permisos](#3-roles-y-permisos)
4. [Guía por Rol](#4-guía-por-rol)
5. [Funcionalidades Detalladas](#5-funcionalidades-detalladas)
6. [Preguntas Frecuentes](#6-preguntas-frecuentes)

---

## 1. Introducción

### ¿Qué es Synapse?

Synapse es un sistema de gestión de tareas empresarial que permite organizar, asignar y dar seguimiento a tareas en equipos de trabajo. El sistema incluye:

- ✅ Gestión de tareas con múltiples estados y prioridades
- ✅ Asignación individual o a equipos completos
- ✅ Notificaciones automáticas por email
- ✅ Adjuntos de archivos e imágenes
- ✅ Gestión de equipos y miembros
- ✅ Sistema de roles (Admin, Gerente, Empleado)

### Acceso al Sistema

**URL**: Aplicación de escritorio (ejecutar `ant run`)

**Credenciales iniciales**:
- Email: `admin@synapse.com`
- Contraseña: `admin123`

---

## 2. Inicio Rápido

### Primera Vez - Administrador

1. **Iniciar sesión** con las credenciales de administrador
2. **Crear usuarios**:
   - Ir a "Gestión de Usuarios"
   - Clic en "Crear Usuario"
   - Completar formulario (nombre, email, rol)
3. **Crear equipos** (opcional):
   - Ir a "Gestión de Equipos"
   - Clic en "Crear Equipo"
   - Asignar líder y miembros

### Primera Vez - Gerente

1. **Iniciar sesión** con tus credenciales
2. **Crear un equipo**:
   - Ir a "Mis Equipos"
   - Clic en "Crear Equipo"
   - Agregar miembros
3. **Crear primera tarea**:
   - Ir a "Crear Tarea"
   - Completar formulario
   - Asignar a empleado o equipo

### Primera Vez - Empleado

1. **Iniciar sesión** con tus credenciales
2. **Ver tareas asignadas** en el dashboard
3. **Actualizar estado** de tus tareas
4. **Revisar emails** para notificaciones

---

## 3. Roles y Permisos

### 👨‍💼 Administrador

**Permisos**:
- ✅ Crear, editar y eliminar usuarios
- ✅ Asignar roles a usuarios
- ✅ Gestionar todos los equipos
- ✅ Ver todas las tareas del sistema
- ✅ Acceso completo a configuración

**Pantallas**:
- Dashboard de administración
- Gestión de usuarios
- Gestión de equipos
- Configuración del sistema

### 👔 Gerente

**Permisos**:
- ✅ Crear y asignar tareas
- ✅ Crear y gestionar equipos propios
- ✅ Agregar/quitar miembros de equipos
- ✅ Editar tareas creadas por él
- ✅ Ver tareas de sus equipos

**Pantallas**:
- Dashboard de tareas
- Crear tarea
- Mis equipos
- Ver tareas

### 👨‍💻 Empleado

**Permisos**:
- ✅ Ver tareas asignadas
- ✅ Actualizar estado de tareas
- ✅ Ver detalles de tareas
- ✅ Editar perfil propio
- ✅ Cambiar contraseña

**Pantallas**:
- Dashboard de tareas asignadas
- Detalle de tarea
- Mi perfil

---

## 4. Guía por Rol

### 4.1 Guía del Administrador

#### Crear Usuario

1. Ir a **Gestión de Usuarios**
2. Clic en **Crear Usuario**
3. Completar formulario:
   - **Nombre**: Nombre completo
   - **Email**: Correo único
   - **Código Empleado**: Identificador único
   - **Rol**: Administrador / Gerente / Empleado
   - **Contraseña**: Mínimo 6 caracteres
4. Clic en **Guardar**

**Nota**: La contraseña predeterminada puede ser el email del usuario.

#### Editar Usuario

1. Seleccionar usuario de la lista
2. Clic en **Editar**
3. Modificar campos necesarios
4. Clic en **Guardar Cambios**

#### Eliminar Usuario

1. Seleccionar usuario
2. Clic en **Eliminar**
3. Confirmar acción
4. El usuario se marca como inactivo (no se elimina de BD)

#### Gestionar Equipos

1. Ir a **Gestión de Equipos**
2. Ver todos los equipos del sistema
3. Editar o eliminar cualquier equipo
4. Ver miembros de equipos

### 4.2 Guía del Gerente

#### Crear Tarea

1. Ir a **Crear Tarea**
2. Completar formulario:

   **Información Básica**:
   - **Título**: Nombre descriptivo de la tarea
   - **Descripción**: Detalles completos
   - **Fecha Límite**: Seleccionar fecha (solo futuras)
   - **Prioridad**: Baja / Media / Alta

   **Asignación**:
   - **Tipo**: Individual o Equipo
   - Si es Individual: Seleccionar empleado
   - Si es Equipo: Seleccionar equipo

   **Adjuntos** (opcional):
   - Clic en **Adjuntar Imagen** para imágenes
   - Clic en **Adjuntar Archivos** para documentos
   - Se pueden adjuntar múltiples archivos

3. Clic en **Crear Tarea**

**Resultado**:
- ✅ Tarea creada en la base de datos
- ✅ Email enviado automáticamente al asignado
- ✅ Si es equipo, email a todos los miembros
- ✅ Adjuntos incluidos en el email

#### Editar Tarea

1. Ir a **Ver Tareas** o Dashboard
2. Seleccionar tarea
3. Clic en **Editar**
4. Modificar campos:
   - Título
   - Descripción
   - Fecha límite
   - Prioridad
   - Estado
   - Asignación (cambiar empleado o equipo)
5. Clic en **Guardar Cambios**

#### Ver Detalles de Tarea

1. Seleccionar tarea
2. Clic en **Ver**
3. Se muestra:
   - Título y descripción
   - Prioridad y estado
   - Fecha límite
   - Asignado por (gerente)
   - Asignado a (empleado o equipo)
   - Nota sobre archivos adjuntos

#### Crear Equipo

1. Ir a **Mis Equipos**
2. Clic en **Crear Equipo**
3. Completar:
   - **Nombre**: Nombre del equipo
   - **Descripción**: Propósito del equipo
4. Clic en **Crear**
5. Agregar miembros:
   - Seleccionar empleados de la lista
   - Clic en **Agregar**

#### Gestionar Equipo

**Ver Miembros**:
1. Seleccionar equipo
2. Clic en **Ver Miembros**
3. Se muestra lista completa

**Editar Equipo**:
1. Clic en **Editar**
2. Modificar nombre o descripción
3. Clic en **Guardar**

**Eliminar Equipo**:
1. Clic en **Eliminar**
2. Confirmar acción
3. El equipo se elimina (las tareas asignadas se mantienen)

### 4.3 Guía del Empleado

#### Ver Tareas Asignadas

1. Al iniciar sesión, el dashboard muestra tus tareas
2. Puedes ver:
   - Tareas pendientes
   - Tareas en progreso
   - Tareas completadas
   - Tareas pausadas

#### Actualizar Estado de Tarea

1. Seleccionar tarea
2. Clic en **Editar** o cambiar estado directamente
3. Seleccionar nuevo estado:
   - **Pendiente**: No iniciada
   - **En Progreso**: Trabajando en ella
   - **Completada**: Terminada
   - **Pausada**: Temporalmente detenida
4. Guardar cambios

#### Ver Detalles de Tarea

1. Seleccionar tarea
2. Clic en **Ver**
3. Información mostrada:
   - Título y descripción completa
   - Prioridad (Baja/Media/Alta)
   - Fecha límite
   - Asignado por (nombre del gerente)
   - Archivos adjuntos (enviados por email)

#### Cambiar Contraseña

1. Ir a **Mi Perfil**
2. Clic en **Cambiar Contraseña**
3. Ingresar:
   - Contraseña actual
   - Nueva contraseña
   - Confirmar nueva contraseña
4. Clic en **Cambiar**

---

## 5. Funcionalidades Detalladas

### 5.1 Asignación de Tareas

#### Asignación Individual

**Cuándo usar**: Tarea específica para una persona

**Proceso**:
1. Seleccionar "Asignar a Empleado"
2. Elegir empleado de la lista (solo muestra empleados, no gerentes/admins)
3. El empleado recibe un email con:
   - Detalles de la tarea
   - Archivos adjuntos
   - Fecha límite

#### Asignación a Equipo

**Cuándo usar**: Tarea para todo un equipo

**Proceso**:
1. Seleccionar "Asignar a Equipo"
2. Elegir equipo de la lista (solo equipos del gerente)
3. **Todos los miembros** del equipo reciben email individual
4. Cada miembro ve la tarea en su dashboard

### 5.2 Adjuntos de Archivos

#### Tipos de Adjuntos

**Imágenes**:
- Formatos: JPG, PNG, GIF
- Botón: "Adjuntar Imagen"
- Múltiples imágenes permitidas

**Archivos**:
- Formatos: PDF, DOC, DOCX, XLS, XLSX, TXT, etc.
- Botón: "Adjuntar Archivos"
- Múltiples archivos permitidos

#### Cómo Adjuntar

1. Clic en "Adjuntar Imagen" para imágenes
2. Seleccionar archivos (Ctrl+Click para múltiples)
3. Clic en "Adjuntar Archivos" para documentos
4. Seleccionar archivos
5. **Importante**: Los archivos se combinan (no se sobrescriben)
6. Al crear la tarea, todos los adjuntos se envían por email

**Nota**: Los adjuntos NO se guardan en la base de datos, solo se envían por email.

### 5.3 Notificaciones por Email

#### Cuándo se Envían

- ✅ Al crear una tarea (asignación)
- ✅ Al asignar a equipo (email a cada miembro)

#### Contenido del Email

```
📋 Nueva Tarea Asignada

Hola [Nombre],

Se te ha asignado una nueva tarea:

[Título de la Tarea]

Descripción: [Descripción completa]
Fecha Límite: [Fecha]

📎 Archivos adjuntos (enviados por correo):
  • archivo1.pdf
  • imagen1.jpg

Por favor, revisa los detalles y la prioridad en el sistema.
```

#### Configurar Email (Gerente/Admin)

Ver archivo: `src/com/synapse/core/services/notifications/EmailService.java`

### 5.4 Gestión de Equipos

#### Estructura de Equipos

- **Líder**: Gerente que creó el equipo
- **Miembros**: Empleados asignados
- **Tareas**: Asignadas al equipo completo

#### Agregar Miembros

1. Abrir equipo
2. Clic en "Ver Miembros"
3. Clic en "Agregar"
4. Seleccionar empleados
5. Confirmar

#### Quitar Miembros

1. Ver miembros del equipo
2. Seleccionar miembro
3. Clic en "Quitar"
4. Confirmar

**Nota**: Al quitar un miembro, las tareas asignadas al equipo se mantienen.

### 5.5 Estados y Prioridades

#### Estados de Tarea

| Estado | Descripción | Color |
|--------|-------------|-------|
| **Pendiente** | No iniciada | Gris |
| **En Progreso** | Trabajando | Azul |
| **Completada** | Terminada | Verde |
| **Pausada** | Detenida temporalmente | Naranja |

#### Prioridades

| Prioridad | Cuándo usar | Color |
|-----------|-------------|-------|
| **Baja** | Tareas no urgentes | Verde |
| **Media** | Tareas normales | Amarillo |
| **Alta** | Tareas urgentes | Rojo |

---

## 6. Preguntas Frecuentes

### ¿Cómo cambio mi contraseña?

**R**: Ir a Mi Perfil → Cambiar Contraseña

### ¿Puedo asignar una tarea a múltiples personas individualmente?

**R**: No directamente. Opciones:
1. Crear un equipo con esas personas
2. Crear tareas individuales para cada uno

### ¿Los archivos adjuntos se guardan en la base de datos?

**R**: No, solo se envían por email. El sistema muestra una nota indicando que fueron enviados por correo.

### ¿Puedo editar una tarea después de crearla?

**R**: Sí, puedes editar todos los campos incluyendo la asignación.

### ¿Qué pasa si elimino un equipo?

**R**: El equipo se elimina pero las tareas asignadas se mantienen en la base de datos.

### ¿Puedo ver tareas de otros empleados?

**R**: 
- **Administrador**: Sí, todas las tareas
- **Gerente**: Solo tareas que él creó
- **Empleado**: Solo sus tareas asignadas

### ¿Cómo sé si una tarea fue asignada a un equipo o individuo?

**R**: En el diálogo "Ver Tarea", si es equipo muestra "Equipo: [Nombre]", si es individual muestra solo el nombre del empleado.

### ¿Puedo adjuntar archivos después de crear la tarea?

**R**: No, los adjuntos solo se pueden agregar al crear la tarea (se envían por email).

### ¿Qué hago si olvidé mi contraseña?

**R**: Contacta al administrador para que la restablezca.

### ¿Puedo filtrar tareas por estado o prioridad?

**R**: Sí, el dashboard incluye filtros para estado y prioridad.

---

## Soporte

Para soporte técnico o preguntas adicionales:

- **Administrador del Sistema**: Contacta a tu administrador interno
- **Documentación Técnica**: Ver `docs/DOCUMENTACION_TECNICA.md`
- **Diagramas**: Ver `docs/DIAGRAMAS_UML.md`

---

**Versión**: 2.0  
**Última actualización**: Noviembre 2025  
**Sistema**: Synapse - Gestión de Tareas
