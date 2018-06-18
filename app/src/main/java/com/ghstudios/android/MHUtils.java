package com.ghstudios.android;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.*;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.annotation.*;
import android.util.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

    public interface CursorProcessFunction<T, J extends Cursor> {
        T getValue(J c);
    }

    /**
     * Extracts every value in a cursor, returning a list of objects.
     * This method exhausts the cursor.
     * @param c
     * @param process
     * @param <T>
     * @return
     */
    public static <T, J extends Cursor> List<T> cursorToList(J c, CursorProcessFunction<T, J> process) {
        ArrayList<T> results = new ArrayList<>();

        while (c.moveToNext()) {
            results.add(process.getValue(c));
        }

        return results;
    }

    interface Builder<T> {
        T build();
    }

    /**
     * Creates a new livedata that is populated asynchronously using the provided
     * builder function.
     * @param builder
     * @param <T>
     * @return
     */
    @NonNull
    public static <T> LiveData<T> createLiveData(Builder<T> builder) {
        MutableLiveData<T> result = new MutableLiveData<>();
        new Thread(() -> result.postValue(builder.build())).start();
        return result;
    }
}
