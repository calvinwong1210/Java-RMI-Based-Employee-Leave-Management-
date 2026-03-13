
import java.io.IOException;
import javax.swing.JOptionPane;

import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author yongt
 */
public class YearlySummaryReportGUI extends javax.swing.JFrame {
    

    private final Service service;
    private final String employeeId;

    public YearlySummaryReportGUI(String employeeId, Service service) {
        this.employeeId = employeeId;
        this.service = service;
        initComponents();
        loadEmployeeList();
        
        // Optional: make report area look better for monospaced text
        ReportTable.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 13));
        ReportTable.setEditable(false);
        ReportTable.setLineWrap(true);
        ReportTable.setWrapStyleWord(true);
    }
    @SuppressWarnings("unchecked")
    
    private void loadEmployeeList() {
    EmployeeList.removeAllItems();
    EmployeeList.addItem("Select an Employee");

    if (service == null) {
        EmployeeList.addItem("(RMI service not available - test mode)");
        return;
    }

    try {
        // Call the server method to get all employees
        List<String[]> employees = service.getAllEmployees();

        if (employees == null || employees.isEmpty()) {
            EmployeeList.addItem("No employees found");
            return;
        }

        for (String[] emp : employees) {
            if (emp.length >= 3) {  // ID, FirstName, LastName at minimum
                String id = emp[0].trim();
                String first = emp[1].trim();
                String last = emp[2].trim();
                String displayName = (first + " " + last).trim();
                if (displayName.isEmpty()) {
                    displayName = "No Name";
                }
                String item = id + " - " + displayName;
                EmployeeList.addItem(item);
            }
        }

    } catch (RemoteException ex) {
        EmployeeList.addItem("Error loading employees: " + ex.getMessage());
    }
}
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        generate = new javax.swing.JButton();
        back = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        ReportTable = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        EmployeeList = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Yearly Summary Report");

        generate.setText("Generate");
        generate.addActionListener(this::generateActionPerformed);

        back.setText("Back");
        back.addActionListener(this::backActionPerformed);

        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        ReportTable.setColumns(20);
        ReportTable.setRows(5);
        jScrollPane1.setViewportView(ReportTable);

        jLabel2.setText("Year: ");

        EmployeeList.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        EmployeeList.addActionListener(this::EmployeeListActionPerformed);

        jLabel3.setText("Employee:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 790, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(375, 375, 375)
                        .addComponent(back))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EmployeeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(generate))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generate)
                    .addComponent(jLabel2)
                    .addComponent(EmployeeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                .addGap(29, 29, 29)
                .addComponent(back)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
       this.dispose();
    }//GEN-LAST:event_backActionPerformed

    private void generateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateActionPerformed
if (service == null) {
        ReportTable.setText("RMI service not available (test mode)");
        return;
    }

    String yearText = jTextField1.getText().trim();
    if (yearText.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter a year", "Input Required", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int year;
    try {
        year = Integer.parseInt(yearText);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Invalid year format", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // ─── Get selected employee ──────────────────────────────────────
    String selectedItem = (String) EmployeeList.getSelectedItem();
    if (selectedItem == null || 
        selectedItem.equals("Select an Employee") || 
        selectedItem.contains("Error") || 
        selectedItem.contains("No employees")) {
        
        JOptionPane.showMessageDialog(this, 
            "Please select a valid employee from the list", 
            "Selection Required", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Extract employee ID from "E001 - Tan Yong Teck" → take part before " - "
    String employeeIdSelected = selectedItem.split(" - ")[0].trim();

    try {
        String report = service.getYearlyEmployeeReport(employeeIdSelected, year);
        ReportTable.setText(report);
        ReportTable.setCaretPosition(0); // scroll to top
    } catch (RemoteException ex) {
        JOptionPane.showMessageDialog(this,
            "Failed to generate report:\n" + ex.getMessage(),
            "RMI Error", JOptionPane.ERROR_MESSAGE);
    }   catch (IOException ex) {
            Logger.getLogger(YearlySummaryReportGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_generateActionPerformed

    private void EmployeeListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EmployeeListActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_EmployeeListActionPerformed

    /**
     * @param args the command line arguments
     */
    /**
 * @param args the command line arguments
 */
public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                "This form is meant to be opened from Client.java after connecting to the RMI server.\n" +
                "Running in test mode now (service = null)",
                "Test Mode", JOptionPane.INFORMATION_MESSAGE);

            new YearlySummaryReportGUI("E0001", null).setVisible(true);
        });
  
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> EmployeeList;
    private javax.swing.JTextArea ReportTable;
    private javax.swing.JButton back;
    private javax.swing.JButton generate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}