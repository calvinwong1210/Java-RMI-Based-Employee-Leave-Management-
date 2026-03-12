import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Service extends Remote {
    String applyLeave(String employeeId, String description, String startDate, String endDate)
            throws RemoteException;
    List<String[]> getLeavesByEmployee(String empId) throws RemoteException;
    void registerEmployee(String name, String ic, String gender,
                          String dob, String dept, String email,
                          String password,
                          String phone, String familyName,
                          String relationship, String familyPhone,
                          int leaveBalance) throws RemoteException;
    //PayrollManagement_Add
    void addPayroll(String empID, double basic, double allowance, double ot, double deduction, double total) 
         throws RemoteException;
    boolean isEmployeeExist(String empID) throws RemoteException;
    boolean isPayrollExist(String empID) throws RemoteException;

    //PayrollManagement_Edit
    String[] getPayroll(String empID) throws RemoteException;
    void updatePayroll(String empID, double basic, double allowance, double ot, double deduction, double total) throws RemoteException;
    void deletePayroll(String empID) throws RemoteException;
    
    //PayrollManagement_UpdateSalary
    String[] getSalaryStatus(String empID, String month, int year) throws RemoteException;
    void saveSalaryStatus(String empID, double salary, String month, String status, int year) throws RemoteException;
    void deleteSalaryStatus(String empID, String month, int year) throws RemoteException;
    
    //PayrollManagement_ViewSalary
    List<String[]> getAllSalaryStatusForEmployee(String empID) throws RemoteException;
}



