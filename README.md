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

Logging is done via log4j2. Adapt the configuration parameters in _log4j2.properties_ as you like or use your own configuration.

### Start programm
bin/meecrowave.sh start
```bash

```
