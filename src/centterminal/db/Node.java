/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centterminal.db;

import java.sql.Timestamp;

/**
 *
 * @author gkovacs02
 * Adatbázis nodes tábla adatainak a letárolására szolgáló osztály
 */
public class Node {

    /**
     *@id adatbázis növekményes kulcs azonositó 
     */
    public Integer id;

    /**
     *@nodeszám 
     */
    public Integer node_addr;

    /**
     *A berendezés neve
     */
    public String node_name ;

    /**
     *Aktuális leálláés kezdete
     */
    public Timestamp current_downtime_start;

    /**
     * Aktuális leállás kódja
     */
    public Integer current_downtime_code;

    /**
     * Annak a gépnek az ip címe, amelyiken a centralográf terminál program fut
     */
    public String terminal_ip_address;

    /**
     * A centralográf szerverrel a tcp kapcsolat portja
     */
    public Integer server_port;

    /**
     *A PLC IP címe
     */
    public String plc_ip_address;

    /**
     * Az udp kapcsolat (PLC-cel) portja
     */
    public Integer plc_port;
}
