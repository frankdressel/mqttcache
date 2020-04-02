package de.moduliertersingvogel.mqttcache.mqtt;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.net.ssl.SSLContext;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
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
	private static Logger logger = LogManager.getFormatterLogger("mqttcache");

	public MQTTConnector() {
		Configurations configs = new Configurations();

		try{
		    Configuration config = configs.properties(new File("conf/mqttcache.properties"));
		    final String url = config.getString("url", "");
		    final String user = config.getString("user", "");
		    final String password = config.getString("password", "");
		    final String topic = config.getString("topic", "");
		    final int cachesize = config.getInt("cachesize", 1000);
		    
			MQTTConnector.client = new MqttClient(url, MqttClient.generateClientId(),
					new MemoryPersistence());

			logger.info(String.format("%s created", this.getClass().getName()));

			client.setCallback(new MqttCallback() {
				@Override
				public void connectionLost(Throwable cause) {
					logger.warn("Connection to MQTTServer lost");
				}

				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					while (mqttcache.size() > cachesize) {
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
			options.setUserName(user);
			options.setAutomaticReconnect(true);
			options.setPassword(password.toCharArray());
			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(null, null, null);
			options.setSocketFactory(sslContext.getSocketFactory());
			client.connect(options);

			logger.info("MQTTClient connected");
			client.subscribe(topic, 1);
			logger.info(String.format("MQTTClient subscribed to topic: %s", topic));
		}
		catch (ConfigurationException e){
		    logger.catching(e);
		} catch (MqttException e) {
		    logger.catching(e);
		} catch (NoSuchAlgorithmException e) {
		    logger.catching(e);
		} catch (KeyManagementException e) {
		    logger.catching(e);
		}
	}

	@Produces
	@Named("mqttcache")
	public List<CacheEntry> getCache() {
		return Collections.unmodifiableList(mqttcache);
	}
}
