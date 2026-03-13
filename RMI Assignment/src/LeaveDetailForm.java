
import java.rmi.RemoteException;
import java.util.List;
import javax.swing.JOptionPane;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author yongt
 */
public class LeaveDetailForm extends javax.swing.JFrame {

    private final Service service;
    private final String leaveId;
    private final LeaveManagementGUI parent;

    public LeaveDetailForm(Service service, String leaveId,LeaveManagementGUI parent) {
        this.service = service;
        this.leaveId = leaveId;
        this.parent = parent;
        initComponents();

        // Make all text fields read-only
        employeeid.setEditable(false);
        leaveid.setEditable(false);
        employeename.setEditable(false);
        description.setEditable(false);
        startdate.setEditable(false);
        enddate.setEditable(false);
        currentstatus.setEditable(false);

        // Load data by calling the SERVER
        loadLeaveData();
    }

    private LeaveDetailForm() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void loadLeaveData() {
        if (service == null) {
            JOptionPane.showMessageDialog(this, "No RMI connection (test mode)", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (leaveId == null || leaveId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No leave ID provided", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<String[]> allLeaves = service.getAllLeaves();  // ← calls server
            boolean found = false;

            for (String[] leave : allLeaves) {
                if (leave.length < 6) continue;

                if (leave[0].trim().equalsIgnoreCase(leaveId.trim())) {
                    found = true;

                    leaveid.setText(leave[0].trim());
                    employeeid.setText(leave[1].trim());
                    description.setText(leave[2].trim());
                    startdate.setText(leave[3].trim());
                    enddate.setText(leave[4].trim());
                    currentstatus.setText(leave[5].trim());

                    // Load employee name (optional)
                    try {
                        String[] emp = service.getEmployeeProfile(leave[1].trim());  // ← calls server
                        if (emp != null && emp.length >= 3) {
                            String name = (emp[1].trim() + " " + emp[2].trim()).trim();
                            employeename.setText(name.isEmpty() ? "Unknown" : name);
                        } else {
                            employeename.setText("Name not found");
                        }
                    } catch (Exception ex) {
                        employeename.setText("Error loading name");
                    }

                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "Leave ID not found: " + leaveId, "Not Found", JOptionPane.ERROR_MESSAGE);
            }
        } catch (RemoteException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load leave details:\n" + ex.getMessage(), "RMI Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   private void updateLeaveStatus(String newStatus) {
    if (service == null) {
        JOptionPane.showMessageDialog(this, "No RMI connection (test mode)", "Warning", JOptionPane.WARNING_MESSAGE);
        return;  // do NOT close if failed
    }

    try {
        service.updateLeaveStatus(leaveId, newStatus);
        
        // Update the text field immediately (visual feedback)
        currentstatus.setText(newStatus);

        // Show success message
        JOptionPane.showMessageDialog(this, 
            "Leave " + leaveId + " has been " + newStatus.toLowerCase() + " successfully.", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);

        // If you added parent refresh earlier:
        if (parent != null) {
            parent.refreshTable();
        }

        // ─── MOST IMPORTANT: close the detail form AFTER success ───
        dispose();

    } catch (RemoteException ex) {
        JOptionPane.showMessageDialog(this, 
            "Failed to update leave status:\n" + ex.getMessage(), 
            "Update Failed", 
            JOptionPane.ERROR_MESSAGE);
        // Do NOT close if failed → user can try again or see the error
    }
}
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        employeeid = new javax.swing.JTextField();
        leaveid = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        description = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        startdate = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        enddate = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        currentstatus = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        employeename = new javax.swing.JTextField();
        Approve = new javax.swing.JButton();
        reject = new javax.swing.JButton();
        back = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Leave");

        jLabel2.setText("Leave ID: ");

        jLabel3.setText("Employee ID: ");

        employeeid.addActionListener(this::employeeidActionPerformed);

        leaveid.addActionListener(this::leaveidActionPerformed);

        jLabel4.setText("Description: ");

        description.addActionListener(this::descriptionActionPerformed);

        jLabel5.setText("Start Date: ");

        startdate.addActionListener(this::startdateActionPerformed);

        jLabel6.setText("End Date: ");

        enddate.addActionListener(this::enddateActionPerformed);

        jLabel7.setText("Current Status: ");

        currentstatus.addActionListener(this::currentstatusActionPerformed);

        jLabel8.setText("Employee Name: ");

        employeename.addActionListener(this::employeenameActionPerformed);

        Approve.setText("Approve");
        Approve.addActionListener(this::ApproveActionPerformed);

        reject.setText("Reject");
        reject.addActionListener(this::rejectActionPerformed);

        back.setText("Back");
        back.addActionListener(this::backActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(157, 157, 157)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel6))
                                    .addComponent(jLabel3)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(description, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                            .addComponent(startdate)
                            .addComponent(enddate)
                            .addComponent(currentstatus)
                            .addComponent(leaveid, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(employeeid)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Approve)
                            .addComponent(jLabel8))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(employeename))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(reject)
                                .addGap(18, 18, 18)
                                .addComponent(back)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap(48, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(leaveid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(employeeid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(employeename, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(description, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(startdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(enddate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(currentstatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Approve)
                    .addComponent(reject)
                    .addComponent(back))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void employeeidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employeeidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_employeeidActionPerformed

    private void leaveidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leaveidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_leaveidActionPerformed

    private void employeenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employeenameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_employeenameActionPerformed

    private void descriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descriptionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descriptionActionPerformed

    private void startdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_startdateActionPerformed

    private void enddateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enddateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_enddateActionPerformed

    private void currentstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentstatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_currentstatusActionPerformed

    private void ApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ApproveActionPerformed
        updateLeaveStatus("Approved");
    // NEW: close the window after action
    dispose();
    }//GEN-LAST:event_ApproveActionPerformed

    private void rejectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rejectActionPerformed
        updateLeaveStatus("Rejected");
        dispose();
    }//GEN-LAST:event_rejectActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        dispose();
    }//GEN-LAST:event_backActionPerformed

   
    public static void main(String args[]) {
       

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LeaveDetailForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Approve;
    private javax.swing.JButton back;
    private javax.swing.JTextField currentstatus;
    private javax.swing.JTextField description;
    private javax.swing.JTextField employeeid;
    private javax.swing.JTextField employeename;
    private javax.swing.JTextField enddate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField leaveid;
    private javax.swing.JButton reject;
    private javax.swing.JTextField startdate;
    // End of variables declaration//GEN-END:variables

  
  

    
} // End of class


    

