package learn.android.kangel.mycontacts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

/**
 * Created by Kangel on 2016/4/17.
 */
public class TelephoneReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent blackListIntent = new Intent(context, BlockCallService.class);
        Bundle bundle = intent.getExtras();
        String phoneNr = bundle.getString("incoming_number");
        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
            blackListIntent.putExtra("number", phoneNr);
            context.startService(blackListIntent);
        }
    }
}
