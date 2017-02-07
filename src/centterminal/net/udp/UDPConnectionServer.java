/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package centterminal.net.udp;

import centterminal.net.common.FillDataFromBuffer;
import centterminal.CentTerminal;
import static centterminal.CentTerminal.calculatedTime;
import static centterminal.CentTerminal.calculatedTimeEnable;
import java.io.IOException;
import java.net.InetAddress;
import javax.swing.JLabel;

/**
 *
 * @author gabesz
 */
public class UDPConnectionServer extends Thread {

    @SuppressWarnings("FieldMayBeFinal")
    private UDPNet udp;
    @SuppressWarnings("FieldMayBeFinal")
    private Object object;
    @SuppressWarnings("FieldMayBeFinal")
    private int bufferSize;
    @SuppressWarnings("FieldMayBeFinal")
    private JLabel label;
    @SuppressWarnings("FieldMayBeFinal")
    private short lastPlantStatus = 1;
    @SuppressWarnings("FieldMayBeFinal")
    private static boolean firstCycleFlag = true;

    /**
     *
     * @param Object
     * @param plcPort
     * @param bufferSize
     * @param ipAddress
     * @throws IOException
     */
    public UDPConnectionServer(Object Object, int plcPort, int bufferSize, InetAddress ipAddress) throws IOException {
        udp = new UDPNet(plcPort, bufferSize, ipAddress);  //Új UDP szerver inditása
        this.object = Object;
        this.bufferSize = bufferSize;
    }

    @Override
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void run() {
        while (true) {
            try {
                byte[] receiveTelegram = udp.receiveTelegram();
                if (receiveTelegram != null) {
                    // Adatok fogadása az UDP portról
                    // Adatok átalakítása, és betöltése a DB alapján elkészült osztályba.
                    FillDataFromBuffer.load(this.object, receiveTelegram);
                    // Az adatok kiírása tesztelés céljából
                    if (calculatedTimeEnable) {
                        CentTerminal.lastConnectionTime = calculatedTime.getTime();
                    } else {
                        CentTerminal.lastConnectionTime = System.currentTimeMillis();
                    }
                    if (CentTerminal.mode == 1) {
                        if (CentTerminal.pLCSignals.plantStatus == 0) {
                            /**
                             * A berendezés most állt meg
                             */
                            if (lastPlantStatus == 1 || firstCycleFlag) {
                                CentTerminal.frame.runToStop();
                            }
                        } else if (CentTerminal.pLCSignals.plantStatus == 1) {
                            /**
                             * A berendezés elindul leállás után
                             */
                            if (lastPlantStatus == 0) {
                                CentTerminal.frame.stopToRun();
                            }
                        }
                    }
                    lastPlantStatus = CentTerminal.pLCSignals.plantStatus;
                    firstCycleFlag = false;
                }

            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }
}
