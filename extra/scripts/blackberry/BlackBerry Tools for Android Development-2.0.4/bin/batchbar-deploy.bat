@echo off
IF "%~1"=="" GOTO args
IF "%~2"=="" GOTO args
IF "%~3"=="" GOTO noPassword

FOR %%c in ("%~1\*.bar") DO (
echo.
echo Deploying %%c
call blackberry-deploy -installApp %2 "%%c" -password %3)
goto end

:noPassword
  FOR %%c in ("%~1\*.bar") DO (
  echo.
  echo Deploying %%c
  call blackberry-deploy -installApp %2 "%%c")
  goto end

:args
echo.
echo Deploys a folder of bars to a device.
echo.
echo Usage: batchbar-deploy.bat path-to-bars device-ip [device-password]
echo.

:end
  @echo on