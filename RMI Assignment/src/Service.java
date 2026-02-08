import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Service extends Remote {

    List<String[]> getData() throws RemoteException;

    void registerEmployee(String name, String ic, String gender,
                          String dob, String dept,
                          String email, String password)
            throws RemoteException;
}


