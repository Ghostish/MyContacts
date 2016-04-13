package learn.android.kangel.mycontacts.activities;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import learn.android.kangel.mycontacts.ContactCommonEditorView;
import learn.android.kangel.mycontacts.ContactEditorViewGroup;
import learn.android.kangel.mycontacts.ContactInfoBean;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.fragments.EditTextDialogFragment;

/**
 * Created by Kangel on 2016/3/30.
 */
public class EditContactActivity extends AppCompatActivity implements ContactCommonEditorView.onSpinnerItemSelectedListener, EditTextDialogFragment.onEditDialogButtonClickListener, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public final static String ACTION_ADD = "learn.android.kangel.mycontacts.add";
    public final static String ACTION_EDIT = "learn.android.kangel.mycontacts.edit";
    private static final String[] REQUEST_PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    private static final int REQUEST_STORAGE_PERMISSION = 11;
    private static final int REQUEST_TAKE_PHOTO = 211;
    private static final int REQUEST_CROP = 212;

    private EditTextDialogFragment mEditDialog;
    private Spinner targetSpinner; //reference to the spinner which is about to be modified
    private List<String> targetTypeStrings; //reference to the typeStrings which is about to be modified

    private ContactEditorViewGroup phoneGroup;
    private ContactEditorViewGroup emailGroup;
    private ContactEditorViewGroup addressGroup;
    private EditText nameText;
    private ImageView headShowImage;
    private ListPopupWindow mListPopupWindow;


    private boolean isContactHasName = false;
    private int structureNameRowId;
    private int rawContactId;

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
            "'" + ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE + "')";
    private String[] mSelectionArgs = {""};
    private String mLookUpKey;
    private int mContactId;

    private String mCurrentPhotoPath;


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
            mContactId = getIntent().getIntExtra("contactId", -1);
            mSelectionArgs[0] = String.valueOf(mContactId);
            getSupportLoaderManager().restartLoader(QUERY_CONTACT, null, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    //add the photo to gallery
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File f = new File(mCurrentPhotoPath);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    this.sendBroadcast(mediaScanIntent);

                    //decode a scale image
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(mCurrentPhotoPath, options);
                    int photoWidth = options.outWidth;
                    int photoHeight = options.outHeight;
                    int targetWidth= headShowImage.getWidth();
                    int targetHeight = headShowImage.getHeight();
                    int scaleFactor = Math.min(photoWidth/targetWidth, photoHeight/targetHeight);
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = scaleFactor;
                    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);

                    //set the image view
                    headShowImage.setImageBitmap(bitmap);
                   // startPhotoZoom(Uri.fromFile(temp));
                    break;
                }
                case REQUEST_CROP:{
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        headShowImage.setImageBitmap(photo);
                    }
                }
            }
        }
    }

    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if(intent.resolveActivity(getPackageManager()) != null){
            intent.setDataAndType(uri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 96);
            intent.putExtra("outputY", 96);
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CROP);
        }else {
            Toast.makeText(getApplicationContext(), "no such app", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onCustomTypeRequest(Spinner spinner, List<String> typeStrings) {
        targetSpinner = spinner;
        targetTypeStrings = typeStrings;
        if (mEditDialog == null) {
            mEditDialog = EditTextDialogFragment.newInstance(R.string.title_input_type, 0);
        }
        mEditDialog.show(getSupportFragmentManager(), "input");

    }

    @Override
    public void onPositiveButtonClick(String s) {
        if (!s.trim().isEmpty()) {
            targetTypeStrings.add(0, s);
            targetSpinner.setSelection(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button: {
                // TODO: 2016/4/9  show a progress dialog , and do the batch work in worker thread
                commitChanges();
                finish();
                break;
            }
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
                                    if (ActivityCompat.checkSelfPermission(EditContactActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        if (ActivityCompat.shouldShowRequestPermissionRationale(EditContactActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                            Toast.makeText(getApplicationContext(), R.string.permission_write_storage_request, Toast.LENGTH_LONG).show();
                                        } else {
                                            ActivityCompat.requestPermissions(EditContactActivity.this, REQUEST_PERMISSION, REQUEST_STORAGE_PERMISSION);
                                        }
                                    } else {
                                        takePicture();
                                    }
                                }
                            }
                        }
                    });
                }
                mListPopupWindow.show();
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                }
                break;
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath =  image.getAbsolutePath();
        return image;
    }

    private void commitChanges() {
        if (ACTION_ADD.equals(getIntent().getAction())) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder op = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "learn.kangel.mycontact")
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "cookie");
            ops.add(op.build());
            String name = nameText.getText().toString();
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
}
