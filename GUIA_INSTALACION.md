# 🚀 Synapse - Guía de Instalación y Ejecución

Esta es una versión **portable** de Synapse. No requiere instalar Java ni configurar variables de entorno manualmente, ya que todo lo necesario viene incluido en este paquete.

---

## 📋 Requisitos Previos

Para ejecutar este sistema, el único requisito externo es:

1. **Docker Desktop**: Debe estar instalado y ejecutándose para levantar la base de datos.
   * [Descargar Docker Desktop](https://www.docker.com/products/docker-desktop/)
2. **Sistema Operativo**: Windows 10 o superior (64-bits).

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
1. Levantará los contenedores de PostgreSQL y pgAdmin.
2. Esperará unos segundos a que la conexión esté lista.
3. Mostrará las credenciales de acceso.
4. Lanzará la aplicación **Synapse**.

---

## 🔑 Credenciales de Acceso

### 🖥️ Aplicación Synapse

Para iniciar sesión en la aplicación, utilice el usuario administrador por defecto:

| Rol | Email | Contraseña |
| :--- | :--- | :--- |
| **Administrador** | `admin@synapse.com` | `admin123` |

---

### 🗄️ Base de Datos PostgreSQL

#### Conexión desde la Aplicación (Automática)
La aplicación se conecta automáticamente usando:
* **Host:** `localhost`
* **Puerto:** `5433`
* **Base de Datos:** `synapse_db`
* **Usuario:** `postgres`
* **Contraseña:** `postgres`

#### Conexión Externa (DBeaver, pgAdmin externo, etc.)
Si desea conectarse desde una herramienta externa instalada en su PC:

* **Host:** `localhost`
* **Puerto:** `5433` ⚠️ (Importante: no es el puerto por defecto 5432)
* **Base de Datos:** `synapse_db`
* **Usuario:** `postgres`
* **Contraseña:** `postgres`

---

### 🌐 pgAdmin Web (Incluido en Docker)

El sistema incluye **pgAdmin 4** corriendo en un contenedor Docker para administrar la base de datos visualmente.

#### Acceso a pgAdmin
1. Abra su navegador web
2. Vaya a: **`http://localhost:5050`**
3. Inicie sesión con:
   * **Email:** `admin@synapse.com`
   * **Password:** `admin123`

#### Conectar pgAdmin al Servidor PostgreSQL
Una vez dentro de pgAdmin, debe registrar el servidor PostgreSQL:

1. Clic derecho en **"Servers"** → **"Register"** → **"Server"**
2. En la pestaña **"General"**:
   * **Name:** `Synapse DB` (o el nombre que prefiera)
3. En la pestaña **"Connection"**:
   * **Host name/address:** `postgres` ⚠️ (Importante: use `postgres`, NO `localhost`)
   * **Port:** `5432` ⚠️ (Importante: puerto interno, NO 5433)
   * **Maintenance database:** `synapse_db`
   * **Username:** `postgres`
   * **Password:** `postgres`
   * ✅ Marque **"Save password"**
4. Clic en **"Save"**

> **Nota Importante:** Desde pgAdmin (que corre en Docker), debe usar `postgres` como host porque ambos contenedores están en la misma red Docker. El puerto 5433 es solo para conexiones desde fuera de Docker.

---

## 🛠️ Solución de Problemas Comunes

**1. La ventana negra se cierra y dice "No se pudo iniciar Docker"**
* **Causa:** Docker Desktop no está ejecutándose.
* **Solución:** Abra Docker Desktop desde el menú inicio, espere a que el icono de la ballena se quede fijo y vuelva a ejecutar `Iniciar_Todo.bat`.

**2. Error "Port 5433 is already allocated"**
* **Causa:** Ya hay otro contenedor o servicio usando el puerto 5433.
* **Solución:** 
  1. Detenga el servicio conflictivo: `docker-compose down`
  2. O edite el archivo `docker-compose.yml` para cambiar el puerto externo (ej. `"5434:5432"`)
  3. Si cambia el puerto, también debe actualizar `Conexion.java` con el nuevo puerto

**3. La aplicación no conecta a la base de datos**
* **Causa:** La base de datos tardó más de lo esperado en iniciar.
* **Solución:** 
  1. Cierre la aplicación
  2. Espere 10-15 segundos
  3. Vuelva a ejecutar `Iniciar_Todo.bat`

**4. pgAdmin no carga en el navegador**
* **Causa:** El contenedor de pgAdmin no inició correctamente.
* **Solución:**
  1. Verifique que Docker esté corriendo: `docker ps`
  2. Debería ver dos contenedores: `synapse_db` y `synapse_pgadmin`
  3. Si no aparece pgAdmin, ejecute: `docker-compose up -d`

**5. Error "Server not found" en pgAdmin**
* **Causa:** Usó `localhost` en lugar de `postgres` como host.
* **Solución:** En la configuración del servidor en pgAdmin, use:
  * **Host:** `postgres` (nombre del contenedor)
  * **Puerto:** `5432` (puerto interno)

---

## 📁 Estructura de Archivos

* `app/`: Contiene el ejecutable Java y las librerías.
* `runtime/`: Entorno de ejecución Java (JRE) empaquetado.
* `docker/`: Scripts de inicialización de la base de datos.
  * `init-scripts/01-init-db.sql`: Schema y datos iniciales
* `Synapse.exe`: Ejecutable principal de la aplicación.
* `Iniciar_Todo.bat`: Script automatizado de arranque.
* `docker-compose.yml`: Configuración de contenedores (PostgreSQL + pgAdmin).

---

## 📊 Puertos Utilizados

| Servicio | Puerto | Descripción |
| :--- | :--- | :--- |
| PostgreSQL | `5433` | Base de datos (acceso externo) |
| pgAdmin | `5050` | Interfaz web de administración |

---

## 🔄 Comandos Útiles

### Detener todos los servicios:
```bash
docker-compose down
```

### Reiniciar la base de datos (mantiene datos):
```bash
docker-compose restart postgres
```

### Reiniciar TODO desde cero (BORRA DATOS):
```bash
docker-compose down -v
docker-compose up -d
```

### Ver logs de la base de datos:
```bash
docker-compose logs postgres
```

---

Desarrollado por **Fernando** para la gestión eficiente de tareas empresariales.