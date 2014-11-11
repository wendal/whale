package org.octopus.iot.mqtt;

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

	public void init() throws Exception {
		broker = String.format("tcp://%s:%d", mqttIp, mqttPort);
		sampleClient = new MqttClient(broker, clientId, persistence);
	}
	
	public boolean _init() {
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
	
	public void publish(String topic, String msg) {
		if (!_init())
			return;
		log.debugf("mqtt topic=%s msg=%s", topic, msg);
		try {
			sampleClient.publish(topic, msg.getBytes(), 2, true);
		} catch (Exception e) {
			log.infof("publish mqtt msg fail topic=%s msg=%s", topic, msg, e);
		}
	}

	public void close() throws Exception {
		if (sampleClient != null)
			sampleClient.close();
	}
}
