@ECHO OFF
cd ../build/mcp

IF "%~1" == "" (
    @echo | call recompile.bat
)

@echo | call startserver.bat