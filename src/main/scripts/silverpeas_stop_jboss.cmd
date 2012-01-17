@echo off

cd "%JBOSS_HOME%/bin"

set username=
set password=

call shutdown.bat -u %username% -p %password% -S

if ERRORLEVEL 1 pause
