package me.alvarezkevin.notetaking.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.alvarezkevin.notetaking.R;
import me.alvarezkevin.notetaking.data.NoteContract;

/**
 * Created by Kevin on 3/10/2017.
 */

public class NoteCursorAdapter extends CursorAdapter {

    public NoteCursorAdapter(Context context,Cursor cursor) {
        super(context,cursor,0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.note_text_views_list_view,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView noteNameTV = (TextView)view.findViewById(R.id.note_name_text_view);
        TextView noteTextTV = (TextView)view.findViewById(R.id.note_preview_text_view);
        LinearLayout noteLinearLayout = (LinearLayout)view.findViewById(R.id.list_item_note_linear_layout);

        int noteColor = cursor.getInt(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NOTE_COLOR));
        String noteName = cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NOTE_NAME));
        String noteText = cursor.getString(cursor.getColumnIndexOrThrow(NoteContract.NoteEntry.COLUMN_NOTE_TEXT));

        switch (noteColor) {
            case NoteContract.NoteEntry.NOTE_COLOR_WHITE:
                noteLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
                break;
            case NoteContract.NoteEntry.NOTE_COLOR_YELLOW:
                noteLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorYellow));
                break;
            case NoteContract.NoteEntry.NOTE_COLOR_BLUE:
                noteLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorBlue));
                break;
            case NoteContract.NoteEntry.NOTE_COLOR_RED:
                noteLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
                break;

        }

        noteNameTV.setText(noteName);
        if(noteText != null) {
            if(noteText.length() > 40) {
                noteTextTV.setText(noteText.substring(0,40) + "...");
            }else {
                noteTextTV.setText(noteText);
            }
        }
    }
}