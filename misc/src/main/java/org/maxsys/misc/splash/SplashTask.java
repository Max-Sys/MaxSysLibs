package org.maxsys.misc.splash;

import javax.swing.JLabel;

public interface SplashTask {

    public void doTask();

    public default void setLabel(JLabel jLabel) {
    }

}
