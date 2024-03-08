import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {

    private static final String EMPLOYEE_CSV_FILE = "src/MotorPH Employee Data - Employee Details.csv";
    private static final String ATTENDANCE_CSV_FILE = "src/MotorPH Employee Data - Attendance Record.csv";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter Employee ID:");
        String employeeId = scanner.nextLine();

        // Read employee details
        String[] employeeDetails = readEmployeeDetails(employeeId);
        if (employeeDetails == null) {
            System.out.println("Employee not found.");
            return;
        }

        // Display employee details
        System.out.println("Last Name: " + employeeDetails[0]);
        System.out.println("First Name: " + employeeDetails[1]);
        System.out.println("Birthday: " + employeeDetails[2]);
        System.out.println("Address: " + employeeDetails[3]);
        System.out.println("Phone Number: " + employeeDetails[4]);

        // Prompt user to enter start date
        Date startDate = promptForStartDate();
        if (startDate == null) {
            System.out.println("Invalid start date.");
            return;
        }

        // Calculate hours worked
        double hoursWorked = calculateHoursWorked(employeeId, startDate);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DecimalFormat decimalFormat = new DecimalFormat("00");

        // Convert decimal hours to HH:mm format
        int hours = (int) hoursWorked;
        int minutes = (int) ((hoursWorked - hours) * 60);
        String formattedHoursWorked = decimalFormat.format(hours) + ":" + decimalFormat.format(minutes);

        System.out.println("Hours worked in a week starting from " + dateFormat.format(startDate) + ": " + formattedHoursWorked);

        // Display hourly rate
        double hourlyRate = readHourlyRate(employeeId);
        System.out.println("Hourly rate: ₱" + String.format("%.2f", hourlyRate));

        // Calculate pay
        double weeklySalary = calculateWeeklySalary(hourlyRate, hoursWorked);
        DecimalFormat currencyFormat = new DecimalFormat("#0.00");
        System.out.println("Weekly pay: ₱" + currencyFormat.format(weeklySalary));
    }

    private static String[] readEmployeeDetails(String employeeId) {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_CSV_FILE))) {
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[0].equals(employeeId)) {
                    return new String[]{parts[1], parts[2], parts[3], parts[4], parts[5]};
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static double readHourlyRate(String employeeId) {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_CSV_FILE))) {
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[0].equals(employeeId)) {
                    String hourlyRateString = parts[18].trim(); // Assuming hourly rate is in column 19 (index 18)
                    hourlyRateString = hourlyRateString.replace("\"", ""); // Remove quotation marks
                    try {
                        return Double.parseDouble(hourlyRateString);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid hourly rate format: " + hourlyRateString);
                        return 0.0; // Default hourly rate if parsing fails
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0; // Default hourly rate if not found
    }

    private static Date promptForStartDate() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter start date (MM/DD/YYYY):");
        String inputDate = scanner.nextLine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            return dateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static double calculateHoursWorked(String employeeId, Date startDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String line;
        double hoursWorked = 0;
        long weekStartMillis = startDate.getTime(); // Start of the week in milliseconds
        long weekEndMillis = weekStartMillis + (7 * 24 * 60 * 60 * 1000); // End of the week in milliseconds

        try (BufferedReader br = new BufferedReader(new FileReader(ATTENDANCE_CSV_FILE))) {
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6 && parts[0].equals(employeeId)) {
                    Date attendanceDate = dateFormat.parse(parts[3].trim());
                    long attendanceMillis = attendanceDate.getTime();

                    // Check if the attendance date falls within the week
                    if (attendanceMillis >= weekStartMillis && attendanceMillis < weekEndMillis) {
                        Date timeIn = new SimpleDateFormat("HH:mm").parse(parts[4]);
                        Date timeOut = new SimpleDateFormat("HH:mm").parse(parts[5]);
                        long timeDifference = timeOut.getTime() - timeIn.getTime();
                        hoursWorked += (double) timeDifference / (1000 * 60 * 60);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return hoursWorked;
    }

    private static double calculateWeeklySalary(double hourlyRate, double hoursWorkedPerWeek) {
        return hourlyRate * hoursWorkedPerWeek;
    }
}
