package by.vshkl.pxlsrt.core.utils;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String getReadableTime(long time) {
        return String.format(Locale.getDefault(), "%d sec", TimeUnit.MILLISECONDS.toSeconds(time));
    }
}
