package com.ghstudios.android;

import android.content.*;
import android.graphics.drawable.Drawable;
import android.support.annotation.*;
import android.util.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * A static class that provides helper methods for accessing and managing the {@code res} directory.
 */
public class MHUtils {

    private MHUtils() { }

    /**
     * Attempts to split the specified string by a comma.
     * @param stringArray The {@code string-array} resource from which to parse.
     * @param index The index in the string array to fetch a string from.
     * @param part The 0-based index of final piece of the string to retrieve.
     * @return The {@code part} index of a group of strings created by splitting the desired string by commas.
     */
    public static String splitStringInArrayByComma(@ArrayRes int stringArray, int index, int part, Context context) {
        String fullString;

        try {
            fullString = context.getResources().getStringArray(stringArray)[index];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            Log.e("App", "The string array resource does not have the specified index.");
            return "";
        }

        try {
            return fullString.split(",")[part];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            Log.e("App", "The specified string in the array does not have a comma in it.");
            return "";
        }
    }

    /**
     * Loads an image from the Assets folder as a drawable
     * @param ctx
     * @param path
     * @return
     */
    public static Drawable loadAssetDrawable(Context ctx, String path)  {
        InputStream stream = null;
        try {
            stream = ctx.getAssets().open(path);
            return Drawable.createFromStream(stream, null);
        } catch (Exception ex) {
            Log.e("MHGenUtils", "Failed to load asset " + path, ex);
            return null;
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException ex) { /* do nothing */ }
        }
    }
}
