/*
 * Beállítási adatok kezelése
 * 
 */
package centterminal.setup;

import centterminal.CentTerminal;
import centterminal.tools.IpAddress;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

/**
 *
 * @author Fehér Dávid, Gabesz
 */
public class SetupDataManager {
    private static String ipaddress;

    private SetupDataManager() {
    }

    /**
     * inicializálás.
     */
    public static void init() {

        /*
         * Adathelyesség ellenőrzés miatt a hibatároló bit létrehozása, és
         * false -ba állítása
         */
        boolean error = false;
        /*
         * IP_ADDRESS vizsgálata
         */
        try {
            ipaddress = IpAddress.getLocalHostLANAddress().getHostAddress();
            System.out.println("A számítógép ip cime: " + ipaddress);

            CentTerminal.node = CentTerminal.sql.getNode(ipaddress);
            System.out.println(CentTerminal.node.node_name);

        } catch (UnknownHostException ex) {
            //popup stb
            ex.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, "Kivétel történt az adatok inicializálása közben:\n" + ex, "Hiba", JOptionPane.ERROR_MESSAGE);
            CentTerminal.debug.printDebugMsg(null, SetupDataManager.class.getName(),
                    "Kivétel történt az adatok inicializálása közben", ex);
            System.exit(-1);
        } catch (Exception ex) {
            //popup stb
            ex.printStackTrace(System.err);
            if (CentTerminal.node == null) {
                JOptionPane.showMessageDialog(null, "Kivétel történt az adatok inicializálása közben:\n" + "\"A "+ ipaddress+
                        "\" ip cimmel nincs regisztrált számítógép a centralográf adatbázisban.", "Hiba", JOptionPane.ERROR_MESSAGE);
                CentTerminal.debug.printDebugMsg(null, SetupDataManager.class.getName(),
                        "Kivétel történt az adatok inicializálása közben:\n" + "\""+ ipaddress+
                        "\" ip cimmel nincs regisztrált számítógép a centralográf adatbázisban.", ex);
            } else {
                JOptionPane.showMessageDialog(null, "Kivétel történt az adatok inicializálása közben:\n" + ex, "Hiba", JOptionPane.ERROR_MESSAGE);
                CentTerminal.debug.printDebugMsg(null, SetupDataManager.class.getName(),
                        "Kivétel történt az adatok inicializálása közben", ex);
            }
            System.exit(-1);
        }

    }
}
