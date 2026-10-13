package bookingengine;

import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        // Start background seat cleaner service
        SeatCleanupWorker cleanerWorker = new SeatCleanupWorker();
        cleanerWorker.startAutoReleaseService();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("\n================================================");
        System.out.println("   WELCOME TO MOVIE BOOKING CONCURRENCY ENGINE  ");
        System.out.println("================================================");

        while (running) {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. View Active Shows & Available Seats");
            System.out.println("2. Hold Seats (Temporary Reservation)");
            System.out.println("3. Confirm & Pay for Booking");
            System.out.println("4. Exit Engine");
            System.out.print("Enter your choice (1-4): ");

            String choiceInput = scanner.nextLine().trim();

            switch (choiceInput) {
                case "1":
                    BookingService.displayAllEvents();
                    break;

                case "2":
                    try {
                        System.out.print("Enter User ID: ");
                        int userId = Integer.parseInt(scanner.nextLine().trim());

                        System.out.print("Enter Event ID: ");
                        int eventId = Integer.parseInt(scanner.nextLine().trim());

                        System.out.print("Enter Number of Seats: ");
                        int seats = Integer.parseInt(scanner.nextLine().trim());

                        System.out.print("Enter Hold TTL Duration (Seconds, e.g. 15): ");
                        int ttl = Integer.parseInt(scanner.nextLine().trim());

                        int bookingId = BookingService.holdSeats(userId, eventId, seats, ttl);
                        if (bookingId != -1) {
                            System.out.println(">>> Keep this Booking ID safe for payment: " + bookingId);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid numeric input. Please enter valid numbers.");
                    }
                    break;

                case "3":
                    try {
                        System.out.print("Enter Booking ID to Confirm: ");
                        int bookingId = Integer.parseInt(scanner.nextLine().trim());

                        boolean success = BookingService.confirmBooking(bookingId);
                        if (success) {
                            System.out.println(">>> Ticket officially confirmed! Enjoy the movie.");
                        } else {
                            System.out.println(">>> Confirmation rejected. Either booking expired or invalid ID.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid numeric input. Please enter valid numbers.");
                    }
                    break;

                case "4":
                    System.out.println("\nShutting down Seat Cleanup Worker...");
                    cleanerWorker.stopService();
                    running = false;
                    System.out.println("Exited successfully. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid selection. Please choose an option between 1 and 4.");
            }
        }

        scanner.close();
    }
}