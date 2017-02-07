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
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.Arrays;

/**
 *
 * @author gabesz
 */
public class TCPNetClient {

    /**
     *Kliens socket
     */
    public Socket clientSocket;
    byte[] receiveTelegram;
    InetAddress ipAddress;
    int port;

    /**
     * @param port
     * @param ipAddress
     * @throws IOException
     */
    public TCPNetClient(int port, InetAddress ipAddress) throws IOException {
        clientSocket = new Socket(ipAddress,port); //Az TCP számára Bind -olja a port-ot
        System.out.println("TCP kliens csatlakozik a " + port + " porton a "+ipAddress.getHostName() +" géphez");
       // clientSocket.setReuseAddress(true);     // Bind hiba elkerülése miatt
        this.port=port;                          //port szám
        this.ipAddress = ipAddress;              //IP Address         
    }

    /**
     *
     * @return receiveTelegram
     * @throws IOException
     */
    public byte[] receiveTelegram() throws IOException {
        
        try {
            /*
             * !!!! A "clientSocket.receive(receivePacket)" -nek meg kell
             * előznie a "receivePacket.getAddress()" -t , mert különben null
             * pointer exception kivétel történik
             */

            // Ellenőrizzük hogy jó címről érkeztek az adtok
           
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "ISO-8859-1"));
                CharBuffer cbuf = CharBuffer.allocate(1024);
                int bufsize = inFromClient.read(cbuf);
                String retString="";
                for(int i=0;i<bufsize;i++){
                    retString = retString + cbuf.get(i);
                }
                // Adatok bemásolása a byte bufferbe
                receiveTelegram = retString.getBytes("ISO-8859-1");
                
                
           
        } catch (Exception e) {
            e.printStackTrace(System.err);
            clientSocket.close();
        }
        return receiveTelegram;
    }

    /**
     *
     * @param sendtelegram
     * @throws IOException
     */
    public void sendTelegram(byte[] sendtelegram) throws IOException {
        DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
        outToClient.write(sendtelegram);
        //System.out.println(Arrays.toString(sendtelegram));
    }
}
