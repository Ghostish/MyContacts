package learn.android.kangel.mycontacts;

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
    public MyDatabaseHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
