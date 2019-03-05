package eu.captech.digitalization.commons.basic.api;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by emelg on 18.02.2016.
 * Utility class to handle checked exceptions in Java 8 Streams API
 */
public class LambdaExceptionUtil {

    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T, E extends Exception> {
        void accept(T t) throws E;
    }

    public static <T, R, E extends Exception> Function<T, R> rethrowFunction(ThrowingFunction<T, R, E> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T, E extends Exception> Consumer<T> rethrowConsumer(ThrowingConsumer<T, E> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
