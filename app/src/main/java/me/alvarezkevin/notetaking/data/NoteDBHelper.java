package me.alvarezkevin.notetaking.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import me.alvarezkevin.notetaking.data.NoteContract.NoteEntry;

/**
 * Created by Kevin on 3/9/2017.
 */

public class NoteDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "note.db";

    public NoteDBHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ENTRIES = "CREATE TABLE " + NoteEntry.TABLE_NAME + "(" +
                NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NoteEntry.COLUMN_NOTE_NAME + " TEXT NOT NULL, " +
                NoteEntry.COLUMN_NOTE_TEXT + " TEXT, " +
                NoteEntry.COLUMN_NOTE_COLOR + " INTEGER NOT NULL DEFAULT 0);";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
