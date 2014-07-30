bcps - ByteCode Performance Statistics
====

This AOP-style agent library plugs-in to your app as a Java Agent, allowing you to instrument methods that you find interesting. 
The injected bytecode collects and stores performance statistics without you having to necessarily adapt or change your code, for easy offline inspection.

The library uses low-latency and GC-free (no heap-allocating) techniques, to avoid being detrimental to your application's performance.

