package learn.android.kangel.mycontacts.adapters;

import android.content.Context;
import android.provider.CallLog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.utils.CallogBean;

/**
 * Created by Kangel on 2016/4/19.
 */
public class CallLogDetailAdapter extends RecyclerView.Adapter<CallLogDetailAdapter.ViewHolder> {
    private Context context;
    private CallogBean data;

    public CallLogDetailAdapter(Context context, CallogBean data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_call_log_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.timeText.setText(DateUtils.formatDateTime(context, data.getTimeMillisAtPosition(position), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_WEEKDAY|DateUtils.FORMAT_SHOW_DATE));
        holder.durationText.setText(DateUtils.formatElapsedTime(data.getDurationAtPosition(position)));
        switch (data.getCallTypeAtPosition(position)) {
            case CallLog.Calls.INCOMING_TYPE:
                holder.typeIcon.setImageResource(R.drawable.ic_call_received_24dp);
                holder.typeText.setText(R.string.incoming_call);
                break;
            case CallLog.Calls.OUTGOING_TYPE:
                holder.typeIcon.setImageResource(R.drawable.ic_call_made_24dp);
                holder.typeText.setText(R.string.outgoing_call);
                break;
            case CallLog.Calls.MISSED_TYPE:
                holder.typeIcon.setImageResource(R.drawable.ic_call_missed_24dp);
                holder.typeText.setText(R.string.missed_call);
                break;
            default:
                holder.typeIcon.setImageResource(R.drawable.ic_call_missed_24dp);
                holder.typeText.setText(R.string.unknow_call);
        }
    }

    @Override
    public int getItemCount() {
        return data.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView typeIcon;
        TextView timeText;
        TextView typeText;
        TextView durationText;

        public ViewHolder(View itemView) {
            super(itemView);
            typeIcon = (ImageView) itemView.findViewById(R.id.type_icon);
            timeText = (TextView) itemView.findViewById(R.id.time_text);
            typeText = (TextView) itemView.findViewById(R.id.type_text);
            durationText = (TextView) itemView.findViewById(R.id.duration_text);

        }
    }
}
