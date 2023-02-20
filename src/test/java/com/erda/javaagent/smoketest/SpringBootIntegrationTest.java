/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package javaagent.smoketest;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class SpringBootIntegrationTest extends IntegrationTest {

  @Override
  protected String getTargetImage(int jdk) {
    return "ghcr.io/open-telemetry/opentelemetry-java-instrumentation/smoke-test-spring-boot:jdk"
        + jdk
        + "-20211213.1570880324";
  }

  @Test
  public void extensionsAreLoadedFromJar() throws IOException, InterruptedException {
    // startTarget("/opentelemetry-extensions.jar");

    // testAndVerify();

    // stopTarget();
  }

  @Test
  public void extensionsAreLoadedFromFolder() throws IOException, InterruptedException {
    // startTarget("/");

    // testAndVerify();

    // stopTarget();
  }

  @Test
  public void extensionsAreLoadedFromJavaagent() throws IOException, InterruptedException {
    // startTargetWithExtendedAgent();

    // testAndVerify();

    // stopTarget();
  }

  private void testAndVerify() throws IOException, InterruptedException {
    // String url = String.format("http://localhost:%d/greeting", target.getMappedPort(8080));
    // Request request = new Request.Builder().url(url).get().build();

    // String currentAgentVersion =
    //     (String)
    //         new JarFile(agentPath)
    //             .getManifest()
    //             .getMainAttributes()
    //             .get(Attributes.Name.IMPLEMENTATION_VERSION);

    // Response response = client.newCall(request).execute();

    // Collection<ExportTraceServiceRequest> traces = waitForTraces();

    // Assertions.assertNotNull(response.header("X-server-id"));
    // Assertions.assertEquals(1, response.headers("X-server-id").size());
    // Assertions.assertTrue(TraceId.isValid(response.header("X-server-id")));
    // Assertions.assertEquals("Hi!", response.body().string());
    // Assertions.assertEquals(1, countSpansByName(traces, "/greeting"));
    // Assertions.assertEquals(0, countSpansByName(traces, "WebController.greeting"));
    // Assertions.assertEquals(1, countSpansByName(traces, "WebController.withSpan"));
    // Assertions.assertEquals(2, countSpansByAttributeValue(traces, "custom", "demo"));
    // Assertions.assertNotEquals(
    //     0, countResourcesByValue(traces, "telemetry.auto.version", currentAgentVersion));
    // Assertions.assertNotEquals(0, countResourcesByValue(traces, "custom.resource", "demo"));
  }
}