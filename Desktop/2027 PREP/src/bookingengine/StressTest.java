package bookingengine;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StressTest {
    public static void main(String[] args) throws InterruptedException {
        int totalUsers = 20;
        int eventId = 1;

        // Dedicated thread pool matching DB connection pool sizing
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Latches to synchronize concurrent thread release
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(totalUsers);

        System.out.println(">>> Initializing stress test for " + totalUsers + " concurrent users...");

        for (int i = 1; i <= totalUsers; i++) {
            final int userId = i;
            executorService.submit(() -> {
                try {
                    startGate.await(); // Synchronize all worker threads
                    BookingService.bookTicket(userId, eventId, 1);
                } catch (Exception e) {
                    System.err.println("Execution failed for User " + userId + ": " + e.getMessage());
                } finally {
                    endGate.countDown();
                }
            });
        }

        // Release all concurrent requests simultaneously
        long startTime = System.currentTimeMillis();
        startGate.countDown();

        // Await completion of all submitted tasks
        endGate.await();
        long endTime = System.currentTimeMillis();

        executorService.shutdown();

        System.out.println("\n>>> Stress Test Complete! Execution Time: " + (endTime - startTime) + " ms");
    }
}