package by.vshkl.pxlsrt.core.utils;

import java.util.Locale;

public class TimeUtils {

    public static String getReadableTime(long time) {
        return String.format(Locale.getDefault(), "%.3f sec", time / 1000.0);
    }
}
