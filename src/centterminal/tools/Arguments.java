/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centterminal.tools;

import centterminal.CentTerminal;
import static centterminal.CentTerminal.beep;
import static centterminal.CentTerminal.beepTimeOut;
import static centterminal.CentTerminal.debug;
import static centterminal.CentTerminal.calculatedTimeEnable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 *
 * @author gkovacs02
 */
public final class Arguments {

    /**
     *
     * @param args
     */
    @SuppressWarnings({"UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch", "ConvertToStringSwitch"})
    public Arguments(String[] args) {
        debug.printDebugMsg(null, CentTerminal.class.getName(), "Operációs rendszer típusa: " + System.getProperty("os.name"));

        /**
         * Argumentumok kezelése
         */
        for (String arg : args) {
            String[] setupData = arg.split(":");
            if (setupData.length == 2) {

                String command = setupData[0];
                String value = setupData[1];

                if (command.equals("beep")) {
                    if (value.equals("false")) {
                        beep = false;
                        debug.printDebugMsg(null, CentTerminal.class.getName(), "beep:false");
                    }
                } else if (command.equals("beepTimeOut")) {
                    try {
                        beepTimeOut = Integer.parseInt(value) * 1000;
                        debug.printDebugMsg(null, CentTerminal.class.getName(), "beepTimeOut: " + beepTimeOut);
                    } catch (Exception ex) {
                        debug.printDebugMsg(null, CentTerminal.class.getName(), "Érvénytelen argumentum: " + arg);
                        JOptionPane.showMessageDialog(null, "Érvénytelen argumentum: " + arg + "\n"
                                + "az eredeti beállítások lesznek használva", "Hiba", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (command.equals("centralografIpAddress")) {
                    try {
                        Pattern p = Pattern.compile("[0-9]+.[0-9]+.[0-9]+.[0-9]+");
                        Matcher m = p.matcher(value);
                        boolean match = m.matches();
                        if (match) {
                            CentTerminal.CENTRALOGRAFSERVERIPADDRESS = value;
                        } else {

                            debug.printDebugMsg(null, CentTerminal.class.getName(), "Érvénytelen argumentum: " + arg);
                            JOptionPane.showMessageDialog(null, "Érvénytelen argumentum: " + arg + "\n"
                                    + "az eredeti beállítások lesznek használva", "Hiba", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        debug.printDebugMsg(null, CentTerminal.class.getName(), "Érvénytelen argumentum: " + arg);
                        JOptionPane.showMessageDialog(null, "Érvénytelen argumentum: " + arg + "\n"
                                + "az eredeti beállítások lesznek használva", "Hiba", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (command.equals("calculatedTimeEnable")) {
                    if (value.equals("false")) {
                        calculatedTimeEnable = false;
                        debug.printDebugMsg(null, CentTerminal.class.getName(), "calculatedTimeEnable:false");
                    }
                }

            } else {
                debug.printDebugMsg(null, CentTerminal.class.getName(), "Érvénytelen argumentum: " + arg);
                JOptionPane.showMessageDialog(null, "Érvénytelen argumentum: " + arg + "\n"
                        + "az eredeti beállítások lesznek használva", "Hiba", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
