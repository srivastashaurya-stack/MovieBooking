package bookingengine;

public class PaymentConfirmDemo {
    public static void main(String[] args) throws InterruptedException {
        // 1. Background worker start karte hain
        SeatCleanupWorker cleanerWorker = new SeatCleanupWorker();
        cleanerWorker.startAutoReleaseService();

        System.out.println("=== INITIAL STATE ===");
        BookingService.displayAllEvents();

        // 2. User 1 hold karega 2 seats (10 second TTL)
        System.out.println(">>> User 1 holding 2 seats for 10 seconds...");
        int bookingId = BookingService.holdSeats(1, 1, 2, 10);

        if (bookingId != -1) {
            // 3. User 3 second baad payment confirm karta hai (well before 10s TTL)
            System.out.println(">>> User proceeding to payment gateway (waiting 3 seconds)...");
            Thread.sleep(3000);

            System.out.println(">>> Payment received! Confirming booking...");
            boolean confirmed = BookingService.confirmBooking(bookingId);

            if (confirmed) {
                System.out.println(">>> Booking successfully finalized!");
            }
        }

        // 4. Wait karte hain taaki check karein ki worker galti se CONFIRMED seat ko release toh nahi karta
        System.out.println(">>> Waiting 8 seconds to ensure background cleaner ignores CONFIRMED status...\n");
        Thread.sleep(8000);

        System.out.println("=== FINAL STATE (Seats should remain permanently booked) ===");
        BookingService.displayAllEvents();

        cleanerWorker.stopService();
        System.out.println(">>> Verification complete.");
    }
}