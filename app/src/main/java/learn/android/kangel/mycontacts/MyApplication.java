package learn.android.kangel.mycontacts;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by hex on 16/4/23.
 */
public class MyApplication extends Application{
    private RequestQueue mQueue = null;
    public RequestQueue getRequestQueue() {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }
        return mQueue;
    }
}
