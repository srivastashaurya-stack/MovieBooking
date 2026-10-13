package bookingengine;

public class ConcurrencyTest {

    public static void main(String[] args) {
        System.out.println("Starting concurrent booking simulation...\n");

        // User 1 ke liye thread
        Thread user1 = new Thread(() -> {
            System.out.println("User 1 booking request bhej raha hai...");
            BookingService.bookTicket(1, 1, 1);
        });

        // User 2 ke liye thread
        Thread user2 = new Thread(() -> {
            System.out.println("User 2 booking request bhej raha hai...");
            BookingService.bookTicket(2, 1, 1);
        });

        // Dono threads ko lagbhag ek hi pal me start karenge
        user1.start();
        user2.start();

        // Wait karo dono finish hone ka
        try {
            user1.join();
            user2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final status print karo
        System.out.println("\nFinal Database State:");
        BookingService.displayAllEvents();
    }
}