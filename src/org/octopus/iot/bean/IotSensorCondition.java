package org.octopus.iot.bean;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;

/**
 * 传感器参数的约束条件
 * @author wendal
 *
 */
public class IotSensorCondition {

	public double max;
	public double min;
	public String el;
	
	public String toString() {
		return Json.toJson(this, JsonFormat.compact());
	}
	
	public String toJson(JsonFormat jf) {
		return Json.toJson(Lang.obj2map(this), jf);
	}
}
