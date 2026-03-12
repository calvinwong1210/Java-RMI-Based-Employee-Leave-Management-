/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Owner
 */public class Session {

    private static String employeeId;
    private static String name;
    private static String department;

    private Session() {
        // prevent instantiation
    }

    public static void setUser(String empId, String userName, String dept) {
        employeeId = empId;
        name = userName;
        department = dept;
    }

    public static String getEmployeeId() {
        return employeeId;
    }

    public static String getName() {
        return name;
    }

    public static String getDepartment() {
        return department;
    }

    public static boolean isLoggedIn() {
        return employeeId != null;
    }

    public static void clear() {
        employeeId = null;
        name = null;
        department = null;
    }
}

