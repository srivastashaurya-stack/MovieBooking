# High-Concurrency Movie Booking Engine

A robust, production-grade ticket booking backend engine built in pure Java and MySQL. The system solves concurrent race conditions (such as double-booking) and implements a 2-phase booking lifecycle with automatic TTL-based seat release.

---

## Key Highlights & System Architecture

- **Pessimistic Locking & ACID Compliance:** Utilizes MySQL row-level locks (`SELECT ... FOR UPDATE`) within explicit transaction boundaries (`setAutoCommit(false)`) to eliminate race conditions under heavy concurrent traffic.
- **2-Phase Booking Pattern (BookMyShow Style):**
    - **Phase 1 (Hold):** Temporarily reserves seats and assigns a configurable TTL (`hold_until` timestamp).
    - **Phase 2 (Confirmation):** Verifies hold validity and converts status to `CONFIRMED` upon payment.
- **Background Auto-Release Worker:** Employs a Java `ScheduledExecutorService` that periodically scans for expired hold windows, marks them as `EXPIRED`, and automatically restores seat inventory back to the show.
- **High-Performance Connection Pooling:** Integrated HikariCP for minimal connection overhead and optimal thread reuse.
- **Interactive CLI & Automated Test Suites:** Includes automated concurrency stress tests and an interactive console menu.

---

## Tech Stack

- **Language:** Java 17+ (Multithreading, Concurrency Utilities, JDBC)
- **Database:** MySQL 8.0 (InnoDB engine, Transactions, Row-level Locks)
- **Connection Pool:** HikariCP 5.1.0
- **Logging Facade:** SLF4J 2.0.9

---

## Database Schema Design

```sql
CREATE DATABASE IF NOT EXISTS movie_booking_db;
USE movie_booking_db;

CREATE TABLE events (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    price_per_seat DECIMAL(10, 2) NOT NULL
);

CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    seats_booked INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    booking_status ENUM('HELD', 'CONFIRMED', 'EXPIRED') DEFAULT 'HELD',
    hold_until TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(event_id)
);