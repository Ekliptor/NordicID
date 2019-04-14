# Nordic RFID reader
This project reads RFID tags based on the official [Inventory Stream sample](https://github.com/NordicID/nur_sample_java/tree/master/samples/02_InventoryStream) and
posts the data to a HTTP backend specified in the application's config file.

## Running binaries
* ensure you ahve JVM installed on your system and JAVA_HOME is set in your PATH
* windows: open run/run.bat
* UNIX: open run/run.sh

## Building from source
* install Eclipse with Apache Maven support
* import this project and the [connection project](https://github.com/Ekliptor/NordicID-SamplesCommon) into
your eclipse workspace
* possibly fix buildpath depending on your setup to ensure all classes are found
* right-click on this project -> Run as -> Java Application
