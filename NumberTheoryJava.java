import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.math.BigInteger;

public class NumberTheoryJava {

    // Fast probabilistic primality (deterministic for 64-bit in practice)
    private static boolean isProbablePrime(long x) {
        if (x < 2)
            return false;
        if ((x & 1L) == 0L)
            return x == 2L;
        // Quick small prime checks
        if (x % 3L == 0L)
            return x == 3L;
        if (x % 5L == 0L)
            return x == 5L;
        return BigInteger.valueOf(x).isProbablePrime(20);
    }

    public static List<Long> findPrimes(long limit) {
        List<Long> primesList = new ArrayList<>();
        if (limit >= 2) {
            // n = 1 gives 2
            primesList.add(2L);
        }

        if (limit <= 5)
            return primesList;

        long nMax = (long) Math.floor(Math.sqrt(limit - 1));

        // Iterate even n only (for n>1, odd n => n^2+1 is even > 2, hence composite)
        for (long n = 2; n <= nMax; n += 2) {
            int r5 = (int) (n % 5);
            // Skip n ≡ 2 or 3 (mod 5): then n^2 + 1 ≡ 0 (mod 5) and > 5, composite
            if ((r5 == 2 || r5 == 3) && n != 2)
                continue; // allow n=2 (gives 5)

            long value = n * n + 1;
            if (isProbablePrime(value)) {
                primesList.add(value);
            }
        }
        return primesList;
    }

    // Faster count without allocating a list
    public static long countPrimes(long limit) {
        if (limit < 2)
            return 0L;

        long count = 0L;
        // n=1 -> 2
        if (limit >= 2)
            count++;
        long x = 0;

        long nMax = (long) Math.floor(Math.sqrt(limit - 1));

        long totalEven = (nMax - 2) / 2 + 1;
        long m = (totalEven * 3) / 5;
        long sec = m / (20000);
        for (long n = 2; n <= nMax; n += 2) {
            int r5 = (int) (n % 5);
            if ((r5 == 2 || r5 == 3) && n != 2)
                continue;

            x++;
            sec = (m - x) / 20000;

            long value = n * n + 1;
            if (isProbablePrime(value))
                count++;
            System.out.print(
                    "\r" + x + "/" + m + " tasks completed. (" + (100 * x / m) + "%) | Est: " + sec + " sec left. (~"
                            + sec / 60 + " minutes (~" + sec / 3660 + " hours)).");

        }
        return count;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long x = (long) Math.pow(10, 16);
        long order = 1;
        while (true) {
            try {
                // System.out.print("Enter the limit x (0 to quit): ");
                // long x = scanner.nextLong();
                // if (x == 0)
                // break;

                x = x * 10;

                long count = countPrimes(x);
                // List<Long> result = findPrimes(x); // Uncomment to also build the list
                System.out.println("Order: " + order + " | Count: " + count);
                System.out.println();
                order++;
            } catch (Exception e) {
                System.out.println("Enter a valid integer.");
                scanner.nextLine(); // clear invalid input
            }
        }

        // scanner.close();
    }
}
