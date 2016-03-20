package learn.android.kangel.mycontacts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import learn.android.kangel.mycontacts.DateParseUtil;
import learn.android.kangel.mycontacts.MyRecyclerView;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.activities.RecyclerViewActivity;

/**
 * Created by Kangel on 2016/3/17.
 */
public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {
    private Cursor cursor;
    private Context context;
    private final static String infoString = "%s,%s";
    public final static String TAG_DIAL = "DIAL_NUMBER";

    public void updateCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public CallHistoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_call_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        switch (cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))) {
            case CallLog.Calls.MISSED_TYPE:
                holder.callType.setImageResource(R.drawable.ic_call_missed_24dp);
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                holder.callType.setImageResource(R.drawable.ic_call_made_24dp);
                break;
            case CallLog.Calls.INCOMING_TYPE:
                holder.callType.setImageResource(R.drawable.ic_call_received_24dp);
                break;
        }
        holder.numOrName.setText(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)) == null ? cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)) : cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
        holder.callInfo.setText(String.format(infoString, DateParseUtil.getTimeString(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))),"locationHere"));
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView numOrName;
        TextView callInfo;
        ImageView callType;
        ImageView headShow;
        ImageButton callButton;

        public ViewHolder(View itemView) {
            super(itemView);
            numOrName = (TextView) itemView.findViewById(R.id.caller_name_or_num);
            callInfo = (TextView) itemView.findViewById(R.id.call_info);
            callType = (ImageView) itemView.findViewById(R.id.call_type);
            headShow = (ImageView) itemView.findViewById(R.id.head_show);
            callButton = (ImageButton) itemView.findViewById(R.id.call_button);
            callButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (context instanceof RecyclerViewActivity) {
                ((RecyclerViewActivity) context).onRecyclerViewItemClick(getAdapterPosition(), TAG_DIAL, null);
            }
        }
    }
}
