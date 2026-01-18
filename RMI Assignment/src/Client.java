import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Client {

    public static void main(String[] args) {
     try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            Service service =
                    (Service) registry.lookup("Service");
            String loggedInEmployeeId = "E001";

        SwingUtilities.invokeLater(() -> {
            // Replace ApplyLeaveFrame with your actual JFrame class name
            ViewLeavesGUI frame = new ViewLeavesGUI(loggedInEmployeeId, service);
            frame.setVisible(true);
        });
     }
     
     catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Cannot connect to RMI Server.\nPlease run Server.java first.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    }
    
