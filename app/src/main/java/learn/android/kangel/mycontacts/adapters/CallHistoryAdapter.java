package learn.android.kangel.mycontacts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import learn.android.kangel.mycontacts.DateParseUtil;
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
    private final static int TYPE_HEADER = 110;
    private final static int TYPE_ITEM = 111;

    public Cursor getCursor() {
        return cursor;
    }

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
        if (viewType == TYPE_HEADER) {
            ViewStub viewStub = (ViewStub) v.findViewById(R.id.header_view_stub);
            viewStub.inflate();
            v.setTag(TYPE_HEADER);
        }
        return new ViewHolder(v);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && getItemCount() != 0) {
            return TYPE_HEADER;
        }
        cursor.moveToPosition(position);
        Long date1 = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
        cursor.moveToPrevious();
        Long date2 = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));

        if (!DateParseUtil.isSameDay(date1, date2) && (DateParseUtil.isTodayDate(date1) || DateParseUtil.isYesterdayDate(date1) || DateParseUtil.isTodayDate(date2) || DateParseUtil.isYesterdayDate(date2))) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
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
        holder.callInfo.setText(String.format(infoString, DateParseUtil.getTimeString(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))), "locationHere"));
        if (holder.headerText != null) {
            holder.headerText.setText(DateParseUtil.getDateString(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))));
        }
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
        TextView headerText;

        public ViewHolder(View itemView) {
            super(itemView);
            numOrName = (TextView) itemView.findViewById(R.id.caller_name_or_num);
            callInfo = (TextView) itemView.findViewById(R.id.call_info);
            callType = (ImageView) itemView.findViewById(R.id.call_type);
            headShow = (ImageView) itemView.findViewById(R.id.head_show);
            callButton = (ImageButton) itemView.findViewById(R.id.call_button);
            callButton.setOnClickListener(this);

            if (itemView.getTag() != null && ((int) itemView.getTag()) == TYPE_HEADER) {
                headerText = (TextView) itemView.findViewById(R.id.header_text);
            }
        }

        @Override
        public void onClick(View v) {
            if (context instanceof RecyclerViewActivity) {
                cursor.moveToPosition(getAdapterPosition());
                Bundle data = new Bundle();
                data.putString("number", cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));
                ((RecyclerViewActivity) context).onRecyclerViewItemClick(getAdapterPosition(), TAG_DIAL, data);
            }
        }
    }
}
