package clepto.info.melody.listsview.content;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing albums content
 */
public class AlbumContent {

    /**
     * Album's cursor columns
     */
    public static final String ALBUM_ID         =   MediaStore.Audio.Albums._ID;
    public static final String ALBUM            =   MediaStore.Audio.Albums.ALBUM;
    public static final String ALBUM_ART        =   MediaStore.Audio.Albums.ALBUM_ART;
    public static final String ALBUM_KEY        =   MediaStore.Audio.Albums.ALBUM_KEY;
    public static final String ARTIST           =   MediaStore.Audio.Albums.ARTIST;
    public static final String FIRST_YEAR       =   MediaStore.Audio.Albums.FIRST_YEAR;
    public static final String LAST_YEAR        =   MediaStore.Audio.Albums.LAST_YEAR;
    public static final String NUMBER_OF_SONGS  =   MediaStore.Audio.Albums.NUMBER_OF_SONGS;

    /**
     * Uri pointing to all albums
     */
    public static final Uri uri                 =   MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

    /**
     * Columns for album cursor
     */
    public static final String[] columns        =   {
            ALBUM_ID, ALBUM, ALBUM_ART,
            ALBUM_KEY, ARTIST, FIRST_YEAR,
            LAST_YEAR, NUMBER_OF_SONGS
    };

    /**
     * A cursor pointing to all albums in the device
     */
    public static Cursor albumsCursor = null;

    public static CursorLoader getAlbumsCursor(Context context) {

        return new CursorLoader(context, uri, columns, null, null, ALBUM + " ASC");
    }

}
