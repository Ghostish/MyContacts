package learn.android.kangel.mycontacts.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by hex on 16/4/16.
 */
public class AttributionUtil {

    public static class LocationBean {
        private String province;
        private String city;
        private String isp;

        public void setProvince(String province) {
            this.province = province;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public void setIsp(String isp) {
            this.isp = isp;
        }

        public String getProvince() {

            return province;
        }

        public String getCity() {
            return city;
        }

        public String getIsp() {
            return isp;
        }

        @Override
        public String toString() {
            return province + city;
        }
    }

    private static final String BASIC_URL = "http://apis.juhe.cn/mobile/get?phone=%s&key=68f885969551080b4e1397ca3a82f434";

    private static final String QUERY_SQL = "SELECT province, city, isp FROM tel WHERE ? >= start AND ? <= end";

    public static LocationBean getAttribution(Context context, String phone) {

//        String httpUrl = String.format(BASIC_URL, phone);
//
//        JsonObjectRequest jsonObjectRequest =
//                new JsonObjectRequest(httpUrl, null,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                Log.d("TAG", response.toString());
//                            }
//                        },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Log.e("TAG", error.getMessage(), error);
//                            }
//                        }
//                );
//
//        requestQueue.add(jsonObjectRequest);
        String newphone = "";
        for (char ch : phone.toCharArray()) {
            if (Character.isDigit(ch)) {
                newphone += ch;
            }
        }
        phone = newphone;
        Log.d("ATTR", phone);

        TelDbHelper dbHelper = new TelDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Log.d("ATTR", String.format(QUERY_SQL, phone, phone));
        LocationBean bean = null;
        Cursor c = db.rawQuery(QUERY_SQL, new String[]{phone, phone});
        while (c.moveToNext()) {
            bean = new LocationBean();
            String province = c.getString(0);
            String city = c.getString(1);
            String isp = c.getString(2);
            bean.setProvince(province);
            bean.setCity(city);
            bean.setIsp(isp);

        }
        c.close();
        db.close();
        return bean;
    }

    public static LocationBean getAttribution(String phone, SQLiteDatabase db) {
        String newphone = "";
        for (char ch : phone.toCharArray()) {
            if (Character.isDigit(ch)) {
                newphone += ch;
            }
        }
        phone = newphone;
        Log.d("ATTR", phone);
        Log.d("ATTR", String.format(QUERY_SQL, phone, phone));
        LocationBean bean = null;
        Cursor c = db.rawQuery(QUERY_SQL, new String[]{phone, phone});
        while (c.moveToNext()) {
            bean = new LocationBean();
            String province = c.getString(0);
            String city = c.getString(1);
            String isp = c.getString(2);
            bean.setProvince(province);
            bean.setCity(city);
            bean.setIsp(isp);

        }
        c.close();
        return bean;
    }
}
