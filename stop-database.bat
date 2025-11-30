@echo off
echo ========================================
echo   Synapse - Detener Base de Datos
echo ========================================
echo.

echo Deteniendo contenedor de PostgreSQL...
docker-compose down

echo.
echo Base de datos detenida correctamente
echo.
echo Para eliminar datos y reiniciar: docker-compose down -v
echo.
pause
