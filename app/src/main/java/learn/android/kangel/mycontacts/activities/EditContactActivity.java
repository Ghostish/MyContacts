package learn.android.kangel.mycontacts.activities;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import learn.android.kangel.mycontacts.ContactCommonEditorView;
import learn.android.kangel.mycontacts.ContactEditorViewGroup;
import learn.android.kangel.mycontacts.fragments.ConfirmDialogFragment;
import learn.android.kangel.mycontacts.utils.ContactInfoBean;
import learn.android.kangel.mycontacts.utils.HeadShowLoader;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.fragments.EditTextDialogFragment;

/**
 * Created by Kangel on 2016/3/30.
 */
public class EditContactActivity extends AppCompatActivity implements ContactCommonEditorView.onSpinnerItemSelectedListener, EditTextDialogFragment.onEditDialogButtonClickListener,ConfirmDialogFragment.onConfirmDialogButtonClickListener, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public final static String ACTION_ADD = "learn.android.kangel.mycontacts.add";
    public final static String ACTION_EDIT = "learn.android.kangel.mycontacts.edit";


    private static final int REQUEST_TAKE_PHOTO = 211;
    private static final int REQUEST_CROP = 212;
    private static final int REQUEST_PICK_PHOTO = 213;

    private EditTextDialogFragment mEditDialog;
    private ConfirmDialogFragment mConfirmDialog;

    private Spinner targetSpinner; //reference to the spinner which is about to be modified
    private List<String> targetTypeStrings; //reference to the typeStrings which is about to be modified

    private ContactEditorViewGroup phoneGroup;
    private ContactEditorViewGroup emailGroup;
    private ContactEditorViewGroup addressGroup;
    private EditText nameText;
    private ImageView headShowImage;
    private ListPopupWindow mListPopupWindow;


    private boolean isContactHasName = false;
    private boolean isHasHeadShow = false;
    private int structureNameRowId;
    private int photoRowId;
    private int rawContactId;

    private Bitmap mNewHeadShowBitmap;
    private final static int QUERY_CONTACT = 33;
    private static final String[] PROJECTION =
            new String[]{
                    ContactsContract.Data._ID,
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.Data.DATA1,
                    ContactsContract.Data.DATA2,
                    ContactsContract.Data.DATA3,
                    ContactsContract.Data.RAW_CONTACT_ID
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
    private static final int ID_INDEX = 0;
    private static final int MIME_INDEX = 1;
    private static final int DATA1_INDEX = 2;
    private static final int DATA2_INDEX = 3;
    private static final int DATA3_INDEX = 4;
    private static final int RAW_CONTACT_ID_INDEX = 5;
    private static final String SELECTION = ContactsContract.Data.CONTACT_ID + " = ?" + " AND " +
            ContactsContract.Data.MIMETYPE + " in (" +
            "'" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "', " +
            "'" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "', " +
            "'" + ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE + "', " +
            "'" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "', " +
            "'" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "')";
    private String[] mSelectionArgs = {""};
    private String mLookUpKey;
    private long mContactId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        nameText = (EditText) findViewById(R.id.name_field);
        phoneGroup = (ContactEditorViewGroup) findViewById(R.id.phone);
        emailGroup = (ContactEditorViewGroup) findViewById(R.id.email);
        addressGroup = (ContactEditorViewGroup) findViewById(R.id.address);

        headShowImage = (ImageView) findViewById(R.id.head_show);
        String action = getIntent().getAction();
        if (ACTION_ADD.equals(action)) {
            toolbar.setTitle(R.string.title_add_contact);
            phoneGroup.setData(new ArrayList<ContactInfoBean>());
            emailGroup.setData(new ArrayList<ContactInfoBean>());
            addressGroup.setData(new ArrayList<ContactInfoBean>());
        } else if (ACTION_EDIT.equals(action)) {
            toolbar.setTitle(R.string.title_edit_contact);
            mLookUpKey = getIntent().getStringExtra("lookUpKey");
            mContactId = getIntent().getLongExtra("contactId", -1);
            mSelectionArgs[0] = String.valueOf(mContactId);
            InputStream in = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, mLookUpKey));
            if (in != null) {
                headShowImage.setImageBitmap(BitmapFactory.decodeStream(in));
            }
        }
        getSupportLoaderManager().restartLoader(QUERY_CONTACT, null, this);
        setSupportActionBar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.submit_edit: {
                commitChanges();
                if (getIntent().getExtras() != null) {
                    Intent intent = new Intent(this, ContactDetailActivity.class);
                    intent.putExtras(getIntent().getExtras());
                    startActivity(intent);
                }
                finish();
                return true;
            }
            case android.R.id.home: {
                if (mConfirmDialog == null) {
                    mConfirmDialog = ConfirmDialogFragment.newInstance(R.string.title_discard_changes, R.string.msg_changes_not_saved,R.style.mAlertDialogStyle);
                }
                mConfirmDialog.show(getSupportFragmentManager(), "confirmQuit");
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_PHOTO: {
                    Uri selectedImage = data.getData();
                    InputStream imageInputStream = null;
                    try {
                        imageInputStream = getContentResolver().openInputStream(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (imageInputStream != null) {
                        Display display = getWindowManager().getDefaultDisplay();
                        Point size = new Point();
                        display.getSize(size);
                        int screenWidth = size.x;
                        int screenHeight = size.y;
                        BitmapFactory.Options mOptions = new BitmapFactory.Options();
                        mOptions.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(imageInputStream, null, mOptions);
                        int photoW = mOptions.outWidth;
                        int photoH = mOptions.outHeight;
                        int scaleFactor = Math.max(photoH / screenHeight, photoW / screenWidth);
                        if (scaleFactor != 0) {
                            Log.d("test", "none zero");
                            Log.d("test", "screen width " + screenWidth);
                            Log.d("test", "screen height " + screenHeight);
                            Log.d("test", "image width " + photoW);
                            Log.d("test", "image height " + photoH);
                            mOptions.inJustDecodeBounds = false;
                            mOptions.inSampleSize = scaleFactor;
                            try {
                                imageInputStream.close();
                                imageInputStream = getContentResolver().openInputStream(selectedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mNewHeadShowBitmap = BitmapFactory.decodeStream(imageInputStream, null, mOptions);
                            Log.d("test", String.valueOf(mNewHeadShowBitmap == null));
                        } else {
                            try {
                                imageInputStream.close();
                                imageInputStream = getContentResolver().openInputStream(selectedImage);
                                mNewHeadShowBitmap = BitmapFactory.decodeStream(imageInputStream);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        headShowImage.setImageBitmap(mNewHeadShowBitmap);
                    }
                    break;
                }
                case REQUEST_TAKE_PHOTO: {
                    Bundle extras = data.getExtras();
                    mNewHeadShowBitmap = (Bitmap) extras.get("data");
                    headShowImage.setImageBitmap(mNewHeadShowBitmap);
                    break;
                }
                case REQUEST_CROP: {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        headShowImage.setImageBitmap(photo);
                    }
                }
            }
        }
    }
   /*called when spinner user wants to create a custom type*/

    @Override
    public void onCustomTypeRequest(Spinner spinner, List<String> typeStrings) {
        targetSpinner = spinner;
        targetTypeStrings = typeStrings;
        if (mEditDialog == null) {
            mEditDialog = EditTextDialogFragment.newInstance(R.string.title_input_type, 0);
        }
        mEditDialog.show(getSupportFragmentManager(), "input");

    }

   /*called when spinner item selected*/
    @Override
    public void onPositiveButtonClick(String s) {
        if (!s.trim().isEmpty()) {
            targetTypeStrings.add(0, s);
            targetSpinner.setSelection(0);
        }
    }

    /*called when user click the negative button on the confirmQuit */
    @Override
    public void onNegativeButtonClick() {

    }
    /*called when user click the positive button on the confirmQuit */
    @Override
    public void onPositiveButtonClick() {
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_photo_button: {
                if (mListPopupWindow == null) {
                    mListPopupWindow = new ListPopupWindow(this);
                    mListPopupWindow.setAnchorView(v);
                    String[] res = getResources().getStringArray(R.array.pick_photo_type);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, res);
                    mListPopupWindow.setAdapter(adapter);
                    mListPopupWindow.setContentWidth(v.getWidth() * 2);
                    View.OnTouchListener mListener = mListPopupWindow.createDragToOpenListener(v);
                    v.setOnTouchListener(mListener);
                    mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0: {
                                    takePicture();
                                    break;
                                }
                                case 1: {
                                    pickPicture();
                                    break;
                                }
                            }
                            mListPopupWindow.dismiss();
                        }
                    });
                }
                mListPopupWindow.show();
                break;
            }
        }
    }


    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_camera, Toast.LENGTH_LONG).show();
        }
    }

    private void pickPicture() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (pickPhotoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickPhotoIntent, REQUEST_PICK_PHOTO);
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_gallery, Toast.LENGTH_LONG).show();
        }
    }

    private void commitChanges() {
        if (ACTION_ADD.equals(getIntent().getAction())) {
            String name = nameText.getText().toString();

            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder op = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
            if (isRelative(name)) {
                op.withValue(ContactsContract.RawContacts.STARRED, 1);
            }
            ops.add(op.build());

            if (mNewHeadShowBitmap != null) {
                ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                mNewHeadShowBitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageStream);
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, imageStream.toByteArray());
                ops.add(op.build());
            }
            if (!name.trim().isEmpty()) {
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
                ops.add(op.build());
            }
            List<ContactInfoBean> phoneBean = phoneGroup.getData();
            List<ContactInfoBean> emailBean = emailGroup.getData();
            List<ContactInfoBean> addressBean = addressGroup.getData();
            int remainSize = phoneBean.size() + emailBean.size() + addressBean.size(); //use to notify when to add the yield point
            for (ContactInfoBean bean : phoneBean) {
                if (bean.isDirty() && !bean.getValue().isEmpty()) {
                    op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, bean.getValue())
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                            .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, bean.getLabel());
                    ops.add(op.build());
                }
                if (--remainSize == 0) {
                    op.withYieldAllowed(true);
                }
            }
            for (ContactInfoBean bean : emailBean) {
                if (bean.isDirty() && !bean.getValue().isEmpty()) {
                    op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, bean.getValue())
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
                            .withValue(ContactsContract.CommonDataKinds.Email.LABEL, bean.getLabel());
                    ops.add(op.build());
                }
                if (--remainSize == 0) {
                    op.withYieldAllowed(true);
                }
            }
            for (ContactInfoBean bean : addressBean) {
                if (bean.isDirty() && !bean.getValue().isEmpty()) {
                    op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, bean.getValue())
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, bean.getLabel());
                    ops.add(op.build());
                }
                if (--remainSize == 0) {
                    op.withYieldAllowed(true);
                }
            }
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Toast.makeText(getApplicationContext(), R.string.toast_contact_added, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.toast_contact_failed_add, Toast.LENGTH_LONG).show();
            }
        } else if (ACTION_EDIT.equals(getIntent().getAction())) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            ContentProviderOperation.Builder op;
            String name = nameText.getText().toString();
            if (isRelative(name)) {
                op = ContentProviderOperation.newUpdate(ContactsContract.RawContacts.CONTENT_URI)
                        .withSelection(ContactsContract.RawContacts._ID + "=?", new String[]{String.valueOf(rawContactId)})
                        .withValue(ContactsContract.RawContacts.STARRED, 1);
                ops.add(op.build());
            }

            if (mNewHeadShowBitmap != null) {
                HeadShowLoader.removeCacheItem(ContactsContract.Contacts.getLookupUri(mContactId, mLookUpKey));
                ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                mNewHeadShowBitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageStream);
                op = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.RAW_CONTACT_ID + "= ? AND " + ContactsContract.Data.MIMETYPE + "= '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", new String[]{String.valueOf(rawContactId)});
                ops.add(op.build());
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.Data.IS_PRIMARY, 1)
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, imageStream.toByteArray());
                ops.add(op.build());
            }
            if (isContactHasName && !name.trim().isEmpty()) {
                op = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(structureNameRowId)})
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
                ops.add(op.build());
            } else if (!isContactHasName && !TextUtils.isEmpty(name)) {
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
                ops.add(op.build());
            } else if (isContactHasName && TextUtils.isEmpty(name)) {
                op = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(structureNameRowId)});
                ops.add(op.build());
            }
            List<ContactInfoBean> phoneBean = phoneGroup.getData();
            List<ContactInfoBean> emailBean = emailGroup.getData();
            List<ContactInfoBean> addressBean = addressGroup.getData();
            for (ContactInfoBean bean : phoneBean) {
                if (bean.isDirty()) {
                    switch (bean.getUpdateType()) {
                        case ContactInfoBean.TYPE_DELETE:
                            op = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                                    .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(bean.getDataRowId())});
                            ops.add(op.build());
                            break;
                        case ContactInfoBean.TYPE_UPDATE:
                            if (!bean.getValue().isEmpty()) {
                                op = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                        .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(bean.getDataRowId())})
                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, bean.getValue())
                                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, bean.getLabel());
                                ops.add(op.build());
                            } else {
                                op = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                                        .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(bean.getDataRowId())});
                                ops.add(op.build());
                            }
                            break;
                        case ContactInfoBean.TYPE_INSERT:
                            if (!bean.getValue().isEmpty()) {
                                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, bean.getValue())
                                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                                        .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, bean.getLabel());
                                ops.add(op.build());
                            }
                            break;
                    }
                }
            }
            for (ContactInfoBean bean : emailBean) {
                if (bean.isDirty()) {
                    switch (bean.getUpdateType()) {
                        case ContactInfoBean.TYPE_DELETE:
                            op = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                                    .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(bean.getDataRowId())});
                            ops.add(op.build());
                            break;
                        case ContactInfoBean.TYPE_UPDATE:
                            if (!bean.getValue().isEmpty()) {
                                op = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                        .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(bean.getDataRowId())})
                                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, bean.getValue())
                                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
                                        .withValue(ContactsContract.CommonDataKinds.Email.LABEL, bean.getLabel());
                                ops.add(op.build());
                            } else {
                                op = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                                        .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(bean.getDataRowId())});
                                ops.add(op.build());
                            }
                            break;
                        case ContactInfoBean.TYPE_INSERT:
                            if (!bean.getValue().isEmpty()) {
                                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, bean.getValue())
                                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
                                        .withValue(ContactsContract.CommonDataKinds.Email.LABEL, bean.getLabel());
                                ops.add(op.build());
                            }
                            break;
                    }
                }
            }
            for (ContactInfoBean bean : addressBean) {
                if (bean.isDirty()) {
                    switch (bean.getUpdateType()) {
                        case ContactInfoBean.TYPE_DELETE:
                            op = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                                    .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(bean.getDataRowId())});
                            ops.add(op.build());
                            break;
                        case ContactInfoBean.TYPE_UPDATE:
                            if (!bean.getValue().isEmpty()) {
                                op = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                        .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(bean.getDataRowId())})
                                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, bean.getValue())
                                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM)
                                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, bean.getLabel());
                                ops.add(op.build());
                            } else {
                                op = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                                        .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(bean.getDataRowId())});
                                ops.add(op.build());
                            }
                            break;
                        case ContactInfoBean.TYPE_INSERT:
                            if (!bean.getValue().isEmpty()) {
                                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, bean.getValue())
                                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM)
                                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, bean.getLabel());
                                ops.add(op.build());
                            }
                            break;
                    }
                }
            }
            try {
                getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Toast.makeText(getApplicationContext(), R.string.toast_contact_edited, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.toast_contact_failed_editing, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                SELECTION,
                mSelectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<ContactInfoBean> phoneBean = new ArrayList<>();
        List<ContactInfoBean> emailBean = new ArrayList<>();
        List<ContactInfoBean> addressBean = new ArrayList<>();
        while (data.moveToNext()) {
            rawContactId = data.getInt(RAW_CONTACT_ID_INDEX);
            String mimeType = data.getString(MIME_INDEX);
            int rowId = data.getInt(ID_INDEX);
            String value = data.getString(DATA1_INDEX);
            int valueType = data.getInt(DATA2_INDEX);
            String label = data.getString(DATA3_INDEX);
            CharSequence typeString;
            switch (mimeType) {
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE:
                    typeString = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), valueType, label);
                    phoneBean.add(new ContactInfoBean(rowId, mimeType, ContactInfoBean.TYPE_UPDATE, value, typeString.toString()));
                    break;
                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE:
                    typeString = ContactsContract.CommonDataKinds.Email.getTypeLabel(getResources(), valueType, label);
                    emailBean.add(new ContactInfoBean(rowId, mimeType, ContactInfoBean.TYPE_UPDATE, value, typeString.toString()));
                    break;
                case ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE:
                    typeString = ContactsContract.CommonDataKinds.StructuredPostal.getTypeLabel(getResources(), valueType, label);
                    addressBean.add(new ContactInfoBean(rowId, mimeType, ContactInfoBean.TYPE_UPDATE, value, typeString.toString()));
                    break;
                case ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE:
                    nameText.setText(value);
                    isContactHasName = true;
                    structureNameRowId = rowId;
                    break;
                /*case ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE:
                    // TODO: 2016/4/13 use options to scale the image
                    byte[] image = data.getBlob(DATA15_INDEX);
                    if (image != null) {
                        isHasHeadShow = true;
                        photoRowId = rowId;
                        mNewHeadShowBitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(image));
                        headShowImage.setImageBitmap(mNewHeadShowBitmap);
                    }
                    break;*/
            }
        }
        phoneGroup.setData(phoneBean);
        emailGroup.setData(emailBean);
        addressGroup.setData(addressBean);
        loader.abandon();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public boolean isRelative(String name) {
        return name != null && (
                name.contains("爸") ||
                        name.contains("妈") ||
                        name.contains("老婆") ||
                        name.contains("老公") ||
                        name.contains("儿子") ||
                        name.contains("女儿"));
    }


}
