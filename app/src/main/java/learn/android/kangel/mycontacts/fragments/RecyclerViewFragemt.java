package learn.android.kangel.mycontacts.fragments;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import learn.android.kangel.mycontacts.MyRecyclerView;

/**
 * Created by Kangel on 2016/3/19.
 */
public abstract class RecyclerViewFragemt extends Fragment {
    protected MyRecyclerView recyclerView;
    protected RecyclerView.Adapter adapter;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

}
