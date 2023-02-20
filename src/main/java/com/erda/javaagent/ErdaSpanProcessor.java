/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.erda.javaagent;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.context.Context;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.trace.ReadWriteSpan;
import io.opentelemetry.sdk.trace.ReadableSpan;
import io.opentelemetry.sdk.trace.SpanProcessor;

/**
 * See <a
 * href="https://github.com/open-telemetry/opentelemetry-specification/blob/master/specification/trace/sdk.md#span-processor">
 * OpenTelemetry Specification</a> for more information about {@link SpanProcessor}.
 *
 * @see ErdaAutoConfigurationCustomizerProvider
 */
public class ErdaSpanProcessor implements SpanProcessor {

  @Override
  public void onStart(Context parentContext, ReadWriteSpan span) {
    /*
    The sole purpose of this attribute is to introduce runtime dependency on some external library.
    We need this to demonstrate how extension can use them.
     */
    // span.setAttribute("random", RandomStringUtils.random(10));

    SpanContext spanContext = span.getSpanContext();
    span.setAttribute("trace_id", spanContext.getTraceState().get("trace_id"));
    span.setAttribute("parent_span_id", spanContext.getTraceState().get("parent_span_id"));
    span.setAttribute("is_from_erda", spanContext.getTraceState().get("is_from_erda"));
    span.setAttribute("is_erda_propagator", "true");
    span.setAttribute("service_instance_id", System.getenv("POD_UUID"));
    span.setAttribute("service_ip", System.getenv("POD_IP"));
    span.setAttribute("host_ip", System.getenv("HOST_IP"));

    Baggage baggage = Baggage.fromContext(parentContext);
    span.setAttribute(
        "source_application_name",
        baggage.getEntryValue("terminus-request-bg-source_application_name"));
    span.setAttribute(
        "source_service_name", baggage.getEntryValue("terminus-request-bg-source_service_name"));
    span.setAttribute("source_org_id", baggage.getEntryValue("terminus-request-bg-source_org_id"));
    span.setAttribute(
        "source_terminus_key", baggage.getEntryValue("terminus-request-bg-source_terminus_key"));
    span.setAttribute(
        "source_service_id", baggage.getEntryValue("terminus-request-bg-source_service_id"));
    span.setAttribute(
        "source_project_id", baggage.getEntryValue("terminus-request-bg-source_project_id"));
    span.setAttribute(
        "source_project_name", baggage.getEntryValue("terminus-request-bg-source_project_name"));
    span.setAttribute(
        "source_workspace", baggage.getEntryValue("terminus-request-bg-source_workspace"));
    span.setAttribute(
        "source_runtime_name", baggage.getEntryValue("terminus-request-bg-source_runtime_name"));
    span.setAttribute(
        "source_runtime_id", baggage.getEntryValue("terminus-request-bg-source_runtime_id"));
    span.setAttribute(
        "source_application_id",
        baggage.getEntryValue("terminus-request-bg-source_application_id"));
    span.setAttribute(
        "source_instance_id", baggage.getEntryValue("terminus-request-bg-source_instance_id"));
  }

  @Override
  public boolean isStartRequired() {
    return true;
  }

  @Override
  public void onEnd(ReadableSpan span) {}

  @Override
  public boolean isEndRequired() {
    return false;
  }

  @Override
  public CompletableResultCode shutdown() {
    return CompletableResultCode.ofSuccess();
  }

  @Override
  public CompletableResultCode forceFlush() {
    return CompletableResultCode.ofSuccess();
  }
}
