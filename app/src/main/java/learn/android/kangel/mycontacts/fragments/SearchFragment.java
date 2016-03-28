package learn.android.kangel.mycontacts.fragments;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import learn.android.kangel.mycontacts.R;

/**
 * Created by Kangel on 2016/3/24.
 */
public class SearchFragment extends android.support.v4.app.ListFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        return v;
    }


}
