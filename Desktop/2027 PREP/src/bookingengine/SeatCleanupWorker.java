package bookingengine;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SeatCleanupWorker {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void startAutoReleaseService() {
        // Runs every 3 seconds in the background
        scheduler.scheduleAtFixedRate(this::releaseExpiredSeats, 2, 3, TimeUnit.SECONDS);
        System.out.println(">>> Seat Auto-Release Worker initialized and running...");
    }

    private void releaseExpiredSeats() {
        String findExpiredQuery = "SELECT booking_id, event_id, seats_booked FROM bookings " +
                "WHERE booking_status = 'HELD' AND hold_until < NOW() FOR UPDATE";
        String restoreSeatsQuery = "UPDATE events SET available_seats = available_seats + ? WHERE event_id = ?";
        String markExpiredQuery = "UPDATE bookings SET booking_status = 'EXPIRED' WHERE booking_id = ?";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement findStmt = conn.prepareStatement(findExpiredQuery);
                 ResultSet rs = findStmt.executeQuery()) {

                while (rs.next()) {
                    int bookingId = rs.getInt("booking_id");
                    int eventId = rs.getInt("event_id");
                    int seatsToRestore = rs.getInt("seats_booked");

                    // 1. Restore seats to events table
                    try (PreparedStatement restoreStmt = conn.prepareStatement(restoreSeatsQuery)) {
                        restoreStmt.setInt(1, seatsToRestore);
                        restoreStmt.setInt(2, eventId);
                        restoreStmt.executeUpdate();
                    }

                    // 2. Mark booking as EXPIRED
                    try (PreparedStatement expireStmt = conn.prepareStatement(markExpiredQuery)) {
                        expireStmt.setInt(1, bookingId);
                        expireStmt.executeUpdate();
                    }

                    System.out.println(">>> [AUTO-RELEASE] Booking ID " + bookingId +
                            " expired! Restored " + seatsToRestore + " seat(s) for Event ID " + eventId);
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
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

    public void stopService() {
        scheduler.shutdown();
    }
}
