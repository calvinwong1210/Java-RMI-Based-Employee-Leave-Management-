# Distributed Employee Leave & Payroll Management System (Java RMI)

A distributed client-server application built using **Java Remote Method Invocation (Java RMI)** and **Java Swing (GUI)**. This system provides centralized management for employee records, leave applications, approval workflows, payroll administration, and financial/leave summary reporting across different organizational roles (**Admin**, **HR**, and **Employee**).

---

## 📌 Project Overview

This project was developed for a Distributed Communication Systems assignment. It demonstrates key concepts in distributed systems, such as:
- **Client-Server Architecture**: Decoupled GUI client frontend from backend service logic via Java RMI interfaces.
- **Remote Method Invocation (RMI)**: Remote procedure calls using Java `Registry` listening on port `1099`.
- **Thread-Safe Data Persistence**: Text-based flat file storage (`.txt`) managed with synchronized file locking (`LOCK`) to handle multi-client concurrent operations safely.
- **Role-Based Access Control (RBAC)**: Role-tailored dashboards and permission controls for **Admin**, **HR**, and standard **Employees**.

---

## 🌟 Key Features

### 👨‍💼 1. Admin Module
- **User Registration**: Register new employees and admin accounts with details including IC, Department, Emergency Contact, and Credentials.
- **Employee ID Generation**: Automatic sequential ID generation (e.g., `E0001`, `E0002`).
- **Profile Management**: View and update employee details across the organization.

### 👩‍💼 2. HR Module
- **Leave Approval Workflow**: Review pending leave applications, approve or reject applications, and inspect leave details.
- **Payroll Management**:
  - Add and edit employee payroll structures (Basic Salary, Allowance, Overtime, Deductions, and Net Total).
  - Update monthly salary payment statuses (**Paid** / **Pending**).
  - View salary status logs for all employees.
- **Yearly Summary Reports**: Generate comprehensive yearly summary reports combining leave history and salary statements.

### 🧑‍💻 3. Employee Module
- **Profile Management**: View personal profile information and update contact details or passwords.
- **Leave Application**:
  - Submit leave requests with start date, end date, and reason.
  - Automatic calculation of requested leave duration and verification against annual leave entitlement.
- **Leave Tracking**: View status of applied leaves (**Pending**, **Approved**, **Rejected**).
- **Salary Statements**: View monthly salary slips, breakdown of pay components, and payment status.

---

## 🏗️ System Architecture

```
+-----------------------------------+            +-----------------------------------+
|            RMI Client             |            |            RMI Server             |
|   (Java Swing GUI Applications)   |            |     (Business Logic & Files)      |
|                                   |  RMI Call  |                                   |
|  - Login / Session Management     | ---------> |  - Server.java (Registry @ 1099)  |
|  - Admin_Main GUI                 |            |  - Service.java (Remote Interface)|
|  - HR_Main GUI                    | <--------- |  - ServiceImplement.java          |
|  - Employee_Main GUI              |  Returns   |                                   |
+-----------------------------------+            +-----------------------------------+
                                                           |
                                                           v
                                                +----------------------+
                                                |  Flat File Storage   |
                                                |  - employees.txt     |
                                                |  - leaves.txt        |
                                                |  - payroll.txt       |
                                                |  - payroll_status.txt|
                                                +----------------------+
```

---

## 📁 Directory Structure

```
dcoms_assignment/
└── RMI Assignment/
    ├── src/
    │   ├── Server.java                   # RMI Server entry point (starts Registry on port 1099)
    │   ├── Client.java                   # RMI Client entry point (looks up Service & launches Login)
    │   ├── Service.java                  # Remote interface defining server operations
    │   ├── ServiceImplement.java         # RMI Service implementation & file handling logic
    │   ├── Session.java                  # Holds current logged-in user state
    │   ├── Login.java                    # Login GUI & department-based routing
    │   ├── Register.java                 # Employee self-registration GUI
    │   ├── Register_Admin.java           # Admin employee creation GUI
    │   ├── Admin_Main.java               # Admin main control dashboard
    │   ├── HR_Main.java                  # HR main control dashboard
    │   ├── Employee_Main.java            # Employee main control dashboard
    │   ├── EditProfile.java              # Profile editing interface
    │   ├── ApplyLeaveFormGUI.java        # Leave submission GUI
    │   ├── ViewLeavesGUI.java            # Employee leave history GUI
    │   ├── LeaveManagementGUI.java       # HR leave review & approval GUI
    │   ├── LeaveDetailForm.java          # HR detailed leave inspection form
    │   ├── PayrollManagement.java        # HR Payroll main panel
    │   ├── PayrollManagement_Add.java    # Add payroll structure GUI
    │   ├── PayrollManagement_Edit.java   # Edit payroll structure GUI
    │   ├── PayrollManagement_UpdateSalary.java # Salary payout status GUI
    │   ├── PayrollManagement_ViewSalary.java   # HR view salary records GUI
    │   ├── ViewSalary.java               # Employee salary slip viewer GUI
    │   └── YearlySummaryReportGUI.java   # Yearly summary report generator GUI
    ├── dist/
    │   └── RMI_Assignment.jar            # Compiled executable JAR file
    └── nbproject/                        # NetBeans project configuration files
```

---

## 🗄️ Data Storage Schema

The system uses delimited text files (`.txt`) for persistent data storage:

1. **`employees.txt`**
   `EmployeeID; Name; IC/NRIC; Gender; DOB; Department; Email; Password; Phone; Emergency Contact Name; Emergency Contact Relation; Emergency Contact Phone`

2. **`leaves.txt`**
   `LeaveID; EmployeeID; Description; StartDate; EndDate; Status (Pending/Approved/Rejected)`

3. **`payroll.txt`**
   `EmployeeID; BasicSalary; Allowance; OvertimePay; Deductions; TotalNetSalary`

4. **`payroll_status.txt`**
   `EmployeeID; NetSalary; Month; PayoutStatus (Paid/Pending); Year`

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK)**: Version 8 or higher.
- **IDE** (Optional): NetBeans IDE, IntelliJ IDEA, or Eclipse.

### Option 1: Running via Command Line

1. **Clone or Download** this repository.
2. Navigate to the project directory:
   ```bash
   cd "dcoms_assignment-main/RMI Assignment"
   ```
3. Compile all source code into a `bin` directory:
   ```bash
   mkdir bin
   javac -d bin src/*.java
   ```
4. **Start the RMI Server** (Run this first in Terminal 1):
   ```bash
   java -cp bin Server
   ```
   *Output:* `RMI Server started and bound.`

5. **Start the RMI Client** (Run this in Terminal 2):
   ```bash
   java -cp bin Client
   ```

### Option 2: Running via NetBeans IDE

1. Open **NetBeans IDE**.
2. Select **File > Open Project** and choose the `RMI Assignment` folder.
3. Right-click `Server.java` and select **Run File** to start the RMI server.
4. Right-click `Client.java` and select **Run File** to launch the client GUI.

---

## 🔑 Usage & Roles Setup

| Department | Default Dashboard | Key Features Access |
|---|---|---|
| **Admin** | `Admin_Main` | Employee Registration, Profile Updates, System Administration |
| **HR** | `HR_Main` | Leave Approval/Rejection, Payroll Setup & Updates, Summary Reports |
| **Employee** (IT, Marketing, etc.) | `Employee_Main` | Submit Leave, Track Leave Status, View Monthly Salary Slips |

*Note: Department string assigned during registration determines which main dashboard is loaded upon successful login.*

---

## 🛠️ Built With

- **Language**: Java SE
- **GUI Framework**: Java Swing & AWT (Nimbus Look & Feel)
- **Distributed Computing**: Java RMI (`java.rmi.*`)
- **IDE**: NetBeans IDE
