import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NumberTheoryJava {

    public static boolean isPrime(long num) {
        if (num < 2)
            return false;
        if (num == 2)
            return true;
        if (num % 2 == 0)
            return false;
        long sqrt = (long) Math.sqrt(num);
        for (long i = 3; i <= sqrt; i += 2) {
            if (num % i == 0)
                return false;
        }
        return true;
    }

    public static List<Long> findPrimes(long limit) {
        List<Long> primesList = new ArrayList<>();
        long n = 1;
        while (true) {
            long value = n * n + 1;
            if (value > limit)
                break;
            if (isPrime(value)) {
                primesList.add(value);
            }
            n++;
        }
        return primesList;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.print("Enter the limit x (0 to quit): ");
                long x = scanner.nextLong();
                if (x == 0)
                    break;

                List<Long> result = findPrimes(x);
                // System.out.println("Primes ≤ " + x + " of the form n^2 + 1: " + result);
                System.out.println("Count: " + result.size());
                System.out.println();
            } catch (Exception e) {
                System.out.println("Enter a valid integer.");
                scanner.nextLine(); // clear invalid input
            }
        }

        scanner.close();
    }
}
