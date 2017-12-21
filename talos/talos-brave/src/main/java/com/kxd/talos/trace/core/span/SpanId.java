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
package com.kxd.talos.trace.core.span;

import com.kxd.talos.trace.core.utils.Nullable;
import com.kxd.talos.trace.core.utils.JdkRuntimeUtilities;

import static com.kxd.talos.trace.core.utils.Util.*;

/**
 * Contains trace data that's propagated in-band across requests, sometimes known as Baggage.
 *
 * <p>This implementation is biased towards a fixed-length binary serialization format that doesn't
 * have a way to represent an absent parent (root span). In this serialized form, a root span is
 * when all three ids are the same. Alternatively, you can use {@link #nullableParentId}.
 *
 * <p>Particularly, this includes sampled state, and a portable binary representation. The
 * implementation is a port of {@code com.twitter.finagle.tracing.TraceId}.
 */
public final class SpanId {

  public static final int FLAG_DEBUG = 1 << 0;
  /** When set, we can interpret {@link #FLAG_SAMPLED} as a set value. */
  public static final int FLAG_SAMPLING_SET = 1 << 1;
  public static final int FLAG_SAMPLED = 1 << 2;
  /**
   * When set, we can ignore the value of the {@link #parentId}
   *
   * <p>While many zipkin systems re-use a trace id as the root span id, we know that some don't.
   * With this flag, we can tell for sure if the span is root as opposed to the convention trace id
   * == span id == parent id.
   */
  public static final int FLAG_IS_ROOT = 1 << 3;

  /**
   * Creates a new span id.
   *
   * @param traceId Trace Id.
   * @param spanId Span Id.
   * @param parentSpanId Nullable parent span id.
   * @deprecated Please use {@link SpanId.Builder}
   */
  @Deprecated
  public static SpanId create(String traceId, String spanId, @Nullable String parentSpanId) {
    return SpanId.builder().traceId(traceId).parentId(parentSpanId).spanId(spanId).build();
  }

  /**
   * @deprecated Please use {@link SpanId.Builder}
   */
  @Deprecated
  public SpanId(String traceId, String parentId, String spanId, long flags) {
    this.traceId = (parentId == traceId) ? parentId : traceId;
    this.parentId = (parentId == spanId) ? traceId : parentId;
    this.spanId = spanId;
    this.flags = flags;
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * Get Trace id.
   *
   * @return Trace id.
   * @deprecated use {@link #traceId}
   */
  @Deprecated
  public String getTraceId() {
    return traceId;
  }

  /**
   * Get span id.
   *
   * @return span id.
   * @deprecated use {@link #spanId}
   */
  @Deprecated
  public String getSpanId() {
    return spanId;
  }

  /**
   * Get parent span id.
   *
   * @return Parent span id. Can be <code>null</code>.
   * @deprecated use {@link #nullableParentId()}
   */
  @Deprecated
  @Nullable
  public String getParentSpanId() {
    return nullableParentId();
  }

  /**
   * Unique 8-byte identifier for a trace, set on all spans within it.
   */
  public final String traceId;

  /**
   * The parent's {@link #spanId} or {@link #spanId} if this the root span in a trace.
   */
  public final String parentId;

  /** Returns null when this is a root span. */
  @Nullable
  public String nullableParentId() {
    return root() ? null : parentId;
  }

  /**
   * Unique 8-byte identifier of this span within a trace.
   *
   * <p>A span is uniquely identified in storage by ({@linkplain #traceId}, {@code #id}).
   */
  public final String spanId;

  /** Returns true if this is the root span. */
  public final boolean root() {
    return (flags & FLAG_IS_ROOT) == FLAG_IS_ROOT || parentId == traceId && parentId == spanId;
  }

  /**
   * True is a request to store this span even if it overrides sampling policy. Implies {@link
   * #sampled()}.
   */
  public final boolean debug() {
    return (flags & FLAG_DEBUG) == FLAG_DEBUG;
  }

  /**
   * Should we sample this request or not? True means sample, false means don't, null means we defer
   * decision to someone further down in the stack.
   */
  @Nullable
  public final Boolean sampled() {
    if (debug()) return true;
    return (flags & FLAG_SAMPLING_SET) == FLAG_SAMPLING_SET
        ? (flags & FLAG_SAMPLED) == FLAG_SAMPLED
        : null;
  }

  /** Raw flags encoded in {@link #bytes()} */
  public final long flags;

  public Builder toBuilder() {
    return new Builder(this);
  }


  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (o instanceof SpanId) {
      SpanId that = (SpanId) o;
      return (this.traceId == that.traceId)
          && (this.parentId == that.parentId)
          && (this.spanId == that.spanId);
    }
    return false;
  }

  /** Preferred way to create spans, as it properly deals with the parent id */
  public Span toSpan() {
    Span result = new Span();
    result.setId(spanId);
    result.setTrace_id(traceId);
    result.setParent_id(nullableParentId());
    result.setName(""); // avoid NPE on equals
    result.setThreadName(JdkRuntimeUtilities.getCurrentThreadName());
    if (debug()) result.setDebug(debug());
    return result;
  }

  public static final class Builder {
    String traceId;
    String parentId;
    String spanId;
    long flags;

    Builder() {
    }

    Builder(SpanId source) {
      this.traceId = source.traceId;
      this.parentId = source.nullableParentId();
      this.spanId = source.spanId;
      this.flags = source.flags;
    }

    /** @see SpanId#traceId */
    public Builder traceId(String traceId) {
      this.traceId = traceId;
      return this;
    }

    /**
     * If your trace ids are not span ids, you must call this method to indicate absent parent.
     *
     * @see SpanId#nullableParentId()
     */
    public Builder parentId(@Nullable String parentId) {
      if (parentId == null) {
        this.flags |= FLAG_IS_ROOT;
      } else {
        this.flags &= ~FLAG_IS_ROOT;
      }
      this.parentId = parentId;
      return this;
    }

    /** @see SpanId#spanId */
    public Builder spanId(String spanId) {
      this.spanId = spanId;
      return this;
    }

    /** @see SpanId#flags */
    public Builder flags(long flags) {
      this.flags = flags;
      return this;
    }

    /** @see SpanId#debug() */
    public Builder debug(boolean debug) {
      if (debug) {
        this.flags |= FLAG_DEBUG;
      } else {
        this.flags &= ~FLAG_DEBUG;
      }
      return this;
    }

    /** @see SpanId#sampled */
    public Builder sampled(@Nullable Boolean sampled) {
      if (sampled != null) {
        this.flags |= FLAG_SAMPLING_SET;
        if (sampled) {
          this.flags |= FLAG_SAMPLED;
        } else {
          this.flags &= ~FLAG_SAMPLED;
        }
      } else {
        this.flags &= ~FLAG_SAMPLING_SET;
      }
      return this;
    }

    public SpanId build() {
      String traceId = this.traceId != null ? this.traceId : checkNotNull(spanId, "spanId");
      String parentId = this.parentId != null ? this.parentId : traceId;
      return new SpanId(traceId, parentId, checkNotNull(spanId, "spanId"), flags);
    }
  }

  /** Inspired by {@code okio.Buffer.writeLong} */
  static void writeHexLong(char[] data, int pos, long v) {
    writeHexByte(data, pos + 0,  (byte) ((v >>> 56L) & 0xff));
    writeHexByte(data, pos + 2,  (byte) ((v >>> 48L) & 0xff));
    writeHexByte(data, pos + 4,  (byte) ((v >>> 40L) & 0xff));
    writeHexByte(data, pos + 6,  (byte) ((v >>> 32L) & 0xff));
    writeHexByte(data, pos + 8,  (byte) ((v >>> 24L) & 0xff));
    writeHexByte(data, pos + 10, (byte) ((v >>> 16L) & 0xff));
    writeHexByte(data, pos + 12, (byte) ((v >>> 8L) & 0xff));
    writeHexByte(data, pos + 14, (byte)  (v & 0xff));
  }

  static final char[] HEX_DIGITS =
      {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  static void writeHexByte(char[] data, int pos, byte b) {
    data[pos + 0] = HEX_DIGITS[(b >> 4) & 0xf];
    data[pos + 1] = HEX_DIGITS[b & 0xf];
  }
}
