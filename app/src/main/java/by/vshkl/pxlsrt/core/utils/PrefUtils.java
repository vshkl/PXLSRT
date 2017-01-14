package by.vshkl.pxlsrt.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;

import by.vshkl.pxlsrt.R;

public class PrefUtils {

    public static String getImageFormatPref(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(
                context.getString(R.string.pref_format_key),
                context.getString(R.string.pref_format_default));
    }

    public static Bitmap.CompressFormat getImageBitmapCompressFormatPref(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String format = sharedPref.getString(
                context.getString(R.string.pref_format_key),
                context.getString(R.string.pref_format_default));
        if (format.equals(".png")) {
            return Bitmap.CompressFormat.PNG;
        } else {
            return Bitmap.CompressFormat.JPEG;
        }
    }

    public static int getImageQualityPref(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sharedPref.getString(
                context.getString(R.string.pref_quality_key),
                context.getString(R.string.pref_quality_default)));
    }

    public static int getImageResolutionPref(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sharedPref.getString(
                context.getString(R.string.pref_resolution_key),
                context.getString(R.string.pref_resolution_default)));
    }
}
