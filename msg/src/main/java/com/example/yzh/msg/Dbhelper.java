package com.example.yzh.msg;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Yzh on 2016/4/15.
 */
public class Dbhelper extends SQLiteOpenHelper {
    public Dbhelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        //将res/raw中的数据库导入到安装的程序中的database目录下

        //数据库的目录
        String dirPath = context.getApplicationInfo().dataDir + "/databases";
        Log.d("path", dirPath);
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        //数据库文件
        File dbfile = new File(dir, "msg.db");
        try {
            if (!dbfile.exists()) {
                dbfile.createNewFile();
                //加载欲导入的数据库
                InputStream is = context.getApplicationContext().getResources().openRawResource(R.raw.msg);
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

    public void onCreate(SQLiteDatabase db) {

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
