/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.erda.javaagent;

import io.opentelemetry.sdk.trace.IdGenerator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom {@link IdGenerator} which provides span and trace ids.
 *
 * @see io.opentelemetry.sdk.trace.SdkTracerProvider
 * @see ErdaAutoConfigurationCustomizerProvider
 */
public class ErdaIdGenerator implements IdGenerator {
  private static final AtomicLong traceId = new AtomicLong(0);
  private static final AtomicLong spanId = new AtomicLong(0);

  @Override
  public String generateSpanId() {
    return IdGenerator.random().generateSpanId();
  }

  @Override
  public String generateTraceId() {
    return IdGenerator.random().generateTraceId();
  }
}
