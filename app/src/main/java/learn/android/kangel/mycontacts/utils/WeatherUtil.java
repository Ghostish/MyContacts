package learn.android.kangel.mycontacts.utils;

import android.util.Log;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.Charset;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.WeatherInfoView;

/**
 * Created by hex on 16/4/16.
 */
public class WeatherUtil {

    static class WeatherCache {
        JSONObject weatherJson;
        Long time;

        public WeatherCache(JSONObject weatherJson) {
            this.weatherJson = weatherJson;
            time = System.currentTimeMillis();
        }
    }

    private static LruCache<String, WeatherCache> lruCache = new LruCache<String, WeatherCache>(30);

    private static final String BASIC_URL = "https://api.heweather.com/x3/weather?city=%s&key=c78bf6d615ee47f9bda04882eaf38df2";

    public static void getWeather(RequestQueue requestQueue, final String cityName, final WeatherInfoView view) {
        WeatherCache weatherCache = lruCache.get(cityName);
        if (weatherCache != null) {
            if (System.currentTimeMillis() - weatherCache.time < 20 * 60 * 60) {
                setView(weatherCache.weatherJson, view);
                return;
            }
        }

        String httpUrl = String.format(BASIC_URL, URLEncoder.encode(cityName));
        Log.d("URL", httpUrl);

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, httpUrl, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("TAG", response.toString());
                                lruCache.put(cityName, new WeatherCache(response));
                                setView(response, view);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("TAG", error.getMessage(), error);

                            }
                        }
                );

        requestQueue.add(jsonObjectRequest);
    }

    protected static void setView(JSONObject response, WeatherInfoView view) {
        try {
            JSONObject resultJson = response.getJSONArray("HeWeather data service 3.0").getJSONObject(0);
            String status = resultJson.getString("status");
            if ("ok".equals(status)) {
                try {
                    String aqiDesc = resultJson.getJSONObject("aqi").getJSONObject("city").getString("qlty");
                    view.setAirQuality(aqiDesc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject weatherJson = resultJson.getJSONObject("now");
                String weatherCode = weatherJson.getJSONObject("cond").getString("code");
                String weatherDesc = weatherJson.getJSONObject("cond").getString("txt");
                view.setWeatherDesc(weatherDesc);
                view.setWeatherImageResourse(getResIdByCode(weatherCode));
                String temperatureDesc = weatherJson.getString("tmp");
                view.setTemperatureDesc(Float.valueOf(temperatureDesc));
                String humidityDesc = weatherJson.getString("hum");
                view.setHumidityDesc(Float.valueOf(humidityDesc));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
//    protected static void setView(JSONObject response, ViewStub view) {
//        try {
//
//            JSONObject resultJson = response.getJSONArray("HeWeather data service 3.0").getJSONObject(0);
//            String status = resultJson.getString("status");
//            if ("ok".equals(status)) {
//                String aqiDesc = resultJson.getJSONObject("aqi").getJSONObject("city").getString("qlty");
//                view.setAirQuality(aqiDesc);
//                JSONObject weatherJson = resultJson.getJSONObject("now");
//                String weatherCode = weatherJson.getJSONObject("cond").getString("code");
//                String weatherDesc = weatherJson.getJSONObject("cond").getString("txt");
//                view.setWeatherDesc(weatherDesc);
//                // view.setWeatherImageResourse()
//                String temperatureDesc = weatherJson.getString("tmp");
//                view.setTemperatureDesc(Float.valueOf(temperatureDesc));
//                String humidityDesc = weatherJson.getString("hum");
//                view.setHumidityDesc(Float.valueOf(humidityDesc));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }

    protected static int getResIdByCode(String weatherCode) {
        int code = Integer.valueOf(weatherCode);
        switch (code) {
            case 100:
                return R.mipmap.code100;
            case 101:
                return R.mipmap.code101;
            case 102:
                return R.mipmap.code102;
            case 103:
                return R.mipmap.code103;
            case 104:
                return R.mipmap.code104;
            case 200:
                return R.mipmap.code200;
            case 201:
                return R.mipmap.code201;
            case 202:
                return R.mipmap.code202;
            case 203:
                return R.mipmap.code203;
            case 204:
                return R.mipmap.code204;
            case 205:
                return R.mipmap.code205;
            case 206:
                return R.mipmap.code206;
            case 207:
                return R.mipmap.code207;
            case 208:
                return R.mipmap.code208;
            case 209:
                return R.mipmap.code209;
            case 210:
                return R.mipmap.code210;
            case 211:
                return R.mipmap.code211;
            case 212:
                return R.mipmap.code212;
            case 213:
                return R.mipmap.code213;
            case 300:
                return R.mipmap.code300;
            case 301:
                return R.mipmap.code301;
            case 302:
                return R.mipmap.code302;
            case 303:
                return R.mipmap.code303;
            case 304:
                return R.mipmap.code304;
            case 305:
                return R.mipmap.code305;
            case 306:
                return R.mipmap.code306;
            case 307:
                return R.mipmap.code307;
            case 308:
                return R.mipmap.code308;
            case 309:
                return R.mipmap.code309;
            case 310:
                return R.mipmap.code310;
            case 311:
                return R.mipmap.code311;
            case 312:
                return R.mipmap.code312;
            case 313:
                return R.mipmap.code313;
            case 400:
                return R.mipmap.code400;
            case 401:
                return R.mipmap.code401;
            case 402:
                return R.mipmap.code402;
            case 403:
                return R.mipmap.code403;
            case 404:
                return R.mipmap.code404;
            case 405:
                return R.mipmap.code405;
            case 406:
                return R.mipmap.code406;
            case 407:
                return R.mipmap.code407;
            case 500:
                return R.mipmap.code500;
            case 501:
                return R.mipmap.code501;
            case 502:
                return R.mipmap.code502;
            case 503:
                return R.mipmap.code503;
            case 504:
                return R.mipmap.code504;
            case 507:
                return R.mipmap.code507;
            case 508:
                return R.mipmap.code508;
            case 900:
                return R.mipmap.code900;
            case 901:
                return R.mipmap.code901;
            case 999:
                return R.mipmap.code999;
        }
        return R.mipmap.code999;
    }
}
