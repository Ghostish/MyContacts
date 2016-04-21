package learn.android.kangel.mycontacts.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kangel on 2016/3/19.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "contact_db";
    private final static String CREATE_CONTACT =
            "CREATE TABLE contact (" +
                    "_id integer primary key, " +
                    "address text, " +
                    "img_id integer, " +
                    "birthday text" +
                    ")";
    private final static String CREATE_BLACK_LIST_CONTACT =
            "CREATE TABLE black_list_contact (" +
                    "look_up_key text primary key)";
    private final static String CREATE_BLACK_LIST_NUMBER =
            "CREATE TABLE black_list_number (" +
                    "number text primary key," +
                    "look_up_key text references black_list_contact(look_up_key) on delete cascade)";

    /*private final static String CREATE_INDEX_ON_BLACK_LIST =
            "CREATE INDEX BLIndx ON black_list(number)";
*/
    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BLACK_LIST_CONTACT);
        db.execSQL(CREATE_BLACK_LIST_NUMBER);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
