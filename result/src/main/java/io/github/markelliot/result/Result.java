package io.github.markelliot.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** A Rust-inspired result/error container. */
public final class Result<T, E> {
    private final T result;
    private final E error;

    public static <T, E> Result<T, E> ok(T result) {
        return new Result<>(result, null);
    }

    public static <T, E> Result<T, E> error(E error) {
        return new Result<>(null, error);
    }

    private Result(T result, E error) {
        // TODO(markelliot): it's possible we can/should skip this check, the static initializers
        //  guarantee we get this correct anyway.
        if ((result != null) == (error != null)) {
            throw new IllegalStateException(
                    "Result objects may contain strictly a result xor an error.");
        }
        this.result = result;
        this.error = error;
    }

    public <U> Result<U, E> mapResult(Function<T, U> fn) {
        return !isError() ? Result.ok(fn.apply(result)) : Result.error(error);
    }

    public <U> Result<U, E> flatMapResult(Function<T, Result<U, E>> fn) {
        return !isError() ? fn.apply(result) : Result.error(error);
    }

    public <F> Result<T, F> mapError(Function<E, F> fn) {
        return !isError() ? Result.ok(result) : Result.error(fn.apply(error));
    }

    public <F> Result<T, F> flatMapError(Function<E, Result<T, F>> fn) {
        return !isError() ? Result.ok(result) : fn.apply(error);
    }

    public <U, F> Result<U, F> map(Function<T, U> resultFn, Function<E, F> errorFn) {
        return mapResult(resultFn).mapError(errorFn);
    }

    public <U, F> Result<U, F> flatMap(
            Function<T, Result<U, F>> resultFn, Function<E, Result<U, F>> errorFn) {
        return !isError() ? resultFn.apply(result) : errorFn.apply(error);
    }

    public boolean isError() {
        return error != null;
    }

    /** Returns an Optional containing the result if it's present or empty otherwise. */
    public Optional<T> result() {
        return result != null ? Optional.of(result) : Optional.empty();
    }

    /** Returns an Optional containing the error if it's present or empty otherwise. */
    public Optional<E> error() {
        return error != null ? Optional.of(error) : Optional.empty();
    }

    public <X extends Exception> T orElseThrow(Function<E, X> exceptionSupplier) throws X {
        if (isError()) {
            throw exceptionSupplier.apply(error);
        }
        return result;
    }

    /**
     * Throws an exception with a string rendering of the error type. This is provided as an
     * ergonomic method to avoid boilerplate, but most callers should prefer {@link
     * #orElseThrow(Function)}.
     */
    public T orElseThrow() throws Exception {
        if (isError()) {
            throw new Exception(String.valueOf(error));
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Result<?, ?> otherResult = (Result<?, ?>) other;
        return Objects.equals(result, otherResult.result)
                && Objects.equals(error, otherResult.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(result, error);
    }
}
