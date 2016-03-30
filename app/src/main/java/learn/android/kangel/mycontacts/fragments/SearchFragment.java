package learn.android.kangel.mycontacts.fragments;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.adapters.SearchResultAdapter;

/**
 * Created by Kangel on 2016/3/24.
 */
public class SearchFragment extends android.support.v4.app.ListFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener, View.OnClickListener {
    private SearchResultAdapter mAdapter;
    private final static int SEARCH_QUERY = 23;
    private static final String[] PROJECTION =
            {
                    ContactsContract.Data._ID,
                    ContactsContract.Data.LOOKUP_KEY,
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.Data.DISPLAY_NAME_PRIMARY,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.TYPE,
                    ContactsContract.CommonDataKinds.Phone.LABEL,
                    ContactsContract.Data.CONTACT_ID,
            };
    private static final String SELECTION = ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' AND ("
            + ContactsContract.CommonDataKinds.Phone.NUMBER + " like ? OR "
            + ContactsContract.Data.DISPLAY_NAME_PRIMARY + " like ?) ";
    private String[] mSearchArgs = new String[]{"", ""};
    private String mSearchString;
    private EditText queryEdit;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView listView = getListView();
        listView.setDivider(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        queryEdit = (EditText) v.findViewById(R.id.edit_query);
        mAdapter = new SearchResultAdapter(getActivity(), null);
        setListAdapter(mAdapter);
        v.findViewById(R.id.exit_button).setOnClickListener(this);
        v.findViewById(R.id.clear_text_button).setOnClickListener(this);
        queryEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    mSearchString = s.toString();
                    mSearchArgs[0] = "%" + mSearchString + "%";
                    mSearchArgs[1] = "%" + mSearchString + "%";
                    getLoaderManager().restartLoader(SEARCH_QUERY, null, SearchFragment.this);
                } else {
                    mSearchString = "";
                    mSearchArgs[0] = "";
                    mSearchArgs[1] = "";
                    mAdapter.setCursor(null);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        return v;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SEARCH_QUERY) {
            return new CursorLoader(getActivity(), ContactsContract.Data.CONTENT_URI, PROJECTION, SELECTION, mSearchArgs, ContactsContract.Data.SORT_KEY_PRIMARY);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.setCursor(data);
        Log.d("count", data.getCount() + "");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.setCursor(null);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_text_button:
                queryEdit.setText("");
                break;
            case R.id.exit_button:{
                getFragmentManager().popBackStack();
                break;
            }
        }
    }
}
