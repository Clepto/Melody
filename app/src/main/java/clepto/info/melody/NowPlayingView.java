package clepto.info.melody;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.Timer;
import java.util.TimerTask;

import clepto.info.melody.listsview.content.SongContent;

import static clepto.info.melody.R.id.now_playing_queue_button;

public class NowPlayingView extends FragmentActivity implements MediaPlaybackService.OnPlaybackInteractionListener {

    public static final String INFO_RECEIVER    =   "INFO_RECEIVER";
    public static final String UPDATE           =   "UPDATE";
    public static final String MODE             =   "MODE";

    public static final int INFO_UPDATE         =   0;
    public static final int PROGRESS_UPDATE     =   1;
    public static final int PROGRESS_START      =   2;
    public static final int PROGRESS_PAUSE      =   3;
    public static final int PROGRESS_STOP       =   4;
    public static final int PROGRESS_RESUME     =   5;

    private ImageButton nextButton;
    private ImageButton previousButton;
    private ImageButton playButton;
    private ImageButton shuffleButton;
    private ImageButton replayButton;
    private ImageButton queueButton;

    private ViewSwitcher viewSwitcher;
    public MediaSliderView mediaSliderView;
    private TextView songTitleView;
    private TextView artistTitleView;

    private ListView queueListView;
    private ArrayAdapter<String> queueAdapter;
    private Timer mTimer;
    private int mDuration;
    private boolean isPlaying = false;
    private UpdateInfoReceiver mReceiver;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    public NowPlayingView() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_now_playing);

        //mediaSliderView = new MediaSliderView(this);

        songTitleView = (TextView) findViewById(R.id.now_playing_song_title);
        artistTitleView = (TextView) findViewById(R.id.now_playing_artist_title);
        queueListView = (ListView) findViewById(R.id.queue_listview);
        queueAdapter = new ArrayAdapter<String>(this, R.layout.list_item_song_queue, MediaPlaybackService.queueItems);
        mediaSliderView = (MediaSliderView) findViewById(R.id.now_playing_media_slider);

        nextButton      = (ImageButton) findViewById(R.id.now_playing_next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mService.sendMessageToService(MediaPlaybackService.PLAY_NEXT, null);
            }
        });

        previousButton  = (ImageButton) findViewById(R.id.now_playing_previous_button);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mService.sendMessageToService(MediaPlaybackService.PLAY_PREVIOUS, null);
            }
        });

        playButton      = (ImageButton) findViewById(R.id.now_playing_play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton button = (ImageButton) v;
                if (button.getTag() == MainActivity.BUTTON_PLAY) {
                    MainActivity.mService.sendMessageToService(MediaPlaybackService.PAUSE, null);
                    button.setImageResource(R.drawable.ic_action_play);
                    button.setTag(null);
                }
                else {
                    MainActivity.mService.sendMessageToService(MediaPlaybackService.RESUME, null);
                    button.setImageResource(R.drawable.ic_action_pause);
                    button.setTag(MainActivity.BUTTON_PLAY);
                }
            }
        });

        shuffleButton   = (ImageButton) findViewById(R.id.now_playing_shuffle_button);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO make it work
            }
        });

        replayButton    = (ImageButton) findViewById(R.id.now_playing_replay_button);
        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO make it work
            }
        });

        queueButton     = (ImageButton) findViewById(now_playing_queue_button);
        queueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSwitcher.showNext();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            songTitleView.setText(bundle.getString(MainActivity.OVERVIEW_SONG_TITLE));
            artistTitleView.setText(bundle.getString(MainActivity.OVERVIEW_ARTIST_TITLE));
        }

        viewSwitcher    = (ViewSwitcher) findViewById(R.id.now_playing_view_swicther);
        //TODO add in xml the list view
    }

    @Override
    protected void onStart() {
        super.onStart();
        mReceiver = new UpdateInfoReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NowPlayingView.INFO_RECEIVER);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    public MediaSliderView getMediaSliderView() {
        return mediaSliderView;
    }

    private class UpdateInfoReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            int i = extras.getInt(MODE);
            switch (i) {
                case INFO_UPDATE:
                    songTitleView.setText(extras.getString(MainActivity.OVERVIEW_SONG_TITLE));
                    artistTitleView.setText(extras.getString(MainActivity.OVERVIEW_ARTIST_TITLE));

                    break;
                case PROGRESS_UPDATE:
                    int t = extras.getInt(UPDATE);
                    switch (t) {
                        case PROGRESS_START:
                            mDuration = extras.getInt(MediaPlaybackService.DURATION);
                            final int amount = mDuration / 100;
                            mTimer.schedule(new TimerTask() {

                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!(amount * mediaSliderView.getProgress() >= mDuration) && !isPlaying) {
                                                int p = mediaSliderView.getProgress();
                                                p += 1;
                                                mediaSliderView.setProgress(p);
                                            }
                                        }
                                    });
                                }
                            }, mDuration);

                            break;
                        case PROGRESS_PAUSE:
                            isPlaying = false;

                            break;
                        case PROGRESS_STOP:
                            mTimer.cancel();
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public void onProgressListener(int progress, int duration) {
        switch (progress) {
            case PROGRESS_START:
                mDuration = duration;
                final int amount = mDuration / 100;
//                runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (!(amount * mediaSliderView.getProgress() >= mDuration) && !isPlaying) {
//                                    int p = mediaSliderView.getProgress();
//                                    p += 1;
//                                    mediaSliderView.setProgress(p);
//                                }
//                            }
//                        });
//                mRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        if (MediaPlaybackService.mediaPlayer.isPlaying() ) {
//                            try {
//                                int p = (int) (MediaPlaybackService.mediaPlayer.getCurrentPosition() / getMediaSliderView().getmEndAngle());
//                                p += 1;
//                                getMediaSliderView().setProgress(p);
//                            } catch (NullPointerException e) {
//
//                            }
//                        }
//                        mHandler.postDelayed(this, 1000);
//                    }
//                };
                runOnUiThread(new MediaSliderRunnable(this, mHandler));
                runOnUiThread(mRunnable);
                break;
            case PROGRESS_PAUSE:
                isPlaying = false;

                break;
            case PROGRESS_RESUME:
                isPlaying = true;

                break;
            case PROGRESS_STOP:
                mTimer.cancel();
                break;
        }
    }

    @Override
    public void onInfoListener(Cursor c) {
        songTitleView.setText(c.getString(c.getColumnIndex(SongContent.TRACK_NAME)));
        artistTitleView.setText(c.getString(c.getColumnIndex(SongContent.ARTIST)));
        //TODO add image to view
    }

//    class ProgressRefresher implements Runnable {
//        public void run() {
//            if () {
//                int progress = mPlayer.getCurrentPosition() / mDuration;
//                mSeekBar.setProgress(mPlayer.getCurrentPosition());
//            }
//            mProgressRefresher.removeCallbacksAndMessages(null);
//            mProgressRefresher.postDelayed(new ProgressRefresher(), 200);
//        }
//    }
}
