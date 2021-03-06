package learn.android.kangel.mycontacts.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import java.util.Date;

import learn.android.kangel.mycontacts.MyRecyclerView;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.activities.CallLogActivity;
import learn.android.kangel.mycontacts.activities.CallLogDetailActivity;
import learn.android.kangel.mycontacts.adapters.CallHistoryAdapter;
import learn.android.kangel.mycontacts.utils.CallogBean;
import learn.android.kangel.mycontacts.utils.TelDbHelper;

/**
 * Created by Kangel on 2016/3/17.
 */
public class CallHistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public final static int MODE_ALL = CallHistoryAdapter.MODE_ALL;
    public final static int MODE_PARTIAL = CallHistoryAdapter.MODE_PARTIAL;
    private static final int REQUEST_CALL_PHONE = 22;

    private SQLiteDatabase locationDB;
    private int mMode;
    private CallHistoryAdapter mAdapter;
    private final static int QUERY_CALL_HISTORY = 2;
    private final static String[] CALL_LOG_PROJECTION = new String[]
            {
                    CallLog.Calls._ID,
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.GEOCODED_LOCATION
            };
    private final static String SELECTION_PARTIAL = null;//CallLog.Calls.DATE + ">=" + System.currentTimeMillis() + " - 259200000";

    public static CallHistoryFragment newInstance(int mode) {
        CallHistoryFragment f = new CallHistoryFragment();
        f.mMode = mode;
        return f;
    }

    private final CallHistoryAdapter.onCallLogItemClickListener mListener = new CallHistoryAdapter.onCallLogItemClickListener() {
        @Override
        public void onItemClick(CallogBean bean) {
            Intent intent = new Intent(getActivity(), CallLogDetailActivity.class);
            Bundle args = new Bundle();
            args.putParcelable("data", bean);
            intent.putExtras(args);
            startActivity(intent);
        }

        @Override
        public void onCallButtonClick(String number) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(getContext(), R.string.permission_call_phone_request, Toast.LENGTH_LONG).show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
                }
                return;
            }
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
            startActivity(intent);
        }

        @Override
        public void onShowAllItemClick() {
            Intent intent = new Intent(getActivity(), CallLogActivity.class);
            startActivity(intent);
        }
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
        mAdapter = new CallHistoryAdapter(getActivity(), null, mMode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAdapter = new CallHistoryAdapter(getActivity(), null, mMode);
        } else {
            TelDbHelper dbHelper = new TelDbHelper(getActivity());
            locationDB = dbHelper.getReadableDatabase();
            mAdapter = new CallHistoryAdapter(getActivity(), null, mMode, locationDB);
        }
        mAdapter.setListener(mListener);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setEmptyView(v.findViewById(R.id.empty_view));
        // recyclerView.setLoadingView(v.findViewById(R.id.progress_view));
        getLoaderManager().initLoader(QUERY_CALL_HISTORY, null, this);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationDB != null && locationDB.isOpen()) {
            locationDB.close();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == QUERY_CALL_HISTORY) {
            if (mMode == MODE_PARTIAL) {
                return new CursorLoader(getActivity(), CallLog.Calls.CONTENT_URI, CALL_LOG_PROJECTION, SELECTION_PARTIAL, null, CallLog.Calls.DATE + " desc limit 20");
            } else {
                return new CursorLoader(getActivity(), CallLog.Calls.CONTENT_URI, CALL_LOG_PROJECTION, null, null, CallLog.Calls.DATE + " desc");

            }
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
