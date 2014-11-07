package clepto.info.melody.listsview.content;

import android.content.Context;
import android.database.Cursor;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;


/**
 * Helper class for providing artists content
 */

public class ArtistContent {

    /**
     * Artist's cursor columns
     */
    public static final String ARTIST_ID            =   MediaStore.Audio.Artists._ID;
    public static final String ARTIST               =   MediaStore.Audio.Artists.ARTIST;
    public static final String ARTIST_KEY           =   MediaStore.Audio.Artists.ARTIST_KEY;
    public static final String NUMBER_OF_ALBUMS     =   MediaStore.Audio.Artists.NUMBER_OF_ALBUMS;
    public static final String NUMBER_OF_TRACKS     =   MediaStore.Audio.Artists.NUMBER_OF_TRACKS;

    /**
     * Uri pointing to all artists
     */
    public static final Uri uri                     =   MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;

    /**
     * Columns for artist cursor
     */
    public static final String[] columns            =   {
            ARTIST_ID, ARTIST, ARTIST_KEY,
            NUMBER_OF_ALBUMS, NUMBER_OF_TRACKS
    };

    /**
     * A cursor providing all artists in the device
     */
    public static Cursor albumsCursor = null;

    public static CursorLoader getArtistCursor(Context context) {
        return new CursorLoader(context, uri, columns, null, null, ARTIST + " ASC");
    }

}
