import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelMonteCarloPi {

    public static void main(String[] args) {
        final long TOTAL_ITERATIONS = 1_000_000_000L;

        int numThreads = 1;
        if (args.length > 0) {
            try {
                numThreads = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Аргумент має бути цілим числом.");
                return;
            }
        }

        long startTime = System.currentTimeMillis();

        AtomicLong totalPointsInCircle = new AtomicLong(0);

        Thread[] threads = new Thread[numThreads];

        long iterationsPerThread = TOTAL_ITERATIONS / numThreads;

        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(() -> {
                long pointsInCircle = 0;

                ThreadLocalRandom random = ThreadLocalRandom.current();

                for (long j = 0; j < iterationsPerThread; j++) {
                    double x = random.nextDouble();
                    double y = random.nextDouble();

                    if (x * x + y * y <= 1) {
                        pointsInCircle++;
                    }
                }

                totalPointsInCircle.addAndGet(pointsInCircle);
            });

            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        double pi = 4.0 * totalPointsInCircle.get() / TOTAL_ITERATIONS;

        System.out.println("PI is " + pi);
        System.out.println("THREADS " + numThreads);
        System.out.println("ITERATIONS " + TOTAL_ITERATIONS);
        System.out.println("TIME " + executionTime + "ms");
    }
}