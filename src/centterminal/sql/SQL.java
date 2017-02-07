/*
 * SQL lekérdezések
 */
package centterminal.sql;

/**
 *
 * @author Gabesz
 */
import centterminal.db.Node;
import centterminal.CentTerminal;
import static centterminal.CentTerminal.debug;
import static centterminal.CentTerminal.frame;
import centterminal.tools.ActualDate;
import java.sql.*;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 *
 * @author gkovacs02
 */
public class SQL {

    @SuppressWarnings("FieldMayBeFinal")
    private String url;
    @SuppressWarnings("FieldMayBeFinal")
    private String user;
    @SuppressWarnings("FieldMayBeFinal")
    private String password;
    private static long difTime = 0;

    /**
     * SQL kapcsolat létrehozása.
     */
    public SQL() {
        this.url = "jdbc:postgresql://allasido.dunaferr.hu:5432/centralograf";
        //this.url = "jdbc:postgresql://10.1.39.11:5432/centralograf";
        //this.url = "jdbc:postgresql://hh-allasido.ms.dunaferr.hu:5432/centralograf";
        this.user = "centcli";
        this.password = "centcli";
    }

    /**
     *
     * @param url
     * @param user
     * @param password
     */
    public SQL(String url, String user, String password) {
        //this.url = "jdbc:postgresql://allasido.dunaferr.hu:5432/centralograf";
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private String serverTime() {
        Connection con = null;
        Statement st = null;
        String time = ActualDate.actualDate();
        String query = "";
        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            query = "SELECT localtimestamp";
            //System.out.println(query);
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                time = rs.getTimestamp(1).toString();
            }
            

        } catch (SQLException ex) {
            /*
             * Hiba esetén a hibát kiírjuk
             */
            ex.printStackTrace(System.err);
            debug.printDebugMsg(null, SQL.class.getName(), "Hiba a következő lekérdezéskor: " + query, ex);
           
        } finally {
            closeConnection(con, st);
        }
        return time.split("\\.")[0];
    }

    /**
     *
     * @return
     */
    public long serverUnixTime() {
        Connection con = null;
        Statement st = null;
        long time;
        if (CentTerminal.calculatedTimeEnable) {
            time = CentTerminal.calculatedTime.getTime();
        } else {
            time = System.currentTimeMillis();
        }

        String query = "";
        try {
            /**
             * Ha nincs kapcsolat az adatbázissal akkor a hibát detektáljuk
             * gyorsabban
             */
            DriverManager.setLoginTimeout(1);
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            query = "SELECT extract (epoch from localtimestamp)*1000::bigint";
            st.setQueryTimeout(1);
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                long temp = time;
                time = rs.getLong(1);
                if (CentTerminal.calculatedTimeEnable) {
                    CentTerminal.calculatedTime.setTime(time);
                    difTime = 0;
                } else {
                    difTime = time - temp;
                }
            }
            if (frame.dbPanel.getBackground() != frame.communicationOkColor) {
                frame.dbPanel.setBackground(frame.communicationOkColor);
            }
        } catch (SQLException ex) {
            /*
             * Hiba esetén a hibát kiírjuk
             */
            ex.printStackTrace(System.err);
            debug.printDebugMsg(null, SQL.class.getName(), "Hiba a következő lekérdezéskor: " + query, ex);
            if (CentTerminal.calculatedTimeEnable) {
                time = CentTerminal.calculatedTime.getTime();
            } else {
                time = System.currentTimeMillis();
            }
            time += difTime;
             if (frame.dbPanel.getBackground() != frame.communicationFaultColor) {
                frame.dbPanel.setBackground(frame.communicationFaultColor);
            }
        } finally {
            closeConnection(con, st);
        }
        return time;
    }

    /**
     *
     * @param ipaddress
     * @return
     */
    public Node getNode(String ipaddress) {

        Connection con = null;
        Statement st = null;
        Node node = null;

        try {
            DriverManager.setLoginTimeout(10);
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            String query = "SELECT * FROM nodes WHERE terminal_ip_address = '" + ipaddress + "'";
            //System.out.println(query);
            ResultSet rs = st.executeQuery(query);
            if (rs.next()) {
                node = new Node();
                node.id = rs.getInt("id");
                node.node_addr = rs.getInt("node_addr");
                node.node_name = rs.getString("node_name");
                node.terminal_ip_address = rs.getString("terminal_ip_address");
                node.server_port = rs.getInt("server_port");
                node.plc_ip_address = rs.getString("plc_ip_address");
                node.plc_port = rs.getInt("plc_port");
                node.current_downtime_code = rs.getInt("current_downtime_code");
                node.current_downtime_start = rs.getTimestamp("current_downtime_start");
            }
        } catch (SQLException ex) {
            /*
             * Hiba esetén a hibát kiírjuk
             */
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, "Kivétel történt az SQL adatbázis kezelése közben:\n" + ex, "Hiba", JOptionPane.ERROR_MESSAGE);
            debug.printDebugMsg(null, SQL.class.getName(),
                    "getNode(" + ipaddress + ") Hiba történt az adatbázis kapcsolódása közben", ex);
            System.exit(-1);
        } finally {
            closeConnection(con, st);
        }
        return node;
    }

    /**
     * Helyi kódszöveg gyorsítótár létrehozása. Azért van rá szükség, hogy ne
     * kelljen mindig az adatbázisból lekérdezni egyesével a kódokat.
     *
     * @param nodeAddress
     * @return
     */
    public HashMap<Integer, String> getCodes(Integer nodeAddress) {
        HashMap<Integer, String> newCodeTextCacheMap = new HashMap<>();
        Connection con = null;
        Statement st = null;
        Node node = null;

        try {

            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            String query = "SELECT * FROM node_" + nodeAddress.toString() + "_codes ORDER BY code ASC;";
            System.out.println(query);
            CentTerminal.debug.printDebugMsg(null, SQL.class.getName(), "Plant.createCodeTextCache(): Álláskód szövegek gyorsítótárazása...");

            ResultSet rs = st.executeQuery(query);

            if (rs != null) {
                while (rs.next()) {
                    newCodeTextCacheMap.put(rs.getInt("code"), rs.getString("description"));
                }
                rs.close();
            }
            //System.out.println("Az álláskód szövegek gyorsítótárazása sikerült");
            debug.printDebugMsg(null, SQL.class.getName(), "Az álláskód szövegek gyorsítótárazása sikerült");

        } catch (SQLException ex) {
            System.out.println("Kivétel történt az álláskódok adatbázisból való lekérdezése közben: ");
            CentTerminal.debug.printDebugMsg(null, SQL.class.getName(), "Kivétel történt az álláskódok adatbázisból való lekérdezése közben: ", ex);

        } finally {
            closeConnection(con, st);
        }

        return newCodeTextCacheMap;
    }
    /*
     * Kapcsolat lezárása
     */

    private void closeConnection(Connection con, Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
                CentTerminal.debug.printDebugMsg(null, SQL.class.getName(), "Hiba történt az SQL kapcsolat bezárásakor", ex);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
                CentTerminal.debug.printDebugMsg(null, SQL.class.getName(), "Hiba történt az SQL kapcsolat bezárásakor", ex);
            }
        }
    }

    /*
     * Kapcsolat lezárása
     */
    private void closeConnection(Connection con, PreparedStatement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
                CentTerminal.debug.printDebugMsg(null, SQL.class.getName(), "Hiba történt az SQL kapcsolat bezárásakor", ex);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace(System.err);
                CentTerminal.debug.printDebugMsg(null, SQL.class.getName(), "Hiba történt az SQL kapcsolat bezárásakor", ex);
            }
        }
    }

    /**
     *
     * @return
     */
    public long getDifTime() {
        return difTime;
    }
}
