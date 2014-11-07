package clepto.info.melody;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import clepto.info.melody.listsview.AlbumListFragment;
import clepto.info.melody.listsview.ArtistListFragment;
import clepto.info.melody.listsview.SongListFragment;
import clepto.info.melody.listsview.content.AlbumContent;
import clepto.info.melody.listsview.content.SongContent;


public class MainActivity extends FragmentActivity implements SongListFragment.OnFragmentInteractionListener,
        ArtistListFragment.OnFragmentInteractionListener, AlbumListFragment.OnFragmentInteractionListener {

    /**
     * ID's for CursorLoaders
     */
    public static final int SONG_LOADER     = 0;
    public static final int ALBUM_LOADER    = 1;
    public static final int ARTIST_LOADER   = 2;

    public static final String SERVICE_ACTION = "SERVICE_ACTION";
    public static final String BUTTON_PLAY = "PLAY";

    public static final String OVERVIEW_SONG_TITLE  =   "SONG_TITLE";
    public static final String OVERVIEW_ARTIST_TITLE =  "ARTIST_TITLE";
    public static final String OVERVIEW_IMAGE_URI   =   "IMAGE_URI";

    private final int OVERVIEW_IMAGE_SIZE   = 50;

    private Cursor mCurrentSongCursor;

    public static LoaderManager loaderManager;
    private ViewSwitcher viewSwitcher;

    public static NowPlayingView nowPlayingView;
    private RelativeLayout nowPlayingOverview;
    private TextView nowPlayingOverviewTitle;
    private ImageView nowPlayingOverviewAlbum;

    private ImageButton nowPlayingOverviewButton;
    private BroadcastReceiver mReceiver;
    public static MediaPlaybackService mService;
    private boolean mServiceConnected = false;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((MediaPlaybackService.LocalBinder) service).getService();
            mServiceConnected = true;
            Log.d("melody", "Service was initialized");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mServiceConnected = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my);
        if (savedInstanceState == null) {
            loaderManager = getSupportLoaderManager();
        }

        nowPlayingView = new NowPlayingView();
        nowPlayingOverview = (RelativeLayout) findViewById(R.id.now_playing_overview);
        nowPlayingOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ((Object) nowPlayingView).getClass());
                intent.putExtra(OVERVIEW_SONG_TITLE, nowPlayingOverviewTitle.getText().toString());
                mCurrentSongCursor.moveToPosition(0);
                intent.putExtra(OVERVIEW_ARTIST_TITLE, mCurrentSongCursor.getString(
                        mCurrentSongCursor.getColumnIndex(SongContent.ARTIST)));
                intent.putExtra(OVERVIEW_IMAGE_URI, nowPlayingOverviewAlbum.getTag().toString());
                startActivity(intent);
            }
        });
        nowPlayingOverviewTitle = (TextView) findViewById(R.id.now_playing_overview_title);
        nowPlayingOverviewAlbum = (ImageView) findViewById(R.id.now_playing_overview_album);
        nowPlayingOverviewButton = (ImageButton) findViewById(R.id.now_playing_overview_button);
        nowPlayingOverviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton button = (ImageButton) v;
                if (button.getTag() == BUTTON_PLAY) {
                    mService.sendMessageToService(MediaPlaybackService.PAUSE, null);
                    button.setImageResource(R.drawable.ic_action_play);
                    button.setTag(null);
                }
                else {
                    mService.sendMessageToService(MediaPlaybackService.RESUME, null);
                    button.setImageResource(R.drawable.ic_action_pause);
                    button.setTag(BUTTON_PLAY);
                }
            }
        });

        FragmentTabHost tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        tabHost.setup(getApplicationContext(), getSupportFragmentManager(), android.R.id.tabcontent);

        tabHost.addTab(tabHost.newTabSpec("artists tab").setIndicator("Artists"), ArtistListFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("albums tab").setIndicator("Albums"), AlbumListFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("songs tab").setIndicator("Songs"), SongListFragment.class, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mReceiver = new MainActivityReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.SERVICE_ACTION);
        registerReceiver(mReceiver, intentFilter);

        Intent intent = new Intent(this, MediaPlaybackService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnected) {
            unbindService(serviceConnection);
            stopService(new Intent(this, MediaPlaybackService.class));
            mServiceConnected = false;
        }
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSongListFragmentInteraction(Cursor c) {
//        String id = c.getString(c.getColumnIndex(SongContent.TRACK_ID));
        mCurrentSongCursor =  c;
        nowPlayingOverviewButton.setTag(BUTTON_PLAY);
        mService.sendMessageToService(MediaPlaybackService.ADD_SONG_TO_QUEUE, c);
        mService.sendMessageToService(MediaPlaybackService.PLAY, null);
    }

    @Override
    public void onFragmentInteraction(Cursor c, boolean songItem) {

    }

    private class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int mode = intent.getIntExtra("MODE", 0);
            Uri uri = Uri.parse(intent.getStringExtra("SONG_URI"));

            Cursor c = getContentResolver().query(uri, SongContent.columns, null, null, null);
            if (!c.moveToFirst()) {
                Log.d("melody", "MainActivityReceiver: song not found");
                return;
            }

            String title = c.getString(c.getColumnIndex(SongContent.TRACK_NAME));
            String album = c.getString(c.getColumnIndex(SongContent.ALBUM));

            c = getContentResolver().query(AlbumContent.uri, AlbumContent.columns, AlbumContent.ALBUM + "='" + album +"'", null, null);
            if (!c.moveToFirst()) {
                Log.d("melody", "MainActivityReceiver: album not found");
                return;
            }

            String file = c.getString(c.getColumnIndex(AlbumContent.ALBUM_ART));
            Bitmap bitmap = BitmapFactory.decodeFile(file);
            nowPlayingOverviewAlbum.setTag(R.drawable.default_image);
            if (bitmap == null) {
                bitmap = Bitmap.createScaledBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.default_image),
                        OVERVIEW_IMAGE_SIZE, OVERVIEW_IMAGE_SIZE, false);
                nowPlayingOverviewAlbum.setTag(R.drawable.default_image);
            } else {
                bitmap = Bitmap.createScaledBitmap(bitmap, OVERVIEW_IMAGE_SIZE, OVERVIEW_IMAGE_SIZE, false);
                nowPlayingOverviewAlbum.setTag(file);
            }

            nowPlayingOverviewAlbum.setImageBitmap(bitmap);
            nowPlayingOverviewTitle.setText(title);
            nowPlayingOverviewButton.setImageResource(R.drawable.ic_action_pause);
            nowPlayingOverviewButton.setTag(BUTTON_PLAY);
        }
    }
}
