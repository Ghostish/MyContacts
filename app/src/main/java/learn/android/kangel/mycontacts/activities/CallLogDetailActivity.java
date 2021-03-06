package learn.android.kangel.mycontacts.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.CallLogDetailAdapter;
import learn.android.kangel.mycontacts.utils.BlackListUtil;
import learn.android.kangel.mycontacts.utils.CallogBean;
import learn.android.kangel.mycontacts.utils.HeadShowLoader;

/**
 * Created by Kangel on 2016/4/19.
 */
public class CallLogDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CALL_PHONE = 22;
    private static final int REQUEST_CALL_LOG = 23;
    private HeadShowLoader mHeadShowLoader = new HeadShowLoader();
    private CallogBean mBean;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.block_number: {
                boolean result = BlackListUtil.addToNumberBlackList(this, null, new String[]{mBean.getNumber()});
                if (result) {
                    Toast.makeText(getApplicationContext(), R.string.block_number_successful, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.block_number_fail, Toast.LENGTH_LONG).show();
                }
                return true;
            }
            case R.id.delete_call_log: {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALL_LOG)) {
                        Toast.makeText(getApplicationContext(), R.string.permission_call_log_request, Toast.LENGTH_LONG).show();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_CALL_LOG}, REQUEST_CALL_LOG);
                    }
                    return true;
                }
                try {
                    Log.d("idlist", mBean.getCallIdsString());
                    int deletedCount = getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + " in " + mBean.getCallIdsString(), null);
                    if (deletedCount > 0) {
                        Toast.makeText(getApplicationContext(), R.string.toast_call_log_deleted, Toast.LENGTH_LONG).show();
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_call_log_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        mBean = args.getParcelable("data");
        setContentView(R.layout.activity_call_log_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String number = mBean.getNumber();
        String location = mBean.getLocation();
        String name = mBean.getContactName();
        TextView numberText = (TextView) findViewById(R.id.number);
        TextView nameText = (TextView) findViewById(R.id.name);
        TextView locationText = (TextView) findViewById(R.id.location);
        numberText.setText(number);
        nameText.setText(name);
        locationText.setText(location);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CallLogDetailAdapter adapter = new CallLogDetailAdapter(this, mBean);
        recyclerView.setAdapter(adapter);
        mHeadShowLoader.bindImageView((ImageView) findViewById(R.id.head_show), this, mBean.getNumber());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call_button: {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                        Toast.makeText(getApplicationContext(), R.string.permission_call_phone_request, Toast.LENGTH_LONG).show();
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
                    }
                    return;
                }
                String number = mBean.getNumber();
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
                startActivity(intent);
                break;
            }
            case R.id.head_show: {
                String[] projection = new String[]{ContactsContract.Data.CONTACT_ID, ContactsContract.Data.LOOKUP_KEY};
                Uri mUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(mBean.getNumber()));
                Cursor c = getContentResolver().query(mUri, projection, null, null, null);
                if (c != null && c.moveToNext()) {
                    long contactId = c.getLong(0);
                    String lookUpKey = c.getString(1);
                    Intent intent = new Intent(this, ContactDetailActivity.class);
                    intent.putExtra("lookUpKey", lookUpKey);
                    intent.putExtra("contactId", contactId);
                    startActivity(intent);
                    finish();
                    break;
                }
                c.close();
            }
        }
    }
}
