import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Service extends Remote {
    List<String[]> getData() throws RemoteException;
    String applyLeave(String employeeId, String description, String startDate, String endDate)
            throws RemoteException;
}
