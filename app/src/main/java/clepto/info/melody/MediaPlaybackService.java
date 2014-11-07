package clepto.info.melody;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import clepto.info.melody.listsview.content.SongContent;

public class MediaPlaybackService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener{

    /*
    * Modes for activity-service communication
    */
    public static final int STOP =   -1;
    public static final int PAUSE =   0;
    public static final int PLAY =   1;
    public static final int RESUME =   2;
    public static final int PLAY_NEXT =   3;
    public static final int PLAY_PREVIOUS =   4;
    public static final int SHUFFLE =   5;
    public static final int REPLAY =   6;
    public static final int ADD_TO_QUEUE =   10;
    public static final int ADD_SONG_TO_QUEUE =   11;

    public static final String DURATION = "DURATION";
//    public static final int

    public static ArrayList<String> queueItems;

    private Uri uri;
    private int position = 0;
    private int resumeSeekTo = 0;
    private boolean seekTo = false;
    public static MediaPlayer mediaPlayer;
    private final IBinder mBinder = new LocalBinder();

    public MediaPlaybackService() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);

        queueItems = new ArrayList<String>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void createNotification() {
//        float logicalDensity = getResources().getDisplayMetrics().density;
  //      int px = (int) Math.ceil(24 * logicalDensity);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification builder = new Notification.Builder(this)
                .setContentTitle("Song")
                .setSmallIcon(R.drawable.default_image)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(24091995, builder);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {


        Cursor c = getContentResolver().query(uri, SongContent.columns, null, null, null);
        c.moveToFirst();
//        MainActivity.nowPlayingView.onInfoListener(c);

        Intent intent = new Intent();
        intent.setAction(MainActivity.SERVICE_ACTION);
        intent.putExtra("SONG_URI", uri.toString());
        sendBroadcast(intent);
        mediaPlayer.start();



        Intent infoIntent = new Intent();
        infoIntent.setAction(NowPlayingView.INFO_RECEIVER);
        infoIntent.putExtra(NowPlayingView.MODE, NowPlayingView.INFO_UPDATE);
        infoIntent.putExtra(MainActivity.OVERVIEW_SONG_TITLE, c.getString(c.getColumnIndex(SongContent.TRACK_NAME)));
        infoIntent.putExtra(MainActivity.OVERVIEW_ARTIST_TITLE, c.getString(c.getColumnIndex(SongContent.ARTIST)));
        //TODO add image path to intent
        sendBroadcast(infoIntent);
//
        MainActivity.nowPlayingView.onProgressListener(NowPlayingView.PROGRESS_START, mp.getDuration());
//        Intent updateIntent = new Intent();
//        updateIntent.setAction(NowPlayingView.INFO_RECEIVER);
//        updateIntent.putExtra(NowPlayingView.MODE, NowPlayingView.PROGRESS_UPDATE);
//        updateIntent.putExtra(NowPlayingView.UPDATE, NowPlayingView.PROGRESS_START);
//        updateIntent.putExtra(MediaPlaybackService.DURATION, mp.getDuration());
//        sendBroadcast(updateIntent);
//        MainActivity.nowPlayingView.onProgressListener();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (position < queueItems.size())
            setPosition(position + 1);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d("melody", String.valueOf(percent));
    }

    public class LocalBinder extends Binder {
        MediaPlaybackService getService() {
            return MediaPlaybackService.this;
        }
    }

    public void setPosition(int position) {
        this.position = position;
        playSong(position);
    }

    public int getPosition() {
        return position;
    }

    private void playSong(int position) {
        sendMessageToService(PLAY, null);
    }

    public void sendMessageToService(int mode, Cursor c) {
        /*
        * Send message from activity to this service
        */
        Intent updateIntent;
        switch (mode) {
            case ADD_TO_QUEUE:
                // Get id of first song to play, because we don't know if the queue list is empty
                String id = c.getString(c.getColumnIndex(SongContent.TRACK_ID));
                position = queueItems.size();

                do {
                    queueItems.add(c.getString(c.getColumnIndex(SongContent.TRACK_ID)));
                } while (c.moveToNext());
                Log.d("melody", "Number of songs in queue: " + queueItems.size());
                break;
            case ADD_SONG_TO_QUEUE:
                queueItems.add(c.getString(c.getColumnIndex(SongContent.TRACK_ID)));
                Log.d("melody", "Number of songs in queue: " + queueItems.size());
                break;
            case PLAY:
                uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        queueItems.get(position));

                try {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(this, uri);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                catch (IllegalStateException e) {

                }
                createNotification();
                break;
            case RESUME:
                MainActivity.nowPlayingView.onProgressListener(NowPlayingView.PROGRESS_RESUME, 0);
                mediaPlayer.start();
                break;
            case PAUSE:
//                updateIntent = new Intent();
//                updateIntent.putExtra(NowPlayingView.UPDATE, NowPlayingView.PROGRESS_PAUSE);
//                sendBroadcast(updateIntent);
                MainActivity.nowPlayingView.onProgressListener(NowPlayingView.PROGRESS_PAUSE, 0);
                resumeSeekTo = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
                break;
            case STOP:
//                updateIntent = new Intent();
//                updateIntent.putExtra(NowPlayingView.UPDATE, NowPlayingView.PROGRESS_STOP);
//                sendBroadcast(updateIntent);
                MainActivity.nowPlayingView.onProgressListener(NowPlayingView.PROGRESS_STOP, 0);
                resumeSeekTo = 0;
                seekTo = false;
                mediaPlayer.stop();
                break;
            case PLAY_NEXT:
                if (position >= queueItems.size())
                    break;

                Log.d("melody", "Position of next song is: " + position);
                setPosition(position + 1);
                break;
            case PLAY_PREVIOUS:
                if (position == 0)
                    break;

                Log.d("melody", "Position of previous song is: " + position);
                setPosition(position - 1);
                break;
            default:
                break;
        }
    }

    public interface OnPlaybackInteractionListener {
        public void onProgressListener(int progress, int duration);
        public void onInfoListener(Cursor c);
    }
}
