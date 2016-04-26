package learn.android.kangel.mycontacts.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;

import learn.android.kangel.mycontacts.MyRecyclerView;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.BlockNumberAdapter;
import learn.android.kangel.mycontacts.fragments.ConfirmDialogFragment;
import learn.android.kangel.mycontacts.fragments.EditTextDialogFragment;
import learn.android.kangel.mycontacts.utils.BlackListUtil;
import learn.android.kangel.mycontacts.utils.MyDatabaseHelper;

/**
 * Created by Kangel on 2016/4/26.
 */
public class ShowBlockedNumbersActivity extends AppCompatActivity implements EditTextDialogFragment.onEditDialogButtonClickListener, ConfirmDialogFragment.onConfirmDialogButtonClickListener, View.OnClickListener {
    private Cursor mCursor;
    private SQLiteDatabase db;
    private BlockNumberAdapter mAdapter;
    private ConfirmDialogFragment mConfirmDialog;
    private EditTextDialogFragment mEditDialog;

    private int currPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_blocked_numbers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this);
        db = myDatabaseHelper.getWritableDatabase();

        MyRecyclerView recyclerView = (MyRecyclerView) findViewById(R.id.fast_scroll_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mCursor = BlackListUtil.getBlockedNumbers(db);
        mAdapter = new BlockNumberAdapter(this, mCursor);
        mAdapter.setItemActionListener(new BlockNumberAdapter.onItemActionListener() {
            @Override
            public void onItemClick(int position) {
                currPosition = position;
                mCursor.moveToPosition(position);
                String number = mCursor.getString(mCursor.getColumnIndex("number"));
                showEditDialog(number);
            }

            @Override
            public void onItemDeleteClick(int position) {
                currPosition = position;
                showConfirmDialog();
            }
        });
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    private void showConfirmDialog() {
        if (mConfirmDialog == null) {
            mConfirmDialog = ConfirmDialogFragment.newInstance(R.string.title_remove_block, R.string.msg_remove_block, R.style.mAlertDialogStyle);
        }
        mConfirmDialog.show(getSupportFragmentManager(), "confirm");
    }

    private void showEditDialog(String number) {
        if (mEditDialog == null) {
            mEditDialog = EditTextDialogFragment.newInstance(R.string.title_edit_block_number, R.style.mAlertDialogStyle);
        }
        mEditDialog.setDisplayTextText(number);
        mEditDialog.setInputType(InputType.TYPE_CLASS_NUMBER);
        mEditDialog.show(getSupportFragmentManager(), "edit");

    }

    @Override
    public void onNegativeButtonClick() {

    }

    @Override
    public void onPositiveButtonClick() {
        mCursor.moveToPosition(currPosition);
        String number = mCursor.getString(mCursor.getColumnIndex("number"));
        boolean result = BlackListUtil.removeNumberFromBlackList(db, number);
        if (result) {
            mCursor.close();
            mCursor = BlackListUtil.getBlockedNumbers(db);
            mAdapter.setCursor(mCursor);
        }
    }

    @Override
    public void onPositiveButtonClick(String s) {
        if (currPosition == -1) {
            boolean result = BlackListUtil.addToNumberBlackList(db, s);
            if (result) {
                mCursor.close();
                mCursor = BlackListUtil.getBlockedNumbers(db);
                mAdapter.setCursor(mCursor);
            }
        } else if (mCursor.moveToPosition(currPosition)) {
            mCursor.moveToPosition(currPosition);
            int rowid = mCursor.getInt(mCursor.getColumnIndex("rowid"));
            boolean result = BlackListUtil.updateNumberBlackList(db, s, rowid);
            if (result) {
                mCursor.close();
                mCursor = BlackListUtil.getBlockedNumbers(db);
                mAdapter.setCursor(mCursor);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
                /*set current position to -1 to indicate that is must be inserted*/
                currPosition = -1;
                showEditDialog(null);
            }
        }
    }
}
