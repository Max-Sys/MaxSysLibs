package org.maxsys.misc.splash;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

public interface SplashTask {

    public void doTask();

    public default void setLabel(JLabel jLabel) {
    }

    public default void setLabel(JProgressBar jProgressBar) {
    }
}
