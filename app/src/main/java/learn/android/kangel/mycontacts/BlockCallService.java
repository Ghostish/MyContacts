package learn.android.kangel.mycontacts;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.*;

import learn.android.kangel.mycontacts.utils.BlackListUtil;

/**
 * Created by Kangel on 2016/4/17.
 */
public class BlockCallService extends IntentService {
    private static final String TAG = "BlockCallService";

    public BlockCallService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String number = intent.getStringExtra("number");
        if (number != null && BlackListUtil.isBlockedNumber(this, number)) {
            endCall();
        }
    }

    public void endCall() {
        try {
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getMethod("getService", String.class);
            IBinder ibinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(ibinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
