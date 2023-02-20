/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.erda.javaagent;

import com.google.auto.service.AutoService;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigurablePropagatorProvider;

/**
 * Registers the custom propagator used by this example.
 *
 * @see ConfigurablePropagatorProvider
 * @see ErdaPropagator
 */
@AutoService(ConfigurablePropagatorProvider.class)
public class ErdaPropagatorProvider implements ConfigurablePropagatorProvider {
  @Override
  public TextMapPropagator getPropagator(ConfigProperties config) {
    return new ErdaPropagator();
  }

  @Override
  public String getName() {
    return "erda";
  }
}
