package ceres;

import ceres.crawler.PoemFetcher;
import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.SchedulerConfig;
import com.coreoz.wisp.schedule.Schedules;
import com.google.inject.Guice;
import java.time.Duration;

public class App {
    public static void main(String[] args) {
        var injector = Guice.createInjector(new AppModule());
        Scheduler scheduler = new Scheduler(SchedulerConfig
            .builder()
            .minThreads(2)
            .maxThreads(15)
            .threadsKeepAliveTime(Duration.ofHours(1))
            .build()
        );
        scheduler.schedule(() -> {
            var poemFetcher = injector.getInstance(PoemFetcher.class);
            poemFetcher.fetchPoems()
                .onComplete(result -> System.out.printf("Fetch poems success: %s%n", result.succeeded()));
        }, Schedules.afterInitialDelay(Schedules.fixedDelaySchedule(Duration.ofHours(2)), Duration.ZERO));
    }
}
