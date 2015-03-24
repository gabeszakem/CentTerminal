/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package centterminal.record;

/**
 *
 * @author gkovacs02
 * A tárolt rekord szerkezete.
 */
public class Record {

    /**
     *Leállás kezdete.
     */
    public long downtimeStart=0;

    /**
     *Leállás kezdete (Szövegesen).
     */
    public String downtimeStartString="";

    /**
     *Leállás vége
     */
    public long downtimeStop=-1;

    /**
     *Leállás vége (szövegesen).
     */
    public String downtimeStopString="";

    /**
     *Állás Kódja.
     */
    public int code;
}
