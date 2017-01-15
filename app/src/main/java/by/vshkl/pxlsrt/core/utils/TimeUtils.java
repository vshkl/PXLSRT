package by.vshkl.pxlsrt.core.utils;

import java.util.Locale;

public class TimeUtils {

    public static String getReadableTime(long time) {
        return String.format(Locale.getDefault(), "%.3f sec", time / 1000.0);
    }

    public static String getProcessingTimeToLog(long time) {
        return String.format(Locale.getDefault(), "%.1f sec", time / 1000.0);
    }
}
