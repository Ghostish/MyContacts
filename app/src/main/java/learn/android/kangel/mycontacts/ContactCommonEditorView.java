package learn.android.kangel.mycontacts;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kangel on 2016/4/6.
 */
public class ContactCommonEditorView extends RelativeLayout {
    private ImageView mTypeIcon;
    private ImageButton mDeleteButton;
    private EditText mInputFiled;
    private Spinner mTypeSpinner;

    private int mType;
    private boolean isCollapsed;
    private boolean shouldShowIcon;
    private List<String> typeStrings;

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
        shouldShowIcon = a.getBoolean(R.styleable.ContactCommonEditorView_shouldShowIcon, false);
        a.recycle();

        mTypeIcon = (ImageView) findViewById(R.id.type_icon);
        mDeleteButton = (ImageButton) findViewById(R.id.delete_button);
        mInputFiled = (EditText) findViewById(R.id.input_field);
        mTypeSpinner = (Spinner) findViewById(R.id.type_spinner);

        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == typeStrings.size() - 1) {
                    // TODO: 2016/4/7 show a dialog to let user input a custom type
                    typeStrings.add(0, "custom");
                    mTypeSpinner.setSelection(0);
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
                    ValueAnimator animator = ValueAnimator.ofInt(0, mTypeSpinner.getMeasuredHeight());
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int currHeight = (int) animation.getAnimatedValue();
                            ViewGroup.LayoutParams lp = mTypeSpinner.getLayoutParams();
                            lp.height = currHeight;
                            mTypeSpinner.setLayoutParams(lp);
                        }
                    });
                    animator.setDuration(300).start();
                }
            }
        });
        mInputFiled.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
            case 0:
                typeStrings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.phone_type)));
                break;
            case 1:
                typeStrings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.email_type)));
                break;
            case 2:
                typeStrings = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.address_type)));
                break;
        }
        SpinnerAdapter adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, typeStrings);
        mTypeSpinner.setAdapter(adapter);
        TextView t = (TextView) mTypeSpinner.findViewById(android.R.id.text1);
        mTypeSpinner.setVisibility(isCollapsed ? GONE : VISIBLE);
        mDeleteButton.setVisibility(TextUtils.isEmpty(mInputFiled.getText()) ? INVISIBLE : VISIBLE);
        if (shouldShowIcon) {
            setIcon(mType);
            mTypeIcon.setVisibility(VISIBLE);
        } else {
            mTypeIcon.setVisibility(INVISIBLE);
        }
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


    public void setShouldShowIcon(boolean shouldShowIcon) {
        this.shouldShowIcon = shouldShowIcon;
        //mTypeIcon.setVisibility(shouldShowIcon ? VISIBLE : INVISIBLE);
    }

    public void setType(int type) {
        mType = type;
    }

    private void setIcon(int type) {
        switch (type) {
            case 0:
                mTypeIcon.setImageResource(R.drawable.ic_call_black_24dp);
                break;
            case 1:
                mTypeIcon.setImageResource(R.drawable.ic_email_black_24dp);
                break;
            case 2:
                mTypeIcon.setImageResource(R.drawable.ic_location_on_black_24dp);
                break;
        }
    }
}
