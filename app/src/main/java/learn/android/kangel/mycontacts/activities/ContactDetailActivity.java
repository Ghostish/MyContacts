package learn.android.kangel.mycontacts.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import learn.android.kangel.mycontacts.R;

/**
 * Created by Kangel on 2016/3/24.
 */
public class ContactDetailActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private LinearLayout container;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private final static int DETAILS_QUERY_ID = 0;
    private final static int NAME_QUERY_ID = 1;
    private final static int EMAIL_QUERY_ID = 2;
    private final static int PHONE_QUERY_ID = 3;
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
        getSupportLoaderManager().initLoader(DETAILS_QUERY_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSelectionArgs[0] = mLookupKey;
        // Starts the query
        CursorLoader mLoader =
                new CursorLoader(
                        this,
                        ContactsContract.Data.CONTENT_URI,
                        PROJECTION,
                        SELECTION,
                        mSelectionArgs,
                        SORT_ORDER
                );
        return mLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        while (data.moveToNext()) {
            String mimeType = data.getString(MIME_INDEX);
            View v = LayoutInflater.from(this).inflate(R.layout.item_contact_info, container, false);
            TextView infoText = (TextView) v.findViewById(R.id.info_text);
            TextView hintText = (TextView) v.findViewById(R.id.hint_text);
            ImageView indicateIcon = (ImageView) v.findViewById(R.id.indicate_icon);
            ImageView action = (ImageView) v.findViewById(R.id.action_icon);
            switch (mimeType) {
                case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE: {
                    collapsingToolbarLayout.setTitleEnabled(true);
                    collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
                    collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
                    collapsingToolbarLayout.setTitle(data.getString(DATA1_INDEX));
                    break;
                }
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE: {
                    infoText.setText(data.getString(DATA1_INDEX));
                    int type = data.getInt(DATA2_INDEX);
                    CharSequence typeString = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, data.getString(DATA3_INDEX));
                    hintText.setText(typeString);
                    action.setImageResource(R.drawable.ic_message_black_24dp);
                    container.addView(v);
                    break;
                }
                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE: {
                    infoText.setText(data.getString(DATA1_INDEX));
                    int type = data.getInt(DATA2_INDEX);
                    CharSequence typeString = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, data.getString(DATA3_INDEX));
                    hintText.setText(typeString);
                    container.addView(v);
                    break;
                }
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

}
