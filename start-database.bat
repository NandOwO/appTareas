@echo off
echo ========================================
echo   Synapse - Iniciar Base de Datos
echo ========================================
echo.

echo Verificando Docker...
docker --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Docker no esta instalado o no esta ejecutandose
    echo Por favor, instala Docker Desktop y asegurate de que este ejecutandose
    pause
    exit /b 1
)

echo Docker encontrado!
echo.
echo Iniciando contenedor de PostgreSQL...
docker-compose up -d

if errorlevel 1 (
    echo.
    echo ERROR: No se pudo iniciar el contenedor
    echo Verifica los logs con: docker-compose logs
    pause
    exit /b 1
)

echo.
echo ========================================
echo   Base de Datos Iniciada Correctamente
echo ========================================
echo.
echo Credenciales de la Base de Datos:
echo   Host: localhost
echo   Puerto: 5433
echo   Base de Datos: synapse_db
echo   Usuario: postgres
echo   Contrasena: postgres
echo.
echo Usuario de la Aplicacion:
echo   Email: admin@synapse.com
echo   Contrasena: admin123
echo.
echo Para ver los logs: docker-compose logs -f
echo Para detener: docker-compose down
echo.
pause
