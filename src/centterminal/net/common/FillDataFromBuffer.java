package centterminal.net.common;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * @author gabesz A "FillDataFromBuffer.load(DB,receiveTelegram )" metódus a
 * byte bufferből alakítja át az adatokat A DB osztályban deklarált mezőkhöz
 * igazitva. Az adatokat hozzárendeli a mezőkhöz, és átalakítja a mező típusának
 * megfelelően
 */
public class FillDataFromBuffer {

    /**
     *
     * @param DB
     * @param receiveTelegram
     */
    @SuppressWarnings({"UseSpecificCatch", "ConvertToStringSwitch"})
    public static void load(Object DB, byte[] receiveTelegram) {

        Field[] fields = DB.getClass().getDeclaredFields(); // A DB.-ben található mezők
        int pointer = 0;    // A mutató kezdő címe
        int shortLength = 2; // A "short" típusu adat hossza
        int floatLength = 4; // A "float" típusu adat hossza
        int longLength = 8; // A "long" típusu adat hossza
        int intLength = 4;// Az "int" típusu adat hossza
        int charLength = 1;
        for (Field field : fields) {
            try {
                // A mező adattípusának meghatározása
                String s = field.getType().getName();
                // "short" típusu adat átalakítása, és hozzárendelése a mezőhöz
                if (s.equals("short")) {
                    field.setShort(DB, ByteBuffer.wrap(receiveTelegram, pointer, shortLength).getShort());
                    pointer += shortLength;
                    // "int" típusu adat átalakítása, és hozzárendelése a mezőhöz
                } else if (s.equals("int")) {
                    field.setLong(DB, ByteBuffer.wrap(receiveTelegram, pointer, intLength).getInt());
                    pointer += longLength;
                    // "long" típusu adat átalakítása, és hozzárendelése a mezőhöz
                } else if (s.equals("long")) {
                    field.setLong(DB, ByteBuffer.wrap(receiveTelegram, pointer, longLength).getLong());
                    pointer += longLength;
                    // "float" típusu adat átalakítása, és hozzárendelése a mezőhöz
                } else if (s.equals("float")) {
                    field.setFloat(DB, ByteBuffer.wrap(receiveTelegram, pointer, floatLength).getFloat());
                    pointer += floatLength;
                    // "java.lang.String" típusu adat átalakítása, és hozzárendelése a mezőhöz
                } else if (s.equals("java.lang.String")) {
                    field.set(DB, byteBufferToString(receiveTelegram, pointer));
                    //pointer növelése a string hossza +2 -vel
                    pointer = (int) receiveTelegram[pointer] + pointer + 2;
                } else if (s.equals("char")) {
                    field.setChar(DB, (char)Arrays.copyOfRange(receiveTelegram, pointer, pointer+charLength)[0]);
                    //field.setChar(DB, ByteBuffer.wrap(receiveTelegram, pointer, charLength).getChar);
                    //pointer növelése a string hossza +2 -vel
                    pointer += charLength;

                } else {
                    System.out.println("!!!! " + s);
                }

            } catch (Exception ex) {
               ex.printStackTrace(System.err);
                //           }

            }
        }
    }

    private static String byteBufferToString(byte[] array, int offset) {
        /**
         * byte[] @array ami a PLC telegrammból nyerünk ki. Egy 14 karakterből
         * álló String :
         *
         *
         * - 0. byte a Sztring álltal lefoglalt terület hossza - 1. byte a
         * hasznos karakterek száma a stringben - Majd az első bájtban
         * meghatározott hosszúságú karakter lánc
         */

        Integer length = (int) array[offset + 1];
        Integer begin = offset + 2;
        Integer end = offset + 2 + length;

        return new String(Arrays.copyOfRange(array, begin, end));

    }
}
