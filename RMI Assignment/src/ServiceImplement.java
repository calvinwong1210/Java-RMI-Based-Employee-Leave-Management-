import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;


public class ServiceImplement extends UnicastRemoteObject implements Service {

    private static final String FILE_PATH = "Leaves.txt";
    private static final Object LOCK = new Object();

    public ServiceImplement() throws RemoteException {
        super();
    }

    @Override
    public List<String[]> getData() throws RemoteException {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                rows.add(line.split(","));
            }
        } catch (IOException e) {
            System.err.println("Error reading file:");
            e.printStackTrace();
        }

        return rows;
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
                if (id.matches("L\\d{3}")) {
                    lastId = id;
                }
            }
        }

        int nextNum = 1;
        if (lastId != null) {
            nextNum = Integer.parseInt(lastId.substring(1)) + 1;
        }

        return String.format("L%03d", nextNum);
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