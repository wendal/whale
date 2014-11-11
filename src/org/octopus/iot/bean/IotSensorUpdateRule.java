package org.octopus.iot.bean;

public enum IotSensorUpdateRule {
	/**
	 * 默认行为,更新最新值及历史记录
	 */
	history, 
	/**
	 * 仅更新最新值, 这种方式允许更高的更新频率
	 */
	contrl
}
