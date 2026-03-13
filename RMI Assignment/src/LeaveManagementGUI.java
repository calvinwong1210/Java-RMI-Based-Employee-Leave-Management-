/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.List;
/**
 *
 * @author yongt
 */
public class LeaveManagementGUI extends javax.swing.JFrame {
  private DefaultTableModel tableModel;



    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LeaveManagementGUI.class.getName());

    /**
     * Creates new form LeaveManagementGUI
     */
    public LeaveManagementGUI() {
    initComponents();

    // Initialize table model
    String[] columns = {"Leave ID", "Employee ID", "Description", "Start Date", "End Date", "Status"};
    tableModel = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // read-only
        }
    };
    LeavesTable.setModel(tableModel);  // ← use LeavesTable (generated name)

    // Make table nicer
    LeavesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    LeavesTable.getColumnModel().getColumn(2).setPreferredWidth(250); // wider description

    // Load data
    loadEmployeeCombo();
    loadLeavesToTable();

    // Combo box listener
    employee.addActionListener(e -> loadLeavesToTable());
}
    
    private void loadEmployeeCombo() {
    employee.removeAllItems();
    employee.addItem("All Employees");

    if (Client.service == null) {
        employee.addItem("(Test mode - no data)");
        return;
    }

    try {
        List<String[]> employees = Client.service.getAllEmployees();
        for (String[] emp : employees) {
            if (emp.length >= 3) {
                String id = emp[0].trim();
                String name = (emp[1].trim() + " " + emp[2].trim()).trim();
                String display = id + " - " + (name.isEmpty() ? "No Name" : name);
                employee.addItem(display);
            }
        }
    } catch (RemoteException ex) {
        JOptionPane.showMessageDialog(this, "Failed to load employees: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
   

    
    private void loadEmployeesIntoCombo() {
    employee.removeAllItems();
    employee.addItem("All Employees");

    if (Client.service == null) {
        employee.addItem("(No connection - test mode)");
        return;
    }

    try {
        List<String[]> employees = Client.service.getAllEmployees();
        for (String[] emp : employees) {
            if (emp.length >= 3) {
                String id = emp[0].trim();
                String name = (emp[1].trim() + " " + emp[2].trim()).trim();
                if (name.isEmpty()) name = "No Name";
                employee.addItem(id + " - " + name);
            }
        }
    } catch (RemoteException ex) {
        JOptionPane.showMessageDialog(this, "Cannot load employees: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void loadLeavesToTable() {
    tableModel.setRowCount(0); // clear table

    if (Client.service == null) {
        tableModel.addRow(new Object[]{"-", "-", "No connection", "-", "-", "-"});
        return;
    }

    try {
        List<String[]> leaves = Client.service.getAllLeaves();

        String selected = (String) employee.getSelectedItem();
        String filterId = null;
        if (selected != null && !selected.equals("All Employees")) {
            filterId = selected.split(" - ")[0].trim();
        }

        for (String[] leave : leaves) {
            if (leave.length < 6) continue;

            String empId = leave[1].trim();
            if (filterId != null && !empId.equalsIgnoreCase(filterId)) {
                continue;
            }

            // NO filter on status — show ALL statuses
            tableModel.addRow(new Object[]{
                leave[0],           // Leave ID
                leave[1],           // Employee ID
                leave[2],           // Description
                leave[3],           // Start Date
                leave[4],           // End Date
                leave[5]            // Status (Waiting / Approved / Rejected)
            });
        }
    } catch (RemoteException ex) {
        JOptionPane.showMessageDialog(this, "Failed to load leaves: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

}

private String getSelectedEmployeeId() {
    String selected = (String) employee.getSelectedItem();
    if (selected == null || selected.equals("All Employees")) {
        return null;
    }
    return selected.split(" - ")[0].trim();
}

// Add this public method so child forms can call it
public void refreshTable() {
    loadLeavesToTable();   // ← reuse your existing load method
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        employee = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        LeavesTable = new javax.swing.JTable();
        viewdetail = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Leave Management ");

        jLabel2.setText("Employee: ");

        employee.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        employee.addActionListener(this::employeeActionPerformed);

        jButton1.setText("Back");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        LeavesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(LeavesTable);

        viewdetail.setText("View");
        viewdetail.addActionListener(this::viewdetailActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(280, 280, 280)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(employee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 331, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(266, 266, 266)
                .addComponent(viewdetail)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(employee, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(viewdetail))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void employeeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employeeActionPerformed
      loadLeavesToTable();
    }//GEN-LAST:event_employeeActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String role = Session.getDepartment();

        switch (role) {
            case "Employee":
                new Employee_Main().setVisible(true);
                break;

            case "HR":
                new HR_Main().setVisible(true);
                break;

            case "Admin":
                new Admin_Main().setVisible(true);
                break;

            default:
                JOptionPane.showMessageDialog(this, "Unknown user role");
                return;
        }
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void viewdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewdetailActionPerformed
       int row = LeavesTable.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Please select a leave request first", "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String leaveId = (String) LeavesTable.getValueAt(row, 0);
    
    // Pass 'this' (the LeaveManagementGUI instance) as parent
    new LeaveDetailForm(leaveId, this).setVisible(true);
    }//GEN-LAST:event_viewdetailActionPerformed

    /**
     * @param args the command line arguments
     */
   /**
 * Temporary main method for quick testing
 */
public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(() -> {
        // Open without real service (test mode
    });
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable LeavesTable;
    private javax.swing.JComboBox<String> employee;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton viewdetail;
    // End of variables declaration//GEN-END:variables
}
