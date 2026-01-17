import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        try {
            // 1. Start registry
            Registry registry = LocateRegistry.createRegistry(1099);

            // 2. Create implementation instance
            Service service = new ServiceImplement();

            // 3. Bind to registry
            registry.rebind("Service", service);

            System.out.println("RMI Server started and bound.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
    
