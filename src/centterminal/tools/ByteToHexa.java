/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package centterminal.tools;

/**
 *
 * @author gkovacs02
 */
public class ByteToHexa {
    
    /**
     *
     * @param bcd
     * @return
     */
    public static int byteToHexa(byte bcd){
        int hex=(bcd/16)*10+bcd%16;
        return hex;
    }
    
}
