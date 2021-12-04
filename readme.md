# Result
A Rust-inspired success and error container, useful for propagating errors instead of exceptions.

One model for this type is as an {@link Optional} that instead of being "present" or "empty"
is "present" or "error", where the error state might provide additional information about why the
state isn't "present".

Typically `Result`s are created as the return values of functions that may not always
succeed. Successful runs of such functions return a non-null value using `Result#ok(T)`
and unsuccessful runs of such functions return a non-null error value us `Result#error(E)`.

It's common to define a descriptive error type as a POJO or record that provides a structured
description of an issue, but equally acceptable to use a `String` or even `Collection<String>` to
provide messages. In the context of a parsing error, for instance, the structured error might
include details like a line and column number in addition to a description of the issue.

## Composition and Usage
`Result` provides a number of convenience methods to make composing usage both easier and more
readable.

`map`, `mapResult`, and `mapError` all provide ways to selectively transform the content of a
`Result` object.

`flatMap`, `flatMapResult`, and `flatMapError` all provide ways to selectively transform the content
of a `Result` where the results of a transformation is also a `Result`, similar to
`Optional#flatMap`.

`isError` provides a means to detect if the `Result` holds an error state.

`result` and `error` transform the `Result` into an `Optional<T>` and `Optional<E>` respectively,
present when the state is a success or error state, respectively, and absent otherwise.

`orElseThrow` and `orElseThrow(Function<E, X extends Exception>)` provide means to extract a
success state or throw an exception if the `Result` holds an error state. These methods are useful
when the state is unknown and the caller wishes to leave `Result`-style error handling in favor
of `Exception` handling.

`unwrap` provides a way to extract a success state when the caller knows the success state is
present. It throws an `IllegalArgumentException` if not in success state. Callers should prefer
`orElseThrow(err -> new IllegalArgumentException(err))` or  similar when the state is unknown.

`coerce` provides a way to adapt the success-type when the `Result` holds an error so that multiple
methods returning `Result` easily compose.

One might commonly use `unwrap` and `coerce` in composing multiple `Result` objects, such as:
```java
Result<ReturnType, CustomError> maybeDo() {
    Result<CustomType, CustomError> maybe = ...;
    if (maybe.isError()) {
        // known error, so fix CustomType to match the desired ReturnType
        // (note that CustomError matches)
        return maybe.coerce();
    }
    // known success, safely extract the success value
    CustomType definitely = maybe.unwrap();
    ...
}
```

## More Reading
Users of `Result` may also find it helpful to take inspiration from
[Railway Oriented Programming](https://fsharpforfunandprofit.com/rop/).

# License
This project is made available under the [Apache 2.0 License](/LICENSE).
