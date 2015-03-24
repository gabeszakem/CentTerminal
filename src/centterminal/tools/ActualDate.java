/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centterminal.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author gkovacs02
 */
public class ActualDate {

    /**
     *
     * @return
     */
    public synchronized static String actualDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     *
     * @param time
     * @return
     */
    public synchronized static String actualDate(long time) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(time);
        return dateFormat.format(date);
    }

    /**
     *
     * @param hour
     * @return
     */
    @SuppressWarnings("FinallyDiscardsException")
    public synchronized static long shiftDate(int hour) {
        //if(hour==6 || hour==14 || hour== 22){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd ");
        Date date = new Date();
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
       /* }else{
            return 0;
        }*/
    }
    
    /**
     *
     * @param time
     * @return
     * @throws Exception
     */
    public static Long longFromString(String time) throws Exception{
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date=dateFormat.parse(time);
        return date.getTime();      
    }

}
