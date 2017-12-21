

package com.kxd.talos.trace.core.annotation;

import java.io.Serializable;

import com.kxd.talos.trace.core.endpoint.Endpoint;

public class KeyValueAnnotation implements Serializable{

	public static KeyValueAnnotation create(String key, String value,Endpoint host) {
		return new KeyValueAnnotation(key, value,host);
	}
	
	public static KeyValueAnnotation create(String key, String value) {
		return new KeyValueAnnotation(key, value,null);
	}

	private String key;
	private String value;
	private Endpoint host;

	private KeyValueAnnotation(String key, String value,Endpoint host) {
		this.key = key;
		this.value = value;
		this.host  = host;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Endpoint getHost() {
		return host;
	}

	public void setHost(Endpoint host) {
		this.host = host;
	}
}
