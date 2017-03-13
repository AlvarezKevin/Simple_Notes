package me.alvarezkevin.notetaking.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Kevin on 3/9/2017.
 */

public class NoteContract {

    private NoteContract() {

    }

    public static final String CONTENT_AUTHORITY = "me.alvarezkevin.notetaking";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_NOTES = "notes";

    public static class NoteEntry implements BaseColumns {
        public static final String TABLE_NAME = "notes";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NOTE_NAME = "name";
        public static final String COLUMN_NOTE_TEXT = "text";
        public static final String COLUMN_NOTE_COLOR = "color";

        public static final int NOTE_COLOR_WHITE = 0;
        public static final int NOTE_COLOR_YELLOW= 1;
        public static final int NOTE_COLOR_BLUE = 2;
        public static final int NOTE_COLOR_RED = 3;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_NOTES);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_NOTES;

    }
}
