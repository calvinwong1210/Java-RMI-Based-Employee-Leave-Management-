import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServiceImplement extends UnicastRemoteObject implements Service {

    private static final String FILE_PATH = "Test.txt";

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
    
    private boolean isDuplicate(String ic, String email) {
        try (BufferedReader br = new BufferedReader(new FileReader("employees.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                String existingIC = data[2];
                String existingEmail = data[6];

                if (existingIC.equals(ic) || existingEmail.equals(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // file first time empty
        }
        return false;
    }

}