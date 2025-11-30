# 🐳 Docker Setup - Base de Datos Synapse

## Requisitos Previos
- Docker Desktop instalado y ejecutándose
- Puerto 5433 disponible (usamos 5433 para evitar conflicto con PostgreSQL local)

## Inicio Rápido

### 1. Iniciar la Base de Datos
```bash
docker-compose up -d
```

### 2. Verificar que esté ejecutándose
```bash
docker-compose ps
```

### 3. Ver logs (opcional)
```bash
docker-compose logs -f postgres
```

### 4. Detener la Base de Datos
```bash
docker-compose down
```

### 5. Eliminar datos y reiniciar (si necesitas resetear)
```bash
docker-compose down -v
docker-compose up -d
```

## Credenciales de Acceso

### Base de Datos
- **Host**: localhost
- **Puerto**: 5433 (no 5432 para evitar conflicto con PostgreSQL local)
- **Base de Datos**: synapse_db
- **Usuario**: postgres
- **Contraseña**: postgres

### Usuario de la Aplicación
- **Email**: admin@synapse.com
- **Contraseña**: admin123
- **Rol**: Administrador

## Conexión desde la Aplicación Java

La aplicación ya está configurada para conectarse a:
```
jdbc:postgresql://localhost:5433/synapse_db
Usuario: postgres
Contraseña: postgres
```

## Notas Importantes

- La base de datos se inicializa automáticamente con el esquema completo
- El usuario admin se crea automáticamente al iniciar por primera vez
- Los datos persisten en un volumen Docker (no se pierden al reiniciar)
- Para resetear completamente, usa `docker-compose down -v`

## Solución de Problemas

### Puerto 5433 ya está en uso
Si tienes otro servicio en el puerto 5433:
1. Cambia el puerto en docker-compose.yml: `"5434:5432"`
2. Actualiza `src/com/synapse/data/database/Conexion.java` con el nuevo puerto

### La base de datos no inicia
```bash
docker-compose logs postgres
```
Revisa los logs para ver el error específico.

### Reiniciar completamente
```bash
docker-compose down -v
docker-compose up -d
```
