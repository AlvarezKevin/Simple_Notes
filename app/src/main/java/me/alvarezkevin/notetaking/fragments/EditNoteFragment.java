package me.alvarezkevin.notetaking.fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import me.alvarezkevin.notetaking.EditNoteActivity;
import me.alvarezkevin.notetaking.R;
import me.alvarezkevin.notetaking.data.NoteContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditNoteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditNoteActivity.class.getSimpleName();

    private static int mNoteColor = 0;
    private static Uri mNoteUri = null;
    private static boolean mNoteChange = false;

    private RelativeLayout mRelativeLayout;
    private EditText mNoteNameEditText;
    private EditText mNoteEditText;
    private Spinner mColorSpinner;

    public EditNoteFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_note, container, false);

        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.content_edit_note);
        mNoteNameEditText = (EditText) view.findViewById(R.id.note_name_edit_text);
        mNoteEditText = (EditText) view.findViewById(R.id.note_edit_text);

        mNoteUri = getActivity().getIntent().getData();
        if (mNoteUri == null) {
            getActivity().setTitle(getString(R.string.add_note));
            mNoteColor = 0;
        } else {
            getActivity().setTitle(getString(R.string.edit_note));
            getLoaderManager().initLoader(1, null, this);
        }

        return view;
    }

    private View.OnTouchListener mTouchListeneer = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mNoteChange = true;
            return false;
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_edit_note, menu);

        MenuItem item = menu.findItem(R.id.spinner_color);
        mColorSpinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter colorSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.arrays_color_option, R.layout.spinner_item);
        colorSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mColorSpinner.setAdapter(colorSpinnerAdapter);

        if (mNoteUri == null) {
            MenuItem deleteItem = menu.findItem(R.id.action_delete);
            deleteItem.setVisible(false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            saveNote();
            getActivity().finish();
            return true;
        }
        if (id == R.id.spinner_color) {
            mColorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String color = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(color)) {
                        if (color.equals("White")) {
                            mNoteColor = NoteContract.NoteEntry.NOTE_COLOR_WHITE;
                        } else if (color.equals("Yellow")) {
                            mNoteColor = NoteContract.NoteEntry.NOTE_COLOR_YELLOW;
                        } else if (color.equals("Blue")) {
                            mNoteColor = NoteContract.NoteEntry.NOTE_COLOR_BLUE;
                        } else if (color.equals("Red")) {
                            mNoteColor = NoteContract.NoteEntry.NOTE_COLOR_RED;
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mNoteColor = NoteContract.NoteEntry.NOTE_COLOR_WHITE;
                }
            });
            return true;
        }
        if (id == android.R.id.home) {
            if(mNoteChange) {
                saveNote();
            }
            NavUtils.navigateUpFromSameTask(getActivity());
            return true;
        }
        if(id == R.id.action_delete) {
            showDeleteBox();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //Function used to insert or save note into database
    private void saveNote() {
        //mNoteNameEditText = (EditText) getActivity().findViewById(R.id.note_name_edit_text);
        String noteName = mNoteNameEditText.getText().toString().trim();
        Log.v(LOG_TAG, "NOTE NAME: " + noteName);

        // mNoteEditText = (EditText) getActivity().findViewById(R.id.note_edit_text);
        String noteText = mNoteEditText.getText().toString().trim();
        Log.v(LOG_TAG, "NOTE: " + noteText);

        int color = mNoteColor;

        if (noteName.length() < 1) {
            Toast.makeText(getActivity(), R.string.failed_save_no_title, Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues noteValues = new ContentValues();
        noteValues.put(NoteContract.NoteEntry.COLUMN_NOTE_NAME, noteName);
        noteValues.put(NoteContract.NoteEntry.COLUMN_NOTE_TEXT, noteText);
        noteValues.put(NoteContract.NoteEntry.COLUMN_NOTE_COLOR, color);

        Uri uri = null;
        int rowsUpdated = 0;
        if (mNoteUri == null) {
            uri = getActivity().getContentResolver().insert(NoteContract.NoteEntry.CONTENT_URI, noteValues);
        } else {
            rowsUpdated = getActivity().getContentResolver().update(mNoteUri, noteValues, null, null);
        }

        if (uri == null && rowsUpdated == 0) {
            Toast.makeText(getActivity(), getString(R.string.error_saving_note), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getString(R.string.note_saved), Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Are you sure you wish to delete the note?");
        builder.setTitle("Delete");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNote();
                getActivity().finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void deleteNote() {
        if(mNoteUri != null) {
            int rowsDeleted = getActivity().getContentResolver().delete(mNoteUri,null,null);
            if (rowsDeleted != 0) {
                Toast.makeText(getActivity(), R.string.note_deleted,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity(), R.string.unable_to_delete, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*Loader data
        Gets CursorLoader
        Sets text fields and colors based on cursor data
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                NoteContract.NoteEntry._ID,
                NoteContract.NoteEntry.COLUMN_NOTE_NAME,
                NoteContract.NoteEntry.COLUMN_NOTE_TEXT,
                NoteContract.NoteEntry.COLUMN_NOTE_COLOR
        };
        return new CursorLoader(getActivity(), mNoteUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            String name = data.getString(data.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NOTE_NAME));
            String text = data.getString(data.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NOTE_TEXT));
            int color = data.getInt(data.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NOTE_COLOR));

            mNoteNameEditText.setText(name);
            mNoteEditText.setText(text);


            switch (color) {
                case NoteContract.NoteEntry.NOTE_COLOR_YELLOW:
                    mNoteNameEditText.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorYellow));
                    mNoteEditText.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorYellow));
                    mRelativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorYellow));
                    mNoteColor = 1;

                    mNoteEditText.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.primary_text_light));
                    mNoteNameEditText.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.primary_text_light));
                    break;
                case NoteContract.NoteEntry.NOTE_COLOR_BLUE:
                    mNoteNameEditText.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBlue));
                    mNoteEditText.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBlue));
                    mRelativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorBlue));
                    mNoteColor = 2;

                    mNoteEditText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                    mNoteNameEditText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));

                    break;
                case NoteContract.NoteEntry.NOTE_COLOR_RED:
                    mNoteNameEditText.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
                    mNoteEditText.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
                    mRelativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
                    mNoteColor = 3;

                    mNoteEditText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                    mNoteNameEditText.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                    break;
                default:
                    mNoteNameEditText.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                    mNoteEditText.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                    mRelativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
                    mNoteColor = 0;

                    mNoteEditText.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.primary_text_light));
                    mNoteNameEditText.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.primary_text_light));
                    break;

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNoteNameEditText.setText(null);
        mNoteEditText.setText(null);
        mColorSpinner.setSelection(NoteContract.NoteEntry.NOTE_COLOR_WHITE);
    }
}


