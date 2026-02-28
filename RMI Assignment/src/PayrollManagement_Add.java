/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author User
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import javax.swing.JOptionPane;

public class PayrollManagement_Add extends javax.swing.JFrame {

    /**
     * Creates new form PayrollManagement_Add
     */
    private Service service;

    public PayrollManagement_Add() {
        initComponents();
        this.service = Client.service;
        
        if (this.service == null) {
            JOptionPane.showMessageDialog(this, 
                "Cannot connect to RMI Server.\nPlease run Server.java first.",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
            save_button.setEnabled(false);
        }
        
        Total.setEditable(false);
        basic_salary.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotal();
            }
        });
        Allowance.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotal();
            }
        });
        ot_fees.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotal();
            }
        });
        Deduction.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotal();
            }
        });
    }

    public PayrollManagement_Add(Service service) {
        this(); 
        this.service = service; 
    }
    
    private boolean isEmployeeExist(String empID) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("employees.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");

                if (data[0].equals(empID)) {
                    br.close();
                    return true;
                }
            }

            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading employee file");
        }

        return false;
    }
    
    private boolean isPayrollExist(String empID) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("payroll.txt"));
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");

                if (data[0].equals(empID)) {
                    br.close();
                    return true;
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            return false; 
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading payroll file");
        }

        return false;
    }
    
    private void savePayroll() {
                if (service == null) {
            JOptionPane.showMessageDialog(this, "Server haven't connected, please check the server");
            return;
        }
        String empID = employeeID.getText();
        double basic = basic_salary.getText().isEmpty() ? 0 : Double.parseDouble(basic_salary.getText());
        double allowance = Allowance.getText().isEmpty() ? 0 : Double.parseDouble(Allowance.getText());
        double ot = ot_fees.getText().isEmpty() ? 0 : Double.parseDouble(ot_fees.getText());
        double deduction = Deduction.getText().isEmpty() ? 0 : Double.parseDouble(Deduction.getText());
        double total = basic + allowance + ot - deduction;

        try {
            if (!service.isEmployeeExist(empID)) {
                JOptionPane.showMessageDialog(this, "Employee ID not found!");
                return;
            }

            if (service.isPayrollExist(empID)) {
                JOptionPane.showMessageDialog(this, "Payroll already exists!");
                return;
            }

            service.addPayroll(empID, basic, allowance, ot, deduction, total);
            JOptionPane.showMessageDialog(this, "Payroll Added Successfully!");

        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Error connecting to server: " + e.getMessage());
        }
    }
    
    private void calculateTotal() {
        try {
            double basic = basic_salary.getText().isEmpty() ? 0 : Double.parseDouble(basic_salary.getText());
            double allowance = Allowance.getText().isEmpty() ? 0 : Double.parseDouble(Allowance.getText());
            double ot = ot_fees.getText().isEmpty() ? 0 : Double.parseDouble(ot_fees.getText());
            double deduction = Deduction.getText().isEmpty() ? 0 : Double.parseDouble(Deduction.getText());

            double totalSalary = basic + allowance + ot - deduction;
            Total.setText(String.valueOf(totalSalary));
        } catch (NumberFormatException e) {
            Total.setText(""); 
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        save_button = new javax.swing.JButton();
        employeeID = new javax.swing.JTextField();
        basic_salary = new javax.swing.JTextField();
        Allowance = new javax.swing.JTextField();
        ot_fees = new javax.swing.JTextField();
        Deduction = new javax.swing.JTextField();
        Total = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        back_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        save_button.setText("Save");
        save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_buttonActionPerformed(evt);
            }
        });

        employeeID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                employeeIDActionPerformed(evt);
            }
        });

        basic_salary.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                basic_salaryKeyTyped(evt);
            }
        });

        Allowance.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                AllowanceKeyTyped(evt);
            }
        });

        ot_fees.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ot_feesKeyTyped(evt);
            }
        });

        Deduction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeductionActionPerformed(evt);
            }
        });
        Deduction.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                DeductionKeyTyped(evt);
            }
        });

        Total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TotalActionPerformed(evt);
            }
        });
        Total.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TotalKeyTyped(evt);
            }
        });

        jLabel1.setText("Add Payroll");

        jLabel2.setText("Employee ID:");

        jLabel3.setText("Basic Salary:");

        jLabel4.setText("Allowance:");

        jLabel5.setText("OT Fees:");

        jLabel6.setText("Deduction:");

        jLabel7.setText("Total:");

        back_button.setText("Back");
        back_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                back_buttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(169, 169, 169)
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(113, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(back_button)
                        .addGap(18, 18, 18)
                        .addComponent(save_button))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Total, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ot_fees, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Allowance, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(basic_salary, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(employeeID, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Deduction, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(116, 116, 116))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(employeeID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(basic_salary, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addComponent(Allowance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ot_fees, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Deduction, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Total, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(save_button)
                    .addComponent(back_button))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TotalActionPerformed

    private void DeductionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeductionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DeductionActionPerformed

    private void basic_salaryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_basic_salaryKeyTyped
        char c = evt.getKeyChar();
        String text = basic_salary.getText();

        if (!Character.isDigit(c) && c != '.' && c != '\b') {
            evt.consume();
        }

        if (c == '.' && text.contains(".")) {
            evt.consume();
        }
    }//GEN-LAST:event_basic_salaryKeyTyped

    private void AllowanceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AllowanceKeyTyped
        char c = evt.getKeyChar();
        String text = Allowance.getText();

        if (!Character.isDigit(c) && c != '.' && c != '\b') {
            evt.consume();
        }

        if (c == '.' && text.contains(".")) {
            evt.consume();
        }
    }//GEN-LAST:event_AllowanceKeyTyped

    private void ot_feesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ot_feesKeyTyped
        char c = evt.getKeyChar();
        String text = ot_fees.getText();

        if (!Character.isDigit(c) && c != '.' && c != '\b') {
            evt.consume();
        }

        if (c == '.' && text.contains(".")) {
            evt.consume();
        }
    }//GEN-LAST:event_ot_feesKeyTyped

    private void DeductionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DeductionKeyTyped
        char c = evt.getKeyChar();
        String text = Deduction.getText();

        if (!Character.isDigit(c) && c != '.' && c != '\b') {
            evt.consume();
        }

        if (c == '.' && text.contains(".")) {
            evt.consume();
        }
    }//GEN-LAST:event_DeductionKeyTyped

    private void TotalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TotalKeyTyped
        char c = evt.getKeyChar();
        String text = Total.getText();

        if (!Character.isDigit(c) && c != '.' && c != '\b') {
            evt.consume();
        }

        if (c == '.' && text.contains(".")) {
            evt.consume();
        }
    }//GEN-LAST:event_TotalKeyTyped

    private void employeeIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_employeeIDActionPerformed
        String empID = employeeID.getText();

        if (!isEmployeeExist(empID)) {
            JOptionPane.showMessageDialog(this, "Employee ID not found!");
            employeeID.setText("");
        }
    }//GEN-LAST:event_employeeIDActionPerformed

    private void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
        savePayroll();
    }//GEN-LAST:event_save_buttonActionPerformed

    private void back_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_back_buttonActionPerformed
        PayrollManagement dashboard = new PayrollManagement();
        dashboard.setVisible(true);

        this.dispose();
    }//GEN-LAST:event_back_buttonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PayrollManagement_Add.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PayrollManagement_Add.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PayrollManagement_Add.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PayrollManagement_Add.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new PayrollManagement_Add().setVisible(true);
            }
        });
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Allowance;
    private javax.swing.JTextField Deduction;
    private javax.swing.JTextField Total;
    private javax.swing.JButton back_button;
    private javax.swing.JTextField basic_salary;
    private javax.swing.JTextField employeeID;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField ot_fees;
    private javax.swing.JButton save_button;
    // End of variables declaration//GEN-END:variables
}
