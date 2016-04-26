package learn.android.kangel.mycontacts.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * Created by Kangel on 2016/4/17.
 */
public class BlackListUtil {
    private final static String SELECTION_CONTACT = "look_up_key = ?";
    private final static String SELECTION_NUMBER = "number = ? ";
    private final static String CONTACT_TABLE_NAME = "black_list_contact";
    private final static String NUMBER_TABLE_NAME = "black_list_number";
    private final static String LOOK_UP_KEY = "look_up_key";
    private final static String NUMBER = "number";

    public static boolean isBlockedNumber(Context context, String number) {
        boolean result;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor c = db.query(NUMBER_TABLE_NAME, null, SELECTION_NUMBER, new String[]{number}, null, null, null);
        result = c.moveToNext();
        c.close();
        db.close();
        return result;
    }

    public static boolean isBlockedContact(Context context, String lookupKey) {
        boolean result;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor c = db.query(CONTACT_TABLE_NAME, null, SELECTION_CONTACT, new String[]{lookupKey}, null, null, null);
        result = c.moveToNext();
        c.close();
        db.close();
        return result;
    }

    public static boolean addToNumberBlackList(Context context, String contactLookUpKey, String[] numbers) {
        boolean result;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (String number : numbers) {
                values.put(NUMBER, number);
                values.put("look_up_key", contactLookUpKey);
                db.insertOrThrow(NUMBER_TABLE_NAME, null, values);
                values.clear();
            }
            db.setTransactionSuccessful();
            result = true;
        } catch (SQLiteConstraintException e) {
            Log.e("SqliteError", e.getLocalizedMessage());
            result = false;
        } finally {
            db.endTransaction();
        }
        return result;
    }

    public static boolean removeContactFromBlackList(Context context, String lookupKey) {
        boolean result;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        try {
            db.delete(CONTACT_TABLE_NAME, SELECTION_CONTACT, new String[]{lookupKey});
            result = true;
        } catch (SQLiteException e) {
            Log.e("SqliteError", e.getLocalizedMessage());
            result = false;
        }
        return result;
    }

    public static boolean removeNumberFromBlackList(Context context, String number) {
        boolean result;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getWritableDatabase();
        try {
            db.delete(NUMBER_TABLE_NAME, SELECTION_NUMBER, new String[]{number});
            result = true;
        } catch (SQLiteException e) {
            Log.e("SqliteError", e.getLocalizedMessage());
            result = false;
        }
        return result;
    }

    public static boolean removeNumberFromBlackList(SQLiteDatabase db, String number) {
        boolean result;
        try {
            db.delete(NUMBER_TABLE_NAME, SELECTION_NUMBER, new String[]{number});
            result = true;
        } catch (SQLiteException e) {
            Log.e("SqliteError", e.getLocalizedMessage());
            result = false;
        }
        return result;
    }

    public static Cursor getBlockedNumbers(SQLiteDatabase db) {
        Cursor c = null;
        try {
            c = db.query(NUMBER_TABLE_NAME, new String[]{"rowid", "number"}, null, null, null, null, null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static boolean addToContactBlackList(Context context, String contactLookUpKey) {
        boolean result;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOOK_UP_KEY, contactLookUpKey);
        try {
            db.insert(CONTACT_TABLE_NAME, null, values);
            result = true;
        } catch (SQLiteException e) {
            Log.e("SqliteError", e.getLocalizedMessage());
            result = false;
        }
        return result;
    }
}
