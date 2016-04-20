package learn.android.kangel.mycontacts.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.CallLogDetailAdapter;
import learn.android.kangel.mycontacts.utils.CallogBean;
import learn.android.kangel.mycontacts.utils.HeadShowLoader;

/**
 * Created by Kangel on 2016/4/19.
 */
public class CallLogDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CALL_PHONE = 22;
    private HeadShowLoader mHeadShowLoader = new HeadShowLoader();
    private CallogBean mBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        mBean = args.getParcelable("data");
        setContentView(R.layout.activity_call_log_detail);
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
            }
        }
    }
}
