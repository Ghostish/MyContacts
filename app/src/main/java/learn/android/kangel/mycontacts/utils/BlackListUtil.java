package learn.android.kangel.mycontacts.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * Created by Kangel on 2016/4/17.
 */
public class BlackListUtil {
    //private final static String SELECTION_NUMBER = "number = ?";
    private final static String SELECTION_CONTACT = "look_up_key = ?";
    private final static String TABLE_NAME = "black_list";
    private final static String LOOK_UP_KEY = "look_up_key";

 /*   public static boolean isBlockedNumber(Context context, String number) {
        boolean result;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor c = db.query("black_list", null, SELECTION_NUMBER, new String[]{number}, null, null, null);
        result = c.moveToNext();
        c.close();
        db.close();
        return result;
    }*/

    public static boolean isBlockedContact(Context context, String lookupKey) {
        boolean result;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor c = db.query("black_list", null, SELECTION_CONTACT, new String[]{lookupKey}, null, null, null);
        result = c.moveToNext();
        c.close();
        db.close();
        return result;
    }

    /*public static boolean addToBlackList(Context context, String contactLookUpKey, String[] numbers) {
        boolean result = false;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (String number : numbers) {
                values.put("number", number);
                values.put("look_up_key", contactLookUpKey);
                db.insert("black_list", null, values);
                values.clear();
            }
            db.setTransactionSuccessful();
            result = true;
        } catch (SQLiteAbortException e) {
            Log.e("SqliteError", e.getLocalizedMessage());
        } finally {
            db.endTransaction();
        }
        return result;
    }*/

    public static boolean removeFromBlackList(Context context, String lookupKey) {
        boolean result;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        try {
            db.delete("black_list", SELECTION_CONTACT, new String[]{lookupKey});
            result = true;
        } catch (SQLiteException e) {
            Log.e("SqliteError", e.getLocalizedMessage());
            result = false;
        }
        return result;
    }

    public static boolean addToBlackList(Context context, String contactLookUpKey) {
        boolean result;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOOK_UP_KEY, contactLookUpKey);
        try {
            db.insert(TABLE_NAME, null, values);
            result = true;
        } catch (SQLiteException e) {
            Log.e("SqliteError", e.getLocalizedMessage());
            result = false;
        }
        return result;
    }
}
