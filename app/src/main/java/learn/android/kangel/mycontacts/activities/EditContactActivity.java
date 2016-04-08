package learn.android.kangel.mycontacts.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.List;

import javax.crypto.interfaces.PBEKey;

import learn.android.kangel.mycontacts.AnimationUtil;
import learn.android.kangel.mycontacts.ContactCommonEditorView;
import learn.android.kangel.mycontacts.ContactEditorViewGroup;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.EditFieldAdapter;
import learn.android.kangel.mycontacts.fragments.EditTextDialogFragment;

/**
 * Created by Kangel on 2016/3/30.
 */
public class EditContactActivity extends AppCompatActivity implements ContactCommonEditorView.onSpinnerItemSelectedListener, EditTextDialogFragment.onEditDialogButtonClickListener, View.OnClickListener {
    private EditTextDialogFragment mEditDialog;
    private Spinner targetSpinner; //reference to the spinner which is about to be modified
    private List<String> targetTypeStrings; //reference to the typeStrings which is about to be modified

    private ContactEditorViewGroup phoneGroup;
    private ContactEditorViewGroup emailGroup;
    private ContactEditorViewGroup addressGroup;
    private EditText nameText;
    private EditText nicknameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        nameText = (EditText) findViewById(R.id.name_field);
        nicknameText = (EditText) findViewById(R.id.nick_name_field);
        phoneGroup = (ContactEditorViewGroup) findViewById(R.id.phone);
        phoneGroup.addEditor();
        ContactEditorViewGroup emailGroup = (ContactEditorViewGroup) findViewById(R.id.email);
        emailGroup.addEditor();
        ContactEditorViewGroup addressGroup = (ContactEditorViewGroup) findViewById(R.id.address);
        addressGroup.addEditor();

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
        }
    }
}
