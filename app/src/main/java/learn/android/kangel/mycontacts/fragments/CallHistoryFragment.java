package learn.android.kangel.mycontacts.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import learn.android.kangel.mycontacts.MyRecyclerView;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.CallHistoryAdapter;

/**
 * Created by Kangel on 2016/3/17.
 */
public class CallHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private CallHistoryAdapter mAdapter;
    private final static int QUERY_CALL_HISTORY = 2;
    private final static String[] CALL_LOG_PROJECTION = new String[]
            {
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DATE,
                    CallLog.Calls.GEOCODED_LOCATION
            };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recyclerview_layout, container, false);
        MyRecyclerView recyclerView = (MyRecyclerView) v.findViewById(R.id.fast_scroll_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
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
        mAdapter = new CallHistoryAdapter(getActivity(), null);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setEmptyView(v.findViewById(R.id.empty_view));
        recyclerView.setLoadingView(v.findViewById(R.id.progress_view));
        getLoaderManager().initLoader(QUERY_CALL_HISTORY, null, this);
        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == QUERY_CALL_HISTORY) {
            return new CursorLoader(getActivity(), CallLog.Calls.CONTENT_URI, CALL_LOG_PROJECTION, null, null, CallLog.Calls.DATE + " desc");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == QUERY_CALL_HISTORY) {
            mAdapter.updateCursor(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == QUERY_CALL_HISTORY) {
            mAdapter.updateCursor(null);
            mAdapter.notifyDataSetChanged();
        }
    }
}
