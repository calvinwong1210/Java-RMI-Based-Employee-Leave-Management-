import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Service extends Remote {
    List<String[]> getData() throws RemoteException;
    String applyLeave(String employeeId, String description, String startDate, String endDate)
            throws RemoteException;
    List<String[]> getAllLeaves() throws RemoteException;
    // Add this to Service.java interface
String getYearlyEmployeeReport(String employeeId, int year) throws RemoteException, IOException ;
String[] getEmployeeProfile(String employeeId) throws RemoteException;
List<String[]> getAllEmployees() throws RemoteException;
void updateLeaveStatus(String leaveId, String newStatus) throws RemoteException;


 
}
