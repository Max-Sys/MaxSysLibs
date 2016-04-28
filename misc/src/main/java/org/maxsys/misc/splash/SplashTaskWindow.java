package org.maxsys.misc.splash;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;

public class SplashTaskWindow extends javax.swing.JDialog {

    private static String labelText = "Please wait...";
    private static final JProgressBar progress = new JProgressBar();

    public SplashTaskWindow(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jProgressBar1 = SplashTaskWindow.progress;

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("...");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void run(SplashTask task, String labelText) {
        SplashTaskWindow.progress.setMinimum(0);
        SplashTaskWindow.progress.setMaximum(100);
        SplashTaskWindow.progress.setValue(0);
        SplashTaskWindow.progress.setIndeterminate(false);

        SplashTaskWindow sw = new SplashTaskWindow(null);
        sw.jLabel1.setText(labelText);
        task.setLabel(sw.jLabel1);
        sw.setLocationRelativeTo(null);

        new Thread(() -> {
            try {
                SplashTaskWindow.progress.setIndeterminate(true);
                task.doTask();
            } catch (Exception ex) {
                Logger.getLogger(SplashTaskWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            sw.dispose();
        }, "SplashTask").start();

        try {
            sw.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(SplashTaskWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void run(SplashTask task) {
        run(task, SplashTaskWindow.labelText);
    }

    public static void setLabelText(String labelText) {
        SplashTaskWindow.labelText = labelText;
    }

    public static void setProgress(int percent) {
        SplashTaskWindow.progress.setIndeterminate(false);
        if (SplashTaskWindow.progress.getMaximum() >= percent) {
            SplashTaskWindow.progress.setValue(percent);
        }
    }

    public static void incProgress() {
        SplashTaskWindow.progress.setIndeterminate(false);
        if (SplashTaskWindow.progress.getValue() < SplashTaskWindow.progress.getMaximum()) {
            SplashTaskWindow.progress.setValue(SplashTaskWindow.progress.getValue() + 1);
        }
    }

    public static void setMaxProgress(int maxProgress) {
        SplashTaskWindow.progress.setIndeterminate(false);
        if (SplashTaskWindow.progress.getValue() > maxProgress) {
            SplashTaskWindow.progress.setValue(maxProgress);
        }
        SplashTaskWindow.progress.setMaximum(maxProgress);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    // End of variables declaration//GEN-END:variables
}
