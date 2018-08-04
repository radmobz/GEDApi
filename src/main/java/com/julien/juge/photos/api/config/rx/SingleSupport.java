package com.julien.juge.photos.api.config.rx;

import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.AsyncHandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import rx.Single;

public class SingleSupport {

    public static class SingleReturnValueHandler implements AsyncHandlerMethodReturnValueHandler {

        @Override
        public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
            return returnValue != null && supportsReturnType(returnType);
        }

        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return Single.class.isAssignableFrom(returnType.getParameterType());
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
            if (returnValue == null) {
                mavContainer.setRequestHandled(true);
                return;
            }
            final Single<?> single = Single.class.cast(returnValue);
            WebAsyncUtils.getAsyncManager(webRequest)
                    .startDeferredResultProcessing(new SingleDeferredResult(single), mavContainer);
        }
    }

    public static class SingleDeferredResult<T> extends DeferredResult<T> {

        private static final Object EMPTY_RESULT = new Object();

        private final ObservableSupport.DeferredResultSubscriber<T> subscriber;

        public SingleDeferredResult(Single<T> single) {
            this(null, EMPTY_RESULT, single);
        }

        public SingleDeferredResult(long timeout, Single<T> single) {
            this(timeout, EMPTY_RESULT, single);
        }

        public SingleDeferredResult(Long timeout, Object timeoutResult, Single<T> single) {
            super(timeout, timeoutResult);
            Assert.notNull(single, "single can not be null");

            subscriber = new ObservableSupport.DeferredResultSubscriber<T>(single.toObservable(), this);
        }
    }
}
