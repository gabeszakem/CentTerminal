/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centterminal;

import centterminal.db.Node;
import centterminal.db.PLCSignals;
import centterminal.frame.CentterminalFrame;
import centterminal.net.tcp.GetMessage;
import centterminal.net.tcp.Message;
import centterminal.net.tcp.TCPConnectionClient;
import centterminal.net.udp.UDPConnectionServer;
import centterminal.record.Record;
import centterminal.setup.SetupDataManager;
import centterminal.sql.SQL;
import centterminal.tools.ActualDate;
import centterminal.tools.Arguments;
import centterminal.tools.Beep;
import centterminal.tools.CT;
import centterminal.tools.Debug;
import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.Calendar;
import java.util.Deque;
import javax.swing.JOptionPane;

/**
 *
 * @author gkovacs02
 */
public class CentTerminal {

    /**
     * Debugolás engedélyezése, Napi loggolással
     */
    public static Debug debug = new Debug(true, 1);

    /**
     * SQL adatbázis kapcsolat létrehozása
     */
    public static SQL sql;

    /**
     * Az SQL adatbázisból vett információk
     */
    public static Node node;
    /**
     * Állásidő terminal ablak
     */
    public static CentterminalFrame frame;
    /**
     * UDP kommunikáció a PLC-vel
     */
    public static UDPConnectionServer udp;
    /**
     * TCP kommunikáció az állásidő szerverrel
     */
    public static TCPConnectionClient tcp;
    /*Az állásidő szervernek küldendő üzeneteket tároló osztály*/

    /**
     * A küldendő üzenet struktúráját tartalmazó kód
     */
    public static Message sendMessage;
    /**
     * AZ állásidő szervertől kapott üzenetek
     */
    public static GetMessage receiveMessage;
    /**
     * A PLC-től kapott jelek
     */
    public static PLCSignals pLCSignals;
    /**
     * Az UDP kapcsolat ellenőrzésére szolgál: utolsó érkezett üzenet időpontja
     */
    public static long lastConnectionTime;
    /**
     * Státusz jelzés az állásidő szerver felé. 1 ha minden ok. -10003 ha plc
     * kommnunikációs hiba van.
     */
    public static int status = 0;
    /**
     * A rögzitett rekordok tárolása
     */
    public static Deque<Record> records;

    /**
     * Az actuális rekordot tartalmazó osztály.
     */
    public static Record actRecord;
    /**
     * Üzemmód: 1:Termelés 2:Üzemszünet 3:TMK Inicializálás a termelésre.
     */
    public static int mode = 1;
    /**
     * A centralográf szerver ip címe.
     */
    public static String CENTRALOGRAFSERVERIPADDRESS = "10.3.10.203";

    /**
     * SQL elérését tartalmazó cím.
     */
    public static String SQLURL;

    /**
     * Az SQL adatbázis portja.
     */
    public static final String SQLPORT = "5432";

    /**
     * SQL felhasználó neve.
     */
    public static final String SQLUSER = "centcli";

    /**
     * SQL jelszó.
     */
    public static final String SQLPASSWORD = "centcli";

    /**
     * Beep engedélyezése.
     */
    public static boolean beep = true;

    /**
     * Beep kezdetének időpontja: (allapbeállítás 2 perc).
     */
    public static long beepTimeOut = 120000;

    /**
     * Beep hangjelzés osztály.
     */
    public static Beep beepClass;
    /**
     * Állásidő megosztáshoz az előző óra.
     */
    public static int prevHour = 0;

    /**
     * A számitott idő
     */
    public static CT calculatedTime;

    /**
     * A számított idő használatának engedélyezése;
     */
    public static boolean calculatedTimeEnable = true;

    /**
     * Verziószám [főverzió].[ÉVutolsó3számjegy][hónap][nap][aznapi
     * fordításszáma]
     */
    public static String VERSION = "1.0160125001";

    /**
     * @param args the command line arguments
     * @beep:false -Beepelés letiltása
     * @beepTimeOut:10 -A beepelés kezdete mp-ben
     * @centralografIpAddress:10.1.49.200 -A centralográf ip címe
     * @calculatedTimeEnable:false A számított rendszeridő letíltása
     */
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch", "ConvertToStringSwitch", "UseSpecificCatch"})
    public static void main(String[] args) {

        /**
         * Argumentumok feldolgozása
         */
        Arguments arg = new Arguments(args);
        /**
         * Környezeti változók beállítása
         */
        SQLURL = "jdbc:postgresql://" + CENTRALOGRAFSERVERIPADDRESS + ":" + SQLPORT + "/centralograf";

        beepClass = new Beep();
        /**
         * SQL kapcsolat definiálása
         */
        sql = new SQL(SQLURL, SQLUSER, SQLPASSWORD);
        /**
         * Aktuális állapot inicializálása
         */
        actRecord = new Record();
        /**
         * A recordok inicializálása
         */
        records = new ArrayDeque<>();
        /**
         * Alkalmazás beállítása
         */
        SetupDataManager.init();
        /**
         * A PLC jelek inicializása
         */
        pLCSignals = new PLCSignals();
        /**
         * Frame létrehozása
         */
        frame = new CentterminalFrame();
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            javax.swing.SwingUtilities.updateComponentTreeUI(frame);
            javax.swing.SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            debug.printDebugMsg(null, CentTerminal.class.getName(), "Hiba a look and feelben :", ex);
        }

        calculatedTime = new CT();

        /**
         * UDP kapcsolat létrehozása
         */
        try {
            int bufferSize = 2048;
            udp = new UDPConnectionServer(pLCSignals, node.plc_port, bufferSize, InetAddress.getByName(node.plc_ip_address));
            udp.start();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            String errmsg = "Hiba az udp kapcsolatban (PLC) :";
            debug.printDebugMsg(null, CentTerminal.class.getName(), errmsg, ex);
            JOptionPane.showMessageDialog(null, errmsg + "\n" + ex.getMessage() + "\n", "Hiba", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
        /**
         * sendMessage létrehozása
         */
        sendMessage = new Message();
        /**
         * receiveMessage létrehozása
         */
        receiveMessage = new GetMessage();

        /**
         * TCP kapcsolat létrehozása
         */
        try {
            int bufferSize = 2048;

            tcp = new TCPConnectionClient(receiveMessage, sendMessage, node.server_port, bufferSize, InetAddress.getByName(CENTRALOGRAFSERVERIPADDRESS));
            tcp.start();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            String errmsg = "Hiba az tcp kapcsolatban (Állásidő Szerver) :";
            debug.printDebugMsg(null, CentTerminal.class.getName(), errmsg, ex);
            JOptionPane.showMessageDialog(null, errmsg + "\n" + ex.getMessage() + "\n", "Hiba", JOptionPane.ERROR_MESSAGE);
            if (frame.serverPanel.getBackground() != frame.communicationFaultColor) {
                frame.serverPanel.setBackground(frame.communicationFaultColor);
            }
        }

        frame.setVisible(true);

        Thread timer = new Thread("Timer(másodpercenként futó)") {
            @Override
            public void run() {
                while (true) {
                    try {

                        synchronized (this) {
                            wait(1000);
                            Calendar now;
                            if (calculatedTimeEnable) {
                                now = calculatedTime.getCalendar();
                            } else {
                                now = Calendar.getInstance();
                                /**
                                 * Az idő szinkronizálása az állásidő
                                 * szerverrel.
                                 */
                                now.add(Calendar.MILLISECOND, (int) sql.getDifTime());
                            }
                            int hour = now.get(Calendar.HOUR_OF_DAY);
                            long elapseTime;
                            if (calculatedTimeEnable) {
                                elapseTime = calculatedTime.getTime() - lastConnectionTime;
                            } else {
                                elapseTime = System.currentTimeMillis() - lastConnectionTime;
                            }
                            if ((elapseTime) > 5000) {
                                pLCSignals.plantStatus = 0;
                                status = -10003;
                                frame.commErr();
                                if (frame.plcPanel.getBackground() != frame.communicationFaultColor) {
                                    frame.plcPanel.setBackground(frame.communicationFaultColor);
                                }

                                //debug.printDebugMsg(null, CentTerminal.class.getName(), "Kommunikációs hiba a plc-vel (UDP)");
                            } else {
                                status = 1;
                                frame.refresh();
                                frame.commOk();

                                if (frame.plcPanel.getBackground() != frame.communicationOkColor) {
                                    frame.plcPanel.setBackground(frame.communicationOkColor);
                                }

                                if (pLCSignals.plantStatus == 0) {
                                    if (((hour == 6) || (hour == 14) || (hour == 22)) && ((hour - prevHour) == 1)) {
                                        // if ((hour - prevHour) == 1) {
                                        /**
                                         * Műszakváltás
                                         */
                                        Long shiftChange = ActualDate.shiftDate(hour);
                                        frame.shiftChange(shiftChange);
                                    }
                                }
                            }
                            prevHour = hour;
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        };
        timer.start();
    }

}
