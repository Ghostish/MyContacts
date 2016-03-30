package learn.android.kangel.mycontacts.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import learn.android.kangel.mycontacts.MyRecyclerView;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.EditFieldAdapter;

/**
 * Created by Kangel on 2016/3/30.
 */
public class EditContactActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_layout);
        MyRecyclerView recyclerView = (MyRecyclerView) findViewById(R.id.fast_scroll_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EditFieldAdapter adapter = new EditFieldAdapter(this);
        recyclerView.setAdapter(adapter);
    }
}
