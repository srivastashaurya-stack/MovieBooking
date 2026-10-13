-- 1. Users Table (Ticket book karne wale)
CREATE TABLE users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Events Table (Shows/Concerts/Movies)
CREATE TABLE events (
                        event_id INT PRIMARY KEY AUTO_INCREMENT,
                        title VARCHAR(150) NOT NULL,
                        total_seats INT NOT NULL,
                        available_seats INT NOT NULL,
                        price_per_seat DECIMAL(10, 2) NOT NULL,
                        event_date DATETIME NOT NULL
);

-- 3. Bookings Table (Dono ka connection)
CREATE TABLE bookings (
                          booking_id INT PRIMARY KEY AUTO_INCREMENT,
                          user_id INT NOT NULL,
                          event_id INT NOT NULL,
                          seats_booked INT NOT NULL,
                          total_amount DECIMAL(10, 2) NOT NULL,
                          booking_status VARCHAR(20) DEFAULT 'CONFIRMED',
                          booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (user_id) REFERENCES users(user_id),
                          FOREIGN KEY (event_id) REFERENCES events(event_id)
);