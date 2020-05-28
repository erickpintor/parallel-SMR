package infra.stats;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

import java.util.concurrent.TimeUnit;

public class ClientMetrics {

    private final MetricRegistry metrics;

    public final Meter requests;

    public ClientMetrics() {
        metrics = new MetricRegistry();
        requests = metrics.meter("requests");
    }

    public void startReporting() {
        ConsoleReporter consoleReporter =
                ConsoleReporter
                        .forRegistry(metrics)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .build();
        consoleReporter.start(10, TimeUnit.SECONDS);
    }
}
