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
                                 String email, String password)
            throws RemoteException {

        String empID = generateEmployeeID();   // 自动生成
        String joinDate = getTodayDate();     // 自动今天

        try (BufferedWriter bw =
             new BufferedWriter(new FileWriter("employees.txt", true))) {

            bw.write(empID + ";" +
                     name + ";" +
                     ic + ";" +
                     gender + ";" +
                     dob + ";" +
                     dept + ";" +
                     email + ";" +
                     password + ";" +
                     joinDate);

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
}
