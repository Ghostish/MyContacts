package learn.android.kangel.mycontacts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import learn.android.kangel.mycontacts.R;

/**
 * Created by Kangel on 2016/3/19.
 */
public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {
    private Cursor cursor;
    private Context context;

    public ContactListAdapter(Context context, Cursor cursor) {
        this.cursor = cursor;
        this.context = context;
    }

    public void updateCursor(Cursor cursor) {
        this.cursor = cursor;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.nameText.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)));
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView headShow;
        TextView nameText;

        public ViewHolder(View itemView) {
            super(itemView);
            headShow = (ImageView) itemView.findViewById(R.id.head_show);
            nameText = (TextView) itemView.findViewById(R.id.contact_name);
        }
    }
}
