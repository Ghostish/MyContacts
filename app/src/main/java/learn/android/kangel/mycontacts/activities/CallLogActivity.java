package learn.android.kangel.mycontacts.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.fragments.CallHistoryFragment;

/**
 * Created by Kangel on 2016/4/19.
 */
public class CallLogActivity extends AppCompatActivity {
    private CallHistoryFragment mFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            mFragment = CallHistoryFragment.newInstance(CallHistoryFragment.MODE_ALL);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.container, mFragment);
            ft.commit();
        }
    }
}
