/*
 Üzenet küldés az álásidő szervernek
 */
package centterminal.net.tcp;

/**
 *
 * @author gkovacs02
 */
public class Message {

    /**
     * Állapot jelző bit 1: Minden rendben -10003 Kommunikációs hiba a plc-vel
     */
    public int status = 0;
    /**
     * Aktuaális állapot ha termelnek akkor minden tagja nulla
     */

    /*Leállás időpontja*/
    public long aDowntime = 0;

    /**
     * Leállás Kódja ha nem ütött kódot akkor 0;
     */
    public int aCode = 0;
    
    /**
     * Leállás kezdete unixtime. (Record).
     */
    public long rDowntimeStart = 0;

    /**
     *Leállás vége unixtime.(Record).
     */
    public long rDowntimeEnd = 0;

    /**
     * Leállás kódja.(Record).
     */
    public int rCode = 0;
}
