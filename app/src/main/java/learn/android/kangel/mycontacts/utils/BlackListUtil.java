package learn.android.kangel.mycontacts.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Kangel on 2016/4/17.
 */
public class BlackListUtil {
    private final static String SELECTION_NUMBER = "number = ?";
    private final static String SELECTION_CONTACT = "look_up_key = ?";

    public static boolean isBlockedNumber(Context context, String number) {
        boolean result;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor c = db.query("black_list", null, SELECTION_NUMBER, new String[]{number}, null, null, null);
        result = c.moveToNext();
        c.close();
        db.close();
        return result;
    }

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

    public static boolean addToBlackList(Context context, String contactLookUpKey, String[] numbers) {
        boolean result = false;
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (String number : numbers) {
                values.put("number", number);
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
    }
}
