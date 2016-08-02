set LIB=%~dp0\..\lib
java -Djava.awt.headless=true -Xmx512M -cp "%LIB%\AndroidTools.jar" net.rim.tools.apk2bar.Apk2Bar %*

