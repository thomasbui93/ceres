package ceres;

import ceres.crawler.PoemFetcher;
import com.google.inject.Guice;

public class App {
    public static void main(String[] args) {
        var injector = Guice.createInjector(new AppModule());
        var poemFetcher = injector.getInstance(PoemFetcher.class);
        poemFetcher.fetchPoems()
            .onComplete(result -> System.out.printf("Fetch poems success: %s%n", result.succeeded()));
    }
}
