* Type the command with no parameters for help.
* Type the command with -gui parameter to start a graphical user interface of the tool

Usage:

a.) Produce BAR files from APKs
>bin\blackberry-apkpackager <source files or folders> [<blacklist file>] [-t <bar folder>][-w[1-5]] [-a <author name>] [-aid <author ID>] [-d [<debug-token file>]] [-m] [-rv] [-etr|-etl|-etn] [-ci] [-la<n>] [-R] [-os <version>]
or
>bin\blackberry-apkpackager -gui

b.) Sign a set of BARs with developer and RIM keys
>bin\blackberry-signer -bbidtoken <bbid-csk-file> -storepass <password> [<options>] <bar-file> (<key-name>)
or
>bin\blackberry-signer -gui


c.) Deploy a BAR app for testing on a device or VMWare simulator
>bin\blackberry-deploy -installApp -device <deviceIP> -package <BAR file path> -password <device-password>
or
>bin\blackberry-deploy -gui


d.) Start ADB Proxy to connect a device or VMWare simulator through ADB to an Android development environment of your choice 
>bin\blackberry-adbproxy [<device IP address>] [-detect] [-gui] [-password <device password>]
or
>bin\blackberry-adbproxy -gui

