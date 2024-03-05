import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Attendance {

    private static final String CSV_FILE_PATH = "src/MotorPH Employee Data - Attendance Record.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter employee number: ");
        int employeeNumber = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            List<String[]> employeeRecords = loadEmployeeRecordsFromCSV();
            String[] employeeInfo = getEmployeeInfo(employeeRecords, employeeNumber);
            if (employeeInfo != null) {
                System.out.println("Last Name: " + employeeInfo[1]);
                System.out.println("First Name: " + employeeInfo[2]);
            } else {
                System.out.println("Employee not found for number: " + employeeNumber);
                return;
            }

            System.out.print("Enter start date (MM/dd/yyyy): ");
            String startDateStr = scanner.nextLine();

            System.out.print("Enter end date (MM/dd/yyyy): ");
            String endDateStr = scanner.nextLine();

            double totalHoursWorked = calculateTotalHoursWorked(employeeRecords, employeeNumber, startDateStr, endDateStr);
            System.out.println("Total hours worked for employee #" + employeeNumber + " in the week: " + totalHoursWorked);
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    private static List<String[]> loadEmployeeRecordsFromCSV() throws IOException {
        List<String[]> records = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH));
        String line;
        br.readLine(); // Skip header line
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            records.add(parts);
        }
        br.close();
        return records;
    }

    private static double calculateTotalHoursWorked(List<String[]> records, int employeeNumber, String startDateStr, String endDateStr) {
        double totalHoursWorked = 0.0;
        for (String[] record : records) {
            int recordEmployeeNumber = Integer.parseInt(record[0]);
            if (recordEmployeeNumber == employeeNumber && isDateInRange(record[3], startDateStr, endDateStr)) {
                totalHoursWorked += calculateHoursDifference(record[4], record[5]);
            }
        }
        return totalHoursWorked;
    }

    private static boolean isDateInRange(String recordDateStr, String startDateStr, String endDateStr) {
        return recordDateStr.compareTo(startDateStr) >= 0 && recordDateStr.compareTo(endDateStr) <= 0;
    }

    private static double calculateHoursDifference(String timeInStr, String timeOutStr) {
        String[] timeInParts = timeInStr.split(":");
        String[] timeOutParts = timeOutStr.split(":");
        int hoursIn = Integer.parseInt(timeInParts[0]);
        int minutesIn = Integer.parseInt(timeInParts[1]);
        int hoursOut = Integer.parseInt(timeOutParts[0]);
        int minutesOut = Integer.parseInt(timeOutParts[1]);
        return (hoursOut - hoursIn) + (minutesOut - minutesIn) / 60.0;
    }

    private static String[] getEmployeeInfo(List<String[]> records, int employeeNumber) {
        for (String[] record : records) {
            int recordEmployeeNumber = Integer.parseInt(record[0]);
            if (recordEmployeeNumber == employeeNumber) {
                return new String[]{record[0], record[1], record[2]};
            }
        }
        return null;
    }
}
