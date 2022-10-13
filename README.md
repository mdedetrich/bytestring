# ByteString

`scala.collection.immutable.ByteString` is a rope-like immutable data structure containing bytes. The goal of this structure is 
to reduce copying of arrays when concatenating and slicing sequences of bytes, and also providing a thread safe
way of working with bytes.

`scala.collection.immutable.ByteString` is based off the latest Akka ASL2 codebase
(see https://github.com/akka/akka/tree/6680c47dcc2305906a44d7794081682211d7ee0b). The original authors of Akka's
`ByteString` can be seen [here](AUTHORS).

## Benchmarking
`ByteString` uses [sbt-jmh](https://github.com/sbt/sbt-jmh) to perform benchmarks, see the [benchmark](benchmark)
folder for more information. Details for running benchmarks are typically noted in the benchmark class file itself.
