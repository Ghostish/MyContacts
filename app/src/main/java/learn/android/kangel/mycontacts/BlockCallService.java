package learn.android.kangel.mycontacts;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import learn.android.kangel.mycontacts.utils.BlackListUtil;

/**
 * Created by Kangel on 2016/4/17.
 */
public class BlockCallService extends IntentService {
    private static final String TAG = "BlockCallService";
    private static final String[] projection = new String[]{
            ContactsContract.Data.LOOKUP_KEY,
    };

    public BlockCallService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String number = intent.getStringExtra("number");
        if (number != null) {
            if (BlackListUtil.isBlockedNumber(this, number)) {
                endCall();
                return;
            }
            Uri mUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(number));
            Cursor c = getContentResolver().query(mUri, projection, null, null, null);
            while (c != null && c.moveToNext()) {
                String lookUpKey = c.getString(0);
                if (BlackListUtil.isBlockedContact(this, lookUpKey)) {
                    endCall();
                    c.close();
                    BlackListUtil.addToNumberBlackList(this, lookUpKey, new String[]{number});
                    break;
                }
            }
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
