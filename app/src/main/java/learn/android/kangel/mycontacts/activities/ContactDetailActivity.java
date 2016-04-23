package learn.android.kangel.mycontacts.activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.yzh.msg.HelloMsg;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.fragments.ConfirmDialogFragment;
import learn.android.kangel.mycontacts.utils.BlackListUtil;
import learn.android.kangel.mycontacts.utils.HeadShowLoader;

/**
 * Created by Kangel on 2016/3/24.
 */
public class ContactDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener, ConfirmDialogFragment.onConfirmDialogButtonClickListener {
    private static final int REQUEST_CALL_PHONE = 110;
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
    private long mContactId;
    private String name;
    private ArrayList<String> mNumberList;

    private LinearLayout EmailContainer;
    private LinearLayout phoneNumContainer;
    private LinearLayout addressContainer;

    private Animator mCurrentAnimator;
    private int mShortAnimationDuration = 300;
    private HelloMsg msgHelper;

    private boolean isContactBlocked = false;
    private boolean isStar = false;
    private HeadShowLoader mHeadShowLoader = new HeadShowLoader();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLookupKey = getIntent().getStringExtra("lookUpKey");
        mContactId = getIntent().getLongExtra("contactId", -1);
        setContentView(R.layout.activity_contact_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_tool_bar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        ImageView imageView = (ImageView) findViewById(R.id.head_show);
        Log.d("contact info", mContactId + " " + mLookupKey);
        /*InputStream in = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), ContactsContract.Contacts.getLookupUri(mContactId, mLookupKey));
        if (in != null) {
            Log.d("contact info", mContactId + " " + mLookupKey);
            imageView.setImageBitmap(BitmapFactory.decodeStream(in));
        }*/
        mHeadShowLoader.bindImageView(imageView, this, mContactId, mLookupKey);

        getResources().getInteger(android.R.integer.config_shortAnimTime);
        getSupportLoaderManager().initLoader(NAME_QUERY_ID, null, this);
        getSupportLoaderManager().initLoader(PHONE_QUERY_ID, null, this);
        getSupportLoaderManager().initLoader(EMAIL_QUERY_ID, null, this);
        getSupportLoaderManager().initLoader(ADDRESS_QUERY_ID, null, this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mHeadShowLoader.bindImageView((ImageView) findViewById(R.id.head_show), this, mContactId, mLookupKey);

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
                    name = data.getString(DATA1_INDEX);
                    collapsingToolbarLayout.setTitle(name);
                } else {
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
                    if (mNumberList == null) {
                        mNumberList = new ArrayList<>();
                    }
                    mNumberList.clear();
                    for (int i = 0; i < data.getCount(); i++) {
                        data.moveToPosition(i);
                        View v = LayoutInflater.from(this).inflate(R.layout.item_contact_info, phoneNumContainer, false);
                        ViewHolder holder = new ViewHolder(v, TAG_PHONE);
                        holder.actionButton.setVisibility(View.VISIBLE);
                        if (i == 0) {
                            holder.indicateIcon.setImageResource(R.drawable.ic_call_black_24dp);
                        }
                        String number = data.getString(DATA1_INDEX);
                        int type = data.getInt(DATA2_INDEX);
                        holder.actionButton.setImageResource(R.drawable.ic_message_black_24dp);
                        holder.infoText.setText(number);
                        CharSequence typeString = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), type, data.getString(DATA3_INDEX));
                        holder.hintText.setText(typeString);
                        mNumberList.add(number);
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
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(ContactDetailActivity.this, Manifest.permission.CALL_PHONE)) {
                                Toast.makeText(getApplicationContext(), R.string.permission_call_phone_request, Toast.LENGTH_LONG).show();
                            } else {
                                ActivityCompat.requestPermissions(ContactDetailActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
                            }
                            return;
                        }
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + holder.infoText.getText()));
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
            case R.id.fab: {
                Intent intent = new Intent(ContactDetailActivity.this, EditContactActivity.class);
                intent.setAction(EditContactActivity.ACTION_EDIT);
                intent.putExtra("lookUpKey", mLookupKey);
                intent.putExtra("contactId", mContactId);
                startActivity(intent);
                break;
            }
            case R.id.head_show: {
                zoomImageFromThumb(v);
                break;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        MenuItem block = menu.findItem(R.id.block_contact);
        MenuItem star = menu.findItem(R.id.star_contact);
        isContactBlocked = BlackListUtil.isBlockedContact(this, mLookupKey);
        block.setTitle(isContactBlocked ? R.string.unblock_contact : R.string.block_contact);
        final Uri contactUri = ContactsContract.Contacts.getLookupUri(mContactId, mLookupKey);
        Cursor c = getContentResolver().query(contactUri, new String[]{ContactsContract.Contacts.STARRED}, null, null, null);
        if (c.moveToNext()) {
            isStar = c.getInt(0) != 0;
        }
        c.close();
        star.setIcon(isStar ? R.drawable.ic_star_white_24dp : R.drawable.ic_star_border_white_24dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_contact: {
                if (mConfirmDialog == null) {
                    mConfirmDialog = ConfirmDialogFragment.newInstance(R.string.title_delete_contact, R.string.msg_delete_contact, R.style.mAlertDialogStyle);
                }
                mConfirmDialog.show(getSupportFragmentManager(), "confirm");
                return true;
            }
            case R.id.block_contact: {
                // TODO: 2016/4/20 try to use a asynchronized way
                if (isContactBlocked) {
                    isContactBlocked = !BlackListUtil.removeContactFromBlackList(this, mLookupKey);
                    getSupportActionBar().invalidateOptionsMenu();
                } else {
                    isContactBlocked = BlackListUtil.addToContactBlackList(this, mLookupKey);
                    if (mNumberList != null && mNumberList.size() > 0) {
                        String[] numbers = new String[mNumberList.size()];
                        BlackListUtil.addToNumberBlackList(this, mLookupKey, mNumberList.toArray(numbers));
                    }
                    getSupportActionBar().invalidateOptionsMenu();
                }
                return true;
            }
            case R.id.star_contact: {
                setStar(isStar = !isStar);
                getSupportActionBar().invalidateOptionsMenu();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.copy: {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(infoString);
                Toast.makeText(ContactDetailActivity.this, getString(R.string.copy_successfully), Toast.LENGTH_SHORT).show();
                return true;
            }
            case R.id.send_email: {
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
            }
            case R.id.warm_sms: {
                // TODO: 2016/3/26 send warm sms
                if (msgHelper == null) {
                    msgHelper = new HelloMsg(this);
                }
                String msg = msgHelper.getMsg(name);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + infoString));
                intent.putExtra("sms_body", msg);
                startActivity(intent);
                return true;
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
        final Uri contactUri = ContactsContract.Contacts.getLookupUri(mContactId, mLookupKey);
        getContentResolver().delete(contactUri, null, null);
    }

    private void setStar(boolean isStar) {
        final Uri contactUri = ContactsContract.Contacts.getLookupUri(mContactId, mLookupKey);
        ContentValues values = new ContentValues();
        values.put(ContactsContract.RawContacts.STARRED, isStar ? 1 : 0);
        getContentResolver().update(contactUri, values, null, null);
    }

    private void zoomImageFromThumb(final View thumbView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        InputStream in = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, mLookupKey), true);
        if (in != null) {
            expandedImageView.setImageBitmap(BitmapFactory.decodeStream(in));
        } else {
            expandedImageView.setImageResource(R.drawable.default_head_show_rec);
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.coordinator).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y, startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
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
