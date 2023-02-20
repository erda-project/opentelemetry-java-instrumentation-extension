/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.erda.javaagent;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.trace.data.LinkData;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import io.opentelemetry.sdk.trace.samplers.SamplingDecision;
import io.opentelemetry.sdk.trace.samplers.SamplingResult;
import java.util.List;

/**
 * This Erda sampler filters out all internal spans whose name contains string "greeting".
 *
 * <p>See <a
 * href="https://github.com/open-telemetry/opentelemetry-specification/blob/master/specification/trace/sdk.md#sampling">
 * OpenTelemetry Specification</a> for more information about span sampling.
 *
 * @see ErdaAutoConfigurationCustomizerProvider
 */
public class ErdaSampler implements Sampler {
  @Override
  public SamplingResult shouldSample(
      Context parentContext,
      String traceId,
      String name,
      SpanKind spanKind,
      Attributes attributes,
      List<LinkData> parentLinks) {
    if (spanKind == SpanKind.INTERNAL && name.contains("greeting")) {
      return SamplingResult.create(SamplingDecision.DROP);
    } else {
      return SamplingResult.create(SamplingDecision.RECORD_AND_SAMPLE);
    }
  }

  @Override
  public String getDescription() {
    return "ErdaSampler";
  }
}
