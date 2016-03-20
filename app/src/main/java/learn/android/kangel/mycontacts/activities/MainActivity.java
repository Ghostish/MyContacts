package learn.android.kangel.mycontacts.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.CallHistoryAdapter;
import learn.android.kangel.mycontacts.fragments.CallHistoryFragment;
import learn.android.kangel.mycontacts.fragments.ContactListFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>, RecyclerViewActivity {
    private SearchView searchView;
    private Cursor callLogCursor;
    private Cursor contactCursor;
    private final static int REQUEST_CALL_LOG_CONTACTS = 110;
    private CallHistoryFragment callHistoryFragment;
    private ContactListFragment contactListFragment;
    private final static int QUERY_CONTACT = 1;
    private final static int QUERY_CALL_HISTROY = 2;

    private final static String[] REQUEST_PERMISSION = new String[]
            {
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS
            };
    private final static String[] CALL_LOG_PROJECTION = new String[]
            {
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DATE
            };
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY

            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CALL_LOG) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                Snackbar.make(findViewById(R.id.coordinator), R.string.permission_deny, Snackbar.LENGTH_INDEFINITE).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, REQUEST_PERMISSION, REQUEST_CALL_LOG_CONTACTS);
            }
        } else {
            callLogCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, CALL_LOG_PROJECTION, null, null, CallLog.Calls.DATE + " desc");
            getSupportLoaderManager().initLoader(QUERY_CONTACT, null, MainActivity.this);
        }

    }

    @Override
    protected void onDestroy() {
        if (callLogCursor != null && !callLogCursor.isClosed()) {
            callLogCursor.close();
        }
        if (contactCursor != null && !contactCursor.isClosed()) {
            contactCursor.close();
        }
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_LOG_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getSupportLoaderManager().initLoader(QUERY_CALL_HISTROY, null, MainActivity.this);
                    getSupportLoaderManager().initLoader(QUERY_CONTACT, null, MainActivity.this);

                } else {
                    Snackbar.make(findViewById(R.id.coordinator), R.string.permission_deny, Snackbar.LENGTH_INDEFINITE).show();
                }

            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_view_card:
        }
    }


    private void initView() {

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_history_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_account_box_white_24dp));
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new contactPagerAdapter(getSupportFragmentManager()));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        searchView = (SearchView) findViewById(R.id.search_view);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case QUERY_CONTACT:
                return new CursorLoader(MainActivity.this, ContactsContract.Contacts.CONTENT_URI, PROJECTION, null, null, ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
            case QUERY_CALL_HISTROY:
                return new CursorLoader(MainActivity.this, CallLog.Calls.CONTENT_URI, CALL_LOG_PROJECTION, null, null, CallLog.Calls.DATE + " desc");
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case QUERY_CONTACT: {
                contactCursor = data;
                if (contactListFragment != null) {
                    contactListFragment.updateRecyclerView(data);
                }
                break;
            }
            case QUERY_CALL_HISTROY: {
                callLogCursor = data;
                if (callHistoryFragment != null) {
                    callHistoryFragment.updateRecyclerView(data);
                }
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    @Override
    public void onRecyclerViewItemClick(int position, Object tag, Bundle data) {
        switch (((String) tag)) {
            case CallHistoryAdapter.TAG_DIAL: {
                position = callHistoryFragment.getSectionedRecyclerViewAdapter().sectionedPositionToPosition(position);
                callLogCursor.moveToPosition(position);
                String number = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                startActivity(intent);
            }
        }
    }


    class contactPagerAdapter extends FragmentPagerAdapter {

        public contactPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return callHistoryFragment = CallHistoryFragment.newInstance(callLogCursor);
            } else {
                return contactListFragment = ContactListFragment.newInstance(contactCursor);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
