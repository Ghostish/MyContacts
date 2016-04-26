package learn.android.kangel.mycontacts.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.cert.TrustAnchor;

import learn.android.kangel.mycontacts.MyRecyclerView;
import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.CallHistoryAdapter;
import learn.android.kangel.mycontacts.adapters.ContactListAdapter;

/**
 * Created by Kangel on 2016/3/19.
 */
public class ContactListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final static int QUERY_CONTACT = 1;
    public final static int MODE_ALL = 110;
    public final static int MODE_STARRED = 111;
    private static final String[] CONTACT_PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                    ContactsContract.Contacts.SORT_KEY_PRIMARY
            };
    private static final String SELECTION = ContactsContract.Contacts.HAS_PHONE_NUMBER + " <> 0";
    private static final String SELECTION_STARRED = ContactsContract.Contacts.STARRED + " <> 0";

    private int mMode;
    private ContactListAdapter mAdapter;

    public static ContactListFragment newInstance(int mode) {
        ContactListFragment f = new ContactListFragment();
        f.mMode = mode;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recyclerview_layout, container, false);
        MyRecyclerView recyclerView = (MyRecyclerView) v.findViewById(R.id.fast_scroll_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        mAdapter = new ContactListAdapter(getActivity(), null);
        recyclerView.setAdapter(mAdapter);
        ImageView emptyImage = (ImageView) v.findViewById(R.id.empty_image);
        TextView emptyDesc = (TextView) v.findViewById(R.id.empty_desc);
        switch (mMode) {
            case MODE_ALL:
                emptyDesc.setText(R.string.no_contact);
                break;
            case MODE_STARRED:
                emptyDesc.setText(R.string.no_starred_contact);
                break;
        }
        emptyImage.setImageResource(R.drawable.ic_perm_contact_calendar_black_48dp);
        recyclerView.setEmptyView(v.findViewById(R.id.empty_view));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mAdapter.isIdle = newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING;
                mAdapter.notifyDataSetChanged();
            }
        });
        getLoaderManager().initLoader(QUERY_CONTACT, null, this);
        return v;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == QUERY_CONTACT) {
            if (mMode == MODE_ALL) {
                return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, CONTACT_PROJECTION, SELECTION, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);

            } else {
                return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, CONTACT_PROJECTION, SELECTION_STARRED, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == QUERY_CONTACT) {
            mAdapter.updateCursor(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.updateCursor(null);
        mAdapter.notifyDataSetChanged();
    }
}
