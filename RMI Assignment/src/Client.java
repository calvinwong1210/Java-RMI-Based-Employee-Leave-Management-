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

            List<String[]> data = service.getData();

            for (String[] row : data) {
                for (String col : row) {
                    System.out.print(col + "\t");
                }
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }
    
