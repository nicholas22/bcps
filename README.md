bcps - ByteCode Performance Statistics
====

This AOP-like library plugs-in to your app as a Java Agent, allowing you to instrument methods that you find interesting in your code, for collecting and storing performance statistics easily, for later analysis.

You don't have to make any changes to your code, or you can annotate which methods you want to instrument.
The library uses low-latency and GC-free (no heap-allocating) techniques, to avoid being detrimental to your application's performance.
