ECHO OFF

REM environment setting below:
SET ANDROID_PLATFORM_TOOLS_ROOT=D:\tools\android-sdk\platform-tools
SET PROJ_ROOT=d:\project\bikecomp
SET SCRIPTS_ROOT=%PROJ_ROOT%\extra\scripts
SET BB_SCRIPTS_ROOT=%SCRIPTS_ROOT%\blackberry
SET BB_TOOLS_DIR=BlackBerry Tools for Android Development-2.0.4\bin

REM device settings below:
SET IP_OF_DEVICE=169.254.0.1
SET PASSWORD_OF_DEVICE=12345
SET TIMEOUT_TO_INIT_BB_SECOUNDS=20


ECHO _________________________________________________________________________________________________________________________________________
ECHO ...... Connecting to blackberry device ......
ECHO %BB_SCRIPTS_ROOT%\%BB_TOOLS_DIR%
CD %BB_SCRIPTS_ROOT%\%BB_TOOLS_DIR%
start blackberry-adbproxy %IP_OF_DEVICE% -password %PASSWORD_OF_DEVICE%
TIMEOUT %TIMEOUT_TO_INIT_BB_SECOUNDS%
ECHO ...... Connecting ADB to blackberry device ......
CD %ANDROID_PLATFORM_TOOLS_ROOT%
adb connect %IP_OF_DEVICE%
adb devices
ECHO _________________________________________________________________________________________________________________________________________