package org.octopus.iot.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.json.Json;
import org.nutz.json.JsonField;
import org.nutz.json.JsonFormat;
import org.nutz.json.JsonIgnore;

/**
 * 描述一个位置
 *
 */
public class IotLocation {

	/**数据类型, 如gps,北斗,伽利略等等*/
	@Column("tp")
	private String loctionType;
	/**
	 * 经度
	 */
	@Column("lng")
	@JsonField("lng")
	private double longitude;
	/**
	 * 纬度
	 */
	@Column("lat")
	@JsonField("lat")
	private double latitude;
	/**
	 * 海拔
	 */
	@Column()
	@JsonField("atd")
	private double altitude;
	/**
	 * 速度
	 */
	@Column("speed")
	@JsonField("speed")
	@JsonIgnore(null_int=0)
	private double speed;
	/**
	 * 是否进行偏移
	 */
	@Column("offs")
	@JsonField("offs")
	private boolean offset;
	
	public IotLocation() {
	}
	
	public String toString() {
		return Json.toJson(this, JsonFormat.compact());
	}

	public String getLoctionType() {
		return loctionType;
	}

	public void setLoctionType(String loctionType) {
		this.loctionType = loctionType;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public boolean isOffset() {
		return offset;
	}

	public void setOffset(boolean offset) {
		this.offset = offset;
	}

}
