/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package centterminal.net.udp;

import static centterminal.CentTerminal.debug;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author gabesz
 */
public class UDPNet {

    DatagramSocket socket;
    byte[] receiveTelegram;
    InetAddress ipAddress;
    private int port;

    /**
     *
     * @param port
     * @param buffersize
     * @param ipAddress
     * @throws IOException
     */
    public UDPNet(int port, int buffersize, InetAddress ipAddress) throws IOException {
        socket = new DatagramSocket(port); // Az UDP számára Bind -olja a port-ot
        socket.setReuseAddress(true);     // Bind hiba elkerülése miatt
        receiveTelegram = new byte[buffersize]; //byte tömb a telegram fogadásához
        this.ipAddress = ipAddress;             // A partner IP Címe
        this.port = port;                       //port szám
    }

    /**
     *
     * @return receiveTelegram
     * @throws IOException
     */
    public byte[] receiveTelegram() throws IOException {
        boolean result = false;
        // Csomagok fogadása.
        DatagramPacket receivePacket = new DatagramPacket(receiveTelegram, receiveTelegram.length);
        // Csomagok fogadása socketben.
        socket.receive(receivePacket);
        try {
            /*
             * !!!! A "socket.receive(receivePacket)" -nek meg kell
             * előznie a "receivePacket.getAddress()" -t , mert különben null
             * pointer exception kivétel történik
             */

            // Ellenőrizzük hogy jó címről érkeztek az adtok
            if (receivePacket.getAddress().equals(ipAddress)) {
                result = true;
            } else {
                //Nem jó helyről érkeztek az adatok
                result = false;
                String msg = "Hiba az udp kapcsolatban, a " + receivePacket.getAddress() + " című géptől jött üzenet!";
                debug.printDebugMsg(null, UDPNet.class.getName(), msg);
                System.err.println(msg);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (result) {
            return receivePacket.getData();
        } else {
            return null;
        }

    }

    /**
     *
     * @param sendtelegram
     * @throws IOException
     */
    public void sendTelegram(byte[] sendtelegram) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(sendtelegram, sendtelegram.length, ipAddress, port);
        socket.send(sendPacket);
    }
}
