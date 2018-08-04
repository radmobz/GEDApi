package com.julien.juge.photos.api.config.rx;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class ObservableSupport {

    private ObservableSupport() {}

    private final static Logger LOGGER = LoggerFactory.getLogger(ObservableSupport.class);

    public static class ObservableReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

        @Override
        public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
            return returnValue != null && supportsReturnType(returnType);
        }

        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return Observable.class.isAssignableFrom(returnType.getParameterType());
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
            if (returnValue == null) {
                mavContainer.setRequestHandled(true);
                return;
            }
            final Observable<?> observable = Observable.class.cast(returnValue);
            WebAsyncUtils.getAsyncManager(webRequest)
                    .startDeferredResultProcessing(new ObservableDeferredResult(observable), mavContainer);
        }
    }

    public static class ObservableDeferredResult<T> extends DeferredResult<T> {

        private static final Object EMPTY_RESULT = new Object();

        private final DeferredResultSubscriber<T> subscriber;

        public ObservableDeferredResult(Observable<T> single) {
            this(null, EMPTY_RESULT, single);
        }

        public ObservableDeferredResult(long timeout, Observable<T> single) {
            this(timeout, EMPTY_RESULT, single);
        }

        public ObservableDeferredResult(Long timeout, Object timeoutResult, Observable<T> single) {
            super(timeout, timeoutResult);
            Assert.notNull(single, "observable can not be null");

            subscriber = new DeferredResultSubscriber<T>(single.toSingle().toObservable(), this);
        }
    }

    public static class DeferredResultSubscriber<T> extends Subscriber<T> implements Runnable {

        private final DeferredResult<T> deferredResult;

        private final Subscription subscription;

        public DeferredResultSubscriber(Observable<T> observable, DeferredResult<T> deferredResult) {
            this.deferredResult = deferredResult;
            this.deferredResult.onTimeout(this);
            this.deferredResult.onCompletion(this);
            this.subscription = observable.subscribe(this);
        }

        @Override
        public void onNext(T value) {
            deferredResult.setResult(value);
        }

        @Override
        public void onError(Throwable e) {
            deferredResult.setErrorResult(e);
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void run() {
            this.subscription.unsubscribe();
        }
    }
}
