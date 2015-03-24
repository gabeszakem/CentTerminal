/*
 *Ez az osztály a rendszeridőt számítja ki. Problémát okozhat ha a windowsban átállítják az órát. 
 *Ezért a java virtuális gép futási idejéből számítjuk a rendszerídőt.
 */

package centterminal.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author gabesz
 */
public class CT {

    private long diffTime;

    /**
     *Calculated Time létrehozása
     */
    public CT() {
        this.diffTime = System.currentTimeMillis() - this.currentTimeMillis();
    }

    /**
     *
     * @param currentTimeMillis
     */
    public void setTime(long currentTimeMillis) {
        this.diffTime = currentTimeMillis - this.currentTimeMillis();
    }
    
    /**
     *
     * @return
     */
    public long getTime(){
        return currentTimeMillis()+diffTime;
    }
    
    /**
     *
     * @return
     */
    public Calendar getCalendar(){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND,(int)(this.getTime()-System.currentTimeMillis()));
        return calendar;
    }
    
    /**
     *
     * @return
     */
    public String actualDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = this.getDate();
        return dateFormat.format(date);
    }
    
   
    
    /**
     *
     * @param hour
     * @return
     */
    @SuppressWarnings("FinallyDiscardsException")
    public long shiftDate(int hour) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd ");
        Date date = new Date(this.getTime());
        String time;
        if (hour > 9) {
          time=  dateFormat.format(date) + Integer.toString(hour) + ":00:00";
        } else {
           time= dateFormat.format(date) + "0" + Integer.toString(hour) + ":00:00";
        }
        DateFormat dt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long uTime=0;
        try{
            uTime=dt.parse(time).getTime();
        }catch(ParseException ex){
            ex.printStackTrace(System.err);
        }finally{
            return uTime;
        }
    }
    
    /**
     *
     * @return
     */
    public Date getDate(){
        Date date= new Date(this.getTime());
        return date;
    }
    
    private long currentTimeMillis(){
        return System.nanoTime() / 1000000l;
    }

}
