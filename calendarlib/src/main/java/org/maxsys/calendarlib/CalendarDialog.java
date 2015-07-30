package org.maxsys.calendarlib;

import java.awt.Color;
import java.awt.Component;
import java.util.Calendar;
import java.util.GregorianCalendar;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CalendarDialog extends javax.swing.JDialog {

    private class MyCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value != null) {
                if (((Calendar) value).get(Calendar.MONTH) != Month) {
                    c.setForeground(Color.LIGHT_GRAY);
                } else {
                    c.setForeground(Color.BLACK);
                }
            }
            return c;
        }
    }

    private class MyDCalendar extends GregorianCalendar {

        public MyDCalendar(int year, int month, int dayOfMonth) {
            super(year, month, dayOfMonth);
        }

        @Override
        public String toString() {
            return String.valueOf(get(Calendar.DAY_OF_MONTH));
        }
    }

    DefaultTableCellRenderer myCellRenderer = new MyCellRenderer();
    private int Month;
    private int Year;
    private int Day;

    public CalendarDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();

        myCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        jTable1.setDefaultRenderer(Object.class, myCellRenderer);
        jTable2.setDefaultRenderer(String.class, centerRenderer);

        Calendar ca = Calendar.getInstance();
        this.Month = ca.get(Calendar.MONTH);
        jComboBox1.setSelectedIndex(this.Month);
        this.Year = ca.get(Calendar.YEAR);
        jTextField1.setText(String.valueOf(this.Year));
        this.Day = ca.get(Calendar.DAY_OF_MONTH);
        refreshButtons();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public Calendar getCalendar() {
        int row = jTable1.getSelectedRow();
        int col = jTable1.getSelectedColumn();
        Object v = jTable1.getValueAt(row, col);

        if (jTextField2.getText().equals("---")) {
            return null;
        } else {
            return (Calendar) v;
        }
    }

    private void refreshButtons() {
        this.Year = Integer.valueOf(jTextField1.getText());
        this.Month = jComboBox1.getSelectedIndex();

        MyDCalendar ca = new MyDCalendar(this.Year, this.Month, 1);
        Calendar caNow = Calendar.getInstance();

        int firstDay = ca.get(Calendar.DAY_OF_WEEK);
        firstDay--;
        ca.add(Calendar.DAY_OF_YEAR, -firstDay);

        boolean dayset = false;

        for (int w = 0; w < 6; w++) {
            for (int d = 0; d < 7; d++) {
                jTable1.setValueAt(ca.clone(), w, d);
                if (ca.get(Calendar.YEAR) == caNow.get(Calendar.YEAR) && ca.get(Calendar.MONTH) == caNow.get(Calendar.MONTH) && ca.get(Calendar.DAY_OF_MONTH) == caNow.get(Calendar.DAY_OF_MONTH)) {
                    jTable1.setRowSelectionInterval(w, w);
                    jTable1.setColumnSelectionInterval(d, d);
                    dayset = true;
                }
                ca.add(Calendar.DAY_OF_YEAR, 1);
            }
        }

        if (dayset) {
            refreshDateString();
            jTable1.requestFocus();
        } else {
            jTextField2.setText("---");
        }
    }

    private void refreshDateString() {
        int row = jTable1.getSelectedRow();
        int col = jTable1.getSelectedColumn();

        Object v = jTable1.getValueAt(row, col);

        int yyyy = ((Calendar) v).get(Calendar.YEAR);
        int mm = ((Calendar) v).get(Calendar.MONTH) + 1;

        this.Day = Integer.valueOf(v.toString());

        String mms = String.valueOf(mm);
        if (mms.length() < 2) {
            mms = "0" + mms;
        }

        String dds = String.valueOf(this.Day);
        if (dds.length() < 2) {
            dds = "0" + dds;
        }

        jTextField2.setText(yyyy + "-" + mms + "-" + dds);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton42 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jTable1 = new javax.swing.JTable();
        jTable2 = new javax.swing.JTable();
        jTextField2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pick date");
        setResizable(false);

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jButton36.setText("<");
        jButton36.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton36ActionPerformed(evt);
            }
        });

        jButton37.setText(">");
        jButton37.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton37ActionPerformed(evt);
            }
        });

        jButton39.setText("Ok");
        jButton39.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton39ActionPerformed(evt);
            }
        });

        jButton40.setText("Now");
        jButton40.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton40ActionPerformed(evt);
            }
        });

        jButton41.setText("<");
        jButton41.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton41ActionPerformed(evt);
            }
        });

        jTextField1.setEditable(false);
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("0000");
        jTextField1.setFocusable(false);

        jButton42.setText(">");
        jButton42.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton42ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "S", "M", "T", "W", "T", "F", "S"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable1.setGridColor(new java.awt.Color(255, 255, 255));
        jTable1.setMinimumSize(new java.awt.Dimension(50, 50));
        jTable1.setRowHeight(25);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable1MouseReleased(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"S", "M", "T", "W", "T", "F", "S"}
            },
            new String [] {
                "S", "M", "T", "W", "T", "F", "S"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable2.setFocusable(false);
        jTable2.setGridColor(new java.awt.Color(255, 255, 255));
        jTable2.setRowHeight(25);
        jTable2.setRowSelectionAllowed(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jTable2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTable2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTable1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTextField2.setEditable(false);
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.setText("---");
        jTextField2.setFocusable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox1, 0, 98, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton42))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton40)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField2)
                        .addGap(18, 18, 18)
                        .addComponent(jButton39)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton36)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton37)
                    .addComponent(jButton41)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton42))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton40)
                    .addComponent(jButton39)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton40ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton40ActionPerformed
        Calendar ca = Calendar.getInstance();
        this.Month = ca.get(Calendar.MONTH);
        jComboBox1.setSelectedIndex(this.Month);
        this.Year = ca.get(Calendar.YEAR);
        jTextField1.setText(String.valueOf(this.Year));
        this.Day = ca.get(Calendar.DAY_OF_MONTH);
        refreshButtons();
    }//GEN-LAST:event_jButton40ActionPerformed

    private void jButton39ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton39ActionPerformed
        dispose();
    }//GEN-LAST:event_jButton39ActionPerformed

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        refreshDateString();
    }//GEN-LAST:event_jTable1KeyReleased

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        refreshButtons();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jTable1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseReleased
        refreshDateString();
    }//GEN-LAST:event_jTable1MouseReleased

    private void jButton36ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton36ActionPerformed
        int mm = jComboBox1.getSelectedIndex();
        mm--;
        if (mm < 0) {
            mm = 11;
            int yyyy = Integer.valueOf(jTextField1.getText());
            yyyy--;
            jTextField1.setText(String.valueOf(yyyy));
        }
        jComboBox1.setSelectedIndex(mm);
    }//GEN-LAST:event_jButton36ActionPerformed

    private void jButton37ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton37ActionPerformed
        int mm = jComboBox1.getSelectedIndex();
        mm++;
        if (mm > 11) {
            mm = 0;
            int yyyy = Integer.valueOf(jTextField1.getText());
            yyyy++;
            jTextField1.setText(String.valueOf(yyyy));
        }
        jComboBox1.setSelectedIndex(mm);
    }//GEN-LAST:event_jButton37ActionPerformed

    private void jButton41ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton41ActionPerformed
        int yyyy = Integer.valueOf(jTextField1.getText());
        yyyy--;
        jTextField1.setText(String.valueOf(yyyy));
        refreshButtons();
    }//GEN-LAST:event_jButton41ActionPerformed

    private void jButton42ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton42ActionPerformed
        int yyyy = Integer.valueOf(jTextField1.getText());
        yyyy++;
        jTextField1.setText(String.valueOf(yyyy));
        refreshButtons();
    }//GEN-LAST:event_jButton42ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
