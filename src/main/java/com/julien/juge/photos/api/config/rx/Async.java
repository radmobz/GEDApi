package com.julien.juge.photos.api.config.rx;

import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Async {

    private static final ExecutorService DEFAULT = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public <T> Observable<T> defer(Func0<T> func) {
        return defer(func, DEFAULT);
    }

    public <T> Observable<T> defer(Func0<T> func, ExecutorService ec) {
        return Observable.<T>create(subscriber -> {
            ec.submit(() -> {
                try {
                    subscriber.onNext(func.call());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            });
        });
    }

    public <T, R> Func1<T, Observable<R>> asyncBlock(Func1<T, R> f) {
        return asyncBlock(f, DEFAULT);
    }

    public <T, R> Func1<T, Observable<R>> asyncBlock(Func1<T, R> f,  ExecutorService ec) {
        return (t) -> defer(() -> f.call(t), ec);
    }
}
