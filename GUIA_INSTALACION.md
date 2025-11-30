# 🚀 Synapse - Guía de Instalación y Ejecución

Esta es una versión **portable** de Synapse. No requiere instalar Java ni configurar variables de entorno manualmente, ya que todo lo necesario viene incluido en este paquete.

---

## 📋 Requisitos Previos

Para ejecutar este sistema, el único requisito externo es:

1.  **Docker Desktop**: Debe estar instalado y ejecutándose para levantar la base de datos.
    * [Descargar Docker Desktop](https://www.docker.com/products/docker-desktop/)
2.  **Sistema Operativo**: Windows 10 o superior (64-bits).

> **Nota:** No es necesario tener Java instalado. Esta aplicación incluye su propio entorno de ejecución (Java Runtime) configurado automáticamente.

---

## ⚙️ Instrucciones de Ejecución

Sigue estos 3 pasos sencillos para iniciar el sistema completo (Base de Datos + Aplicación):

### 1. Iniciar Docker
Asegúrese de que **Docker Desktop** esté abierto y corriendo en su computadora.

### 2. Ejecutar el Lanzador
Dentro de la carpeta de esta entrega, localice el archivo:

👉 **`Iniciar_Todo.bat`**

Haga **doble clic** sobre él.

### 3. Esperar la Carga
Se abrirá una ventana de consola negra que realizará lo siguiente automáticamente:
1.  Levantará el contenedor de la base de datos PostgreSQL.
2.  Esperará unos segundos a que la conexión esté lista.
3.  Mostrará las credenciales de acceso.
4.  Lanzará la aplicación **Synapse**.

---

## 🔑 Credenciales de Acceso

Para iniciar sesión en la aplicación, utilice el usuario administrador por defecto:

| Rol | Email | Contraseña |
| :--- | :--- | :--- |
| **Administrador** | `admin@synapse.com` | `admin123` |

### 🗄️ Credenciales de Base de Datos (PostgreSQL)
Si desea conectarse externamente (ej. pgAdmin / DBeaver):

* **Host:** `localhost`
* **Puerto:** `5433`
* **Base de Datos:** `synapse_db`
* **Usuario:** `postgres`
* **Contraseña:** `postgres`

---

## 🛠️ Solución de Problemas Comunes

**1. La ventana negra se cierra y dice "No se pudo iniciar Docker"**
* **Causa:** Docker Desktop no está ejecutándose.
* **Solución:** Abra Docker Desktop desde el menú inicio, espere a que el icono de la ballena se quede fijo y vuelva a ejecutar `Iniciar_Todo.bat`.

**2. Error "Port 5433 is already allocated"**
* **Causa:** Ya hay otro contenedor o servicio usando el puerto 5433.
* **Solución:** Detenga el servicio conflictivo o edite el archivo `docker-compose.yml` para cambiar el puerto externo (ej. `"5435:5432"`).

**3. La aplicación no conecta a la base de datos**
* **Causa:** La base de datos tardó más de lo esperado en iniciar.
* **Solución:** Cierre la aplicación y vuelva a ejecutar `Iniciar_Todo.bat`. El script intentará reconectar.

---

## 📁 Estructura de Archivos

* `app/`: Contiene el ejecutable Java y las librerías.
* `runtime/`: Entorno de ejecución Java (JRE) empaquetado.
* `docker/`: Scripts de inicialización de la base de datos.
* `Synapse.exe`: Ejecutable principal de la aplicación.
* `Iniciar_Todo.bat`: Script automatizado de arranque.
* `docker-compose.yml`: Configuración del contenedor de BD.

---

Desarrollado por **Fernando** para la gestión eficiente de tareas empresariales.