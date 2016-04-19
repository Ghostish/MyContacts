package learn.android.kangel.mycontacts.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.CallLogDetailAdapter;
import learn.android.kangel.mycontacts.utils.CallogBean;
import learn.android.kangel.mycontacts.utils.HeadShowLoader;

/**
 * Created by Kangel on 2016/4/19.
 */
public class CallLogDetailActivity extends AppCompatActivity {
    private HeadShowLoader mHeadShowLoader = new HeadShowLoader();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        CallogBean bean = args.getParcelable("data");
        setContentView(R.layout.activity_call_log_detail);
        String number = bean.getNumber();
        String location = bean.getLocation();
        String name = bean.getContactName();
        TextView numberText = (TextView) findViewById(R.id.number);
        TextView nameText = (TextView) findViewById(R.id.name);
        TextView locationText = (TextView) findViewById(R.id.location);
        numberText.setText(number);
        nameText.setText(name);
        locationText.setText(location);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CallLogDetailAdapter adapter = new CallLogDetailAdapter(this, bean);
        recyclerView.setAdapter(adapter);
        mHeadShowLoader.bindImageView((ImageView) findViewById(R.id.head_show), this, bean.getNumber());
    }
}
