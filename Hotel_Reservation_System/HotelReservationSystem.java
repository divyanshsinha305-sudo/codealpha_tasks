import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class HotelReservationSystem {

    static Scanner scanner = new Scanner(System.in);
    static Map<String, RoomType> roomTypes = new LinkedHashMap<>();
    static List<Reservation> reservations = new ArrayList<>();
    static final String DATA_FILE = "reservations.csv";
    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        initializeRoomTypes();
        loadReservations();
        while (true) {
            displayMenu();
            int choice = readInteger("Select option", 1, 7);
            switch (choice) {
                case 1:
                    displayRoomTypes();
                    break;
                case 2:
                    bookRoom();
                    break;
                case 3:
                    displayReservations();
                    break;
                case 4:
                    searchReservation();
                    break;
                case 5:
                    cancelReservation();
                    break;
                case 6:
                    saveReservations();
                    break;
                case 7:
                    saveReservations();
                    System.out.println("Exiting system. Goodbye.");
                    return;
                default:
                    System.out.println("Invalid selection. Choose a valid option.");
            }
        }
    }

    static void initializeRoomTypes() {
        roomTypes.put("SINGLE", new RoomType("SINGLE", "Single Room", 1, 3500, 8));
        roomTypes.put("DOUBLE", new RoomType("DOUBLE", "Double Room", 2, 5200, 6));
        roomTypes.put("DELUXE", new RoomType("DELUXE", "Deluxe Room", 3, 7800, 5));
        roomTypes.put("SUITE", new RoomType("SUITE", "Executive Suite", 4, 11200, 3));
    }

    static void displayMenu() {
        System.out.println();
        System.out.println("=======================================");
        System.out.println("         HOTEL RESERVATION SYSTEM      ");
        System.out.println("=======================================");
        System.out.println("1. View Available Room Types");
        System.out.println("2. Book a Room");
        System.out.println("3. View All Reservations");
        System.out.println("4. Search Reservation");
        System.out.println("5. Cancel Reservation");
        System.out.println("6. Save Reservations");
        System.out.println("7. Exit");
        System.out.println("=======================================");
    }

    static void displayRoomTypes() {
        System.out.println();
        System.out.println("Available Rooms");
        System.out.println("-----------------------------------------------------------");
        System.out.printf("%-12s %-16s %-10s %-10s %-10s%n", "Code", "Type", "Capacity", "Rate", "Available");
        System.out.println("-----------------------------------------------------------");
        for (RoomType type : roomTypes.values()) {
            int available = calculateAvailable(type.code);
            System.out.printf("%-12s %-16s %-10d %-10s %-10d%n",
                    type.code,
                    type.title,
                    type.capacity,
                    formatPrice(type.rate),
                    available);
        }
    }

    static void bookRoom() {
        System.out.println();
        displayRoomTypes();
        String code = readRoomCode("Enter room code to book");
        RoomType type = roomTypes.get(code);
        int available = calculateAvailable(code);
        if (available <= 0) {
            System.out.println("No rooms available for selected type.");
            return;
        }
        System.out.print("Enter guest name: ");
        String guestName = scanner.nextLine().trim();
        if (guestName.isEmpty()) {
            System.out.println("Guest name cannot be empty.");
            return;
        }
        System.out.print("Enter contact mobile: ");
        String mobile = scanner.nextLine().trim();
        System.out.print("Enter email address: ");
        String email = scanner.nextLine().trim();
        LocalDate checkInDate = readDate("Enter check-in date (yyyy-MM-dd)");
        int nights = readInteger("Enter number of nights", 1, 30);
        double discount = nights >= 5 ? 0.10 : 0.0;
        double amount = type.rate * nights * (1 - discount);
        int reservationId = nextReservationId();
        LocalDate checkOut = checkInDate.plusDays(nights);
        Reservation reservation = new Reservation(reservationId, guestName, mobile, email, type.code, nights, checkInDate, checkOut, amount, discount);
        reservations.add(reservation);
        System.out.println();
        System.out.println("Reservation confirmed successfully.");
        displayInvoice(reservation, type);
    }

    static void displayReservations() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }
        System.out.println();
        System.out.println("Existing Reservations");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("%-4s %-15s %-8s %-12s %-12s %-8s %-12s%n",
                "ID", "Guest", "Type", "Check-in", "Check-out", "Nights", "Amount");
        System.out.println("--------------------------------------------------------------------------------");
        for (Reservation reservation : reservations) {
            System.out.printf("%-4d %-15s %-8s %-12s %-12s %-8d %-12s%n",
                    reservation.id,
                    shorten(reservation.guestName, 15),
                    reservation.roomCode,
                    reservation.checkIn.format(DATE_FORMAT),
                    reservation.checkOut.format(DATE_FORMAT),
                    reservation.nights,
                    formatPrice(reservation.amount));
        }
    }

    static void searchReservation() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations available to search.");
            return;
        }
        int reservationId = readInteger("Enter reservation ID to search", 1, Integer.MAX_VALUE);
        Reservation reservation = findReservation(reservationId);
        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }
        RoomType type = roomTypes.get(reservation.roomCode);
        displayInvoice(reservation, type);
    }

    static void cancelReservation() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations to cancel.");
            return;
        }
        int reservationId = readInteger("Enter reservation ID to cancel", 1, Integer.MAX_VALUE);
        Reservation reservation = findReservation(reservationId);
        if (reservation == null) {
            System.out.println("Reservation not found.");
            return;
        }
        reservations.remove(reservation);
        System.out.println("Reservation " + reservationId + " has been cancelled.");
    }

    static void saveReservations() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Reservation reservation : reservations) {
                writer.println(reservation.toCsv());
            }
            System.out.println("Reservations saved to " + DATA_FILE + ".");
        } catch (IOException e) {
            System.out.println("Unable to save reservations.");
        }
    }

    static void loadReservations() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Reservation reservation = Reservation.fromCsv(line);
                if (reservation != null) {
                    reservations.add(reservation);
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to load existing reservations.");
        }
    }

    static Reservation findReservation(int id) {
        for (Reservation reservation : reservations) {
            if (reservation.id == id) {
                return reservation;
            }
        }
        return null;
    }

    static int calculateAvailable(String code) {
        RoomType type = roomTypes.get(code);
        if (type == null) {
            return 0;
        }
        int booked = 0;
        for (Reservation reservation : reservations) {
            if (reservation.roomCode.equals(code)) {
                booked++;
            }
        }
        return Math.max(0, type.totalRooms - booked);
    }

    static int nextReservationId() {
        int maxId = 0;
        for (Reservation reservation : reservations) {
            if (reservation.id > maxId) {
                maxId = reservation.id;
            }
        }
        return maxId + 1;
    }

    static String readRoomCode(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (roomTypes.containsKey(input)) {
                return input;
            }
            System.out.println("Invalid room code. Please choose one from the list.");
        }
    }

    static int readInteger(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt + " (" + min + "-" + max + "): ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("Enter a value between " + min + " and " + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd.");
            }
        }
    }

    static String formatPrice(double value) {
        return "Rs " + String.format("%,.0f", value);
    }

    static String shorten(String text, int limit) {
        return text.length() <= limit ? text : text.substring(0, limit - 3) + "...";
    }

    static void displayInvoice(Reservation reservation, RoomType type) {
        System.out.println();
        System.out.println("Reservation Invoice");
        System.out.println("----------------------------------------------");
        System.out.println("Reservation ID : " + reservation.id);
        System.out.println("Guest Name     : " + reservation.guestName);
        System.out.println("Contact        : " + reservation.mobile);
        System.out.println("Email          : " + reservation.email);
        System.out.println("Room Type      : " + type.title + " (" + type.code + ")");
        System.out.println("Capacity       : " + type.capacity + " person(s)");
        System.out.println("Rate per Night : " + formatPrice(type.rate));
        System.out.println("Check-in Date  : " + reservation.checkIn.format(DATE_FORMAT));
        System.out.println("Check-out Date : " + reservation.checkOut.format(DATE_FORMAT));
        System.out.println("Nights         : " + reservation.nights);
        System.out.println("Discount       : " + (int) (reservation.discount * 100) + "%");
        System.out.println("Total Amount   : " + formatPrice(reservation.amount));
        System.out.println("----------------------------------------------");
    }

    static class RoomType {
        String code;
        String title;
        int capacity;
        double rate;
        int totalRooms;

        RoomType(String code, String title, int capacity, double rate, int totalRooms) {
            this.code = code;
            this.title = title;
            this.capacity = capacity;
            this.rate = rate;
            this.totalRooms = totalRooms;
        }
    }

    static class Reservation {
        int id;
        String guestName;
        String mobile;
        String email;
        String roomCode;
        int nights;
        LocalDate checkIn;
        LocalDate checkOut;
        double amount;
        double discount;

        Reservation(int id, String guestName, String mobile, String email, String roomCode, int nights, LocalDate checkIn, LocalDate checkOut, double amount, double discount) {
            this.id = id;
            this.guestName = guestName;
            this.mobile = mobile;
            this.email = email;
            this.roomCode = roomCode;
            this.nights = nights;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.amount = amount;
            this.discount = discount;
        }

        String toCsv() {
            return id + "," + escape(guestName) + "," + mobile + "," + email + "," + roomCode + "," + nights + "," + checkIn.format(DATE_FORMAT) + "," + checkOut.format(DATE_FORMAT) + "," + amount + "," + discount;
        }

        static Reservation fromCsv(String line) {
            String[] parts = line.split(",", -1);
            if (parts.length != 10) {
                return null;
            }
            try {
                int id = Integer.parseInt(parts[0]);
                String guestName = unescape(parts[1]);
                String mobile = parts[2];
                String email = parts[3];
                String roomCode = parts[4];
                int nights = Integer.parseInt(parts[5]);
                LocalDate checkIn = LocalDate.parse(parts[6], DATE_FORMAT);
                LocalDate checkOut = LocalDate.parse(parts[7], DATE_FORMAT);
                double amount = Double.parseDouble(parts[8]);
                double discount = Double.parseDouble(parts[9]);
                return new Reservation(id, guestName, mobile, email, roomCode, nights, checkIn, checkOut, amount, discount);
            } catch (Exception e) {
                return null;
            }
        }

        static String escape(String value) {
            return value.replace(",", "<comma>");
        }

        static String unescape(String value) {
            return value.replace("<comma>", ",");
        }
    }
}
