package learn.android.kangel.mycontacts.activities;

import android.content.ContentProviderOperation;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.interfaces.PBEKey;

import learn.android.kangel.mycontacts.AnimationUtil;
import learn.android.kangel.mycontacts.ContactCommonEditorView;
import learn.android.kangel.mycontacts.ContactEditorViewGroup;
import learn.android.kangel.mycontacts.ContactInfoBean;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.EditFieldAdapter;
import learn.android.kangel.mycontacts.fragments.EditTextDialogFragment;

/**
 * Created by Kangel on 2016/3/30.
 */
public class EditContactActivity extends AppCompatActivity implements ContactCommonEditorView.onSpinnerItemSelectedListener, EditTextDialogFragment.onEditDialogButtonClickListener, View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    public final static String ACTION_ADD = "learn.android.kangel.mycontacts.add";
    public final static String ACTION_EDIT = "learn.android.kangel.mycontacts.edit";
    private EditTextDialogFragment mEditDialog;
    private Spinner targetSpinner; //reference to the spinner which is about to be modified
    private List<String> targetTypeStrings; //reference to the typeStrings which is about to be modified

    private ContactEditorViewGroup phoneGroup;
    private ContactEditorViewGroup emailGroup;
    private ContactEditorViewGroup addressGroup;
    private EditText nameText;
    private EditText nicknameText;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);

        nameText = (EditText) findViewById(R.id.name_field);
        nicknameText = (EditText) findViewById(R.id.nick_name_field);
        phoneGroup = (ContactEditorViewGroup) findViewById(R.id.phone);
        emailGroup = (ContactEditorViewGroup) findViewById(R.id.email);
        addressGroup = (ContactEditorViewGroup) findViewById(R.id.address);
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
            case R.id.expand_button:
                ImageButton button = (ImageButton) v;
                if (!nicknameText.isShown()) {
                    nicknameText.setVisibility(View.VISIBLE);
                    nicknameText.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    AnimationUtil.slide((View) nicknameText.getParent(), ((View) nicknameText.getParent()).getHeight(), ((View) nicknameText.getParent()).getHeight() + nicknameText.getMeasuredHeight());
                    button.setImageResource(R.drawable.ic_expand_less_black_24dp);
                } else {
                    button.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    nicknameText.setVisibility(View.GONE);
                    AnimationUtil.slide((View) nicknameText.getParent(), ((View) nicknameText.getParent()).getHeight(), ((View) nicknameText.getParent()).getHeight() - nicknameText.getMeasuredHeight());

                }
                break;
            case R.id.ok_button:
                // TODO: 2016/4/9  show a progress dialog , and do the batch work in worker thread
                commitChanges();
                finish();
                break;
        }
    }

    private void commitChanges() {
        if (ACTION_ADD.equals(getIntent().getAction())) {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
            ContentProviderOperation.Builder op = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, "learn.kangel.mycontact")
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, "cookie");
            ops.add(op.build());
            op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nameText.getText().toString());
            ops.add(op.build());
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
            if (isContactHasName && !TextUtils.isEmpty(nameText.getText())) {
                op = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(structureNameRowId)})
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nameText.getText().toString());
                ops.add(op.build());
            } else if (!isContactHasName && !TextUtils.isEmpty(nameText.getText())) {
                op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nameText.getText().toString());
                ops.add(op.build());
            } else if (isContactHasName && TextUtils.isEmpty(nameText.getText())) {
                op = ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data._ID + "= ?", new String[]{String.valueOf(structureNameRowId)});
                ops.add(op.build());
            }
            List<ContactInfoBean> phoneBean = phoneGroup.getData();
            List<ContactInfoBean> emailBean = emailGroup.getData();
            List<ContactInfoBean> addressBean = addressGroup.getData();
            int remainSize = phoneBean.size() + emailBean.size() + addressBean.size(); //use to notify when to add the yield point
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
                            }else {
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
                            }else {
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
                                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, nameText.getText().toString())
                                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM)
                                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.LABEL, bean.getLabel());
                                ops.add(op.build());
                            }else {
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
