package learn.android.kangel.mycontacts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**

 * Created by Kangel on 2016/4/8.
 */
public class ContactEditorViewGroup extends RelativeLayout {
    private List<EditText> mInputFields = new ArrayList<>();
    private int mType;
    private Stack<ContactCommonEditorView> recycleChildren = new Stack<>();  //keep the children that have been remove for further use
    private List<ContactCommonEditorView> currChildren = new LinkedList<>();

    private LinearLayout mContainer;
    private ImageView mTypeIcon;
    private List<ContactInfoBean> data;

    private String getMIMETYPE(int mType) {
        switch (mType) {
            case ContactCommonEditorView.TYPE_PHONE:
                return ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE;
            case ContactCommonEditorView.TYPE_EMAIL:
                return ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
            case ContactCommonEditorView.TYPE_ADDRESS:
                return ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE;
            default:
                return null;
        }

    }

    public interface onChildTextChangeListener {

        void onTextChanged(ContactCommonEditorView child, CharSequence s, int before, int count);

    }
    public interface onChildFocusChangeListener {
        void onFocusChanged(ContactCommonEditorView child, boolean hasFocus);

    }
    public interface onChildDeleteClickListener {
        void onChildDeleteClick(ContactCommonEditorView child);

    }
    private onChildDeleteClickListener mOnChildDeleteClickListener = new onChildDeleteClickListener() {
        @Override
        public void onChildDeleteClick(ContactCommonEditorView child) {
            ContactInfoBean bean = child.getBean();
            if (bean.getUpdateType() == ContactInfoBean.TYPE_UPDATE) {
                bean.setUpdateType(ContactInfoBean.TYPE_DELETE);
            } else {
                if (data != null) {
                    data.remove(bean);
                }
            }
            removeEditor(child);
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
            if (s.length() == 0) {
                int i = 0;
                for (; i < mContainer.getChildCount(); i++) {
                    if (child.equals(mContainer.getChildAt(i))) {
                        break;
                    }
                }
                if (i + 1 < mContainer.getChildCount() && TextUtils.isEmpty(((ContactCommonEditorView) mContainer.getChildAt(i + 1)).getInputText())) {
                    removeEditor((ContactCommonEditorView) mContainer.getChildAt(i + 1));
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
        LayoutInflater.from(context).inflate(R.layout.custom_contact_editor_view_group, this);

        mContainer = (LinearLayout) findViewById(R.id.editor_container);
        mTypeIcon = (ImageView) findViewById(R.id.type_icon);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ContactEditorViewGroup, 0, 0);
        mType = a.getInteger(R.styleable.ContactEditorViewGroup_dataType, ContactCommonEditorView.TYPE_PHONE);
        Drawable icon = a.getDrawable(R.styleable.ContactEditorViewGroup_typeIconSrc);
        a.recycle();
        mTypeIcon.setImageDrawable(icon);
    }

    private void addEditor(ContactCommonEditorView child) {
        currChildren.add(child);
        mContainer.addView(child);
        child.setOnChildDeleteClickListener(mOnChildDeleteClickListener);
        child.setOnChildTextChangeListener(mOnChildTextChangeListener);
    }

    public ContactCommonEditorView addEditor() {
        ContactCommonEditorView child;
        if (recycleChildren.isEmpty()) {
            child = new ContactCommonEditorView(getContext());
            child.setType(mType);
            child.setOnChildDeleteClickListener(mOnChildDeleteClickListener);
            child.setOnChildTextChangeListener(mOnChildTextChangeListener);
        } else {
            child = recycleChildren.pop();
        }
        child.setIsCollapsed(true);
        child.getInputFiled().setText("");
        ContactInfoBean bean = new ContactInfoBean(getMIMETYPE(mType));
        child.setBean(bean);
        if (data != null) {
            data.add(bean);
        }
        currChildren.add(child);
        mContainer.addView(child);
        return child;
    }

    public void removeEditor(final ContactCommonEditorView child) {
        TranslateAnimation animation = new TranslateAnimation(0.0f, child.getWidth(), 0.0f, 0.0f);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                currChildren.remove(child);
                mContainer.removeView(child);
                recycleChildren.add(child);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        child.startAnimation(animation);

    }

    public List<ContactInfoBean> getData() {
        return data;
    }

    public void setData(List<ContactInfoBean> data) {
        this.data = data;
        initData();
    }

    private void initData() {
        for (ContactInfoBean bean : data) {
            int update_type = bean.getUpdateType();
            switch (update_type) {
                case ContactInfoBean.TYPE_INSERT:
                case ContactInfoBean.TYPE_UPDATE:
                    ContactCommonEditorView child = new ContactCommonEditorView(getContext());
                    child.setBean(bean);
                    child.setType(mType);
                    child.setIsCollapsed(false);
                    addEditor(child);
                    break;
            }
        }
        addEditor();
    }

}
