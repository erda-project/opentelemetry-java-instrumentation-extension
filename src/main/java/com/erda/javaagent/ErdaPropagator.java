/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.erda.javaagent;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.baggage.BaggageBuilder;
import io.opentelemetry.api.internal.StringUtils;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanId;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceId;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.api.trace.TraceStateBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.context.propagation.TextMapSetter;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

/**
 * See <a
 * href="https://github.com/open-telemetry/opentelemetry-specification/blob/master/specification/context/api-propagators.md">
 * OpenTelemetry Specification</a> for more information about Propagators.
 *
 * @see ErdaPropagatorProvider
 */
public class ErdaPropagator implements TextMapPropagator {
  private static final Logger logger = Logger.getLogger(ErdaPropagator.class.getName());

  private static final String Erda_Request_Id = "traceparent";
  private static final String Erda_Request_Span_Id = "terminus-request-spanid";

  private static final int MAX_TRACE_ID_LENGTH = TraceId.getLength();
  private static final int MAX_SPAN_ID_LENGTH = SpanId.getLength();

  private static final Collection<String> FIELDS = Collections.singletonList(Erda_Request_Id);

  private static final String DICE_APPLICATION_NAME_KEY = "DICE_APPLICATION_NAME";
  private static final String DICE_SERVICE_NAME_KEY = "DICE_SERVICE_NAME";
  private static final String DICE_SERVICE_ID_KEY = "DICE_SERVICE_NAME";
  private static final String DICE_ORG_ID_KEY = "DICE_ORG_ID";
  private static final String TERMINUS_KEY_KEY = "TERMINUS_KEY";
  private static final String DICE_PROJECT_ID_KEY = "DICE_PROJECT_ID";
  private static final String DICE_PROJECT_NAME_KEY = "DICE_PROJECT_NAME";
  private static final String DICE_WORKSPACE_KEY = "DICE_WORKSPACE";
  private static final String DICE_RUNTIME_NAME_KEY = "DICE_RUNTIME_NAME";
  private static final String DICE_RUNTIME_ID_KEY = "DICE_RUNTIME_ID";
  private static final String DICE_APPLICATION_ID_KEY = "DICE_APPLICATION_ID";
  private static final String DICE_INSTANCE_ID_KEY = "POD_UUID";

  private static final String PREFIX_BAGGAGE_HEADER = "terminus-request-bg-source";

  @Override
  public Collection<String> fields() {
    return FIELDS;
  }

  @Override
  public <C> void inject(Context context, C carrier, TextMapSetter<C> setter) {
    if (context == null) {
      return;
    }
    if (setter == null) {
      return;
    }
    SpanContext spanContext = Span.fromContext(context).getSpanContext();
    if (spanContext.isValid()) {
      injectSpan(spanContext, carrier, setter);
    }
  }

  private static <C> void injectSpan(
      SpanContext spanContext, @Nullable C carrier, TextMapSetter<C> setter) {

    String traceId = spanContext.getTraceId();

    String spanId = spanContext.getSpanId();
    String serviceId =
        System.getenv(DICE_APPLICATION_ID_KEY)
            + "_"
            + System.getenv(DICE_RUNTIME_NAME_KEY)
            + "_"
            + System.getenv(DICE_SERVICE_NAME_KEY);

    setter.set(carrier, Erda_Request_Id, traceId);
    setter.set(carrier, "terminus-request-id", traceId);
    setter.set(
        carrier,
        "terminus-request-bg-source_application_name",
        System.getenv(DICE_APPLICATION_NAME_KEY));
    setter.set(
        carrier, "terminus-request-bg-source_service_name", System.getenv(DICE_SERVICE_NAME_KEY));
    setter.set(carrier, "terminus-request-bg-source_org_id", System.getenv(DICE_ORG_ID_KEY));
    setter.set(carrier, "terminus-request-bg-source_terminus_key", System.getenv(TERMINUS_KEY_KEY));
    setter.set(carrier, "terminus-request-bg-source_service_id", serviceId);
    setter.set(
        carrier, "terminus-request-bg-source_project_id", System.getenv(DICE_PROJECT_ID_KEY));
    setter.set(
        carrier, "terminus-request-bg-source_project_name", System.getenv(DICE_PROJECT_NAME_KEY));
    setter.set(carrier, "terminus-request-bg-source_workspace", System.getenv(DICE_WORKSPACE_KEY));
    setter.set(
        carrier, "terminus-request-bg-source_runtime_name", System.getenv(DICE_RUNTIME_NAME_KEY));
    setter.set(
        carrier, "terminus-request-bg-source_runtime_id", System.getenv(DICE_RUNTIME_ID_KEY));
    setter.set(
        carrier,
        "terminus-request-bg-source_application_id",
        System.getenv(DICE_APPLICATION_ID_KEY));
    setter.set(
        carrier, "terminus-request-bg-source_instance_id", System.getenv(DICE_INSTANCE_ID_KEY));
  }

  @Override
  public <C> Context extract(Context context, C carrier, TextMapGetter<C> getter) {
    if (context == null) {
      return Context.root();
    }
    if (getter == null) {
      return context;
    }
    String traceId = getter.get(carrier, Erda_Request_Id);
    String spanId = getter.get(carrier, Erda_Request_Span_Id);
    String sampled = getter.get(carrier, "terminus-request-sampled");
    if (traceId == null) {
      return context;
    }
    SpanContext spanContext = buildSpanContext(traceId, spanId, "1");
    logger.log(Level.FINE, "spanContext " + spanContext);

    if (!spanContext.isValid()) {
      logger.log(Level.FINE, "is valid " + spanContext.isValid());
      return context;
    }
    logger.log(Level.FINE, "context " + context.with(Span.wrap(spanContext)));

    Context extractedContext = context.with(Span.wrap(spanContext));

    if (carrier != null) {
      BaggageBuilder baggageBuilder = Baggage.builder();
      for (String key : getter.keys(carrier)) {
        if (!key.startsWith(PREFIX_BAGGAGE_HEADER)) {
          continue;
        }
        String value = getter.get(carrier, key);
        if (value == null) {
          continue;
        }
        baggageBuilder.put(key, value);
      }
      Baggage baggage = baggageBuilder.build();
      if (!baggage.isEmpty()) {
        extractedContext = extractedContext.with(baggage);
      }
    }

    return extractedContext;
  }

  private static SpanContext buildSpanContext(String traceId, String spanId, String flags) {
    try {
      String otelTraceId = StringUtils.padLeft(traceId, MAX_TRACE_ID_LENGTH);
      String otelSpanId = StringUtils.padLeft(spanId, MAX_SPAN_ID_LENGTH);
      logger.log(Level.FINE, "otelTraceId " + otelTraceId);
      logger.log(Level.FINE, "otelSpanId " + otelSpanId);

      TraceStateBuilder stateBuilder = TraceState.builder();
      stateBuilder.put("trace_id", otelTraceId);
      stateBuilder.put("parent_span_id", otelSpanId);
      if (!TraceId.isValid(otelTraceId)) {
        stateBuilder.put("is_from_erda", "true");
      }
      TraceState state = stateBuilder.build();
      int flagsInt = Integer.parseInt(flags);
      if (TraceId.isValid(otelTraceId)) {
        return SpanContext.createFromRemoteParent(
            otelTraceId,
            otelSpanId,
            ((flagsInt & 1) == 1) ? TraceFlags.getSampled() : TraceFlags.getDefault(),
            state);
      } else {
        return SpanContext.createFromRemoteParent(
            "11111111111111111111111111111111",
            "1111111111111111",
            ((flagsInt & 1) == 1) ? TraceFlags.getSampled() : TraceFlags.getDefault(),
            state);
      }
    } catch (RuntimeException e) {
      logger.log(
          Level.FINE,
          "Error parsing '" + Erda_Request_Id + "' header. Returning INVALID span context.",
          e);
      return SpanContext.getInvalid();
    }
  }
}
