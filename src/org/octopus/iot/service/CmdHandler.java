package org.octopus.iot.service;

import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.octopus.iot.bean.IotSensor;
import org.octopus.iot.bean.IotUser;

public abstract class CmdHandler {
	
	public void exec(Object ctx, String req, IotSensorService iotSensorService, Dao dao) {
		for (String line : req.split("\n")) {
			_exec(ctx, line, iotSensorService, dao);
		}
	}

	public void _exec(Object ctx, String req, IotSensorService iotSensorService, Dao dao) {
		// Generate and write a response.
        if (req.isEmpty()) {
            resp(ctx, "ok", "^-^");
            return;
        } 
        if ("bye".equals(req.toLowerCase())) {
        	close(resp(ctx, "ok", "byte"));
            return;
        }
        String[] tmp = req.split(",", 2);
        if (tmp.length != 2) {
            	resp(ctx,"err","bad cmd");
            	return;
        }
        if ("auth".equals(tmp[0])) {
        	IotUser usr = dao.fetch(IotUser.class, tmp[1]);
        	if (usr == null) {
            	resp(ctx, "err","bad api key");
            	return;
        	}
        	uid(ctx, usr.getUserId());
            resp(ctx,"ok","auth ok");
            return;
        }
        boolean isRead = "r".equals(tmp[0]);
        boolean isWrite = "w".equals(tmp[0]);
        if (isRead || isWrite) {
            Long userId = uid(ctx);
            if (userId == null) {
            	resp(ctx, "err", "not auth yet");
            	return;
            }
            String[] tmp2 = tmp[1].split(",", 2);
            tmp = null;
            long sensorId = -1;
            try {
				sensorId = Long.parseLong(tmp2[0]);
			} catch (Exception e) {
			}
            if (sensorId < 0) {
            	resp(ctx, "err", "bad sensor id = " + tmp2[0]);
            	return;
            }
            IotSensor sensor = dao.fetch(IotSensor.class, sensorId);
            if (sensor == null) {
            	resp(ctx, "err" ,"no such sensor");
            	return;
            }
            if (sensor.getUserId() != userId) {
            	resp(ctx, "err", "not your sensor");
            	return;
            }
            if (isRead) {
            	resp(ctx, "ok", sensor.getValue() == null ? "{}" : sensor.getValue());
            	return;
            }
            if (tmp2.length != 2) {
            	resp(ctx, "err", "need values");
            	return;
            }
            String v = tmp2[1];
            if (Strings.isBlank(v)) {
            	resp(ctx, "err", "bad value");
            	return;
            }
            Map<String, Object> map = null;
            if (v.startsWith("{")) {
            	try {
            		map = Json.fromJsonAsMap(Object.class, v);
            	} catch (Exception e) {
            		resp(ctx, "err", "bad json");
            		return;
            	}
            }
            if (map == null) {
            	resp(ctx, "err", "null json value");
				return;
            }
            try {
            	String re = iotSensorService.updateSensorValue(sensor, map);
            	if (re == null) {
            		resp(ctx, "ok", "done");
            	} else {
            		resp(ctx, "err", re);
            	}
            	return;
			} catch (Exception e) {
				resp(ctx, "err", "udpate fail");
				return;
			}
        }
        resp(ctx, "err", "unknow cmd");
	}
	
	public abstract Object resp(Object ctx, String code, String msg);
	public abstract Long uid(Object ctx);
	public abstract void uid(Object ctx, long uid);
	public abstract void close(Object tx);
}
