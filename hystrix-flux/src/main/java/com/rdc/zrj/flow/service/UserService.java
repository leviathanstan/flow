package com.rdc.zrj.flow.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author leviathanstan
 * @date 05/06/2020 17:36
 */
@Service
public class UserService {
    @SuppressWarnings("PMD.ThreadPoolCreationRule")
    private final ExecutorService pool = Executors.newFixedThreadPool(8);
    private final Scheduler scheduler = Schedulers.fromExecutor(pool);

    public Mono<String> emitSingle() {
        return Mono.just(ThreadLocalRandom.current().nextInt(0, 2000))
                //引发fallback
                .delayElement(Duration.ofMillis(700))
                .map(e -> e + "d");
    }

    public Flux<Integer> emitMultiple() {
        int start = ThreadLocalRandom.current().nextInt(0, 6000);
        return Flux.range(start, 10);
    }

    public Mono<String> doSomethingSlow() {
        return Mono.fromCallable(() -> {
            Thread.sleep(2000);
            System.out.println("doSomethingSlow: " + Thread.currentThread().getName());
            return "ok";
        }).publishOn(scheduler);
    }
}