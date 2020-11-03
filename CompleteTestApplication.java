package com.example.completetest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
//@SpringBootApplication
public class CompleteTestApplication {
    public void handleErrorNoException1() throws Exception {
        String name = null;
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            if (name == null) {
                throw new RuntimeException("Computation error!");
            }
            return supplyTask(name);
        }).exceptionally((t) -> "Hello, Stranger!");
        log.info("Before get...");
        log.info(completableFuture.get());//on-hold until future complete
        log.info("End get...");
    }
    public void handleErrorNoException() throws Exception {
        String name = null;
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            if (name == null) {
                throw new RuntimeException("Computation error!");
            }
            return supplyTask(name);
        }).handle((s, t) -> s != null ? s : "Hello, Stranger!");
        log.info("Before get...");
        log.info(completableFuture.get());//on-hold until future complete
        log.info("End get...");
    }
    public void join1() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> supplyTask("1"));
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> supplyTask("2"));
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> supplyTask("3"));
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(future1, future2, future3);
        log.info("Before get...");
        combinedFuture.get();//on-hold until future complete
        log.info("End get...");
    }
    public void join2() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> supplyTask("1"));
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> supplyTask("2"));
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> supplyTask("3"));
        log.info("Before get...");
        Stream.of(future1, future2, future3).map(CompletableFuture::join)
                .collect(Collectors.joining(","));
        log.info("End get...");
    }
    //flapmap
    public void composeFutures() throws Exception {
        //thenCompose: CompletableFuture1's output feed to a function that returns CompletionStage
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->supplyTask("1"))
                .thenCompose(s ->{
                    return CompletableFuture.supplyAsync(() -> supplyTask("2"));
                });
        log.info("Before get...");
        future.get();//on-hold until future complete
        log.info("End get...");
    }
    public void combineFutures() throws Exception {
        //thenCombine: CompletableFuture1 and CompletableFuture2 individual parallel run, then use both results to do something
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->supplyTask("1"))
                .thenCombine(CompletableFuture.supplyAsync(() -> supplyTask("2")), (s1, s2)->s1 + s2);
        log.info("Before get...");
        future.get();//on-hold until future complete
        log.info("End get...");
    }
    //map
    public void thenApplyAsyncFutures() throws Exception {
        //thenApply: CompletableFuture1's output feed to a function that returns simple type
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->supplyTask("1"))
                .thenApplyAsync(s -> supplyTask(s));
        log.info("Before get...");
        future.get();//on-hold until future complete
        log.info("End get...");
    }
    public void thenApplyFutures() throws Exception {
        //thenApply: CompletableFuture1's output feed to a function that returns simple type
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->supplyTask("1"))
                .thenApply(s -> supplyTask(s));
        log.info("Before get...");
        future.get();//on-hold until future complete
        log.info("End get...");
    }
    private void runTask(final String i) {
        log.info("Start{}...", i);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("End{}...", i);
    }
    private String supplyTask(final String i) {
        runTask(i);
        return "Hello";
    }
    public void simpleCompleteSupplyAsyncTest() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> supplyTask("1"))
                .thenAccept(s -> runTask("2"));
        log.info("Before get...");
        future.get();//on-hold until future complete
        log.info("End get...");
    }
    public void streamTest() {
        List<String> aList = new ArrayList<>();
        for (int i=0; i<10; i++) {
            aList.add(i + "");
        }
        aList.stream()
                .peek(a->log.info("Peek 1 {}", a))
                .map(a->{
                    log.info("map 1 {}", a);
                    return a + "A";
                })
                .peek(a->log.info("Peek 2 {}", a))
                .map(a->{
                    log.info("map 2 {}", a);
                    return a + "B";
                })
                .collect(Collectors.joining(";"));

    }
    public static void main(String[] args) throws Exception {
        CompleteTestApplication application = new CompleteTestApplication();
        //application.streamTest();
        //application.simpleCompleteSupplyAsyncTest();
        //application.combineFutures();
        //application.composeFutures();
        //application.thenApplyFutures();
        //application.join1();
        //application.join2();
        //application.handleError();
        //application.thenApplyFutures();
        //application.thenApplyAsyncFutures();
        application.handleErrorNoException1();
        //SpringApplication.run(CompleteTestApplication.class, args);
    }

}
