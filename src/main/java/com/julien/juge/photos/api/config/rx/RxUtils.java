package com.julien.juge.photos.api.config.rx;

import com.julien.juge.photos.api.utils.exception.ForbiddenInstanciationException;
import com.julien.juge.photos.api.utils.exception.ValidationException;
import org.reactivecouchbase.json.mapping.JsResult;
import rx.Observable;

/**
 * Created by adelegue on 19/10/2016.
 */
public class RxUtils {

    private RxUtils() {
        throw new ForbiddenInstanciationException();
    }


    public static <T> Observable<T> toObservable(JsResult<T> jsResult) {
        if(jsResult.isSuccess()) {
            return Observable.just(jsResult.get());
        } else {
            return jsResult.asError()
                    .map(err -> Observable.<T>error(new ValidationException(err.errors)))
                    .getOrElse(Observable.empty());
        }
    }

}
