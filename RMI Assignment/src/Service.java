import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Service extends Remote {
    String applyLeave(String employeeId, String description, String startDate, String endDate)
            throws RemoteException;
    List<String[]> getLeavesByEmployee(String empId) throws RemoteException;
    void registerEmployee(String name, String ic, String gender,
                          String dob, String dept,
                          String email, String password)
            throws RemoteException;
}



