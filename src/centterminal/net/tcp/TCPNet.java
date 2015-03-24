/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package centterminal.net.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;

/**
 *
 * @author gabesz
 */
public class TCPNet {

    ServerSocket serverSocket;
    byte[] receiveTelegram;
    InetAddress ipAddress;
    int port;

    /**
     * @param port
     * @param ipAddress
     * @throws IOException
     */
    public TCPNet(int port, InetAddress ipAddress) throws IOException {
        serverSocket = new ServerSocket(port); //Az TCP számára Bind -olja a port-ot
        System.out.println("TCP szerver létrehozása a " + port + " porton...");
        serverSocket.setReuseAddress(true);     // Bind hiba elkerülése miatt
        this.port=port;                          //port szám
        this.ipAddress = ipAddress;              //IP Address         
    }

    /**
     *
     * @return receiveTelegram
     * @throws IOException
     */
    public byte[] receiveTelegram() throws IOException {
        Socket socket = serverSocket.accept();
        System.out.println("Kliens (" + socket.getRemoteSocketAddress() + ") kapcsolódott a " + this.port + " porton.");
        

        try {
            /*
             * !!!! A "serverSocket.receive(receivePacket)" -nek meg kell
             * előznie a "receivePacket.getAddress()" -t , mert különben null
             * pointer exception kivétel történik
             */

            // Ellenőrizzük hogy jó címről érkeztek az adtok
            if (socket.getInetAddress().equals(ipAddress)) {
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), "ISO-8859-1"));
                CharBuffer cbuf = CharBuffer.allocate(1024);
                int bufsize = inFromClient.read(cbuf);
                String retString="";
                for(int i=0;i<bufsize;i++){
                    retString = retString + cbuf.get(i);
                }
                // Adatok bemásolása a byte bufferbe
                receiveTelegram = retString.getBytes("ISO-8859-1");
                
            } else {
                //Nem jó helyről érkeztek az adatok
                socket.close();
                receiveTelegram = null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return receiveTelegram;
    }

    /**
     *
     * @param sendtelegram
     * @throws IOException
     */
    public void sendTelegram(byte[] sendtelegram) throws IOException {
        Socket socket = serverSocket.accept();
        DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
        String str = new String(sendtelegram, "ISO-8859-1");
        outToClient.writeBytes(str);
    }
}
