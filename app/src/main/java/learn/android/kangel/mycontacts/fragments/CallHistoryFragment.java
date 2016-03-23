package learn.android.kangel.mycontacts.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import learn.android.kangel.mycontacts.DateParseUtil;
import learn.android.kangel.mycontacts.adapters.CallHistoryAdapter;
import learn.android.kangel.mycontacts.MyRecyclerView;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.SimpleSectionedRecyclerViewAdapter;

/**
 * Created by Kangel on 2016/3/17.
 */
public class CallHistoryFragment extends RecyclerViewFragemt {


    public static CallHistoryFragment newInstance(RecyclerView.Adapter adapter) {
        CallHistoryFragment f = new CallHistoryFragment();
        f.adapter = adapter;
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recyclerview_layout, container, false);
        recyclerView = (MyRecyclerView) v.findViewById(R.id.fast_scroll_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ImageView emptyImage = (ImageView) v.findViewById(R.id.empty_image);
        TextView emptyDesc = (TextView) v.findViewById(R.id.empty_desc);
        String descFormat = getActivity().getString(R.string.empty_desc);
        String callHistory = getActivity().getString(R.string.call_history);
        emptyDesc.setText(String.format(descFormat, callHistory));
        emptyImage.setImageResource(R.drawable.ic_history_black_48dp);
       /* sectionedRecyclerViewAdapter = new SimpleSectionedRecyclerViewAdapter(getActivity(), R.layout.item_call_history_header, R.id.header_text, adapter);
        SimpleSectionedRecyclerViewAdapter.Section[] sections = new SimpleSectionedRecyclerViewAdapter.Section[3];
        sections[0] = new SimpleSectionedRecyclerViewAdapter.Section(getFirstTodayPosition(), "最近");
        sections[1] = new SimpleSectionedRecyclerViewAdapter.Section(getFirstYesterdayPosition(), "昨天");
        sections[2] = new SimpleSectionedRecyclerViewAdapter.Section(getFirstOlderPosition(), "更早");
        sectionedRecyclerViewAdapter.setSections(sections);
        recyclerView.setAdapter(sectionedRecyclerViewAdapter);*/
        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(v.findViewById(R.id.empty_view));
        return v;
    }

}
