package de.moduliertersingvogel.mqttcache.mqtt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import de.moduliertersingvogel.mqttcache.model.CacheEntry;

@ApplicationScoped
public class MQTTConnector {

	private static MqttClient client;
	private static LinkedList<CacheEntry> mqttcache = new LinkedList<>();
	private static Logger logger = LogManager.getLogger("de.moduliertersingvogel.mqttcache");

	public MQTTConnector() throws MqttException, FileNotFoundException, IOException {
		String mqttconfigPath = "mqttcache.properties";
		Properties mqttcacheconfig = new Properties();
		mqttcacheconfig.load(new FileInputStream(mqttconfigPath));

		MQTTConnector.client = new MqttClient(mqttcacheconfig.getProperty("url"), MqttClient.generateClientId(),
				new MemoryPersistence());

		logger.info(String.format("%s created", this.getClass().getName()));

		client.setCallback(new MqttCallback() {
			@Override
			public void connectionLost(Throwable cause) {
				logger.warn("Connection to MQTTServer lost");
			}

			@Override
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				while (mqttcache.size() > 1000) {
					mqttcache.pop();
				}
				final String msgString = new String(message.getPayload());
				logger.debug(String.format("Received message: %s", msgString));
				mqttcache.add(new CacheEntry(msgString));
			}

			@Override
			public void deliveryComplete(IMqttDeliveryToken token) {
			}
		});

		MqttConnectOptions options = new MqttConnectOptions();
		options.setUserName(mqttcacheconfig.getProperty("user"));
		options.setAutomaticReconnect(true);
		options.setPassword(mqttcacheconfig.getProperty("password").toCharArray());
		client.connect(options);

		logger.info("MQTTClient connected");
		final String topic = mqttcacheconfig.getProperty("topic");
		client.subscribe(topic, 1);
		logger.info(String.format("MQTTClient subscribed to topic: %s", topic));
	}

	@Produces
	@Named("mqttcache")
	public List<CacheEntry> getCache() {
		return Collections.unmodifiableList(mqttcache);
	}
}
