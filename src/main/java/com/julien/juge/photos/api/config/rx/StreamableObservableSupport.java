package com.julien.juge.photos.api.config.rx;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import rx.Observable;
import rx.Subscription;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class StreamableObservableSupport {

    @Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface Streamed {}

    public static class MultiObservableReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

        @Override
        public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
            return returnValue != null && supportsReturnType(returnType);
        }

        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return Observable.class.isAssignableFrom(returnType.getParameterType()) && returnType.getMethod().isAnnotationPresent(Streamed.class);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
            if (returnValue == null) {
                mavContainer.setRequestHandled(true);
                return;
            }
            HttpServletResponse hsr = HttpServletResponse.class.cast(webRequest.getNativeResponse());
            RequestMapping rm = returnType.getMethod().getDeclaredAnnotation(RequestMapping.class);
            String mediaType = "text/plain";
            if (rm.produces().length > 0) {
                mediaType = rm.produces()[0];
            }
            hsr.setContentType(mediaType);
            final Observable<?> observable = Observable.class.cast(returnValue);
            WebAsyncUtils.getAsyncManager(webRequest)
                    .startDeferredResultProcessing(
                            new MultiObservableDeferredResult(Long.MAX_VALUE, MediaType.parseMediaType(mediaType), observable), mavContainer);
        }
    }

    public static class MultiObservableDeferredResult<T> extends DeferredResult<ResponseBodyEmitter> {

        private static final Object EMPTY_RESULT = new Object();

        MultiObservableDeferredResult(Long timeout, MediaType mediaType, Observable<T> single) {
            super(timeout, EMPTY_RESULT);
            Assert.notNull(single, "observable can not be null");
            ResponseBodyEmitter rbe = new ResponseBodyEmitter();
            this.setResult(rbe);
            AtomicBoolean completed = new AtomicBoolean(false);
            AtomicReference<Subscription> ref = new AtomicReference<>(null);
            Runnable onCompleted = () -> {
                if (completed.compareAndSet(false, true)) {
                    rbe.complete();
                    if (ref.get() != null) {
                        ref.get().unsubscribe();
                    }
                }
            };
            this.onCompletion(onCompleted);
            this.onTimeout(onCompleted);
            Subscription subscription = single.subscribe(
                    next -> {
                        try {
                            if (!completed.get()) {
                                rbe.send(next, mediaType);
                            }
                        } catch (IOException e) {
                            completed.compareAndSet(false, true);
                            rbe.completeWithError(e);
                            if (ref.get() != null) {
                                ref.get().unsubscribe();
                            }
                            // throw new RuntimeException(e.getMessage(), e);
                        }
                    },
                    e -> {
                        if (!completed.get()) {
                            rbe.completeWithError(e);
                        }
                    },
                    onCompleted::run
            );
            ref.set(subscription);
        }
    }
}
