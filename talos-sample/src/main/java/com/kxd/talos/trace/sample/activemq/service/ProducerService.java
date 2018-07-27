package com.kxd.talos.trace.sample.activemq.service;

public interface ProducerService {

	/**
	 * 发送默认文本信息
	 * @param message
	 */
	public void sendMessage(String message);

}
