package learn.android.kangel.mycontacts.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.CallHistoryAdapter;
import learn.android.kangel.mycontacts.adapters.ContactListAdapter;
import learn.android.kangel.mycontacts.fragments.CallHistoryFragment;
import learn.android.kangel.mycontacts.fragments.ContactListFragment;
import learn.android.kangel.mycontacts.fragments.SearchFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerViewActivity {
    private final static int REQUEST_CALL_LOG_CONTACTS = 110;
    private CallHistoryFragment callHistoryFragment;
    private ContactListFragment contactListFragment;
    private SearchFragment mSearchFragment;
    private FloatingActionButton fab;
    int appbarHeight = 0;


    private final static String[] REQUEST_PERMISSION = new String[]
            {
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
            };
    private ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CALL_LOG) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)) {
                Snackbar.make(findViewById(R.id.coordinator), R.string.permission_deny, Snackbar.LENGTH_INDEFINITE).show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, REQUEST_PERMISSION, REQUEST_CALL_LOG_CONTACTS);
            }
        } else {
            pager.setAdapter(new contactPagerAdapter(getSupportFragmentManager()));
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_LOG_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 3 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    pager.setAdapter(new contactPagerAdapter(getSupportFragmentManager()));
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
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                if (mSearchFragment == null) {
                    mSearchFragment = new SearchFragment();
                }
                if (!mSearchFragment.isVisible()) {
                    ft.add(R.id.coordinator, mSearchFragment, "search");
                    ft.addToBackStack(null);
                    ft.commit();
                    if (fab.isShown()) {
                        fab.hide();
                    }
                }
                break;
            case R.id.fab:
                Intent intent = new Intent(MainActivity.this, EditContactActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && appbarHeight == 0) {
            /*TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
            appbarHeight += tabLayout.getHeight();
            CardView cardView = (CardView) findViewById(R.id.search_view_card);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
            appbarHeight += cardView.getHeight() + lp.topMargin + lp.bottomMargin;
            final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);

            ValueAnimator animator = ValueAnimator.ofInt(((int) getResources().getDimension(R.dimen.app_bar_layout_initial)), appbarHeight);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
                    params.height = (int) animation.getAnimatedValue();
                    appBarLayout.setLayoutParams(params);
                }
            });
            animator.start();*/
            ViewCompat.animate(fab).scaleY(1.0f).scaleX(1.0f).start();
        }
    }

    private void initView(Bundle savedInstanceState) {

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_history_white_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_account_box_white_24dp));
        tabLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
        pager = (ViewPager) findViewById(R.id.pager);
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (mSearchFragment != null && mSearchFragment.isVisible()) {
                    fab.hide();
                }else {
                    fab.show();
                    InputMethodManager imm = (InputMethodManager)MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
            }
        });

    }


    @Override
    public void onRecyclerViewItemClick(int position, Object tag, Bundle data) {
        switch (((String) tag)) {
            case CallHistoryAdapter.TAG_DIAL: {
                String number = data.getString("number");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
                startActivity(intent);
                break;
            }
            case ContactListAdapter.TAG: {
                String lookUpkey = data.getString("lookUpKey");
                int contactId = data.getInt("contactId");
                Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                intent.putExtra("lookUpKey", lookUpkey);
                intent.putExtra("contactId", contactId);
                startActivity(intent);
                break;
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
                return callHistoryFragment = new CallHistoryFragment();
            } else {
                return contactListFragment = new ContactListFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
