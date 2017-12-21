/**
 *  Copyright 2013 <kristofa@github.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kxd.talos.trace.core.endpoint;

import java.io.Serializable;

import com.kxd.talos.trace.core.utils.Nullable;

/**
 * Indicates the network context of a service recording an annotation with two
 * exceptions.
 *
 * When a BinaryAnnotation, and key is CLIENT_ADDR or SERVER_ADDR,
 * the endpoint indicates the source or destination of an RPC. This exception
 * allows zipkin to display network context of uninstrumented services, or
 * clients such as web browsers.
 */
public class Endpoint implements Serializable {

  public static Endpoint create(String serviceName, int ipv4, int port) {
    return new Endpoint(serviceName, ipv4, (short) (port & 0xffff),null);
  }

  public static Endpoint create(String serviceName, int ipv4) {
    return new Endpoint(serviceName, ipv4, null,null);
  }
  
  public static Endpoint create(String serviceName,int ipv4,int port,String processId){
	  return new Endpoint(serviceName,ipv4,(short)(port & 0xffff),processId);
  }

  static final long serialVersionUID = 1L;

  /**
   * IPv4 host address packed into 4 bytes.
   *
   * Ex for the ip 1.2.3.4, it would be (1 << 24) | (2 << 16) | (3 << 8) | 4
   */
  public final int ipv4; // required

  /**
   * IPv4 port
   *
   * Note: this is to be treated as an unsigned integer, so watch for negatives.
   *
   * Null, when the port isn't known.
   */
  @Nullable
  public final Short port; // required

  /**
   * Service name in lowercase, such as "memcache" or "zipkin-web"
   *
   * Conventionally, when the service name isn't known, service_name = "unknown".
   */
  public final String service_name; // required
  
  public final String processId;
  

  Endpoint(String service_name, int ipv4, Short port,String processId) {
    this.ipv4 = ipv4;
    this.port = port;
    this.processId = processId;
    if (service_name != null) {
      service_name = service_name.toLowerCase();
    } else {
      service_name = "";
    }
    this.service_name = service_name;
  }

  @Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ipv4;
	result = prime * result + ((port == null) ? 0 : port.hashCode());
	result = prime * result
			+ ((service_name == null) ? 0 : service_name.hashCode());
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
		return false;
	}
	Endpoint other = (Endpoint) obj;
	if (ipv4 != other.ipv4) {
	    return false;
	}
	if (port == null) {
		if (other.port != null) {
		    return false;
		}
	} else if (!port.equals(other.port)) {
	    return false;
	}
	if (service_name == null) {
		if (other.service_name != null) {
		    return false;
		}
	} else if (!service_name.equals(other.service_name)) {
	    return false;
	}
	return true;
}
}

