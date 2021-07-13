# Result
A Rust-inspired result/error container.

Create a `Result` using one of the static initailizers `Result.ok(Object)` or
`Result.error(Object)`.

Compose multiple methods returning `Result` objects using `flatMap`, `flatMapResult`,
and `flatMapError`.

More generally, skip throwing exceptions in favor of application-defined error types
and use lambdas to define the happy and unhappy pathways for results and errors.

Users may also find it helpful to take inspiration from
[Railway Oriented Programming](https://fsharpforfunandprofit.com/rop/).

