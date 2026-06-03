import java.io.*;
import java.util.*;

public class StudentGradeTracker {

    static ArrayList<String> names = new ArrayList<>();
    static ArrayList<Double> marks = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadData();

        while (true) {
            System.out.println("\n======================================");
            System.out.println("     STUDENT GRADE TRACKER SYSTEM");
            System.out.println("======================================");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Statistics Report");
            System.out.println("5. Save Data");
            System.out.println("6. Exit");
            System.out.println("======================================");

            System.out.print("Enter Choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    viewStudents();
                    break;
                case 3:
                    searchStudent();
                    break;
                case 4:
                    statistics();
                    break;
                case 5:
                    saveData();
                    break;
                case 6:
                    saveData();
                    System.out.println("Thank You!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    static void addStudent() {
        System.out.print("Enter Student Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Marks (0-100): ");
        double mark = sc.nextDouble();
        sc.nextLine();

        names.add(name);
        marks.add(mark);

        System.out.println("Student Added Successfully!");
    }

    static void viewStudents() {
        if (names.isEmpty()) {
            System.out.println("No Records Found.");
            return;
        }

        System.out.println("\n-------------------------------------------------------------");
        System.out.printf("%-20s %-10s %-10s %-10s%n", "Name", "Marks", "Grade", "Status");
        System.out.println("-------------------------------------------------------------");

        for (int i = 0; i < names.size(); i++) {
            String grade = calculateGrade(marks.get(i));
            String status = marks.get(i) >= 40 ? "PASS" : "FAIL";

            System.out.printf("%-20s %-10.2f %-10s %-10s%n",
                    names.get(i), marks.get(i), grade, status);
        }
    }

    static void searchStudent() {
        System.out.print("Enter Name to Search: ");
        String search = sc.nextLine();

        boolean found = false;

        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).equalsIgnoreCase(search)) {
                System.out.println("\nStudent Found");
                System.out.println("Name   : " + names.get(i));
                System.out.println("Marks  : " + marks.get(i));
                System.out.println("Grade  : " + calculateGrade(marks.get(i)));
                System.out.println("Status : " + (marks.get(i) >= 40 ? "PASS" : "FAIL"));
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Student Not Found!");
        }
    }

    static void statistics() {
        if (marks.isEmpty()) {
            System.out.println("No Data Available.");
            return;
        }

        double total = 0;
        double highest = marks.get(0);
        double lowest = marks.get(0);
        int topperIndex = 0;

        for (int i = 0; i < marks.size(); i++) {
            total += marks.get(i);
            if (marks.get(i) > highest) {
                highest = marks.get(i);
                topperIndex = i;
            }
            if (marks.get(i) < lowest) {
                lowest = marks.get(i);
            }
        }

        double average = total / marks.size();

        System.out.println("\n============== REPORT ==============");
        System.out.println("Total Students : " + names.size());
        System.out.println("Average Marks  : " + String.format("%.2f", average));
        System.out.println("Highest Marks  : " + highest);
        System.out.println("Lowest Marks   : " + lowest);
        System.out.println("Topper         : " + names.get(topperIndex) + " (" + highest + ")");
        System.out.println("====================================");
    }

    static String calculateGrade(double marks) {
        if (marks >= 90) return "A+";
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 40) return "D";
        return "F";
    }

    static void saveData() {
        try (FileWriter fw = new FileWriter("students.txt")) {
            for (int i = 0; i < names.size(); i++) {
                fw.write(names.get(i) + "," + marks.get(i) + "\n");
            }
            System.out.println("Data Saved Successfully!");
        } catch (Exception e) {
            System.out.println("Error Saving Data.");
        }
    }

    static void loadData() {
        try {
            File file = new File("students.txt");
            if (!file.exists()) return;
            try (Scanner fileReader = new Scanner(file)) {
                while (fileReader.hasNextLine()) {
                    String line = fileReader.nextLine();
                    String[] data = line.split(",");
                    if (data.length == 2) {
                        names.add(data[0]);
                        marks.add(Double.parseDouble(data[1]));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error Loading Data.");
        }
    }
}
