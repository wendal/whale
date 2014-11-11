package org.octopus.iot.service;

import java.util.Date;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.octopus.iot.bean.IotDevice;
import org.octopus.iot.bean.IotLocation;
import org.octopus.iot.bean.IotSensor;
import org.octopus.iot.bean.IotSensorType;
import org.octopus.iot.bean.IotUser;
import org.octopus.iot.bean.IotUserLevel;
import org.octopus.iot.bean.IotVisible;

@IocBean
public class IotService {

	@Inject
	Dao dao;
	
	public IotUser rootUser() {
		IotUser admin = dao.fetch(IotUser.class, Cnd.where("name", "=", "admin"));
		if (admin != null)
			return admin;
		return addUser("admin", IotUserLevel.SSVIP);
	}
	
	public void makeApiKey(IotUser user) {
		user.setApikey(R.sg(16).next());
	}

	public IotUser addUser(String name, IotUserLevel uv) {
		IotUser user = new IotUser();
		makeApiKey(user);
		user.setName(name);
		user.setUserLevel(uv);
		switch (uv) {
		case VIP:
			user.setDeviceLimit(50);
			user.setSensorLimit(50);
			user.setTriggerLimit(5);
			break;
		case SVIP:
			user.setDeviceLimit(100);
			user.setSensorLimit(100);
			user.setTriggerLimit(5);
			break;
		case SSVIP:
			user.setDeviceLimit(1000);
			user.setSensorLimit(1000);
			user.setTriggerLimit(5);
			break;
		default:
			user.setDeviceLimit(20);
			user.setSensorLimit(20);
			user.setTriggerLimit(5);
			break;
		}
		user = dao.insert(user);

		IotDevice device = new IotDevice();
		device.setTitle("测试设备");
		device.setUserId(user.getUserId());
		IotLocation loc = new IotLocation();
		loc.setLongitude(113.0f);
		loc.setLatitude(23.0f);
		loc.setSpeed(0f);
		loc.setOffset(false);
		loc.setLoctionType("gps");
		device.setLoction(loc);
		device = dao.insert(device);

		IotSensor sensor = new IotSensor();
		sensor.setDeviceId(device.getId());
		sensor.setUserId(user.getUserId());
		sensor.setTitle("DS18B20温度传感器");
		sensor.setType(IotSensorType.number);
		sensor.setVisiable(IotVisible.PUBLIC);
		sensor.setCreateTime(new Date());

		dao.insert(sensor);

		sensor.setTitle("电源开关");
		sensor.setType(IotSensorType.onoff);
		sensor.setVisiable(IotVisible.PUBLIC);
		sensor.setCreateTime(new Date());

		dao.insert(sensor);

		return user;
	}
	
}
