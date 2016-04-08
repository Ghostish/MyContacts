package learn.android.kangel.mycontacts.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import learn.android.kangel.mycontacts.ContactCommonEditorView;
import learn.android.kangel.mycontacts.ContactEditorViewGroup;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.EditFieldAdapter;

/**
 * Created by Kangel on 2016/3/30.
 */
public class EditContactActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        ContactEditorViewGroup v1 = (ContactEditorViewGroup) findViewById(R.id.phone);
        v1.addEditor();
        ContactEditorViewGroup v2 = (ContactEditorViewGroup) findViewById(R.id.email);
        v2.addEditor();
        ContactEditorViewGroup v3 = (ContactEditorViewGroup) findViewById(R.id.address);
        v3.addEditor();
        /*RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EditFieldAdapter adapter = new EditFieldAdapter(this);
        recyclerView.setAdapter(adapter);*/
    }
}
