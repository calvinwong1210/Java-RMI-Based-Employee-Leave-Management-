import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ServiceImplement extends UnicastRemoteObject implements Service {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

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
                                 String relationship, String familyPhone)
                                 throws RemoteException {

        String empID = generateEmployeeID();   // 自动生成
        String joinDate = getTodayDate();     // 自动今天

        try (BufferedWriter bw =
             new BufferedWriter(new FileWriter("employees.txt", true))) {

            bw.write(empID + ";" + name + ";" + ic + ";" +
                    gender + ";" + dob + ";" + dept + ";" +
                    email + ";" + password + ";" +
                    phone + ";" + familyName + ";" +
                    relationship + ";" + familyPhone);

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
    
@Override
public String[] login(String employeeID, String password) throws RemoteException {
    String FILE_PATH = "employees.txt";

    try (Scanner sc = new Scanner(new File(FILE_PATH))) {
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(";");
            if (parts.length < 9) continue;

            String existingIC = parts[0].trim();
            String existingPassword = parts[7].trim();
            String department = parts[5].trim();

            if (employeeID.equals(existingIC)) {
                if (password.equals(existingPassword)) {
                    return new String[] {
                        parts[0].trim(), // empID
                        parts[1].trim(), // name
                        department,      // department
                        "SUCCESS"
                    };
                } else {
                    return new String[] { "", "", "", "Wrong Password" };
                }
            }
        }
    } catch (Exception e) {
        throw new RemoteException("Error reading employees file", e);
    }

    return new String[] { "", "", "", "EmployeeID Not Found" };
}
    
    private String getTodayDate() {
        java.text.SimpleDateFormat sdf =
                new java.text.SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date());
    }
    
    @Override
    public synchronized String applyLeave(String employeeId, String description, String startDate, String endDate)
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
    public int getAvailableLeaveForApplication(String empId, int year) throws RemoteException {
    int reservedDays = 0;
    try (BufferedReader br = new BufferedReader(new FileReader("leaves.txt"))) {
        String line;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(";");
            if (parts.length < 6) continue;
            String status = parts[5].trim();

            if (!empId.equals(parts[1].trim())) continue;
            LocalDate startDate = LocalDate.parse(parts[3].trim(), formatter);
            LocalDate endDate = LocalDate.parse(parts[4].trim(), formatter);

            if (startDate.getYear() != year) continue;

            if (status.equalsIgnoreCase("Approved") ||
                status.equalsIgnoreCase("Waiting")) {

                long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
                reservedDays += (int) days;
            }
        }
    } catch (IOException e) {
        throw new RemoteException("File read error: " + e.getMessage(), e);
    } catch (Exception e) {
        throw new RemoteException("Error calculating available leave: " + e.getMessage(), e);
    }

    int available = 10 - reservedDays;
    return Math.max(available, 0);
}
    @Override
    public List<String[]> getLeavesByEmployee(String empId,int year) throws RemoteException {
    List<String[]> rows = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader("leaves.txt"))) {
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(";");
            // Expected: leaveId;empId;type;start;end;status
            if (parts.length < 6) continue;
            LocalDate startdate = LocalDate.parse(parts[3].trim(), formatter);
            if (empId.equals(parts[1].trim()) && startdate.getYear() == year ) {
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

    @Override
    public List<String[]> getAllLeaves() throws RemoteException {
        List<String[]> rows = new ArrayList<>();

        synchronized (LOCK) {
            File f = new File(FILE_PATH);
            if (!f.exists()) {
                return rows; // no file yet -> return empty
            }

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(FILE_PATH), StandardCharsets.UTF_8))) {

                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    // Format: L001;E001;Desc;17/1/2026;20/1/2026;Waiting
                    String[] parts = line.split(";", -1);
                    rows.add(parts);
                }

            } catch (IOException e) {
                throw new RemoteException("Failed to read leaves file: " + e.getMessage(), e);
            }
        }
            

        return rows;
    }
// Helper: read employee profile by ID
private String[] getEmployeeById(String employeeId) throws IOException {
    ensureFileExists("employees.txt");
    
    try (BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream("employees.txt"), StandardCharsets.UTF_8))) {
        
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            String[] parts = line.split(";", -1);
            if (parts.length >= 5 && parts[0].trim().equalsIgnoreCase(employeeId.trim())) {
                return parts;
            }
        }
    }
    return null; // not found
}

@Override
public String[] getEmployeeProfile(String employeeId) throws RemoteException {
    try {
        String[] data = getEmployeeById(employeeId);
        if (data == null) {
            return new String[]{"Not found", employeeId, "", "", "", "", "", "", ""};
        }
        return data;
    } catch (IOException e) {
        throw new RemoteException("Cannot read employees file", e);
    }
}
// ─── Replace your existing extractYear method with this one ────────────────────────────────
private int extractYear(String dateStr) {
    if (dateStr == null || dateStr.trim().isEmpty()) {
        return -1;
    }
    dateStr = dateStr.trim();

    // Handle dd/MM/yyyy or d/M/yyyy or dd/M/yy etc.
    String[] parts = dateStr.split("/");
    if (parts.length == 3) {
        try {
            // year is the third part
            String yearPart = parts[2].trim();
            int year = Integer.parseInt(yearPart);
            // basic sanity check
            if (year >= 2000 && year <= 2099) {
                return year;
            }
        } catch (NumberFormatException ignored) {
        }
    }

    // fallback - try YYYY-MM-DD
    parts = dateStr.split("-");
    if (parts.length == 3) {
        try {
            int year = Integer.parseInt(parts[0].trim());
            if (year >= 2000 && year <= 2099) {
                return year;
            }
        } catch (NumberFormatException ignored) {
        }
    }

    return -1;   // invalid → won't match any year
}

// ─── Replace your existing generateYearlyEmployeeReport method with this ───────────────────

public String getYearlyEmployeeReport(String employeeId, int year) throws RemoteException {
    StringBuilder sb = new StringBuilder();

    sb.append("═══════════════════════════════════════════════════════════════\n");
    sb.append("          YEARLY EMPLOYEE SUMMARY REPORT\n");
    sb.append("═══════════════════════════════════════════════════════════════\n\n");

    // ─── 1. Employee Profile ────────────────────────────────────────────────────────────────
    sb.append("1. Employee Profile & Family Details\n");
    sb.append("────────────────────────────────────\n");

    String[] emp = null;
    try {
        emp = getEmployeeById(employeeId);
    } catch (Exception e) {
        sb.append("Error reading employee data: ").append(e.getMessage()).append("\n\n");
    }

    // Check for length 12 to include: Phone, Fam Name, Relation, Fam Phone
    if (emp != null && emp.length >= 12) {
        sb.append(String.format("Employee ID       : %s%n", emp[0].trim()));
        sb.append(String.format("Full Name         : %s %s%n", emp[1].trim(), emp[2].trim()));
        sb.append(String.format("Gender            : %s%n", emp[3].trim()));
        sb.append(String.format("Date of Birth     : %s%n", emp[4].trim()));
        sb.append(String.format("Department        : %s%n", emp[5].trim()));
        sb.append(String.format("Email             : %s%n", emp[6].trim()));
        
        // --- NEW COLUMNS ADDED HERE ---
        sb.append(String.format("Phone Number      : %s%n", emp[8].trim()));
        sb.append(String.format("Family Name       : %s%n", emp[9].trim()));
        sb.append(String.format("Relationship      : %s%n", emp[10].trim()));
        sb.append(String.format("Family Phone      : %s%n", emp[11].trim()));
        // ------------------------------
        
    } else if (emp != null) {
        sb.append(String.format("Employee ID       : %s%n", emp[0].trim()));
        sb.append(String.format("Full Name         : %s %s%n", emp[1].trim(), emp[2].trim()));
        sb.append("Note: Family and contact details are missing in the data file.\n");
    } else {
        sb.append("Employee profile not found for ID: ").append(employeeId).append("\n");
    }
    sb.append("\n");

    // ─── 2. Leave History ───────────────────────────────────────────────────────────────────
    sb.append("2. Leave History for Year ").append(year).append("\n");
    sb.append("───────────────────────────────────────────────────────────────\n");
    sb.append(String.format("%-8s %-35s %-11s %-11s %s%n",
            "LeaveID", "Description", "Start", "End", "Status"));
    sb.append("──────── ───────────────────────────────────── ─────────── ─────────── ────────\n");

    int count = 0;
    try {
        List<String[]> leaves = getAllLeaves();
        for (String[] leave : leaves) {
            if (leave.length < 6) continue;

            String leaveEmpId = leave[1].trim();
            if (!leaveEmpId.equalsIgnoreCase(employeeId.trim())) {
                continue;
            }

            String startDate = leave[3].trim();
            int leaveYear = extractYear(startDate);

            if (leaveYear == year) {
                count++;
                String desc = leave[2].trim();
                if (desc.length() > 32) {
                    desc = desc.substring(0, 29) + "...";
                }

                sb.append(String.format("%-8s %-35s %-11s %-11s %s%n",
                        leave[0].trim(),
                        desc,
                        startDate,
                        leave[4].trim(),
                        leave[5].trim()));
            }
        }
    } catch (Exception e) {
        sb.append("Error reading leave records: ").append(e.getMessage()).append("\n");
    }

    if (count == 0) {
        sb.append("No leave records found for year ").append(year).append("\n");
    } else {
        sb.append("\nTotal leave records found: ").append(count).append("\n");
    }

    sb.append("\n═══════════════════════════════════════════════════════════════\n");
    sb.append("                        End of Report\n");

    return sb.toString();
}
@Override
public List<String[]> getAllEmployees() throws RemoteException {
    List<String[]> employees = new ArrayList<>();

    File file = new File("employees.txt");
    if (!file.exists()) {
        return employees; // return empty list if file missing
    }

    try (BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(";", -1);
            // We only need ID + names, but we can keep the whole row
            if (parts.length >= 3) {  // at least ID, FirstName, LastName
                employees.add(parts);
            }
        }
    } catch (IOException e) {
        throw new RemoteException("Failed to read employees.txt: " + e.getMessage(), e);
    }

    return employees;
}
@Override
    public void updateLeaveStatus(String leaveId, String newStatus) throws RemoteException {
        if (leaveId == null || leaveId.trim().isEmpty() || newStatus == null || newStatus.trim().isEmpty()) {
            throw new RemoteException("Invalid leave ID or status");
        }

        synchronized (LOCK) {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                throw new RemoteException("Leaves file not found");
            }

            List<String> lines = new ArrayList<>();
            boolean found = false;

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(";", -1);
                    if (parts.length >= 6 && parts[0].trim().equalsIgnoreCase(leaveId.trim())) {
                        // Update status (last field)
                        parts[5] = newStatus.trim();
                        line = String.join(";", parts);
                        found = true;
                    }
                    lines.add(line);
                }
            } catch (IOException e) {
                throw new RemoteException("Failed to read leaves file", e);
            }

            if (!found) {
                throw new RemoteException("Leave ID " + leaveId + " not found");
            }

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                for (String updatedLine : lines) {
                    writer.write(updatedLine + "\n");
                }
            } catch (IOException e) {
                throw new RemoteException("Failed to update leaves file", e);
            }
        }
    }
    
}
