package learn.android.kangel.mycontacts;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Kangel on 2016/4/21.
 */
public class WeatherInfoView extends CardView {
    private TextView mWeatherDesc;
    private TextView mAirQuality;
    private TextView mTemperatureDesc;
    private TextView mHumidityDesc;
    private ImageView mWeatherImage;
    private float mTemperature;
    private float mHumidity;
    private final static String TEMPERATURE_STRING = "%f°C";
    private final static String HUMIDITY_STRING = "%f%%";

    public WeatherInfoView(Context context) {
        this(context, null);
    }

    public WeatherInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_weather_card, this);
        mWeatherDesc = (TextView) findViewById(R.id.weather_desc);
        mTemperatureDesc = (TextView) findViewById(R.id.temperature_desc);
        mHumidityDesc = (TextView) findViewById(R.id.humidity_desc);
        mAirQuality = (TextView) findViewById(R.id.quality_desc);
        mWeatherImage = (ImageView) findViewById(R.id.weather_image);
    }

    public void setWeatherDesc(CharSequence text) {
        mWeatherDesc.setText(text);
    }

    public void setTemperatureDesc(float temperature) {
        mTemperature = temperature;
        mTemperatureDesc.setText(String.format(Locale.CHINA, TEMPERATURE_STRING, temperature));
    }

    public void setHumidityDesc(float humidity) {
        mHumidity = humidity;
        mHumidityDesc.setText(String.format(Locale.CHINA, HUMIDITY_STRING, mHumidity));
    }

    public void setAirQuality(CharSequence text) {
        mAirQuality.setText(text);
    }

    public void setWeatherImageResourse(int resId) {
        mWeatherImage.setImageResource(resId);
    }

    public String getWeather() {
        return (String) mWeatherDesc.getText();
    }

    public float getHumidity() {
        return mHumidity;
    }

    public float getTemperature() {
        return mTemperature;
    }
}
