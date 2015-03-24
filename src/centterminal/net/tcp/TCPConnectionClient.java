/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package centterminal.net.tcp;

import centterminal.CentTerminal;
import static centterminal.CentTerminal.frame;
import java.io.IOException;
import java.net.InetAddress;
import centterminal.net.common.FillDataFromBuffer;
import centterminal.net.common.FillDataToBuffer;
import centterminal.record.Record;

/**
 *
 * @author gabesz
 */
public class TCPConnectionClient extends Thread {

    @SuppressWarnings("FieldMayBeFinal")
    private TCPNetClient tcp;
    @SuppressWarnings("FieldMayBeFinal")
    private Object receiveMessageClass;
    @SuppressWarnings("FieldMayBeFinal")
    private Object sendMessageClass;
    @SuppressWarnings("FieldMayBeFinal")
    private boolean rw;
    @SuppressWarnings("FieldMayBeFinal")
    private int bufferSize;
    @SuppressWarnings("FieldMayBeFinal")
    private int plcPort;
    @SuppressWarnings("FieldMayBeFinal")
    private InetAddress ipAddress;

    /**
     *
     * @param resseiveMessage
     * @param sendMessage
     * @param plcPort
     * @param bufferSize
     * @param ipAddress
     * @throws IOException
     */
    public TCPConnectionClient(Object resseiveMessage, Object sendMessage, int plcPort, int bufferSize, InetAddress ipAddress) throws IOException {
        this.plcPort = plcPort;
        this.ipAddress = ipAddress;
        this.receiveMessageClass = resseiveMessage;
        this.sendMessageClass = sendMessage;
        this.bufferSize = bufferSize;

    }

    @Override
    @SuppressWarnings({"UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch"})
    public void run() {
        while (true) {
            try {
                synchronized (this) {

                    wait(5000);
                    tcp = new TCPNetClient(this.plcPort, this.ipAddress);
                    if (CentTerminal.frame.serverPanel.getBackground() != CentTerminal.frame.communicationOkColor) {
                        CentTerminal.frame.serverPanel.setBackground(CentTerminal.frame.communicationOkColor);
                    }
                    break;
                }
            } catch (Exception ex) {

                if (frame.serverPanel.getBackground() != frame.communicationFaultColor) {
                    frame.serverPanel.setBackground(frame.communicationFaultColor);
                }
            }
        }
        while (true) {
            if (tcp.clientSocket.isClosed()) {
                synchronized (this) {
                    try {
                        wait(500);
                        tcp = new TCPNetClient(this.plcPort, this.ipAddress);
                        if (frame.serverPanel.getBackground() != frame.communicationOkColor) {
                            frame.serverPanel.setBackground(frame.communicationOkColor);
                        }
                    } catch (Exception ex) {
                        if (frame.serverPanel.getBackground() != frame.communicationFaultColor) {
                            frame.serverPanel.setBackground(frame.communicationFaultColor);
                        }
                    }
                }
            }
            if (!tcp.clientSocket.isClosed()) {
                try {
                    byte[] receiveTelegram = tcp.receiveTelegram();
                    if (receiveTelegram != null) {

                        // Adatok átalakítása, és betöltése a DB alapján elkészült osztályba.
                        FillDataFromBuffer.load(this.receiveMessageClass, receiveTelegram);
                        //System.out.println("A fogadott üzenet: " + CentTerminal.receiveMessage.message);
                        if (CentTerminal.receiveMessage.message == 'D') {
                            CentTerminal.sendMessage = new Message();
                            CentTerminal.sendMessage.status = CentTerminal.status;
                            CentTerminal.sendMessage.aDowntime = CentTerminal.actRecord.downtimeStart;
                            CentTerminal.sendMessage.aCode = CentTerminal.actRecord.code;
                            boolean sendrecord = false;
                            if (!CentTerminal.records.isEmpty()) {
                                Record record = CentTerminal.records.getFirst();
                                CentTerminal.sendMessage.rDowntimeStart = record.downtimeStart;
                                CentTerminal.sendMessage.rDowntimeEnd = record.downtimeStop;
                                CentTerminal.sendMessage.rCode = record.code;
                                sendrecord = true;
                                System.out.println("Állásrecord küldése: "
                                        + record.downtimeStartString + " "
                                        + record.downtimeStopString + " "
                                        + record.code);

                            }
                            String sendMessage = "Allasidőnek küldött rekord: " + CentTerminal.sendMessage.status
                                    + " " + CentTerminal.sendMessage.aDowntime
                                    + " " + CentTerminal.sendMessage.aCode
                                    + " \t" + CentTerminal.sendMessage.rDowntimeStart
                                    + " " + CentTerminal.sendMessage.rDowntimeEnd
                                    + " " + CentTerminal.sendMessage.rCode;

                            //System.out.println(sendMessage);
                            this.sendTelegram(CentTerminal.sendMessage);
                            if (sendrecord) {
                                CentTerminal.debug.printDebugMsg(null, TCPConnectionClient.class.getName(), sendMessage);
                                CentTerminal.records.removeFirst();
                            }
                        }
                    }

                } catch (Exception e) {
                    try {
                        e.printStackTrace(System.err);
                        tcp.clientSocket.close();
                    } catch (IOException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }
        }
    }

    /**
     *
     * @param aObject
     */
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch"})
    public void sendTelegram(Object aObject) {
        try {
            tcp.sendTelegram(FillDataToBuffer.load(aObject, bufferSize));
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
            try {
                tcp.clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
