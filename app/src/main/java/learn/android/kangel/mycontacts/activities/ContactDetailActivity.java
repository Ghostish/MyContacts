package learn.android.kangel.mycontacts.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.List;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.fragments.ConfirmDialogFragment;

/**
 * Created by Kangel on 2016/3/24.
 */
public class ContactDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, ConfirmDialogFragment.onConfirmDialogButtonClickListener {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ConfirmDialogFragment mConfirmDialog;
    private final static int DETAILS_QUERY_ID = 0;
    private final static int NAME_QUERY_ID = 1;
    private final static int EMAIL_QUERY_ID = 2;
    private final static int PHONE_QUERY_ID = 3;
    private final static int ADDRESS_QUERY_ID = 4;
    private final static String TAG_PHONE = "PHONE";
    private final static String TAG_EMAIL = "EMAIL";
    private final static String TAG_ADDRESS = "ADDRESS";
    private String infoString;
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
    private static final int MIME_INDEX = 1;
    private static final int DATA1_INDEX = 2;
    private static final int DATA2_INDEX = 3;
    private static final int DATA3_INDEX = 4;

    private static final String SORT_ORDER = ContactsContract.Data.MIMETYPE;
    private static final String SELECTION = ContactsContract.Data.LOOKUP_KEY + " = ?" + " AND " +
            ContactsContract.Data.MIMETYPE + " in (" +
            "'" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "', " +
            "'" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "', " +
            "'" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "')";
    private static final String NAME_SELECTION = ContactsContract.Data.CONTACT_ID + " = ?" + " AND " +
            ContactsContract.Data.MIMETYPE + " = " +
            "'" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "'";
    private static final String PHONE_SELECTION = ContactsContract.Data.CONTACT_ID + " = ?" + " AND " +
            ContactsContract.Data.MIMETYPE + " = " +
            "'" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";
    private static final String EMAIL_SELECTION = ContactsContract.Data.CONTACT_ID + " = ?" + " AND " +
            ContactsContract.Data.MIMETYPE + " = " +
            "'" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'";
    private static final String ADDRESS_SELECTION = ContactsContract.Data.CONTACT_ID + " = ?" + " AND " +
            ContactsContract.Data.MIMETYPE + " = " +
            "'" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "'";
    // Defines the array to hold the search criteria
    private String[] mSelectionArgs = {""};

    private String mLookupKey;
    private int mContactId;
    private LinearLayout EmailContainer;
    private LinearLayout phoneNumContainer;
    private LinearLayout addressContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLookupKey = getIntent().getStringExtra("lookUpKey");
        mContactId = getIntent().getIntExtra("contactId", -1);
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_tool_bar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        ImageView imageView = (ImageView) findViewById(R.id.head_show);
        InputStream in = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, mLookupKey));
        if (in != null) {
            imageView.setImageBitmap(BitmapFactory.decodeStream(in));
        }
        getSupportLoaderManager().initLoader(NAME_QUERY_ID, null, this);
        getSupportLoaderManager().initLoader(PHONE_QUERY_ID, null, this);
        getSupportLoaderManager().initLoader(EMAIL_QUERY_ID, null, this);
        getSupportLoaderManager().initLoader(ADDRESS_QUERY_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSelectionArgs[0] = String.valueOf(mContactId);
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
            case ADDRESS_QUERY_ID: {
                return new CursorLoader(
                        this,
                        ContactsContract.Data.CONTENT_URI,
                        PROJECTION,
                        ADDRESS_SELECTION,
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
                    collapsingToolbarLayout.setTitle(data.getString(DATA1_INDEX));
                }else {
                    collapsingToolbarLayout.setTitle(getString(R.string.no_name));
                }
                break;
            }
            case PHONE_QUERY_ID: {
                if (data != null && data.getCount() > 0) {
                    if (phoneNumContainer == null) {
                        ViewStub viewStub = (ViewStub) findViewById(R.id.phone_view_stub);
                        View panel = viewStub.inflate();
                        phoneNumContainer = (LinearLayout) panel.findViewById(R.id.container);
                    }
                    phoneNumContainer.removeAllViews();
                    for (int i = 0; i < data.getCount(); i++) {
                        data.moveToPosition(i);
                        View v = LayoutInflater.from(this).inflate(R.layout.item_contact_info, phoneNumContainer, false);
                        ViewHolder holder = new ViewHolder(v, TAG_PHONE);
                        holder.actionButton.setVisibility(View.VISIBLE);
                        if (i == 0) {
                            holder.indicateIcon.setImageResource(R.drawable.ic_call_black_24dp);
                        }
                        holder.actionButton.setImageResource(R.drawable.ic_message_black_24dp);
                        holder.infoText.setText(data.getString(DATA1_INDEX));
                        int type = data.getInt(DATA2_INDEX);
                        CharSequence typeString = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, data.getString(DATA3_INDEX));
                        holder.hintText.setText(typeString);
                        phoneNumContainer.addView(v);
                    }
                }
                break;
            }
            case EMAIL_QUERY_ID: {
                if (data != null && data.getCount() > 0) {
                    if (EmailContainer == null) {
                        ViewStub viewStub = (ViewStub) findViewById(R.id.email_view_stub);
                        View panel = viewStub.inflate();
                        EmailContainer = (LinearLayout) panel.findViewById(R.id.container);
                    }
                    EmailContainer.removeAllViews();
                    for (int i = 0; i < data.getCount(); i++) {
                        data.moveToPosition(i);
                        View v = LayoutInflater.from(this).inflate(R.layout.item_contact_info, EmailContainer, false);
                        ViewHolder holder = new ViewHolder(v, TAG_EMAIL);
                        if (i == 0) {
                            holder.indicateIcon.setImageResource(R.drawable.ic_email_black_24dp);
                        }
                        holder.infoText.setText(data.getString(DATA1_INDEX));
                        int type = data.getInt(DATA2_INDEX);
                        CharSequence typeString = ContactsContract.CommonDataKinds.Email.getTypeLabel(getResources(), type, data.getString(DATA3_INDEX));
                        holder.hintText.setText(typeString);
                        EmailContainer.addView(v);
                    }
                }
                break;
            }
            case ADDRESS_QUERY_ID: {
                if (data != null && data.getCount() > 0) {
                    if (addressContainer == null) {
                        ViewStub viewStub = (ViewStub) findViewById(R.id.address_view_stub);
                        View panel = viewStub.inflate();
                        addressContainer = (LinearLayout) panel.findViewById(R.id.container);
                    }
                    addressContainer.removeAllViews();
                    for (int i = 0; i < data.getCount(); i++) {
                        data.moveToPosition(i);
                        View v = LayoutInflater.from(this).inflate(R.layout.item_contact_info, addressContainer, false);
                        ViewHolder holder = new ViewHolder(v, TAG_ADDRESS);
                        if (i == 0) {
                            holder.indicateIcon.setImageResource(R.drawable.ic_location_on_black_24dp);
                        }
                        holder.infoText.setText(data.getString(DATA1_INDEX));
                        int type = data.getInt(DATA2_INDEX);
                        CharSequence typeString = ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabel(getResources(), type, data.getString(DATA3_INDEX));
                        holder.hintText.setText(typeString);
                        addressContainer.addView(v);
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
                ViewHolder holder = (ViewHolder) v.getTag();
                switch (holder.tag) {
                    case TAG_PHONE:
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + holder.infoText.getText()));
                        startActivity(intent);
                        break;
                }
                break;
            }
            case R.id.action_icon: {
                ViewHolder holder = (ViewHolder) v.getTag();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", holder.infoText.getText().toString(), null)));
                break;
            }
            case R.id.delete_button: {
                if (mConfirmDialog == null) {
                    mConfirmDialog = ConfirmDialogFragment.newInstance(R.string.title_delete_contact, R.string.msg_delete_contact, R.style.mAlertDialogStyle);
                }
                mConfirmDialog.show(getSupportFragmentManager(), "confirm");
                break;
            }
            case R.id.fab: {
                Intent intent = new Intent(ContactDetailActivity.this, EditContactActivity.class);
                intent.setAction(EditContactActivity.ACTION_EDIT);
                intent.putExtra("lookUpKey", mLookupKey);
                intent.putExtra("contactId", mContactId);
                startActivity(intent);
                break;
            }
            case R.id.head_show: {
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        ViewHolder holder = (ViewHolder) v.getTag();
        switch (holder.tag) {
            case TAG_PHONE:
                infoString = holder.infoText.getText().toString();
                inflater.inflate(R.menu.menu_context_phone, menu);
                break;
            case TAG_EMAIL:
                infoString = holder.infoText.getText().toString();
                inflater.inflate(R.menu.menu_context_email, menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(infoString);
                Toast.makeText(ContactDetailActivity.this, getString(R.string.copy_successfully), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.send_email:
                Uri mailTo = Uri.parse("mailto:" + infoString);
                Intent intent = new Intent(Intent.ACTION_SENDTO, mailTo);
                //Verify There is an App to Receive the Intent
                PackageManager packageManager = getPackageManager();
                List activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (activities.size() > 0) {
                    startActivity(intent);
                } else {
                    Toast.makeText(ContactDetailActivity.this, getString(R.string.fail_to_send_Email), Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.warm_sms: {
                // TODO: 2016/3/26 send warm sms
            }
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onNegativeButtonClick() {

    }

    @Override
    public void onPositiveButtonClick() {
        deleteThisContact();
        Toast.makeText(getApplicationContext(), R.string.toast_contact_deleted, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void deleteThisContact() {
        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, mLookupKey);
        getContentResolver().delete(uri, null, null);
    }

    class ViewHolder {
        TextView infoText;
        TextView hintText;
        ImageView indicateIcon;
        ImageView actionButton;
        String tag;

        ViewHolder(View v, String tag) {
            infoText = (TextView) v.findViewById(R.id.info_text);
            hintText = (TextView) v.findViewById(R.id.hint_text);
            indicateIcon = (ImageView) v.findViewById(R.id.indicate_icon);
            this.tag = tag;
            if (TAG_PHONE.equals(tag)) {
                actionButton = (ImageView) v.findViewById(R.id.action_icon);
                actionButton.setOnClickListener(ContactDetailActivity.this);
                actionButton.setTag(this);
            }
            v.setTag(this);
            registerForContextMenu(v);
            v.setOnClickListener(ContactDetailActivity.this);
        }

    }
}
