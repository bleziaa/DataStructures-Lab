@echo off
setlocal
cd /d "%~dp0"
where java >nul 2>&1 || (echo ERROR: Instala JDK 17 o superior. & exit /b 1)
where mvn >nul 2>&1 || (echo ERROR: Instala Maven 3.9 o superior y agregalo a PATH. & exit /b 1)
call mvn -q clean javafx:run
if errorlevel 1 (echo Fallo al iniciar DataStructures Lab. & exit /b 1)
endlocal
