import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.math.BigInteger;
import javax.swing.*;
import java.awt.*;

public class NumberTheoryJava {

    // Fast probabilistic primality (deterministic for 64-bit in practice)
    private static boolean isProbablePrime(long x) {
        return isPrime64(x);
    }

    // Deterministic Miller–Rabin for 64-bit signed longs
    private static boolean isPrime64(long n) {
        if (n < 2)
            return false;
        // small primes
        for (long p : new long[] { 2, 3, 5, 7, 11, 13 }) {
            if (n == p)
                return true;
            if (n % p == 0)
                return false;
        }
        // write n-1 = d * 2^s with d odd
        long d = n - 1;
        int s = Long.numberOfTrailingZeros(d);
        d >>= s;

        // Known deterministic bases for 64-bit
        long[] bases = { 2, 3, 5, 7, 11, 13 };
        for (long a : bases) {
            if (a % n == 0)
                continue;
            if (!millerRabinCheck(a, d, s, n))
                return false;
        }
        return true;
    }

    private static boolean millerRabinCheck(long a, long d, int s, long n) {
        BigInteger bn = BigInteger.valueOf(n);
        BigInteger ba = BigInteger.valueOf(a);
        BigInteger x = ba.modPow(BigInteger.valueOf(d), bn);
        if (x.equals(BigInteger.ONE) || x.equals(bn.subtract(BigInteger.ONE)))
            return true;
        for (int r = 1; r < s; r++) {
            x = x.multiply(x).mod(bn);
            if (x.equals(bn.subtract(BigInteger.ONE)))
                return true;
        }
        return false;
    }

    // Incremental state across calls to avoid reprocessing
    private static long lastNProcessed = 1; // we consider n=1 -> 2 already accounted for
    private static long cumulativeCount = 1; // count includes prime 2 from n=1

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
            // Fast residue filters for primes p ≡ 1 (mod 4): if n^2 ≡ -1 (mod p) then
            // n^2+1 is divisible by p (and > p), so skip. Special-case small n where n^2+1
            // == p.
            int r5 = (int) (n % 5);
            if ((r5 == 2 || r5 == 3) && n != 2)
                continue; // divisible by 5 unless n=2 (gives 5)

            int r13 = (int) (n % 13);
            if (r13 == 5 || r13 == 8)
                continue; // divisible by 13

            int r17 = (int) (n % 17);
            if ((r17 == 4 || r17 == 13) && n != 4)
                continue; // divisible by 17 unless n=4 (gives 17)

            int r29 = (int) (n % 29);
            if (r29 == 12 || r29 == 17)
                continue; // divisible by 29

            int r37 = (int) (n % 37);
            if ((r37 == 6 || r37 == 31) && n != 6)
                continue; // divisible by 37 unless n=6 (gives 37)

            int r41 = (int) (n % 41);
            if (r41 == 9 || r41 == 32)
                continue; // divisible by 41

            long value = n * n + 1;
            if (isProbablePrime(value)) {
                primesList.add(value);
            }
        }
        return primesList;
    }

    // Faster count without allocating a list (incremental across calls)
    public static long countPrimes(double limit, boolean detailed) {
        if (limit < 2)
            return 0L;

        long nMax = (long) Math.floor(Math.sqrt(Math.max(0.0, limit - 1.0)));

        // If we've already processed up to this bound, return cached count
        if (nMax <= lastNProcessed) {
            if (detailed) {
                System.out.print("\r0/0 tasks completed. (100%) | Est: 0 sec left. (~0 minutes).");
            }
            return cumulativeCount;
        }

        // Determine the new even-n range to process: (lastNProcessed, nMax]
        long startN;
        if (lastNProcessed < 2) {
            startN = 2;
        } else {
            startN = (lastNProcessed % 2 == 0) ? lastNProcessed + 2 : lastNProcessed + 1;
        }

        long totalEvenRange = (nMax >= startN) ? ((nMax - startN) / 2 + 1) : 0L;
        if (totalEvenRange <= 0) {
            lastNProcessed = nMax;
            return cumulativeCount;
        }

        // Estimated remaining after pre-sieving: product over p of (1 - 2/p)
        double keepFraction = (3.0 / 5.0) * (11.0 / 13.0) * (15.0 / 17.0) * (27.0 / 29.0) * (35.0 / 37.0)
                * (39.0 / 41.0);
        long m = Math.max(1L, (long) Math.round(totalEvenRange * keepFraction));
        long x = 0;

        long startNs = System.nanoTime();
        long lastPrintNs = startNs;

        for (long n = startN; n <= nMax; n += 2) {
            int r5 = (int) (n % 5);
            if ((r5 == 2 || r5 == 3) && n != 2)
                continue; // divisible by 5 unless n=2 (gives 5)

            int r13 = (int) (n % 13);
            if (r13 == 5 || r13 == 8)
                continue; // divisible by 13

            int r17 = (int) (n % 17);
            if ((r17 == 4 || r17 == 13) && n != 4)
                continue; // divisible by 17 unless n=4 (gives 17)

            int r29 = (int) (n % 29);
            if (r29 == 12 || r29 == 17)
                continue; // divisible by 29

            int r37 = (int) (n % 37);
            if ((r37 == 6 || r37 == 31) && n != 6)
                continue; // divisible by 37 unless n=6 (gives 37)

            int r41 = (int) (n % 41);
            if (r41 == 9 || r41 == 32)
                continue; // divisible by 41

            // We reached a candidate worth testing
            x++;

            long value = n * n + 1;
            if (isProbablePrime(value))
                cumulativeCount++;

            if (detailed) {
                long now = System.nanoTime();
                if (now - lastPrintNs >= 200_000_000L) { // ~5 updates/sec
                    lastPrintNs = now;
                    double elapsedSec = (now - startNs) / 1_000_000_000.0;
                    double rate = (elapsedSec > 0 && x > 0) ? (x / elapsedSec) : 0.0;
                    long estSec = (rate > 0) ? Math.max(0L, (long) Math.ceil((m - x) / rate)) : 0L;
                    System.out.print("\r" + x + "/" + m + " tasks completed. (" + (100 * x / m)
                            + "%) | Est: " + estSec + " sec left. (~" + (estSec / 60) + " minutes).");
                }
            }

        }
        lastNProcessed = nMax;
        return cumulativeCount;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // long x = (long) Math.pow(10, 16);
            System.out.println("'Starting Value' 'Common Ratio' 'Detailed List': "); // 1 = detailed list, 0 =
                                                                                     // copy/pasteable list
            double x = scanner.nextDouble();
            double ratio = scanner.nextDouble();
            boolean detailed = (scanner.nextInt() == 1) ? true : false;
            // long order = 17;
            long order = 1;

            // Live graph when detailed mode is enabled
            LivePlot plot = null;
            RatioPlot ratioPlot = null;
            if (detailed) {
                plot = LivePlot.createAndShow("n^2+1 primes count vs order");
                ratioPlot = RatioPlot.createAndShow("Actual vs Expected ratio per order (ratio: " + ratio + ")");
            }

            // Compute baseline count for starting x so ratios use previous step properly
            long prevCount = countPrimes(x, false);
            double prevX = x;

            while (true) {
                try {
                    // System.out.print("Enter the limit x (0 to quit): ");
                    // long x = scanner.nextLong();
                    // if (x == 0)
                    // break;

                    x = x * ratio;

                    long count = countPrimes(x, detailed);
                    // List<Long> result = findPrimes(x); // Uncomment to also build the list
                    if (detailed) {
                        System.out.println(" Order: " + order + " | Count: " + count);
                        System.out.println();
                        if (plot != null) {
                            plot.addPoint(order, count);
                        }

                        // Plot ratio(actual) and ratio(expected BH)
                        if (ratioPlot != null && prevCount > 0 && prevX > 0) {
                            double actual = (double) count / (double) prevCount;
                            // BH heuristic: count(x) ~ K * sqrt(x) / log x => ratio ~ sqrt(r) *
                            // log(x_prev)/log(x)
                            double expected = Math.sqrt(x / prevX) * (Math.log(prevX) / Math.log(x));
                            ratioPlot.addPoint(order, actual, expected);
                        }

                    } else {
                        System.out.println(count);
                    }
                    // Advance previous references
                    prevCount = count;
                    prevX = x;
                    order++;
                } catch (Exception e) {
                    System.out.println("Enter a valid integer.");
                    // scanner.nextLine(); // clear invalid input
                }
            }
        }
    }

    // Simple Swing live plotter (no external dependencies)
    static class LivePlot extends JPanel {
        private final java.util.List<Double> xs = new java.util.ArrayList<>();
        private final java.util.List<Double> ys = new java.util.ArrayList<>();
        private final int padding = 50;
        private final int tickCount = 6;
        private String title = "";

        static LivePlot createAndShow(String title) {
            LivePlot panel = new LivePlot();
            panel.title = title;
            SwingUtilities.invokeLater(() -> {
                JFrame f = new JFrame(title);
                f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                f.setSize(900, 600);
                f.setLocationByPlatform(true);
                f.setLayout(new BorderLayout());
                f.add(panel, BorderLayout.CENTER);
                f.setVisible(true);
            });
            return panel;
        }

        synchronized void addPoint(double x, double y) {
            xs.add(x);
            ys.add(y);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);
            g2.setColor(Color.BLACK);

            int left = padding;
            int right = w - padding;
            int top = padding;
            int bottom = h - padding;

            // Axes
            g2.drawLine(left, bottom, right, bottom);
            g2.drawLine(left, bottom, left, top);

            java.util.List<Double> xsCopy;
            java.util.List<Double> ysCopy;
            synchronized (this) {
                xsCopy = new java.util.ArrayList<>(xs);
                ysCopy = new java.util.ArrayList<>(ys);
            }
            if (xsCopy.isEmpty()) {
                g2.dispose();
                return;
            }

            double minX = xsCopy.stream().min(Double::compare).orElse(0.0);
            double maxX = xsCopy.stream().max(Double::compare).orElse(1.0);
            double minY = ysCopy.stream().min(Double::compare).orElse(0.0);
            double maxY = ysCopy.stream().max(Double::compare).orElse(1.0);
            if (maxX == minX) {
                maxX = minX + 1.0;
            }
            if (maxY == minY) {
                maxY = minY + 1.0;
            }

            // Ticks and labels
            g2.setFont(g2.getFont().deriveFont(12f));
            for (int i = 0; i <= tickCount; i++) {
                int xTick = left + (int) ((right - left) * (i / (double) tickCount));
                int yTick = bottom - (int) ((bottom - top) * (i / (double) tickCount));
                // X ticks
                g2.drawLine(xTick, bottom - 3, xTick, bottom + 3);
                double xVal = minX + (maxX - minX) * (i / (double) tickCount);
                String xs = String.format("%.0f", xVal);
                int sw = g2.getFontMetrics().stringWidth(xs);
                g2.drawString(xs, xTick - sw / 2, bottom + 18);
                // Y ticks
                g2.drawLine(left - 3, yTick, left + 3, yTick);
                double yVal = minY + (maxY - minY) * (i / (double) tickCount);
                String ys = (yVal >= 1000) ? String.format("%.0e", yVal) : String.format("%.0f", yVal);
                int sh = g2.getFontMetrics().getAscent();
                int swy = g2.getFontMetrics().stringWidth(ys);
                g2.drawString(ys, left - swy - 8, yTick + sh / 2 - 2);
            }

            // Draw data polyline
            g2.setColor(new Color(0x1f77b4));
            int prevX = -1, prevY = -1;
            for (int i = 0; i < xsCopy.size(); i++) {
                double xVal = xsCopy.get(i);
                double yVal = ysCopy.get(i);
                int px = left + (int) ((xVal - minX) / (maxX - minX) * (right - left));
                int py = bottom - (int) ((yVal - minY) / (maxY - minY) * (bottom - top));
                if (prevX >= 0) {
                    g2.drawLine(prevX, prevY, px, py);
                }
                prevX = px;
                prevY = py;
            }

            // Title
            if (title != null && !title.isEmpty()) {
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
                int sw = g2.getFontMetrics().stringWidth(title);
                g2.drawString(title, (w - sw) / 2, top - 12);
            }
            g2.dispose();
        }
    }

    // Plot two series: actual ratio and expected ratio
    static class RatioPlot extends JPanel {
        private final java.util.List<Double> xs = new java.util.ArrayList<>();
        private final java.util.List<Double> ysActual = new java.util.ArrayList<>();
        private final java.util.List<Double> ysExpected = new java.util.ArrayList<>();
        private final int padding = 50;
        private final int tickCount = 6;
        private String title = "";

        static RatioPlot createAndShow(String title) {
            RatioPlot panel = new RatioPlot();
            panel.title = title;
            SwingUtilities.invokeLater(() -> {
                JFrame f = new JFrame(title);
                f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                f.setSize(900, 600);
                f.setLocationByPlatform(true);
                f.setLayout(new BorderLayout());
                f.add(panel, BorderLayout.CENTER);
                f.setVisible(true);
            });
            return panel;
        }

        synchronized void addPoint(double x, double actual, double expected) {
            xs.add(x);
            ysActual.add(actual);
            ysExpected.add(expected);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);
            g2.setColor(Color.BLACK);

            int left = padding;
            int right = w - padding;
            int top = padding;
            int bottom = h - padding;

            // Axes
            g2.drawLine(left, bottom, right, bottom);
            g2.drawLine(left, bottom, left, top);

            java.util.List<Double> xsC, yaC, yeC;
            synchronized (this) {
                xsC = new java.util.ArrayList<>(xs);
                yaC = new java.util.ArrayList<>(ysActual);
                yeC = new java.util.ArrayList<>(ysExpected);
            }
            if (xsC.isEmpty()) {
                g2.dispose();
                return;
            }

            double minX = xsC.stream().min(Double::compare).orElse(0.0);
            double maxX = xsC.stream().max(Double::compare).orElse(1.0);
            double minY = Math.min(
                    yaC.stream().min(Double::compare).orElse(0.0),
                    yeC.stream().min(Double::compare).orElse(0.0));
            double maxY = Math.max(
                    yaC.stream().max(Double::compare).orElse(1.0),
                    yeC.stream().max(Double::compare).orElse(1.0));
            if (maxX == minX) {
                maxX = minX + 1.0;
            }
            if (maxY == minY) {
                maxY = minY + 1.0;
            }

            // Ticks and labels
            g2.setFont(g2.getFont().deriveFont(12f));
            for (int i = 0; i <= tickCount; i++) {
                int xTick = left + (int) ((right - left) * (i / (double) tickCount));
                int yTick = bottom - (int) ((bottom - top) * (i / (double) tickCount));
                // X ticks
                g2.drawLine(xTick, bottom - 3, xTick, bottom + 3);
                double xVal = minX + (maxX - minX) * (i / (double) tickCount);
                String xs = String.format("%.0f", xVal);
                int sw = g2.getFontMetrics().stringWidth(xs);
                g2.drawString(xs, xTick - sw / 2, bottom + 18);
                // Y ticks
                g2.drawLine(left - 3, yTick, left + 3, yTick);
                double yVal = minY + (maxY - minY) * (i / (double) tickCount);
                String ys = String.format("%.2f", yVal);
                int sh = g2.getFontMetrics().getAscent();
                int swy = g2.getFontMetrics().stringWidth(ys);
                g2.drawString(ys, left - swy - 8, yTick + sh / 2 - 2);
            }

            // Draw actual (blue) and expected (orange)
            Stroke old = g2.getStroke();
            // Actual
            g2.setColor(new Color(0x1f77b4));
            drawSeries(g2, xsC, yaC, minX, maxX, minY, maxY, left, right, top, bottom);
            // Expected
            float[] dash = { 6f, 6f };
            g2.setColor(new Color(0xff7f0e));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0f));
            drawSeries(g2, xsC, yeC, minX, maxX, minY, maxY, left, right, top, bottom);
            g2.setStroke(old);

            // Legend
            String aLbl = "Actual";
            String eLbl = "Expected";
            int lx = right - 140;
            int ly = top + 10;
            g2.setColor(Color.WHITE);
            g2.fillRect(lx - 10, ly - 15, 150, 50);
            g2.setColor(Color.GRAY);
            g2.drawRect(lx - 10, ly - 15, 150, 50);
            g2.setColor(new Color(0x1f77b4));
            g2.drawLine(lx, ly, lx + 30, ly);
            g2.setColor(Color.BLACK);
            g2.drawString(aLbl, lx + 40, ly + 5);
            g2.setColor(new Color(0xff7f0e));
            g2.drawLine(lx, ly + 20, lx + 30, ly + 20);
            g2.setColor(Color.BLACK);
            g2.drawString(eLbl, lx + 40, ly + 25);

            // Title
            if (title != null && !title.isEmpty()) {
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(g2.getFont().deriveFont(Font.BOLD, 14f));
                int sw = g2.getFontMetrics().stringWidth(title);
                g2.drawString(title, (w - sw) / 2, top - 12);
            }
            g2.dispose();
        }

        private void drawSeries(Graphics2D g2, java.util.List<Double> xs, java.util.List<Double> ys,
                double minX, double maxX, double minY, double maxY,
                int left, int right, int top, int bottom) {
            int prevX = Integer.MIN_VALUE, prevY = Integer.MIN_VALUE;
            for (int i = 0; i < xs.size(); i++) {
                double xVal = xs.get(i);
                double yVal = ys.get(i);
                int px = left + (int) ((xVal - minX) / (maxX - minX) * (right - left));
                int py = bottom - (int) ((yVal - minY) / (maxY - minY) * (bottom - top));
                if (prevX != Integer.MIN_VALUE) {
                    g2.drawLine(prevX, prevY, px, py);
                }
                prevX = px;
                prevY = py;
            }
        }
    }
}
