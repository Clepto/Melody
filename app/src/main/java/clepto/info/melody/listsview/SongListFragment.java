package clepto.info.melody.listsview;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import clepto.info.melody.MainActivity;
import clepto.info.melody.MediaPlaybackService;
import clepto.info.melody.R;

import clepto.info.melody.listsview.content.SongContent;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link}
 * interface.
 */
public class SongListFragment extends Fragment implements AbsListView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private CursorAdapter mAdapter;
    private Context context;
    private View emptyView;

    private Cursor tempCursor;

    // TODO: Rename and change types of parameters
    public static SongListFragment newInstance(String param1, String param2) {
        SongListFragment fragment = new SongListFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SongListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);

        // Set fast scrolling
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        mListView.setFastScrollEnabled(true);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        registerForContextMenu(mListView);

        MainActivity.loaderManager.initLoader(MainActivity.SONG_LOADER, null, this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            //TODO check if I need this
//            mListener.onFragmentInteraction(((TextView) view.findViewById(R.id.list_item_song_title)).getText().toString());
//            mListener.onFragmentInteraction(parent.getItemAtPosition(position).toString());
            Cursor c = (Cursor) parent.getItemAtPosition(position);
            String _id = c.getString(c.getColumnIndex(SongContent.TRACK_ID));
            Log.d("melody", _id);
            mListener.onSongListFragmentInteraction(c);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        MenuInflater inflater = getActivity().getMenuInflater();
        tempCursor = (Cursor) mAdapter.getItem(info.position);
        inflater.inflate(R.menu.context_menu_song_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.context_menu_song_list_add_to_queue:
                MainActivity.mService.sendMessageToService(MediaPlaybackService.ADD_SONG_TO_QUEUE, tempCursor);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        mListView.getEmptyView().setVisibility(ListView.INVISIBLE);
        mListView.setVisibility(ListView.VISIBLE);
        Log.d("melody", "Song loader created");
        return SongContent.getSongsCursor(context);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        //Set the adapter
        String[] from = {SongContent.TRACK_NAME, SongContent.ARTIST};
        int[] to = {R.id.list_item_song_title, R.id.list_item_song_artist};
        mAdapter = new SimpleCursorAdapter(context, R.layout.list_item_song, cursor,
                from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
        Log.d("melody", "Songs adapter was created");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSongListFragmentInteraction(Cursor c);
    }

}
