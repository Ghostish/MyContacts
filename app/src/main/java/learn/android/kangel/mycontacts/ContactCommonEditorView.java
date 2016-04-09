package learn.android.kangel.mycontacts;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kangel on 2016/4/6.
 */
public class ContactCommonEditorView extends RelativeLayout {
    public final static int TYPE_PHONE = 0;
    public final static int TYPE_EMAIL = 1;
    public final static int TYPE_ADDRESS = 2;
    private ImageButton mDeleteButton;
    private EditText mInputFiled;
    private Spinner mTypeSpinner;

    private int mType;

    private boolean isCollapsed;
    private List<String> typeStrings;

    private ContactEditorViewGroup.onChildTextChangeListener mOnChildTextChangeListener;
    private ContactEditorViewGroup.onChildDeleteClickListener mOnChildDeleteClickListener;

    public interface onSpinnerItemSelectedListener{
        void onCustomTypeRequest(Spinner spinner,List<String> typeStrings);
    }
    public ContactCommonEditorView(Context context) {
        this(context, null);
    }

    public ContactCommonEditorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactCommonEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_edit_file, this);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ContactCommonEditorView, 0, 0);

        mType = a.getInteger(R.styleable.ContactCommonEditorView_dataType, 0);
        isCollapsed = a.getBoolean(R.styleable.ContactCommonEditorView_isCollapsed, true);
        a.recycle();

        mDeleteButton = (ImageButton) findViewById(R.id.delete_button);
        mInputFiled = (EditText) findViewById(R.id.input_field);
        mTypeSpinner = (Spinner) findViewById(R.id.type_spinner);

        mDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnChildDeleteClickListener != null) {
                    mOnChildDeleteClickListener.onChildDeleteClick(ContactCommonEditorView.this);
                }
            }
        });
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == typeStrings.size() - 1) {
                    // TODO: 2016/4/7 show a dialog to let user input a custom type
                    if (getContext() instanceof onSpinnerItemSelectedListener) {
                        ((onSpinnerItemSelectedListener) getContext()).onCustomTypeRequest(mTypeSpinner,typeStrings);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mInputFiled.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isCollapsed) {
                    isCollapsed = false;
                    /**
                     * show the spinner with slide down animation
                     */
                    mTypeSpinner.setVisibility(VISIBLE);
                    mTypeSpinner.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                    AnimationUtil.slide(mTypeSpinner, 0, mTypeSpinner.getMeasuredHeight());
                }
            }
        });
        mInputFiled.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mOnChildTextChangeListener != null) {
                    mOnChildTextChangeListener.onTextChanged(ContactCommonEditorView.this, s, before, count);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                mDeleteButton.setVisibility(TextUtils.isEmpty(s) ? INVISIBLE : VISIBLE);
            }
        });
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        switch (mType) {
            case TYPE_PHONE:
                mInputFiled.setHint(R.string.hint_number);
                mInputFiled.setInputType(InputType.TYPE_CLASS_PHONE);
                typeStrings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.phone_type)));
                break;
            case TYPE_EMAIL:
                mInputFiled.setHint(R.string.hint_email);
                mInputFiled.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                typeStrings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.email_type)));
                break;
            case TYPE_ADDRESS:
                mInputFiled.setHint(R.string.hint_address);
                typeStrings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.address_type)));
                break;
        }
        SpinnerAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, typeStrings);
        mTypeSpinner.setAdapter(adapter);
        mTypeSpinner.setVisibility(isCollapsed ? GONE : VISIBLE);
        mDeleteButton.setVisibility(TextUtils.isEmpty(mInputFiled.getText()) ? INVISIBLE : VISIBLE);

    }

    public Editable getInputText() {
        return mInputFiled != null ? mInputFiled.getText() : null;
    }

    public void setInputText(String text) {
        mInputFiled.setText(text);
    }

    public EditText getInputFiled() {
        return mInputFiled;
    }

    public void setTypeAdapter(SpinnerAdapter adapter) {
        mTypeSpinner.setAdapter(adapter);
    }

    public void setType(int type) {
        mType = type;
    }

    public void setIsCollapsed(boolean isCollapsed) {
        this.isCollapsed = isCollapsed;
    }

    public void setOnChildTextChangeListener(ContactEditorViewGroup.onChildTextChangeListener mOnChildTextChangeListener) {
        this.mOnChildTextChangeListener = mOnChildTextChangeListener;
    }

    public void setOnChildDeleteClickListener(ContactEditorViewGroup.onChildDeleteClickListener mOnChildDeleteClickListener) {
        this.mOnChildDeleteClickListener = mOnChildDeleteClickListener;
    }


}
