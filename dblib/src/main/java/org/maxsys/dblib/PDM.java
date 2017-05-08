package org.maxsys.dblib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PDM {

    private static final ConcurrentHashMap<String, ConnInstance> connInstances = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ConnState, Connection> Connections = new ConcurrentHashMap<>();
    private static ConnPoolWatcher connPoolWatcher = null;
    private PreparedStatement st = null;
    private ResultSet rs = null;
    private ConnState state = null;

    private class ConnState {

        private final String instance;
        private boolean free;
        private int freeCount;

        public ConnState(String instance, boolean free) {
            this.instance = instance;
            this.free = free;
            this.freeCount = 0;
        }

        public String getInstance() {
            return instance;
        }

        public boolean isFree() {
            return free;
        }

        public void setFree() {
            this.free = true;
            this.freeCount = 0;
        }

        public void setUnFree() {
            this.free = false;
        }

        public int getFreeCount() {
            return freeCount;
        }

        public void incFreeCount() {
            this.freeCount++;
        }
    }

    private class ConnInstance {

        private final String username;
        private final String password;
        private final String url;

        public ConnInstance(String username, String password, String url) {
            this.username = username;
            this.password = password;
            this.url = url;
        }

        public Properties getProperties() {
            Properties info = new Properties();
            info.setProperty("user", username);
            info.setProperty("password", password);
            info.setProperty("useUnicode", "true");
            info.setProperty("characterEncoding", "UTF-8");
            return info;
        }

        public String getUrl() {
            return url;
        }
    }

    private class ConnPoolWatcher extends Thread {

        private boolean isConnPoolRunning = false;

        @Override
        public void run() {
            isConnPoolRunning = true;
            while (isConnPoolRunning) {
                //System.out.println("ConnPool is alive. Connections count:" + Connections.size());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConnPoolWatcher.class.getName()).log(Level.SEVERE, null, ex);
                }
                synchronized (Connections) {
                    for (ConnState state : Connections.keySet()) {
                        if (state.isFree()) {
                            if (state.getFreeCount() >= 5) {
                                try {
                                    Connections.get(state).close();
                                } catch (SQLException ex) {
                                    Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                Connections.remove(state);
                            } else {
                                state.incFreeCount();
                            }
                        }
                    }
                    if (Connections.isEmpty()) {
                        isConnPoolRunning = false;
                    }
                }
            }
            //System.out.println("ConnPool is closed...");
            connPoolWatcher = null;
        }
    }

    private ConnState getConnState(String instance) {
        if (!connInstances.containsKey(instance)) {
            return null;
        }

        ConnState connState = null;

        synchronized (Connections) {
            for (ConnState sta : Connections.keySet()) {
                if (sta.getInstance().equals(instance) && sta.isFree()) {
                    sta.setUnFree();
                    connState = sta;
                    break;
                }
            }
        }

        if (connState == null) {
            Connection conn = null;
            int errcount = 0;
            while (errcount <= 5) {
                try {
                    conn = DriverManager.getConnection(connInstances.get(instance).getUrl(), connInstances.get(instance).getProperties());
                    errcount = 0;
                    break;
                } catch (SQLException ex) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex1) {
                        Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    errcount++;
                }
            }
            if (errcount > 0) {
                Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, "Error creating connection");
                return null;
            }

            connState = new ConnState(instance, false);
            synchronized (Connections) {
                Connections.put(connState, conn);
            }
        }

        if (connPoolWatcher == null) {
            connPoolWatcher = new ConnPoolWatcher();
            connPoolWatcher.start();
        }

        return connState;
    }

    public PDM() {
    }

    public PDM(String instanceName, String username, String password, String url) {
        if (connInstances.get(instanceName) != null) {
            synchronized (Connections) {
                for (ConnState sta : Connections.keySet()) {
                    if (sta.getInstance().equals(instanceName) && sta.isFree()) {
                        //System.out.println("Removing connection from " + sta.getInstance());
                        Connections.remove(sta);
                    }
                }
            }
        }
        connInstances.put(instanceName, new ConnInstance(username, password, url));
    }

    public void addConnInstance(String instanceName, String username, String password, String url) {
        connInstances.put(instanceName, new ConnInstance(username, password, url));
    }

    public void removeConnInstance(String instanceName) {
        connInstances.remove(instanceName);
    }

    public boolean executeNonQuery(String instance, String sql) {
        state = getConnState(instance);
        if (state == null) {
            return false;
        }
        Connection conn = Connections.get(state);
        try {
            st = conn.prepareStatement(sql);
            st.execute();
        } catch (SQLException ex) {
            Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            }
            state.setFree();
        }
        return true;
    }

    public int executeNonQueryAI(String instance, String sql) {
        int ai = -1;
        state = getConnState(instance);
        if (state == null) {
            return -1;
        }
        Connection conn = Connections.get(state);
        try {
            st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.execute();
            try (ResultSet rsgk = st.getGeneratedKeys()) {
                rsgk.next();
                ai = rsgk.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            }
            state.setFree();
        }
        return ai;
    }

    public int executeNonQueryAI(String instance, String sql, Object[] params) {
        int ai = -1;
        state = getConnState(instance);
        if (state == null) {
            return -1;
        }
        Connection conn = Connections.get(state);
        try {
            st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            fillStatementParameters(params);
            st.execute();
            try (ResultSet rsgk = st.getGeneratedKeys()) {
                rsgk.next();
                ai = rsgk.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            }
            state.setFree();
        }
        return ai;
    }

    public int executeNonQueryUpdate(String instance, String sql) {
        state = getConnState(instance);
        if (state == null) {
            return 0;
        }
        Connection conn = Connections.get(state);
        int result;
        try {
            st = conn.prepareStatement(sql);
            result = st.executeUpdate();
        } catch (SQLException ex) {
            return -1;
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            }
            state.setFree();
        }
        return result;
    }

    public int executeNonQueryUpdate(String instance, String sql, Object[] params) {
        state = getConnState(instance);
        if (state == null) {
            return 0;
        }
        Connection conn = Connections.get(state);
        int result;
        try {
            st = conn.prepareStatement(sql);
            fillStatementParameters(params);
            result = st.executeUpdate();
        } catch (SQLException ex) {
            return -1;
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            }
            state.setFree();
        }
        return result;
    }

    public ResultSet getResultSet(String instance, String sql) {
        state = getConnState(instance);
        if (state == null) {
            return null;
        }
        Connection conn = Connections.get(state);
        try {
            st = conn.prepareStatement(sql);
            rs = st.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            state.setFree();
            return null;
        }
        return rs;
    }

    public ResultSet getResultSet(String instance, String sql, Object[] params) {
        state = getConnState(instance);
        if (state == null) {
            return null;
        }
        Connection conn = Connections.get(state);
        try {
            st = conn.prepareStatement(sql);
            fillStatementParameters(params);
            rs = st.executeQuery();
        } catch (SQLException ex) {
            Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            state.setFree();
            return null;
        }
        return rs;
    }

    public void closeResultSet() {
        try {
            rs.close();
            st.close();
        } catch (SQLException ex) {
            Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            state.setFree();
        }
    }

    public Object getScalar(String instance, String sql) {
        Object reto = null;
        state = getConnState(instance);
        if (state == null) {
            return null;
        }
        Connection conn = Connections.get(state);
        try {
            st = conn.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                reto = rs.getObject(1);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            }
            state.setFree();
        }
        return reto;
    }

    public Object getScalar(String instance, String sql, Object[] params) {
        Object reto = null;
        state = getConnState(instance);
        if (state == null) {
            return null;
        }
        Connection conn = Connections.get(state);
        try {
            st = conn.prepareStatement(sql);
            fillStatementParameters(params);
            rs = st.executeQuery();
            if (rs.next()) {
                reto = rs.getObject(1);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            }
            state.setFree();
        }
        return reto;
    }

    public Object[] getSingleRow(String instance, String sql) {
        Object[] retos = null;
        state = getConnState(instance);
        if (state == null) {
            return null;
        }
        Connection conn = Connections.get(state);
        try {
            st = conn.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                ArrayList<Object> objs = new ArrayList<>();
                for (int col = 1; col <= rs.getMetaData().getColumnCount(); col++) {
                    objs.add(rs.getObject(col));
                }
                retos = objs.toArray();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            }
            state.setFree();
        }
        return retos;
    }

    public Object[] getSingleRow(String instance, String sql, Object[] params) {
        Object[] retos = null;
        state = getConnState(instance);
        if (state == null) {
            return null;
        }
        Connection conn = Connections.get(state);
        try {
            st = conn.prepareStatement(sql);
            fillStatementParameters(params);
            rs = st.executeQuery();
            if (rs.next()) {
                ArrayList<Object> objs = new ArrayList<>();
                for (int col = 1; col <= rs.getMetaData().getColumnCount(); col++) {
                    objs.add(rs.getObject(col));
                }
                retos = objs.toArray();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
            }
            state.setFree();
        }
        return retos;
    }

    private void fillStatementParameters(Object[] params) throws SQLException {
        if (params != null) {
            int paramnum = 1;
            for (Object param : params) {
                st.setObject(paramnum, param);
                paramnum++;
            }
        }
    }

    public String getInfo() {
        int freeConn = 0;
        for (ConnState s1 : Connections.keySet()) {
            if (s1.isFree()) {
                freeConn++;
            }
        }
        return "PDM info"
                + ": Interfaces: " + connInstances.size()
                + ", Connections: " + Connections.size()
                + ", Free: " + freeConn
                + ", Statement: " + String.valueOf(st)
                + ", Resultset: " + String.valueOf(rs)
                + ", State: " + String.valueOf(state);
    }

    public static String getStaticInfo() {
        int freeConn = 0;
        for (ConnState s1 : Connections.keySet()) {
            if (s1.isFree()) {
                freeConn++;
            }
        }
        return "PDM info"
                + ": Interfaces: " + connInstances.size()
                + ", Connections: " + Connections.size()
                + ", Free: " + freeConn;
    }

    public static String getDTString(Calendar ca) {
        Integer year = ca.get(Calendar.YEAR);
        String s_year = year.toString();
        Integer month = ca.get(Calendar.MONTH);
        month++;
        String s_month = month.toString();
        if (s_month.length() < 2) {
            s_month = "0" + s_month;
        }
        Integer day = ca.get(Calendar.DAY_OF_MONTH);
        String s_day = day.toString();
        if (s_day.length() < 2) {
            s_day = "0" + s_day;
        }
        Integer hour = ca.get(Calendar.HOUR_OF_DAY);
        String s_hour = hour.toString();
        if (s_hour.length() < 2) {
            s_hour = "0" + s_hour;
        }
        Integer minute = ca.get(Calendar.MINUTE);
        String s_minute = minute.toString();
        if (s_minute.length() < 2) {
            s_minute = "0" + s_minute;
        }
        Integer second = ca.get(Calendar.SECOND);
        String s_second = second.toString();
        if (s_second.length() < 2) {
            s_second = "0" + s_second;
        }
        return s_year + "-" + s_month + "-" + s_day + " " + s_hour + ":" + s_minute + ":" + s_second + ".0";
    }

    public static String getDTStringDateOnly(Calendar ca) {
        Integer year = ca.get(Calendar.YEAR);
        String s_year = year.toString();
        Integer month = ca.get(Calendar.MONTH);
        month++;
        String s_month = month.toString();
        if (s_month.length() < 2) {
            s_month = "0" + s_month;
        }
        Integer day = ca.get(Calendar.DAY_OF_MONTH);
        String s_day = day.toString();
        if (s_day.length() < 2) {
            s_day = "0" + s_day;
        }
        return s_year + "-" + s_month + "-" + s_day;
    }

    public static Calendar getCalendarFromTime(Timestamp time) {
        if (time == null) {
            return null;
        }
        Calendar ca = new GregorianCalendar();
        ca.setTimeInMillis(time.getTime());
        return ca;
    }

    public static Calendar getLocalCalendarFromUTCTimestamp(Timestamp ts) {
        Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(ts.getTime() + ca.getTimeZone().getOffset(ca.getTimeInMillis()));
        return ca;
    }

    public static String getHexString(String str) {
        StringBuilder hexstr = new StringBuilder();
        int i = 0;
        while (i < str.length()) {
            Integer ic = str.codePointAt(i);
            hexstr.append(Integer.toHexString(ic));
            hexstr.append("|");
            i++;
        }
        return hexstr.toString();
    }

    public static String getStringFromHex(String hexstr) {
        if (hexstr == null) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        String s = "";
        Integer ci;
        char c;
        int i = 0;
        while (i < hexstr.length()) {
            if (!hexstr.substring(i, i + 1).equals("|")) {
                s = s + hexstr.substring(i, i + 1);
                i++;
            } else {
                try {
                    ci = Integer.parseInt(s, 16);
                } catch (NumberFormatException ex) {
                    Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex);
                    return "HEX parsing error";
                }
                c = (char) ci.intValue();
                str.append(c);
                s = "";
                i++;
            }
        }
        return str.toString();
    }

    public static boolean isConnectionOk(String instance) {
        int errcount = 0;
        while (errcount <= 3) {
            try {
                DriverManager.getConnection(connInstances.get(instance).getUrl(), connInstances.get(instance).getProperties());
                errcount = 0;
                break;
            } catch (SQLException | NullPointerException ex) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, ex1);
                }
                errcount++;
            }
        }
        if (errcount > 0) {
            Logger.getLogger(PDM.class.getName()).log(Level.SEVERE, null, "Error creating connection");
            return false;
        }

        return true;
    }
}
