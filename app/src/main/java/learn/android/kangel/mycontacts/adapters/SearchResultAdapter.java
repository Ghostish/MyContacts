package learn.android.kangel.mycontacts.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import learn.android.kangel.mycontacts.utils.HeadShowLoader;
import learn.android.kangel.mycontacts.R;

/**
 * Created by Kangel on 2016/3/30.
 */
public class SearchResultAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private HeadShowLoader mHeadShowLoader = new HeadShowLoader();

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }


    public SearchResultAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public int getCount() {
        return cursor != null && !cursor.isClosed() ? cursor.getCount() : 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        cursor.moveToPosition(position);
        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY));
        String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        int type = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
        String label = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
        CharSequence numberTypeString = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(), type, label);
        int contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID));
        String lookUpKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY));
        int preContactId = cursor.moveToPrevious() ? cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)) : -1;
        holder.nameText.setText(name);
        holder.numberText.setText(number);
        holder.numberTypeText.setText(numberTypeString);
        if (preContactId != contactId) {
            holder.headShow.setVisibility(View.VISIBLE);
            holder.nameText.setVisibility(View.VISIBLE);
            mHeadShowLoader.bindImageView(holder.headShow, context, contactId,lookUpKey);
        } else {
            holder.nameText.setVisibility(View.INVISIBLE);
            holder.headShow.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public Cursor getCursor() {
        return cursor;
    }

    class ViewHolder {
        ImageView headShow;
        TextView nameText;
        TextView numberText;
        TextView numberTypeText;

        ViewHolder(View v) {
            headShow = (ImageView) v.findViewById(R.id.head_show);
            nameText = (TextView) v.findViewById(R.id.contact_name);
            numberText = (TextView) v.findViewById(R.id.number);
            numberTypeText = (TextView) v.findViewById(R.id.number_type);
        }
    }
}
