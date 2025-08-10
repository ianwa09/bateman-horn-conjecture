import java.util.*;
import java.math.BigInteger;

public class NsqPlus1Sieve {

    // Cap for small primes used in pre-sieving (keeps memory/time bounded)
    static final int P_MAX = 1_000_000;

    // Standard Sieve of Eratosthenes for primes up to limit
    static List<Integer> simpleSieve(int limit) {
        boolean[] isComposite = new boolean[limit + 1];
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= limit; i++) {
            if (!isComposite[i]) {
                primes.add(i);
                if ((long) i * i <= limit) {
                    for (int j = i * i; j <= limit; j += i) {
                        isComposite[j] = true;
                    }
                }
            }
        }
        return primes;
    }

    public static List<Long> findPrimesNsqPlus1(long x) {
        long nMax = (long) Math.floor(Math.sqrt(x - 1));

        // Only even n: halve storage
        int size = (int) ((nMax / 2) + 1);
        BitSet composite = new BitSet(size); // composite.get(i) => n=2*i is composite
        // Use only small primes up to P_MAX for pre-sieving
        List<Integer> primes = simpleSieve(Math.min(P_MAX, (int) Math.sqrt(Math.max(0L, x)) + 1));

        for (int p : primes) {
            if (p == 2)
                continue; // n^2+1 odd for even n, skip p=2

            // Find solutions to n^2 ≡ -1 (mod p)
            int r = modSqrtMinusOne(p);
            if (r == -1)
                continue; // no solution

            // Only even n ⇒ we mark r and p-r if even
            for (int root : new int[] { r, p - r }) {
                if (root < 0 || root >= p)
                    continue;
                // Find the first n >= 2 that is congruent to root (mod p) and even
                long first = root;
                if (first < 2)
                    first += ((2 - first + p - 1) / p) * p;
                // Ensure even
                if ((first & 1L) != 0)
                    first += p;

                // Map n to index = n/2
                for (long n = first; n <= nMax; n += p) {
                    composite.set((int) (n / 2));
                }
            }
        }

        List<Long> result = new ArrayList<>();
        // n=0 gives 1 (not prime), n=2 gives 5 (prime)
        for (int i = 1; i < size; i++) {
            if (!composite.get(i)) {
                long n = 2L * i;
                long val = n * n + 1;
                if (isPrime64(val))
                    result.add(val);
            }
        }
        return result;
    }

    // Returns sqrt(-1) mod p if exists, else -1
    static int modSqrtMinusOne(int p) {
        if (p % 4 != 1)
            return -1; // Only p ≡ 1 mod 4 have -1 as quadratic residue
        // Solve r^2 ≡ -1 (mod p) via Tonelli–Shanks on a = p-1
        long r = tonelliShanks(p - 1L, p);
        return r < 0 ? -1 : (int) r;
    }

    // Quick primality for final candidates (small)
    // Deterministic Miller–Rabin for 64-bit signed longs
    static boolean isPrime64(long n) {
        if (n < 2)
            return false;
        for (long p : new long[] { 2, 3, 5, 7, 11, 13 }) {
            if (n == p)
                return true;
            if (n % p == 0)
                return false;
        }
        long d = n - 1;
        int s = Long.numberOfTrailingZeros(d);
        d >>= s;
        long[] bases = { 2, 3, 5, 7, 11, 13 };
        BigInteger bn = BigInteger.valueOf(n);
        for (long a : bases) {
            if (a % n == 0)
                continue;
            BigInteger x = BigInteger.valueOf(a).modPow(BigInteger.valueOf(d), bn);
            if (x.equals(BigInteger.ONE) || x.equals(bn.subtract(BigInteger.ONE)))
                continue;
            boolean witness = true;
            for (int r = 1; r < s; r++) {
                x = x.multiply(x).mod(bn);
                if (x.equals(bn.subtract(BigInteger.ONE))) {
                    witness = false;
                    break;
                }
            }
            if (witness)
                return false;
        }
        return true;
    }

    // Tonelli–Shanks algorithm: solve x^2 ≡ a (mod p), p odd prime; returns -1 if
    // none
    static long tonelliShanks(long a, int p) {
        a %= p;
        if (a == 0)
            return 0;
        if (p == 2)
            return a;
        if (legendreSymbol(a, p) != 1)
            return -1;
        long q = p - 1;
        int s = 0;
        while ((q & 1) == 0) {
            s++;
            q >>= 1;
        }
        int z = 2;
        while (legendreSymbol(z, p) != -1)
            z++;
        long c = modPow(z, q, p);
        long x = modPow(a, (q + 1) / 2, p);
        long t = modPow(a, q, p);
        int m = s;
        while (t != 1) {
            int i = 1;
            long tt = (t * t) % p;
            while (tt != 1) {
                tt = (tt * tt) % p;
                i++;
                if (i == m)
                    return -1; // should not happen if a is residue
            }
            long b = modPow(c, 1L << (m - i - 1), p);
            x = (x * b) % p;
            t = (t * b % p) * b % p;
            c = (b * b) % p;
            m = i;
        }
        return x;
    }

    static int legendreSymbol(long a, int p) {
        long ls = modPow(a, (p - 1) / 2, p);
        if (ls == p - 1)
            return -1;
        return (int) ls; // 0 or 1
    }

    static long modPow(long a, long e, int mod) {
        long res = 1 % mod;
        a %= mod;
        while (e > 0) {
            if ((e & 1) == 1)
                res = (res * a) % mod;
            a = (a * a) % mod;
            e >>= 1;
        }
        return res;
    }

    public static void main(String[] args) {
        long x = (long) 1e18;
        long t0 = System.currentTimeMillis();
        List<Long> primesList = findPrimesNsqPlus1(x);
        long t1 = System.currentTimeMillis();
        System.out.println("Count: " + primesList.size());
        System.out.println("Time: " + (t1 - t0) / 1000.0 + " sec");
    }
}
