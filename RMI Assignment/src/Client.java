import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Client {
    public static Service service;

    public static void main(String[] args) {
     try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service =(Service) registry.lookup("Service");
 SwingUtilities.invokeLater(() -> {
            // Replace ApplyLeaveFrame with your actual JFrame class name
            Login start = new Login();
            start.setVisible(true);
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
    
