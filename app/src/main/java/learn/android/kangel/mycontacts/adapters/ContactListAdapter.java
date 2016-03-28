package learn.android.kangel.mycontacts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.promeg.pinyinhelper.Pinyin;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.activities.RecyclerViewActivity;

/**
 * Created by Kangel on 2016/3/19.
 */
public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {
    private Cursor cursor;
    private Context context;
    public static final String TAG = "Contact_List_ADAPTER";
    private static final int TYPE_HEADER = 11, TYPE_NORMAL = 12;

    public ContactListAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.context = context;
    }

    public void updateCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || !getSectionName(position).equals(getSectionName(position - 1))) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        v.setTag(viewType);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.nameText.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
        if (holder.headerText != null) {
            holder.headerText.setText(getSectionName(position));
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        cursor.moveToPosition(position);
        String sortKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.SORT_KEY_PRIMARY));
        if (sortKey != null) {
            char initial = sortKey.charAt(0);
            if (Character.isLetter(initial)) {
                return String.valueOf(Character.toUpperCase(Pinyin.toPinyin(initial).charAt(0)));
            }
        }
        return "#";
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView headShow;
        TextView nameText;
        TextView headerText;

        public ViewHolder(View itemView) {
            super(itemView);
            headShow = (ImageView) itemView.findViewById(R.id.head_show);
            nameText = (TextView) itemView.findViewById(R.id.contact_name);
            if (((int) itemView.getTag()) == TYPE_HEADER) {
                ViewStub viewStub = (ViewStub) itemView.findViewById(R.id.header_view_stub);
                headerText = (TextView) viewStub.inflate();
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            cursor.moveToPosition(getAdapterPosition());
            if (context instanceof RecyclerViewActivity) {
                Bundle data = new Bundle();
                data.putString("lookUpKey", cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)));
                ((RecyclerViewActivity) context).onRecyclerViewItemClick(getAdapterPosition(), TAG, data);
            }
        }
    }
}
