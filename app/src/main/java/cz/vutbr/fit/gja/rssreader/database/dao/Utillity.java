package cz.vutbr.fit.gja.rssreader.database.dao;

/**
 * Trida obsahujici pomocne funkce pro databazovou vrstvu
 */
public class Utillity {

    public static long TRUE = 1;

    public static long FALSE = 0;

    public static boolean longToBoolean(long val) {
        if (val == 0)
            return false;
        else
            return true;
    }

    public static long booleanToLong(boolean val) {
        if (val)
            return 1;
        else
            return 0;
    }

}
