package bookingengine;

import database.DatabaseConnection;
import java.sql.*;

public class BookingService {

    // 1. Fetch and display all active events
    public static void displayAllEvents() {
        String query = "SELECT event_id, title, available_seats, price_per_seat FROM events";

        System.out.println("\n--- CURRENT SHOW STATUS ---");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("event_id");
                String title = rs.getString("title");
                int availableSeats = rs.getInt("available_seats");
                double price = rs.getDouble("price_per_seat");

                System.out.println("Show ID: " + id + " | " + title +
                        " | Available Seats: " + availableSeats +
                        " | Price: ₹" + price);
            }
            System.out.println("---------------------------\n");

        } catch (SQLException e) {
            System.err.println("Failed to fetch events: " + e.getMessage());
        }
    }

    // 2. Phase 1: Temporary Seat Hold with Expiration (TTL)
    public static int holdSeats(int userId, int eventId, int seatsToBook, int holdDurationSeconds) {
        String checkSeatsQuery = "SELECT available_seats, price_per_seat FROM events WHERE event_id = ? FOR UPDATE";
        String updateSeatsQuery = "UPDATE events SET available_seats = available_seats - ? WHERE event_id = ?";
        String insertHoldQuery = "INSERT INTO bookings (user_id, event_id, seats_booked, total_amount, booking_status, hold_until) " +
                "VALUES (?, ?, ?, ?, 'HELD', DATE_ADD(NOW(), INTERVAL ? SECOND))";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            double pricePerSeat = 0;
            int availableSeats = 0;

            // Check seat availability with row-level lock
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSeatsQuery)) {
                checkStmt.setInt(1, eventId);
                ResultSet rs = checkStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Hold Failed: Event not found.");
                    conn.rollback();
                    return -1;
                }

                availableSeats = rs.getInt("available_seats");
                pricePerSeat = rs.getDouble("price_per_seat");

                if (availableSeats < seatsToBook) {
                    System.out.println("Hold Failed: Insufficient seats remaining (" + availableSeats + ").");
                    conn.rollback();
                    return -1;
                }
            }

            // Deduct available seats
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSeatsQuery)) {
                updateStmt.setInt(1, seatsToBook);
                updateStmt.setInt(2, eventId);
                updateStmt.executeUpdate();
            }

            // Insert booking record with HELD status and TTL
            double totalAmount = pricePerSeat * seatsToBook;
            int generatedBookingId = -1;

            try (PreparedStatement insertStmt = conn.prepareStatement(insertHoldQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, eventId);
                insertStmt.setInt(3, seatsToBook);
                insertStmt.setDouble(4, totalAmount);
                insertStmt.setInt(5, holdDurationSeconds);
                insertStmt.executeUpdate();

                ResultSet keys = insertStmt.getGeneratedKeys();
                if (keys.next()) {
                    generatedBookingId = keys.getInt(1);
                }
            }

            conn.commit();
            System.out.println(">>> [HOLD SUCCESS] Booking ID " + generatedBookingId +
                    " created for User " + userId + ". Seats held for " + holdDurationSeconds + " seconds.");
            return generatedBookingId;

        } catch (SQLException e) {
            System.err.println("Hold Transaction Error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 3. Phase 2: Confirm Payment and Finalize Booking
    public static boolean confirmBooking(int bookingId) {
        String verifyHoldQuery = "SELECT booking_status, hold_until FROM bookings WHERE booking_id = ? FOR UPDATE";
        String confirmQuery = "UPDATE bookings SET booking_status = 'CONFIRMED', hold_until = NULL WHERE booking_id = ?";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement verifyStmt = conn.prepareStatement(verifyHoldQuery)) {
                verifyStmt.setInt(1, bookingId);
                ResultSet rs = verifyStmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Confirm Failed: Booking record not found.");
                    conn.rollback();
                    return false;
                }

                String status = rs.getString("booking_status");
                Timestamp holdUntil = rs.getTimestamp("hold_until");

                if (!"HELD".equalsIgnoreCase(status)) {
                    System.out.println("Confirm Failed: Booking is already " + status + ".");
                    conn.rollback();
                    return false;
                }

                if (holdUntil != null && holdUntil.before(new Timestamp(System.currentTimeMillis()))) {
                    System.out.println("Confirm Failed: Hold window expired for Booking ID " + bookingId + ".");
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement confirmStmt = conn.prepareStatement(confirmQuery)) {
                confirmStmt.setInt(1, bookingId);
                confirmStmt.executeUpdate();
            }

            conn.commit();
            System.out.println(">>> [CONFIRMED] Booking ID " + bookingId + " payment verified and tickets confirmed!");
            return true;

        } catch (SQLException e) {
            System.err.println("Confirm Transaction Error: " + e.getMessage());
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 4. Backward Compatibility Wrapper (StressTest aur ConcurrencyTest ke liye)
    public static boolean bookTicket(int userId, int eventId, int seatsToBook) {
        int bookingId = holdSeats(userId, eventId, seatsToBook, 60);
        if (bookingId != -1) {
            return confirmBooking(bookingId);
        }
        return false;
    }
}