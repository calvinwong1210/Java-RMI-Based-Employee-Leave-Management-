import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Client {
    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Service service = (Service) registry.lookup("Service");
            System.out.println("Connected to RMI server successfully.");

            // ─── ONLY CHANGE THIS LINE to switch what opens ───
            String mode = "yearly-report";   // options: "yearly-report", "leave-management", "apply-leave", "none"

            SwingUtilities.invokeLater(() -> {
                switch (mode.toLowerCase()) {
                    case "yearly-report":
                        YearlySummaryReportGUI report = new YearlySummaryReportGUI(null, service);
                        report.setVisible(true);
                        report.setLocationRelativeTo(null);
                        break;

                    case "leave-management":
                        LeaveManagementGUI lm = new LeaveManagementGUI(service);
                        lm.setVisible(true);
                        lm.setLocationRelativeTo(null);
                        break;

                    case "apply-leave":
                        // Example: open apply form for a specific employee (change ID as needed)
                        String testEmployeeId = "E001";
                        ApplyLeaveFormGUI apply = new ApplyLeaveFormGUI(testEmployeeId, service);
                        apply.setVisible(true);
                        apply.setLocationRelativeTo(null);
                        break;

                    case "none":
                    default:
                        System.out.println("No GUI opened (test mode)");
                        break;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Cannot connect to RMI Server.\nPlease run Server.java first.\n\nError: " + e.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
