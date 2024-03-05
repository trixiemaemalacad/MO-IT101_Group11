import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Employee {
    private static final String CSV_FILE_PATH = "src/MotorPH Employee Data - Employee Details.csv";
    private static final Map<Integer, String[]> employeeData = new HashMap<>();

    public static void main(String[] args) throws IOException {
        loadEmployeeDataFromCSV();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter employee ID: ");
        int employeeID = scanner.nextInt();

        String[] employeeDetails = employeeData.get(employeeID);
        if (employeeDetails != null) {
            System.out.println("Employee Last Name: " + employeeDetails[0]); // Last name
            System.out.println("Employee First Name: " + employeeDetails[1]); // First name
            System.out.println("Employee Birthday: " + employeeDetails[2]); // Birthday
            System.out.println("Employee Address: " + employeeDetails[3]); // Address
            System.out.println("Employee Phone Number: " + employeeDetails[4]); // Phone number
        } else {
            System.out.println("Employee not found for ID: " + employeeID);
        }

        scanner.close();
    }

    private static void loadEmployeeDataFromCSV() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH));
        String line;
        int lineCount = 0; // Track line number for debugging
        while ((line = br.readLine()) != null) {
            lineCount++;
            if (lineCount == 1) {
                // Skip header line
                continue;
            }
            String[] parts = line.split(",", 19); // Split line into maximum of 19 parts
            if (parts.length >= 5) { // Assuming at least five columns (ID, last name, first name, birthday, address, phone number)
                int employeeID = Integer.parseInt(parts[0]);
                String[] employeeDetails = {parts[1], parts[2], parts[3], parts[4], parts[5]}; // Last name, first name, birthday, address, phone number
                employeeData.put(employeeID, employeeDetails);
            } else {
                System.err.println("Invalid format on line " + lineCount + ": " + line);
            }
        }
        br.close();
    }
}
