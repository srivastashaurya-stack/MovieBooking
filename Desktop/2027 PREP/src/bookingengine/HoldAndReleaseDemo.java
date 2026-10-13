package bookingengine;

public class HoldAndReleaseDemo {
    public static void main(String[] args) throws InterruptedException {
        // 1. Start background auto-release cleaner
        SeatCleanupWorker cleanerWorker = new SeatCleanupWorker();
        cleanerWorker.startAutoReleaseService();

        System.out.println("=== INITIAL STATE ===");
        BookingService.displayAllEvents();

        // 2. User 1 holds 2 seats for 5 seconds TTL
        System.out.println(">>> User 1 requesting temporary hold on 2 seats for 5 seconds...");
        int holdDurationSeconds = 5;
        int bookingId = BookingService.holdSeats(1, 1, 2, holdDurationSeconds);

        System.out.println("\n=== STATE DURING HOLD (Seats should be deducted) ===");
        BookingService.displayAllEvents();

        // 3. Simulate user abandonment (Wait for TTL expiration)
        System.out.println(">>> Waiting 6 seconds (simulating user dropped off without payment)...\n");
        Thread.sleep(8000);

        // 4. Check state after background auto-release has restored seats
        System.out.println("=== FINAL STATE (Seats should be auto-restored) ===");
        BookingService.displayAllEvents();

        cleanerWorker.stopService();
        System.out.println(">>> Simulation finished cleanly.");
    }
}