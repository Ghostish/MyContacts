package learn.android.kangel.mycontacts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import learn.android.kangel.mycontacts.R;

/**
 * Created by Kangel on 2016/4/26.
 */
public class BlockNumberAdapter extends RecyclerView.Adapter<BlockNumberAdapter.ViewHolder> {
    private Context mContext;
    private Cursor mCursor;
    private onItemActionListener mListener;

    public BlockNumberAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    public interface onItemActionListener {
        void onItemClick(int position);

        void onItemDeleteClick(int position);
    }

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    public void setItemActionListener(onItemActionListener listener) {
        this.mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_number_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String number = mCursor.getString(mCursor.getColumnIndex("number"));
        holder.numberText.setText(number);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView numberText;
        ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            numberText = (TextView) itemView.findViewById(R.id.number_text);
            deleteButton = (ImageButton) itemView.findViewById(R.id.delete_button);
            if (mListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(getAdapterPosition());
                    }
                });
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemDeleteClick(getAdapterPosition());
                    }
                });
            }
        }
    }
}
