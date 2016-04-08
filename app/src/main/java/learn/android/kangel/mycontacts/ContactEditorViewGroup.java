package learn.android.kangel.mycontacts;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Kangel on 2016/4/8.
 */
public class ContactEditorViewGroup extends LinearLayout {
    private List<EditText> mInputFields = new ArrayList<>();
    private int mType;
    private Stack<ContactCommonEditorView> recycleChildren = new Stack<>();
    private List<ContactCommonEditorView> currChildren = new LinkedList<>();

    public interface onChildTextChangeListener {
        void onTextChanged(ContactCommonEditorView child, CharSequence s, int before, int count);
    }

    private interface onChildFocusChangeListener {
        void onFocusChanged(ContactCommonEditorView child, boolean hasFocus);
    }
    private interface onChildDeleteClickListener{
        void onChildDeleteClick(ContactCommonEditorView child);
    }

    private onChildDeleteClickListener mOnChildDeleteClickListener = new onChildDeleteClickListener() {
        @Override
        public void onChildDeleteClick(ContactCommonEditorView child) {
            if (currChildren.size()>2) {
                removeEditor(child);
            }else {
                currChildren.get(0).getInputFiled().setText("");
                currChildren.get(0).setShouldShowIcon(true);
            }
        }
    };
    private onChildTextChangeListener mOnChildTextChangeListener = new onChildTextChangeListener() {
        @Override
        public void onTextChanged(ContactCommonEditorView child, CharSequence s, int before, int count) {
            if (before == 0 && count != 0) {
                /**
                 * check if the child is the last one,add a new child if so
                 */
                if (currChildren.get(currChildren.size() - 1).equals(child)) {
                    addEditor();
                }
            }
            if (s.length()==0) {
                int i = 0;
                for (; i < getChildCount(); i++) {
                    if (child.equals(getChildAt(i))) {
                        break;
                    }
                }
                if (i + 1 < getChildCount() && TextUtils.isEmpty(((ContactCommonEditorView) getChildAt(i + 1)).getInputText())) {
                    removeEditor((ContactCommonEditorView) getChildAt(i + 1));
                }
            }
        }
    };

    public ContactEditorViewGroup(Context context) {
        this(context, null);
    }

    public ContactEditorViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContactEditorViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ContactEditorViewGroup, 0, 0);
        mType = a.getInteger(R.styleable.ContactEditorViewGroup_dataType, ContactCommonEditorView.TYPE_PHONE);
        a.recycle();
    }

    public void addEditor() {
        ContactCommonEditorView child = recycleChildren.isEmpty() ? new ContactCommonEditorView(getContext()) : recycleChildren.pop();
        child.setType(mType);
        child.getInputFiled().setText("");
        if (getChildCount() == 0) {
            child.setShouldShowIcon(true);
        }else {
            child.setShouldShowIcon(false);
        }
        currChildren.add(child);
        addView(child);
    }

    public void removeEditor(ContactCommonEditorView child) {
        currChildren.remove(child);
        removeView(child);
        recycleChildren.add(child);
    }

    public void onChildTextChange(ContactCommonEditorView child, CharSequence s, int before, int count) {
        mOnChildTextChangeListener.onTextChanged(child, s, before, count);
    }

    public void onChildDeleteClick(ContactCommonEditorView child) {
        mOnChildDeleteClickListener.onChildDeleteClick(child);
    }
}
