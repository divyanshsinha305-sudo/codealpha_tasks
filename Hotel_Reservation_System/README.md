Hotel Reservation System
========================

Overview
--------

The Hotel Reservation System is a console-based Java application created to manage room bookings, guest details, and reservation records. It provides a structured workflow for viewing room availability, creating new reservations, searching existing bookings, cancelling reservations, and storing data persistently.

Features
--------

- View available room types with capacity, rate, and availability
- Book rooms with guest details, contact information, and stay duration
- Generate reservation invoices with check-in, check-out, and total amount
- Search for existing reservations by reservation ID
- Cancel reservations and update availability automatically
- Save and load reservation data from a persistent CSV file

Requirements
------------

- Java Development Kit (JDK) 17 or later
- Terminal or IDE with Java support

Usage
-----

1. Change directory to the project folder:

   ```bash
   cd codealpha_tasks/Hotel_Reservation_System
   ```

2. Compile the application:

   ```bash
   javac HotelReservationSystem.java
   ```

3. Execute the program:

   ```bash
   java HotelReservationSystem
   ```

Project Structure
-----------------

- `HotelReservationSystem.java`: main source file containing the reservation system logic
- `reservations.csv`: runtime data file used for saving and loading reservation records
- `README.md`: project documentation and usage instructions

System Workflow
---------------

- The application initializes room categories and loads any saved reservations at startup.
- Users interact with a numeric menu to perform operations.
- Bookings are stored in memory and can be persisted using the save option.
- Reservations are retained in `reservations.csv` so data is available in later sessions.

Notes
-----

Compile and run the program from the folder containing `HotelReservationSystem.java`. The data file `reservations.csv` is generated automatically when reservations are saved for the first time.