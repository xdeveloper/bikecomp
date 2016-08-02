@echo off
java -Djava.awt.headless=true -Xmx512M -cp "%~dp0\..\lib\AndroidTools.jar" net.rim.tools.batchsigner.BatchBarSigner %*

@echo The batch file is complete.
