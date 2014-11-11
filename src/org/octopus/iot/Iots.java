package org.octopus.iot;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;

public class Iots {
	
	public static int Limit_Dev_Per_User = 50;
	public static int Limit_Sensor_Per_Dev = 100;
	public static int Limit_Sensor_Update_Interval = 2;
	public static int PART = 5;

	public static final String TOOFAST = Json.toJson(new NutMap().setv("ok", false).setv("msg", "UPLOAD TOO FAST"), JsonFormat.compact());
	public static final String TOOBIG = Json.toJson(new NutMap().setv("ok", false).setv("msg", "UPLOAD TOO BIG"), JsonFormat.compact());
	public static final String NOTFOUND = Json.toJson(new NutMap().setv("ok", false).setv("msg", "NOT FOUND"), JsonFormat.compact());
	public static final String BADIMAGE = Json.toJson(new NutMap().setv("ok", false).setv("msg", "BAD IMAGE"), JsonFormat.compact());
	public static final String NULLJSON = Json.toJson(new NutMap().setv("ok", false).setv("msg", "NULL JSON"), JsonFormat.compact());
	public static final String NEED_CONTENT_LENGTH = Json.toJson(new NutMap().setv("ok", false).setv("msg", "NEED Content-Length"), JsonFormat.compact());
	public static final String EMTRY_BODY = Json.toJson(new NutMap().setv("ok", false).setv("msg", "emtry body"), JsonFormat.compact());
	public static final String BAD_SENSOR_ID = Json.toJson(new NutMap().setv("ok", false).setv("msg", "bad sensor id"), JsonFormat.compact());
	

	
}
