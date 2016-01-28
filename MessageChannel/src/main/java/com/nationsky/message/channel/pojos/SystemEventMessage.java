package com.nationsky.message.channel.pojos;

import java.io.Serializable;

public class SystemEventMessage implements Serializable{
	private String[] keys;
	private MessageType type;
	private MessageEvent event;
	private String source;
	private String operator;
	
	public static enum MessageType{
		DEVICE, USER
	}
	
	public static enum MessageEvent{
		LOGIN,LOGOUT,REMOVE
	}

	public String[] getKeys() {
		return keys;
	}

	public void setKeys(String[] keys) {
		this.keys = keys;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public MessageEvent getEvent() {
		return event;
	}

	public void setEvent(MessageEvent event) {
		this.event = event;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
