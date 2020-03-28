# mqttcache

## Building

```
mvn clean package
```

The uberjar will be in the target folder.

## Running

### Configuration

The configuration file must be in the current working directory and must be named mqttcache.properties. An examplaric file is given below:

```
url=tcp://<server>:<port>
topic=<mqtt topic>
user=<mqtt user>
password=<mqtt user password>
```

### Logging

Logging is done via log4j2. Adapt the configuration parameters in _log4j2.properties_ as you likeor use your own configuration.

### Start programm

mqttcache can be started with the following line. The use of the _-Dlog4j.configurationFile_ is optional and depends on your logging configuration.

```bash
java -Dlog4j.configurationFile=log4j2.properties -jar mqttcache-thorntail.jar
```

If you need multiple instances (for example caching different topics), you can specify different ports with _-Dswarm.http.port=8081_.
