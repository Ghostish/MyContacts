package com.example.yzh.msg;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Yzh on 2016/4/15.
 */
public class HelloMsg {

    private Dbhelper dbhelper;
    private SQLiteDatabase db;
    private Cursor c;
    public HelloMsg(Context context){
        dbhelper=new Dbhelper(context,"msg.db",null,1);
        db=dbhelper.getReadableDatabase();
    }
    private String getHeadmsg(String who){
        int whoInt=0;
        if(who.contains("爸爸")||who.contains("父亲")||who.contains("妈妈")||who.contains("母亲")){
            whoInt=1;
        }
        else if(who.contains("朋友")||who.contains("兄弟")||who.contains("姐妹")||who.contains("闺蜜")){
            whoInt=2;
        }
        else if(who.contains("儿子")||who.contains("女儿")){
            whoInt=3;
        }
        else if(who.contains("老公")||who.contains("老婆")){
            whoInt=4;
        }
        else whoInt=2;
        c=db.query("identity", new String[]{"hmsg"}, "who="+whoInt, null, null, null, null);
        if(!c.isLast())
            c.moveToNext();
        String headmsg=null;
        try{
            headmsg= new String(c.getBlob(0),"gbk");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return headmsg;
    }
    private String getTailmsg(float temper,String weather){//传入float型温度,以及string型的天气
        int tempLevel;
        if(temper<=10)tempLevel=1;
        else if(temper<=20)tempLevel=2;
        else if(temper<30)tempLevel=3;
        else if(temper<90)tempLevel=4;
        else tempLevel=0;

        int weatherInt=0;
        if(weather.contains("云"))weatherInt=1;//"云";
        else if(weather.contains("雨"))weatherInt=2;//"雨";
        else if(weather.contains("晴"))weatherInt=3;//"晴";
        else if(weather.contains("雪"))weatherInt=4;//"雪";
        else if(weather.contains("雾"))weatherInt=5;//"雾";
        else if(weather.contains("霾"))weatherInt=6;//"霾";

        c=db.query("weather",new String[]{"tmsg"},"temp="
                +tempLevel+" and weatherstring="+weatherInt,null,null,null,null);
        if(!c.isLast())
            c.moveToNext();
        String tailmsg=null;
        try{
            tailmsg= new String(c.getBlob(0),"gbk");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return tailmsg;
    }
    public String getMsg(String who){
        return getHeadmsg(who)+","+getTailmsg(100,"");
    }
    public String getMsg(String who,float temp,String weather){//需要三个参数，身份，温度，气候
        return getHeadmsg(who)+","+getTailmsg(temp,weather);
    }

}
