package me.andidroid.artemis.opentelemetry;

import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.logs.GlobalLoggerProvider;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogRecordExporter;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.instrumentation.micrometer.v1_5.OpenTelemetryMeterRegistry;
import io.opentelemetry.instrumentation.runtimemetrics.BufferPools;
import io.opentelemetry.instrumentation.runtimemetrics.Classes;
import io.opentelemetry.instrumentation.runtimemetrics.Cpu;
import io.opentelemetry.instrumentation.runtimemetrics.GarbageCollector;
import io.opentelemetry.instrumentation.runtimemetrics.MemoryPools;
import io.opentelemetry.instrumentation.runtimemetrics.Threads;
//import io.opentelemetry.exporter.otlp.log.OtlpGrpcLogRecordExporter;
//import io.opentelemetry.exporter.otlp.logs.OtlpGrpcLogExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.LogRecordProcessor;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.invoke.MethodHandles;

public class OpenTelemetryInitializer {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final OpenTelemetryInitializer INSTANCE = new OpenTelemetryInitializer();

    private OpenTelemetrySdk openTelemetry;

    /**
     * @return the iNSTANCE
     */
    public static OpenTelemetryInitializer getINSTANCE() {
        return INSTANCE;
    }

    public OpenTelemetryInitializer() {
        logger.info("start OpenTelemetryInitializer");
        try

        {
            InputStream input = OpenTelemetryPlugin.class.getClassLoader().getResourceAsStream("tracing.properties");
            if (input == null) {
                throw new NullPointerException("Unable to find tracing.properties file");
            }
            Properties prop = new Properties(System.getProperties());
            prop.load(input);
            System.setProperties(prop);

            String otelEndpoint = Objects
                    .toString(prop.getOrDefault("otel.exporter.otlp.endpoint", "http://localhost:4317"));

            // sdk = AutoConfiguredOpenTelemetrySdk.initialize().getOpenTelemetrySdk();

            Resource resource = Resource.getDefault()
                    .merge(Resource.create(Attributes.of(ResourceAttributes.SERVICE_NAME, "test-activemq-artemis")));

            SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                    .addSpanProcessor(BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder().build()).build())
                    .setResource(resource)
                    .build();

            SdkMeterProvider sdkMeterProvider = SdkMeterProvider.builder()
                    .registerMetricReader(
                            PeriodicMetricReader.builder(OtlpGrpcMetricExporter.builder().build()).build())
                    .setResource(resource)
                    .build();
            // OtlpGrpcLogRecordExporter

            LogRecordExporter logRecordExporter = OtlpGrpcLogRecordExporter.builder().setEndpoint(otelEndpoint)
                    .build();
            // use syso-log-exporter for testing
            // logRecordExporter = SystemOutLogRecordExporter.create();
            LogRecordProcessor logRecordProcessor = BatchLogRecordProcessor.builder(logRecordExporter).build();
            // LogRecordExporter logRecordExporter =
            // BatchLogRecordProcessor.builder(OtlpJsonLoggingLogRecordExporter.create());
            // LogRecordExporter logRecordExporter = InMemoryLogRecordExporter.create();
            // SimpleLogRecordProcessor.create(logRecordExporter)
            SdkLoggerProvider sdkLoggerProvider = SdkLoggerProvider.builder()
                    .setResource(resource)
                    .addLogRecordProcessor(logRecordProcessor)
                    .build();

            openTelemetry = OpenTelemetrySdk.builder()
                    .setTracerProvider(sdkTracerProvider)
                    .setMeterProvider(sdkMeterProvider)
                    .setLoggerProvider(sdkLoggerProvider)
                    .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                    .buildAndRegisterGlobal();
            // dont call this, already called internally via buildAndRegisterGlobal()
            // GlobalOpenTelemetry.set(sdk);

            GlobalLoggerProvider.set(openTelemetry.getSdkLoggerProvider());

        } catch (Throwable t) {
            t.printStackTrace();
        }

        try {

            // GlobalLoggerProvider.set(sdk.getSdkLoggerProvider());
            // Resource resource = Resource.getDefault();
            // LogRecordExporter logRecordExporter = InMemoryLogRecordExporter.create();
            // SdkLoggerProvider sdkLoggerProvider = SdkLoggerProvider.builder()
            // .setResource(resource)
            // .addLogRecordProcessor(SimpleLogRecordProcessor.create(logRecordExporter))
            // .build();
            // GlobalLoggerProvider.set(sdkLoggerProvider);

            BufferPools.registerObservers(openTelemetry);
            Classes.registerObservers(openTelemetry);
            Cpu.registerObservers(openTelemetry);
            MemoryPools.registerObservers(openTelemetry);
            Threads.registerObservers(openTelemetry);
            GarbageCollector.registerObservers(openTelemetry);

            MeterRegistry otelMeterRegistry = OpenTelemetryMeterRegistry.builder(openTelemetry).setPrometheusMode(true)
                    .build();
            Metrics.addRegistry(otelMeterRegistry);

        } catch (Throwable t) {
            t.printStackTrace();
        }
        // return sdk;
    }

    /**
     * @return the openTelemetry
     */
    public OpenTelemetrySdk getOpenTelemetry() {
        return openTelemetry;
    }
}
