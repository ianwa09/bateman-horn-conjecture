# NumberTheoryJava

Java program for searching and counting primes of the form **n² + 1** up to large limits.

## Features
- **Primality testing**: Deterministic Miller–Rabin for 64-bit integers.  
- **Candidate filtering**: Only even n checked, with modular sieves to skip obvious composites.  
- **Incremental counting**: Caches progress so repeated calls don’t recompute.  
- **Visualization** (Swing):
  - Prime counts vs iteration order.
  - Ratio of actual vs expected counts (Bateman–Horn heuristic).

## Usage
Compile and run:
```bash
javac NumberTheoryJava.java
java NumberTheoryJava
```
When prompted, enter:
```bash
<starting_value> <growth_ratio> <detailed_flag>
```
starting_value → initial search bound (x).

growth_ratio → factor to multiply x each step.

detailed_flag → 1 for progress + live plots, 0 for counts only.

Example:
```bash
1 2 1
```

Starts at 1, multiplies by 2 at each step, and shows detailed information with a graph. 
