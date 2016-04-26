package learn.android.kangel.mycontacts.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import learn.android.kangel.mycontacts.R;

/**
 * Created by hex on 16/4/24.
 */
public class TelDbHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "tel.db";

    public TelDbHelper(Context context) {
        super(context, DB_NAME, null, 1);

        //将res/raw中的数据库导入到安装的程序中的database目录下

        //数据库的目录
        String dirPath = context.getApplicationInfo().dataDir + "/databases";
        Log.d("path", dirPath);
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        //数据库文件
        File dbfile = new File(dir, "tel.db");
        try {
            if (!dbfile.exists()) {
                dbfile.createNewFile();
                //加载欲导入的数据库
                InputStream is = context.getApplicationContext().getResources().openRawResource(R.raw.tel);
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffere = new byte[is.available()];
                while (is.read(buffere) != -1) {
                    fos.write(buffere);
                }
                is.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
