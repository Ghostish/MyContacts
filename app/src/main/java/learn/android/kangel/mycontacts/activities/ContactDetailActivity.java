package learn.android.kangel.mycontacts.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import learn.android.kangel.mycontacts.R;

/**
 * Created by Kangel on 2016/3/24.
 */
public class ContactDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private LinearLayout container;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private final static int DETAILS_QUERY_ID = 0;
    private final static int NAME_QUERY_ID = 1;
    private final static int EMAIL_QUERY_ID = 2;
    private final static int PHONE_QUERY_ID = 3;
    private final static String TAG_PHONE = "PHONE";
    private final static String TAG_EMAIL = "EMAIL";
    private static final String[] PROJECTION =
            new String[]{
                    ContactsContract.Data._ID,
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.Data.DATA1,
                    ContactsContract.Data.DATA2,
                    ContactsContract.Data.DATA3
                   /* ContactsContract.Data.DATA4,
                    ContactsContract.Data.DATA5,
                    ContactsContract.Data.DATA6,
                    ContactsContract.Data.DATA7,
                    ContactsContract.Data.DATA8,
                    ContactsContract.Data.DATA9,
                    ContactsContract.Data.DATA10,
                    ContactsContract.Data.DATA11,
                    ContactsContract.Data.DATA12,
                    ContactsContract.Data.DATA13,
                    ContactsContract.Data.DATA14,
                    ContactsContract.Data.DATA15*/
            };
    private static int MIME_INDEX = 1;
    private static int DATA1_INDEX = 2;
    private static int DATA2_INDEX = 3;
    private static int DATA3_INDEX = 4;

    private static final String SORT_ORDER = ContactsContract.Data.MIMETYPE;
    private static final String SELECTION = ContactsContract.Data.LOOKUP_KEY + " = ?" + " AND " +
            ContactsContract.Data.MIMETYPE + " in (" +
            "'" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "', " +
            "'" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "', " +
            "'" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "')";
    private static final String NAME_SELECTION = ContactsContract.Data.LOOKUP_KEY + " = ?" + " AND " +
            ContactsContract.Data.MIMETYPE + " = " +
            "'" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'";
    private static final String PHONE_SELECTION = ContactsContract.Data.LOOKUP_KEY + " = ?" + " AND " +
            ContactsContract.Data.MIMETYPE + " = " +
            "'" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";
    private static final String EMAIL_SELECTION = ContactsContract.Data.LOOKUP_KEY + " = ?" + " AND " +
            ContactsContract.Data.MIMETYPE + " = " +
            "'" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'";
    // Defines the array to hold the search criteria
    private String[] mSelectionArgs = {""};

    private String mLookupKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLookupKey = getIntent().getStringExtra("lookUpKey");
        setContentView(R.layout.activity_contact_detail);
        container = (LinearLayout) findViewById(R.id.container);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_tool_bar);
        getSupportLoaderManager().initLoader(NAME_QUERY_ID, null, this);
        getSupportLoaderManager().initLoader(PHONE_QUERY_ID, null, this);
        getSupportLoaderManager().initLoader(EMAIL_QUERY_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSelectionArgs[0] = mLookupKey;
        // Starts the query
        switch (id) {
            case NAME_QUERY_ID: {
                return new CursorLoader(
                        this,
                        ContactsContract.Data.CONTENT_URI,
                        PROJECTION,
                        NAME_SELECTION,
                        mSelectionArgs,
                        SORT_ORDER
                );
            }
            case PHONE_QUERY_ID: {
                return new CursorLoader(
                        this,
                        ContactsContract.Data.CONTENT_URI,
                        PROJECTION,
                        PHONE_SELECTION,
                        mSelectionArgs,
                        SORT_ORDER
                );
            }
            case EMAIL_QUERY_ID: {
                return new CursorLoader(
                        this,
                        ContactsContract.Data.CONTENT_URI,
                        PROJECTION,
                        EMAIL_SELECTION,
                        mSelectionArgs,
                        SORT_ORDER
                );
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case NAME_QUERY_ID: {
                if (data != null && data.moveToNext()) {
                    collapsingToolbarLayout.setTitleEnabled(true);
                    collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
                    collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
                    collapsingToolbarLayout.setTitle(data.getString(DATA1_INDEX));
                }
                break;
            }
            case PHONE_QUERY_ID: {
                if (data != null && data.getCount() > 0) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.phone_view_stub);
                    View panel = viewStub.inflate();
                    LinearLayout container = (LinearLayout) panel.findViewById(R.id.container);
                    for (int i = 0; i < data.getCount(); i++) {
                        data.moveToPosition(i);
                        View v = LayoutInflater.from(this).inflate(R.layout.item_contact_info, container, false);
                        TextView infoText = (TextView) v.findViewById(R.id.info_text);
                        TextView hintText = (TextView) v.findViewById(R.id.hint_text);
                        ImageView indicateIcon = (ImageView) v.findViewById(R.id.indicate_icon);
                        ImageView action = (ImageView) v.findViewById(R.id.action_icon);
                        action.setVisibility(View.VISIBLE);
                        if (i == 0) {
                            indicateIcon.setImageResource(R.drawable.ic_call_black_24dp);
                        }
                        action.setImageResource(R.drawable.ic_message_black_24dp);
                        infoText.setText(data.getString(DATA1_INDEX));
                        int type = data.getInt(DATA2_INDEX);
                        CharSequence typeString = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, data.getString(DATA3_INDEX));
                        hintText.setText(typeString);
                        v.setOnClickListener(this);
                        action.setOnClickListener(this);
                        action.setTag(infoText.getText()); //put number which is used to send sms in the actionButton
                        v.setTag(TAG_PHONE);
                        registerForContextMenu(v);
                        container.addView(v);
                    }
                }
                break;
            }
            case EMAIL_QUERY_ID: {
                if (data != null && data.getCount() > 0) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.email_view_stub);
                    View panel = viewStub.inflate();
                    LinearLayout container = (LinearLayout) panel.findViewById(R.id.container);
                    for (int i = 0; i < data.getCount(); i++) {
                        data.moveToPosition(i);
                        View v = LayoutInflater.from(this).inflate(R.layout.item_contact_info, container, false);
                        TextView infoText = (TextView) v.findViewById(R.id.info_text);
                        TextView hintText = (TextView) v.findViewById(R.id.hint_text);
                        ImageView indicateIcon = (ImageView) v.findViewById(R.id.indicate_icon);
                        if (i == 0) {
                            indicateIcon.setImageResource(R.drawable.ic_email_black_24dp);
                        }
                        infoText.setText(data.getString(DATA1_INDEX));
                        int type = data.getInt(DATA2_INDEX);
                        CharSequence typeString = ContactsContract.CommonDataKinds.Email.getTypeLabel(getResources(), type, data.getString(DATA3_INDEX));
                        hintText.setText(typeString);
                        v.setTag(TAG_EMAIL);
                        registerForContextMenu(v);
                        container.addView(v);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_item: {
                String tag = (String) v.getTag();
                switch (tag) {
                    case TAG_PHONE:
                        TextView infoText = (TextView) v.findViewById(R.id.info_text);
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + infoText.getText()));
                        startActivity(intent);
                        break;
                }
                break;
            }
            case R.id.action_icon: {
                String number = (String) v.getTag();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
                break;
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        switch ((String)v.getTag()) {
            case TAG_PHONE:
                inflater.inflate(R.menu.menu_context_phone, menu);
                break;
            case TAG_EMAIL:
                inflater.inflate(R.menu.menu_context_email, menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                TextView infoText = (TextView) info.targetView.findViewById(R.id.info_text);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(infoText.getText().toString());
                Toast.makeText(ContactDetailActivity.this, getString(R.string.copy_successfully), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    class ViewHolder {
        TextView infoText;
        TextView hintText;
        ImageView indicateIcon;
        ImageView actionButton;
        ViewHolder(View v){
            TextView infoText = (TextView) v.findViewById(R.id.info_text);
            TextView hintText = (TextView) v.findViewById(R.id.hint_text);
            ImageView indicateIcon = (ImageView) v.findViewById(R.id.indicate_icon);
            ImageView action = (ImageView) v.findViewById(R.id.action_icon);
        }

    }
}
