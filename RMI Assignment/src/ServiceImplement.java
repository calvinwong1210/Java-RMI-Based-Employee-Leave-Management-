import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServiceImplement extends UnicastRemoteObject implements Service {

    private static final String FILE_PATH = "Leaves.txt";
    private static final Object LOCK = new Object();


    public ServiceImplement() throws RemoteException {
        super();
    }
    
    @Override
    public void registerEmployee(String name, String ic, String gender,
                                 String dob, String dept,
                                 String email, String password,
                                 String phone, String familyName,
                                 String relationship, String familyPhone,
                                 int leaveBalance)
                                 throws RemoteException {

        String empID = generateEmployeeID();   // 自动生成
        String joinDate = getTodayDate();     // 自动今天

        try (BufferedWriter bw =
             new BufferedWriter(new FileWriter("employees.txt", true))) {

            bw.write(empID + ";" + name + ";" + ic + ";" +
                    gender + ";" + dob + ";" + dept + ";" +
                    email + ";" + password + ";" +
                    phone + ";" + familyName + ";" +
                    relationship + ";" + familyPhone + ";" +
                    leaveBalance);

            bw.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String generateEmployeeID() {
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("employees.txt"))) {
            while (br.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            // file not exist first time
        }

        return String.format("E%04d", count + 1);  // E0001
    }

    private String getTodayDate() {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date());
    }
    
    @Override
    public String applyLeave(String employeeId, String description, String startDate, String endDate)
            throws RemoteException {
        // Basic validation (server-side)
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new RemoteException("Employee ID is required.");
        }
        if (description == null) description = "";
        if (startDate == null || startDate.trim().isEmpty() || endDate == null || endDate.trim().isEmpty()) {
            throw new RemoteException("Start/End date is required.");
        }

        String status = "Waiting";

        synchronized (LOCK) {
            try {
                ensureFileExists(FILE_PATH);

                String nextLeaveId = generateNextLeaveId(FILE_PATH);

                // Line format: L001;E001;Description;17/1/2026;20/1/2026;Waiting
                String line = String.join(";",
                        nextLeaveId,
                        employeeId,
                        description.replace(";", ",").replace("\r", " ").replace("\n", " "), // avoid breaking format
                        startDate,
                        endDate,
                        status
                );

                appendLine(FILE_PATH, line);

                return nextLeaveId;
            } catch (IOException e) {
                throw new RemoteException("Failed to save leave. " + e.getMessage(), e);
            }
        }
    }

    private static void ensureFileExists(String path) throws IOException {
        File f = new File(path);
        if (!f.exists()) {
            // create empty file
            try (FileOutputStream fos = new FileOutputStream(f)) {
                // no-op
            }
        }
    }

    private static void appendLine(String path, String line) throws IOException {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(path, true), StandardCharsets.UTF_8);
             BufferedWriter bw = new BufferedWriter(w)) {
            bw.write(line);
            bw.newLine();
        }
    }

    // Reads last valid LeaveID and increments it: L001 -> L002
    private static String generateNextLeaveId(String path) throws IOException {
        String lastId = null;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Expect: L###;...
                String[] parts = line.split(";");
                if (parts.length < 1) continue;

                String id = parts[0].trim();
                if (id.matches("L\\d{4}")) {
                   lastId = id;
                }
            }
        }

        int nextNum = 1;
        if (lastId != null) {
            nextNum = Integer.parseInt(lastId.substring(1)) + 1;
        }

        return String.format("L%04d", nextNum);
    }
    
    
    @Override
    public List<String[]> getLeavesByEmployee(String empId) throws RemoteException {
    List<String[]> rows = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader("leaves.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(";");
            // Expected: leaveId;empId;type;start;end;status
            if (parts.length < 6) continue;

            if (empId.equals(parts[1].trim())) {
                rows.add(new String[]{
                    parts[0].trim(), parts[1].trim(), parts[2].trim(),
                    parts[3].trim(), parts[4].trim(), parts[5].trim()
                });
            }
        }
    } catch (IOException e) {
        throw new RemoteException("File read error: " + e.getMessage(), e);
    }

    return rows;
}
 
    //PayrollManagement_Add
    @Override    
    public boolean isEmployeeExist(String empID) throws RemoteException {
        try (BufferedReader br = new BufferedReader(new FileReader("employees.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data[0].equals(empID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RemoteException("Error reading employee file", e);
        }
        return false;
    }
    
    @Override
    public boolean isPayrollExist(String empID) throws RemoteException {
        try (BufferedReader br = new BufferedReader(new FileReader("payroll.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data[0].equals(empID)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            return false; 
        } catch (IOException e) {
            throw new RemoteException("Error reading payroll file", e);
        }
        return false;
    }

    @Override
    public void addPayroll(String empID, double basic, double allowance, double ot,
                           double deduction, double total) throws RemoteException {
        if (!isEmployeeExist(empID)) {
            throw new RemoteException("Employee ID not found!");
        }
        if (isPayrollExist(empID)) {
            throw new RemoteException("Payroll already exists for this employee!");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("payroll.txt", true))) {
            bw.write(empID + ";" + basic + ";" + allowance + ";" + ot + ";" + deduction + ";" + total);
            bw.newLine();
        } catch (IOException e) {
            throw new RemoteException("Error saving payroll", e);
        }
    }
 
    //PayrollManagement_Edit
    @Override
    public String[] getPayroll(String empID) throws RemoteException {
        try (BufferedReader br = new BufferedReader(new FileReader("payroll.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data[0].equals(empID)) {
                    return data; // [empID, basic, allowance, ot, deduction, total]
                }
            }
        } catch (IOException e) {
            throw new RemoteException("Error reading payroll file", e);
        }
        return null; 
    }

    @Override
    public void updatePayroll(String empID, double basic, double allowance, double ot, double deduction, double total) throws RemoteException {
        File inputFile = new File("payroll.txt");
        File tempFile = new File("temp_payroll.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data[0].equals(empID)) {
                    line = empID + ";" + basic + ";" + allowance + ";" + ot + ";" + deduction + ";" + total;
                    found = true;
                }
                bw.write(line);
                bw.newLine();
            }

            if (!found) {
                throw new RemoteException("Employee ID not found!");
            }

        } catch (IOException e) {
            throw new RemoteException("Error updating payroll file", e);
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    @Override
    public void deletePayroll(String empID) throws RemoteException {
        File inputFile = new File("payroll.txt");
        File tempFile = new File("temp_payroll.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean found = false;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (!data[0].equals(empID)) {
                    bw.write(line);
                    bw.newLine();
                } else {
                    found = true;
                }
            }

            if (!found) {
                throw new RemoteException("Employee ID not found!");
            }

        } catch (IOException e) {
            throw new RemoteException("Error deleting payroll file", e);
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    // PayrollManagement_UpdateSalary
    @Override
    public String[] getSalaryStatus(String empID, String month, int year) throws RemoteException {
        File file = new File("payroll_status.txt");
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 5 &&
                    data[0].equals(empID) &&
                    data[2].equals(month) &&
                    data[4].equals(String.valueOf(year))) {
                    return data;  // [empID, salary, month, status, year]
                }
            }
        } catch (IOException e) {
            throw new RemoteException("Error reading payroll_status file", e);
        }
        return null;
    }

    @Override
    public void saveSalaryStatus(String empID, double salary, String month, String status, int year) throws RemoteException {
        File file = new File("payroll_status.txt");
        File tempFile = new File("temp_status.txt");
        boolean recordExists = false;
        boolean isPaid = false;

        try {
            if (!file.exists()) {
                // First record → append directly
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                    bw.write(empID + ";" + salary + ";" + month + ";" + status + ";" + year);
                    bw.newLine();
                }
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(file));
                 BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

                String line;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split(";");
                    if (data.length >= 5 &&
                        data[0].equals(empID) &&
                        data[2].equals(month) &&
                        data[4].equals(String.valueOf(year))) {

                        recordExists = true;
                        if (data[3].equalsIgnoreCase("Paid")) {
                            isPaid = true;
                            bw.write(line);  // keep original if already paid
                        } else {
                            // update
                            bw.write(empID + ";" + salary + ";" + month + ";" + status + ";" + year);
                        }
                    } else {
                        bw.write(line);
                    }
                    bw.newLine();
                }

                // If no matching record → append new one
                if (!recordExists) {
                    bw.write(empID + ";" + salary + ";" + month + ";" + status + ";" + year);
                    bw.newLine();
                }
            }

            if (!file.delete() || !tempFile.renameTo(file)) {
                throw new RemoteException("Failed to replace payroll_status file");
            }

            if (isPaid) {
                throw new RemoteException("This month's salary is already PAID. Status cannot be changed.");
            }

        } catch (IOException e) {
            throw new RemoteException("Error saving payroll status", e);
        }
    }

    @Override
    public void deleteSalaryStatus(String empID, String month, int year) throws RemoteException {
        File file = new File("payroll_status.txt");
        if (!file.exists()) {
            throw new RemoteException("No payroll status records exist");
        }

        File tempFile = new File("temp_status.txt");
        boolean found = false;
        boolean isPaid = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file));
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 5 &&
                    data[0].equals(empID) &&
                    data[2].equals(month) &&
                    data[4].equals(String.valueOf(year))) {

                    found = true;
                    if (data[3].equalsIgnoreCase("Paid")) {
                        isPaid = true;
                        bw.write(line);  // cannot delete paid record
                    }
                    // else: skip line → effectively delete
                } else {
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new RemoteException("Error processing delete operation", e);
        }

        if (!file.delete() || !tempFile.renameTo(file)) {
            throw new RemoteException("Failed to replace payroll_status file after delete");
        }

        if (isPaid) {
            throw new RemoteException("Cannot delete a record with status PAID");
        }

        if (!found) {
            throw new RemoteException("No matching record found for deletion");
        }
    }

    //PayrollManagement_ViewSalary
    @Override
    public List<String[]> getAllSalaryStatusForEmployee(String empID) throws RemoteException {
        List<String[]> records = new ArrayList<>();
        File file = new File("payroll_status.txt");

        if (!file.exists()) {
            return records; // empty list if no file
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 5 && data[0].equals(empID)) {
                    // Return: [empID, salary, month, status, year]
                    records.add(new String[]{
                        data[0], data[1], data[2], data[4], data[3]
                    });
                }
            }
        } catch (IOException e) {
            throw new RemoteException("Error reading payroll_status.txt", e);
        }

        return records;
    }
}

