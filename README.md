# Bateman-Horn Conjecture Computational Analysis

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

## References

- [Greaves, G. (2001). *Sieves in Number Theory*. Springer.](https://www.cambridge.org/core/journals/mathematical-gazette/article/abs/sieves-in-number-theory-by-george-greaves-pp-304-70-2001-isbn-3-540-41647-1-springerverlag/E56156241A84930DEF5A2B168389FA25)
- [Ford, K. (2023). *Lecture notes on sieve methods*. University of Illinois](https://ford126.web.illinois.edu/sieve2023.pdf)  
- [Cojocaru AC, Murty MR. An Introduction to Sieve Methods and Their Applications. Cambridge University Press; 2005.](https://www.cambridge.org/core/books/an-introduction-to-sieve-methods-and-their-applications/A7B896FAE6B543D73F4FDBDA8609BAC6)  
- [Rosser–Iwaniec sieve (Joni’s Math Notes)](https://jonismathnotes.blogspot.com/2015/02/the-rosser-iwaniec-sieve.html)  
- [Terrence Tao – Fundamental Lemma of Sieve Theory](https://terrytao.wordpress.com/tag/fundamental-lemma-of-sieve-theory/)  
- [Halberstam & Richert, *Sieve Methods* (Archive.org)](https://archive.org/details/sievemethods0000halb/page/146/mode/2up)  
- [Projects of Number Theory (Yau Mathcamp)](https://drive.google.com/file/d/1pBrK-WRkFVC7FmIfD2RaWL9WXVg7ATBt/view)
