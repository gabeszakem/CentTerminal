/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package centterminal.tools;

import centterminal.CentTerminal;
import static centterminal.CentTerminal.debug;
import centterminal.frame.CentterminalFrame;
import com.excelsior.xFunction.Argument;
import com.excelsior.xFunction.xFunction;
import java.lang.reflect.Field;

/**
 *
 * @author gkovacs02
 */
public class Beep {

    private final String path;

    /**
     *Beep osztály alapértelmezett konstruktor
     */
    public Beep() {
        String separator = System.getProperty("file.separator");
        String userDir = System.getProperty("user.dir");
        this.path = userDir + separator;
        properties();
    }

    /**
     * Beep osztály megváltoztatott elérési úttal konstruktor.
     * @param path
     */
    public Beep(String path) {
        this.path = path;
        properties();
    }

    @SuppressWarnings({"UseSpecificCatch", "BroadCatchBlock", "TooBroadCatch"})
    private void properties() {
        try {
            if (CentTerminal.beep) {

                System.setProperty("java.library.path", path);
                Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
                fieldSysPath.setAccessible(true);
                fieldSysPath.set(null, null);
                debug.printDebugMsg(null, CentTerminal.class.getName(), "System property: java.library.path: " + path);
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            debug.printDebugMsg(null, CentTerminal.class.getName(), "Hiba a java.library.path beállítása közben: ", ex);
        }
    }

    /**
     *
     * @param beepWorking
     * @return beep is ok.
     */
    @SuppressWarnings({"BroadCatchBlock", "TooBroadCatch", "UseSpecificCatch"})
    public boolean beep(boolean beepWorking) {
        if (System.getProperty("os.name").equals("Windows XP") && beepWorking) {
            try {
                xFunction b = new xFunction("kernel32", "int Beep(int,int)");
                b.invoke(new Argument(1000), new Argument(250));
            } catch (Exception ex) {
                beepWorking = false;
                ex.printStackTrace(System.err);
                CentTerminal.debug.printDebugMsg(null, CentterminalFrame.class.getName(), "beep error", ex);
            }
        }
        return beepWorking;
    }
}
