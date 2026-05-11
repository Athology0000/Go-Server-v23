@echo off
setlocal

set OUTPUT_JAR=%~dp0build\libs\cobalt-obf.jar
set OUTPUT_DLL=%~dp0build\libs\cobalt-obf.dll

echo [*] Building obfuscator and obfuscated artifacts...
call "%~dp0gradlew.bat" obfuscateArtifacts

if %ERRORLEVEL% NEQ 0 (
    echo [!] Obfuscation build failed.
    pause
    exit /b 1
)

echo [+] Done. Output: %OUTPUT_JAR%
echo [+] Done. Output: %OUTPUT_DLL%
pause
