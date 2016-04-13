package learn.android.kangel.mycontacts.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import learn.android.kangel.mycontacts.R;
import learn.android.kangel.mycontacts.activities.ContactDetailActivity;
import learn.android.kangel.mycontacts.adapters.SearchResultAdapter;

/**
 * Created by Kangel on 2016/3/24.
 */
public class SearchFragment extends android.support.v4.app.ListFragment implements LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener{
    private SearchResultAdapter mAdapter;
    private final static int SEARCH_QUERY = 23;
    private static final String[] PROJECTION =
            {
                    ContactsContract.Data._ID, //0
                    ContactsContract.Data.MIMETYPE, //1
                    ContactsContract.Data.DISPLAY_NAME_PRIMARY, //2
                    ContactsContract.CommonDataKinds.Phone.NUMBER, //3
                    ContactsContract.CommonDataKinds.Phone.TYPE, //4
                    ContactsContract.CommonDataKinds.Phone.LABEL, //5
                    ContactsContract.Data.CONTACT_ID, //6
                    ContactsContract.Data.LOOKUP_KEY //7
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
        InputMethodManager imr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        queryEdit.requestFocus();
        imr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
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
               /* if (s.length() != 0) {
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
                }*/
                mSearchString = s.toString();
                getLoaderManager().restartLoader(SEARCH_QUERY, null, SearchFragment.this);
            }
        });
        return v;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SEARCH_QUERY) {
            Uri mUri = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, Uri.encode(mSearchString));
            return new CursorLoader(getActivity(), mUri, PROJECTION, null, null, ContactsContract.Data.SORT_KEY_PRIMARY);
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        Cursor c = mAdapter.getCursor();
        if (c != null) {
            c.moveToPosition(position);
            String lookUpKey = c.getString(7);
            int contactId = c.getInt(6);
            Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
            intent.putExtra("lookUpKey", lookUpKey);
            intent.putExtra("contactId", contactId);
            getActivity().startActivity(intent);
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_text_button:
                queryEdit.setText("");
                break;
            case R.id.exit_button: {
                getFragmentManager().popBackStack();
                break;
            }
        }
    }
}
