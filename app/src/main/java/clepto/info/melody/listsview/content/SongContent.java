package clepto.info.melody.listsview.content;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * Helper class for providing songs content
 */
public class SongContent {

    /**
     * Song's cursor columns
     */
    public static final String TRACK_ID     =   MediaStore.Audio.Media._ID;
    public static final String TRACK_NO     =   MediaStore.Audio.Media.TRACK;
    public static final String TRACK_NAME   =   MediaStore.Audio.Media.TITLE;
    public static final String ARTIST       =   MediaStore.Audio.Media.ARTIST;
    public static final String DURATION     =   MediaStore.Audio.Media.DURATION;
    public static final String ALBUM        =   MediaStore.Audio.Media.ALBUM;
    public static final String COMPOSER     =   MediaStore.Audio.Media.COMPOSER;
    public static final String YEAR         =   MediaStore.Audio.Media.YEAR;
    public static final String PATH         =   MediaStore.Audio.Media.DATA;

    /**
     * Uri pointing to all songs
     */
    public static final Uri uri             =   MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    /**
     * Columns for songs cursor
     */
    public static final String[] columns    =   {
            TRACK_ID, TRACK_NO, ARTIST,
            TRACK_NAME, ALBUM, DURATION,
            PATH, YEAR, COMPOSER
    };

    /**
     * A cursor pointing to all songs in the device
     */
    public static Cursor songsCursor = null;

    public static CursorLoader getSongsCursor(Context context) {
        return new CursorLoader(context, uri, columns, null, null, TRACK_NAME + " ASC");
    }
}
