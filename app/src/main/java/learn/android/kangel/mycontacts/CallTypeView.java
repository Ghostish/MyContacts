package learn.android.kangel.mycontacts;

import android.content.Context;
import android.provider.CallLog;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by Kangel on 2016/4/18.
 */
public class CallTypeView extends LinearLayout {
    public CallTypeView(Context context) {
        this(context, null);
    }

    public CallTypeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CallTypeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
    }

    public void setCallTypes(List<Integer> types) {
        removeAllViews();
        for (int type : types) {
            ImageView imageView = new ImageView(getContext());
            imageView.setAlpha(0.76f);
            int imageSize = getResources().getDimensionPixelSize(R.dimen.call_type_icon_size);
            LinearLayout.LayoutParams params = new LayoutParams(imageSize, imageSize);
            imageView.setLayoutParams(params);
            switch (type) {
                case CallLog.Calls.MISSED_TYPE:
                    imageView.setImageResource(R.drawable.ic_call_missed_24dp);
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    imageView.setImageResource(R.drawable.ic_call_made_24dp);
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    imageView.setImageResource(R.drawable.ic_call_received_24dp);
                    break;
                default:
                    imageView.setImageResource(R.drawable.ic_call_missed_24dp);
                    Log.d("type", type+"");
            }
            addView(imageView);
        }
    }
}
