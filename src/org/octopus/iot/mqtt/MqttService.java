package org.octopus.iot.mqtt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.octopus.iot.bean.IotUser;
import org.octopus.iot.service.IotService;

@IocBean(create = "init", depose = "close")
public class MqttService {
	
	private static final Log log = Logs.get();

	@Inject
	Dao dao;
	
	@Inject
	IotService iotService;
	
	@Inject("java:$conf.get('mqtt.host')")
	String mqttIp;
	
	@Inject("java:$conf.get('mqtt.port')")
	int mqttPort;
	
	int qos = 2;
	String broker ;
	String clientId = "JavaSample" + System.currentTimeMillis();
	MemoryPersistence persistence = new MemoryPersistence();
	MqttClient sampleClient;
	ExecutorService es;

	public void init() throws Exception {
		broker = String.format("tcp://%s:%d", mqttIp, mqttPort);
		sampleClient = new MqttClient(broker, clientId, persistence);
		es = Executors.newFixedThreadPool(16);
	}
	
	public synchronized boolean _init() {
		if (sampleClient.isConnected())
			return true;
		IotUser root = iotService.rootUser();
		if (root == null)
			return false;
		MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setUserName("admin");
        connOpts.setPassword(root.getApikey().toCharArray());
        connOpts.setKeepAliveInterval(15);
        try {
			sampleClient.connect(connOpts);
			return true;
		} catch (Exception e) {
			log.info("mqtt connect fail", e);
			return false;
		}
	}
	
	public void publish(final String topic, final String msg) {
		log.debugf("mqtt topic=%s msg=%s", topic, msg);
		es.submit(new Runnable() {
			public void run() {
				try {
					if (!_init())
						return;
					sampleClient.publish(topic, msg.getBytes(), 2, true);
				} catch (Exception e) {
					log.infof("publish mqtt msg fail topic=%s msg=%s", topic,
							msg, e);
				}
			}
		});
	}

	public void close() throws Exception {
		es.shutdownNow();
		if (sampleClient != null)
			sampleClient.close();
		
	}
}
