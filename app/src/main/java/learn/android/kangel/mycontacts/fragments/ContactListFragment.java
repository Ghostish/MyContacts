package learn.android.kangel.mycontacts.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import learn.android.kangel.mycontacts.MyRecyclerView;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.CallHistoryAdapter;
import learn.android.kangel.mycontacts.adapters.ContactListAdapter;

/**
 * Created by Kangel on 2016/3/19.
 */
public class ContactListFragment extends RecyclerViewFragemt{
    public static ContactListFragment newInstance(Cursor cursor) {
        ContactListFragment f = new ContactListFragment();
        f.cursor = cursor;
        return f;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recyclerview_layout, container, false);
        recyclerView = (MyRecyclerView) v.findViewById(R.id.fast_scroll_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        adapter = new ContactListAdapter(getActivity(), cursor);
        recyclerView.setAdapter(adapter);
        return v;
    }
    @Override
    public void updateRecyclerView(Cursor cursor) {
        this.cursor = cursor;
        ((ContactListAdapter) adapter).updateCursor(cursor);
        adapter.notifyDataSetChanged();
    }
}
