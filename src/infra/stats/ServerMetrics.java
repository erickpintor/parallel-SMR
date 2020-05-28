package infra.stats;

import com.codahale.metrics.*;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ServerMetrics {

    private final MetricRegistry metrics;

    public final Meter requests;
    public final Meter commands;
    public final Meter conflicts;

    public ServerMetrics() {
        metrics = new MetricRegistry();
        requests = metrics.meter("requests");
        commands = metrics.meter("commands");
        conflicts = metrics.meter("conflicts");
        metrics.register("conflict-ratio", new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                return Ratio.of(conflicts.getOneMinuteRate(),
                        commands.getOneMinuteRate());
            }
        });
    }

    public void startReporting(File path) {
        CsvReporter csvReporter =
                CsvReporter
                        .forRegistry(metrics)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .build(path);
        csvReporter.start(1, TimeUnit.SECONDS);

        ConsoleReporter consoleReporter =
                ConsoleReporter
                        .forRegistry(metrics)
                        .convertRatesTo(TimeUnit.SECONDS)
                        .build();
        consoleReporter.start(10, TimeUnit.SECONDS);
    }
}
