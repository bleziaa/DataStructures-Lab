@echo off
setlocal
cd /d "%~dp0"
where mvn >nul 2>&1 || (echo ERROR: Instala Maven y agregalo a PATH. & exit /b 1)
call mvn clean test
exit /b %errorlevel%
