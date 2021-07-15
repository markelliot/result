package io.github.markelliot.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A Rust-inspired success and error container, useful for propagating errors instead of exceptions.
 *
 * <p>One model for this type is as an {@link Optional} that instead of being "present" or "empty"
 * is "present" or "error", where the error state might provide additional information about why the
 * state isn't "present".
 *
 * <p>Typically {@link Result}s are created as the return values of functions that may not always
 * succeed. Successful runs of a function return a non-null value using {@link Result#ok(Object)}
 * and unsuccessful runs of a function return a non-null error value us {@link
 * Result#error(Object)}.
 *
 * <p>It's common to define a descriptive error type as a POJO or record that provides a structured
 * description of an issue, but equally acceptable to use a {@link String} or even {@code
 * Collection<String>} to provide messages. In the context of a parsing error, for instance, the
 * structured error might include details like a line and column number in addition to a description
 * of the issue.
 */
public final class Result<T, E> {
    private final T result;
    private final E error;

    /** Returns a {@link Result} holding a success state. */
    public static <T, E> Result<T, E> ok(T result) {
        Objects.requireNonNull(result);
        return new Result<>(result, null);
    }

    /** Returns a {@link Result} holding an error state. */
    public static <T, E> Result<T, E> error(E error) {
        Objects.requireNonNull(error);
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

    /**
     * Returns a {@link Result} with the success state transformed according to the supplied
     * function or the untransformed error state.
     */
    public <U> Result<U, E> mapResult(Function<T, U> fn) {
        return !isError() ? Result.ok(fn.apply(result)) : Result.error(error);
    }

    /**
     * When this object holds a success state, returns the {@link Result} produced by transforming
     * the success state with the supplied function, or the untransformed error state. Note that
     * callers may use this method to coerece a success state to an error state.
     */
    public <U> Result<U, E> flatMapResult(Function<T, Result<U, E>> fn) {
        return !isError() ? fn.apply(result) : Result.error(error);
    }

    /**
     * Returns a {@link Result} with the error state transformed according to the supplied function
     * or the untransformed success state.
     */
    public <F> Result<T, F> mapError(Function<E, F> fn) {
        return !isError() ? Result.ok(result) : Result.error(fn.apply(error));
    }

    /**
     * When this object holds an error state, returns the {@link Result} produced by transforming
     * the error state with the supplied function, or the untransformed success state. Note that
     * callers may use this method to coerce an error state to a success state.
     */
    public <F> Result<T, F> flatMapError(Function<E, Result<T, F>> fn) {
        return !isError() ? Result.ok(result) : fn.apply(error);
    }

    /**
     * Transforms this result to another using resultFn for success states and errorFn for error
     * states.
     */
    public <U, F> Result<U, F> map(Function<T, U> resultFn, Function<E, F> errorFn) {
        return mapResult(resultFn).mapError(errorFn);
    }

    /**
     * Returns the result of transforming this result to another, taking the output result from the
     * supplied transformation functions, and using resultFn for success states and errorFn for
     * error states.
     */
    public <U, F> Result<U, F> flatMap(
            Function<T, Result<U, F>> resultFn, Function<E, Result<U, F>> errorFn) {
        return !isError() ? resultFn.apply(result) : errorFn.apply(error);
    }

    /** Returns if this object is an error state. */
    public boolean isError() {
        return error != null;
    }

    /** Returns an Optional containing the result if it's present or empty otherwise. */
    public Optional<T> result() {
        return !isError() ? Optional.of(result) : Optional.empty();
    }

    /** Returns an Optional containing the error if it's present or empty otherwise. */
    public Optional<E> error() {
        return !isError() ? Optional.empty() : Optional.of(error);
    }

    /**
     * Returns the success state of this object or invokes {@code exceptionFn} with the error state
     * as the argument, and throws the exception that that function returns.
     */
    public <X extends Exception> T orElseThrow(Function<E, X> exceptionFn) throws X {
        if (isError()) {
            throw exceptionFn.apply(error);
        }
        return result;
    }

    /**
     * Returns the success sate of this object or throws an {@link Exception} with a message that is
     * the simple {@link String} rendering of the error state. This method is provided as an
     * ergonomic convenience, most commonly in test code to reduce boilerplate. Almost always,
     * callers should prefer {@link #orElseThrow(Function)}.
     */
    public T orElseThrow() throws Exception {
        return orElseThrow(err -> new Exception(String.valueOf(err)));
    }

    /**
     * Returns the value of result or throwing an {@link IllegalStateException} if no result.
     *
     * <p>Syntactically this is the same as {@link #orElseThrow()}, except callers will note that it
     * throws a {@link RuntimeException} instead of a checked exception, in part because
     * <em>semantically</em> this method implies the caller knows this is safe to do.
     *
     * <p>When the caller does not know the internal state and wishes to throw an ISE, prefer using
     * {@link #orElseThrow(Function)}.
     *
     * <p>Example usage might follow the pattern:
     *
     * <pre>{@code
     * Result<CustomType, CustomError> maybe = ...;
     * if (maybe.isError()) {
     *     return maybe.coerce();
     * }
     * CustomType definitely = maybe.unwrap();
     * }</pre>
     */
    public T unwrap() throws IllegalStateException {
        return orElseThrow(err -> new IllegalStateException(String.valueOf(err)));
    }

    /**
     * Returns a {@link Result} object containing the same error as the current object and adjusting
     * the result type to match local call-site requirements. Note that this method will throw an
     * {@link IllegalStateException} if it is not an error.
     *
     * <p>Callers should use this method when the state is known to be an error but when the result
     * is of the wrong type. This is useful when composing results between functions:
     *
     * <pre>{@code
     * Result<Integer, CustomError> maybeParseOuter() {
     *     ...
     *     Result<String, CustomError> maybe = maybeParseInner();
     *     if (maybe.isError()) {
     *         return maybe.coerce();
     *     }
     *     ...
     * }
     * }</pre>
     *
     * <p>Aside from generating a nicer runtime exception, this is syntactically equivalent (and
     * preferable to):
     *
     * <pre>{@code
     * errorStateResult.mapResult(ignored -> null);
     * }</pre>
     */
    public <U> Result<U, E> coerce() {
        if (!isError()) {
            throw new IllegalStateException("Cannot coerce a success-state result");
        }
        return Result.error(error);
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
