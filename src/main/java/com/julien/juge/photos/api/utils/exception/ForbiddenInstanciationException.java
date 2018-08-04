package com.julien.juge.photos.api.utils.exception;


/**
 * Exception to throw in utility classes constructor.
 * <p>
 *     Utility classes should not have public constructors
 * <p>
 * Utility classes, which are collections of static members, are not meant to be instantiated.
 * Even abstract utility classes, which can be extended, should not have public constructors.
 * Java adds an implicit public constructor to every class which does not define at least one explicitly.
 * Hence, at least one non-public constructor should be defined.
 */
public class ForbiddenInstanciationException extends IllegalStateException {

    public ForbiddenInstanciationException() {
        throw new IllegalStateException("Utility class : instanciation is forbidden");
    }
}
