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
package com.kxd.talos.trace.core.annotation;

import java.io.Serializable;
import java.util.Arrays;

import com.kxd.talos.trace.core.endpoint.Endpoint;
import com.kxd.talos.trace.core.utils.Nullable;
import com.kxd.talos.trace.core.utils.Util;

/**
 * Binary annotations are tags applied to a Span to give it context. For
 * example, a binary annotation of "http.url" could be the path to a resource in a
 * RPC call.
 * 
 * Binary annotations of type STRING are always queryable, though more a
 * historical implementation detail than a structural concern.
 * 
 * Binary annotations can repeat, and vary on the host. Similar to Annotation,
 * the host indicates who logged the event. This allows you to tell the
 * difference between the client and server side of the same key. For example,
 * the key "http.url" might be different on the client and server side due to
 * rewriting, like "/api/v1/myresource" vs "/myresource. Via the host field,
 * you can see the different points of view, which often help in debugging.
 */
public class BinaryAnnotation implements Serializable {

  static final long serialVersionUID = 1L;

  /**
   * Special-cased form supporting {@link zipkin.Constants#CLIENT_ADDR} and {@link
   * zipkin.Constants#SERVER_ADDR}.
   *
   * @param key {@link zipkin.Constants#CLIENT_ADDR} or {@link zipkin.Constants#SERVER_ADDR}
   * @param endpoint associated endpoint.
   */
  public static BinaryAnnotation address(String key, Endpoint endpoint) {
    return create(key, new byte[] {1}, AnnotationType.BOOL, Util.checkNotNull(endpoint, "endpoint"));
  }

  /** String values are the only queryable type of binary annotation. */
  public static BinaryAnnotation create(String key, String value,
      @Nullable Endpoint endpoint) {
    return create(key, value.getBytes(Util.UTF_8), AnnotationType.STRING, endpoint);
  }

  public static BinaryAnnotation create(String key, byte[] value, AnnotationType type,
      @Nullable Endpoint endpoint) {
    return new BinaryAnnotation(key, value, type, endpoint);
  }

  public final String key; // required
  public final byte[] value; // required
  /**
   * 
   * @see AnnotationType
   */
  public final AnnotationType type; // required
  /**
   * The host that recorded tag, which allows you to differentiate between
   * multiple tags with the same key. There are two exceptions to this.
   * 
   * When the key is CLIENT_ADDR or SERVER_ADDR, host indicates the source or
   * destination of an RPC. This exception allows zipkin to display network
   * context of uninstrumented services, or clients such as web browsers.
   */
  public final Endpoint host; // optional

  BinaryAnnotation(String key, byte[] value, AnnotationType type, @Nullable Endpoint host) {
    this.key = Util.checkNotBlank(key, "Null or blank key");
    this.value = Util.checkNotNull(value, "Null value");
    this.type = type;
    this.host = host;
  }

  public String getKey() {
    return this.key;
  }

  public byte[] getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof BinaryAnnotation) {
      BinaryAnnotation that = (BinaryAnnotation) o;
      return (this.key.equals(that.key))
          && (Arrays.equals(this.value, that.value))
          && (this.type.equals(that.type))
          && Util.equal(this.host, that.host);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int h = 1;
    h *= 1000003;
    h ^= key.hashCode();
    h *= 1000003;
    h ^= Arrays.hashCode(value);
    h *= 1000003;
    h ^= type.hashCode();
    h *= 1000003;
    h ^= (host == null) ? 0 : host.hashCode();
    return h;
  }
}

