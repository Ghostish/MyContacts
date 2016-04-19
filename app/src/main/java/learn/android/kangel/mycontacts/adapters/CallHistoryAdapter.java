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

import java.util.ArrayList;
import java.util.List;

import learn.android.kangel.mycontacts.CallTypeView;
import learn.android.kangel.mycontacts.utils.CallogBean;
import learn.android.kangel.mycontacts.utils.DateParseUtil;
import learn.android.kangel.mycontacts.utils.HeadShowLoader;
import learn.android.kangel.mycontacts.MyRecyclerView;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.activities.RecyclerViewActivity;

/**
 * Created by Kangel on 2016/3/17.
 */
public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder>/* implements MyRecyclerView.EndlessScrollingAdapter*/ {
    private Cursor cursor;
    private List<CallogBean> data = new ArrayList<>();
    private Context context;
    private final static String infoString = "%s,%s";
    public final static String TAG_DIAL = "DIAL_NUMBER";
    private final static int TYPE_HEADER = 110;
    private final static int TYPE_ITEM = 111;
    private int showCount = 10;
    private final static int increment = 20;

    public Cursor getCursor() {
        return cursor;
    }

    private HeadShowLoader mHeadShowLoader = new HeadShowLoader();

    public void updateCursor(Cursor cursor) {
        this.cursor = cursor;
        convertCursorToBeans(cursor);

    }

    public CallHistoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        convertCursorToBeans(cursor);
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
        CallogBean beanCurr = data.get(position);
        CallogBean beanPre = data.get(position - 1);

        long date1 = beanCurr.getFirstTimeMillis();
        long date2 = beanPre.getFirstTimeMillis();

        if (!DateParseUtil.isSameDay(date1, date2) && (DateParseUtil.isTodayDate(date1) || DateParseUtil.isYesterdayDate(date1) || DateParseUtil.isTodayDate(date2) || DateParseUtil.isYesterdayDate(date2))) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        /*cursor.moveToPosition(position);
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
        holder.callInfo.setText(String.format(infoString, DateParseUtil.getTimeString(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))), cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION))));
        if (holder.headerText != null) {
            holder.headerText.setText(DateParseUtil.getDateString(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))));
        }
       *//* int oldPosition = holder.headShow.getTag() == null ? -1 : (int) holder.headShow.getTag();
        if (oldPosition != position) {
            holder.headShow.setImageResource(R.drawable.default_head_show_list);
        }
        holder.headShow.setTag(position);*//*
        mHeadShowLoader.bindImageView(holder.headShow, context, cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)));*/
        CallogBean bean = data.get(position);
        String name = bean.getContactName();
        String number = bean.getNumber();
        String location = bean.getLocation();
        Long time = bean.getFirstTimeMillis();
        int count = bean.getCount();
        List<Integer> callTypes = bean.getTypeList();
        List<Integer> subCallTypes = callTypes.subList(0, callTypes.size() >= 3 ? 3 : callTypes.size());
        holder.numOrName.setText(name == null ? number : name);
        String info = (count > 1 ? "(" + count + ") " : "") + location + " " + DateParseUtil.getTimeString(time);
        holder.callInfo.setText(info);
        holder.callType.setCallTypes(subCallTypes);

        if (holder.headerText != null) {
            holder.headerText.setText(DateParseUtil.getDateString(time));
        }

        mHeadShowLoader.bindImageView(holder.headShow, context, number);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

  /*  @Override
    public void onLoadMore() {
        int oldShowCount = showCount;
        showCount = showCount + increment > cursor.getCount() ? cursor.getCount() : showCount + increment;
        notifyItemRangeChanged(oldShowCount, showCount - oldShowCount);
    }*/

/*
    @Override
    public boolean canLoadMore() {
        return cursor.getCount() > showCount;
    }
*/


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView numOrName;
        TextView callInfo;
        CallTypeView callType;
        ImageView headShow;
        ImageButton callButton;
        TextView headerText;

        public ViewHolder(View itemView) {
            super(itemView);
            numOrName = (TextView) itemView.findViewById(R.id.caller_name_or_num);
            callInfo = (TextView) itemView.findViewById(R.id.call_info);
            callType = (CallTypeView) itemView.findViewById(R.id.call_type);
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

    private void convertCursorToBeans(Cursor cursor) {
        data.clear();
        int curr = 0;
        while (cursor != null && cursor.moveToNext()) {
            int callType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            String name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long time = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String location = cursor.getString(cursor.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION));
            if (curr == 0 || !data.get(curr - 1).getNumber().equals(number)) {
                CallogBean bean = new CallogBean();
                bean.setContactName(name);
                bean.setNumber(number);
                bean.setLocation(location);
                bean.addTimeMillis(time);
                bean.addType(callType);
                bean.increamentCount();
                data.add(bean);
                curr++;
            } else {
                CallogBean bean = data.get(curr - 1);
                if (bean.getNumber().equals(number)) {
                    bean.addTimeMillis(time);
                    bean.addType(callType);
                    bean.increamentCount();
                }
            }
        }
    }
}
