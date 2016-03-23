package learn.android.kangel.mycontacts.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import learn.android.kangel.mycontacts.MyRecyclerView;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.ContactListAdapter;

/**
 * Created by Kangel on 2016/3/19.
 */
public class ContactListFragment extends RecyclerViewFragemt{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    public static ContactListFragment newInstance(RecyclerView.Adapter adapter) {
        ContactListFragment f = new ContactListFragment();
        f.adapter = adapter;
        return f;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        if (adapter != null) {
            ((ContactListAdapter) adapter).updateCursor(null);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recyclerview_layout, container, false);
        recyclerView = (MyRecyclerView) v.findViewById(R.id.fast_scroll_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        return v;
    }
}
