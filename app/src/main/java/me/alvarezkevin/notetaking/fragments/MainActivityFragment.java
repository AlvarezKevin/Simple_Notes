package me.alvarezkevin.notetaking.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import me.alvarezkevin.notetaking.EditNoteActivity;
import me.alvarezkevin.notetaking.R;
import me.alvarezkevin.notetaking.adapters.NoteCursorAdapter;
import me.alvarezkevin.notetaking.data.NoteContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final int NOTE_LOADER = 0;
    private NoteCursorAdapter mNoteAdapter;

    public MainActivityFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mNoteAdapter = new NoteCursorAdapter(getActivity(), null);

        ListView listView = (ListView) view.findViewById(R.id.notes_list_view);
        listView.setAdapter(mNoteAdapter);
        listView.setEmptyView(view.findViewById(R.id.textview_empty));

        //Creates uri with note id and passes it to intent once clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri noteUri = ContentUris.withAppendedId(NoteContract.NoteEntry.CONTENT_URI, id);
                Intent intent = new Intent(getActivity(), EditNoteActivity.class);
                intent.setData(noteUri);
                startActivity(intent);
            }

        });

        //Init loaders when view is created
        getLoaderManager().initLoader(NOTE_LOADER, null, this);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_button) {
            Intent intent = new Intent(getActivity(), EditNoteActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_delete_all) {
            deleteAll();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAll() {
        //Creates a AlertDialog with delete and cancel button
        //Deletes whole list if delete is clicked, else it cancels
        final AlertDialog.Builder deleteAllDialog = new AlertDialog.Builder(getActivity());
        deleteAllDialog.setTitle("Delete All");
        deleteAllDialog.setMessage("Are you sure you want to delete all?");

        deleteAllDialog.setPositiveButton("Delete all", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getActivity().getContentResolver().delete(NoteContract.NoteEntry.CONTENT_URI, null, null);
            }
        });
        deleteAllDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        deleteAllDialog.show();
    }

    //Creates cursorloader and gets data from content provider and swaps cursor on adapter if any change detected
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                NoteContract.NoteEntry._ID,
                NoteContract.NoteEntry.COLUMN_NOTE_NAME,
                NoteContract.NoteEntry.COLUMN_NOTE_TEXT,
                NoteContract.NoteEntry.COLUMN_NOTE_COLOR
        };
        return new CursorLoader(getActivity(), NoteContract.NoteEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mNoteAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNoteAdapter.swapCursor(null);
    }
}
