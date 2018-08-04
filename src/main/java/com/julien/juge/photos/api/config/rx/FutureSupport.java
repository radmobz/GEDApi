package com.julien.juge.photos.api.config.rx;

import org.reactivecouchbase.concurrent.Future;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class FutureSupport {

    public static class FutureReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

        @Override
        public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
            return returnValue != null && supportsReturnType(returnType);
        }

        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return Future.class.isAssignableFrom(returnType.getParameterType());
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
            if (returnValue == null) {
                mavContainer.setRequestHandled(true);
                return;
            }
            final Future<?> future = Future.class.cast(returnValue);
            WebAsyncUtils.getAsyncManager(webRequest)
                    .startDeferredResultProcessing(new FutureDeferredResult(future), mavContainer);
        }
    }

    public static class FutureDeferredResult<T> extends DeferredResult<T> {

        private static final Object EMPTY_RESULT = new Object();

        public FutureDeferredResult(Future<T> future) {
            this(null, EMPTY_RESULT, future);
        }

        public FutureDeferredResult(long timeout, Future<T> future) {
            this(timeout, EMPTY_RESULT, future);
        }

        public FutureDeferredResult(Long timeout, Object timeoutResult, Future<T> future) {
            super(timeout, timeoutResult);
            Assert.notNull(future, "single can not be null");
            future.andThen(ttry -> {
                for (T value : ttry.asSuccess()) {
                    this.setResult(value);
                }
                for (Throwable t : ttry.asFailure()) {
                    this.setErrorResult(t);
                }
            });
        }
    }
}
