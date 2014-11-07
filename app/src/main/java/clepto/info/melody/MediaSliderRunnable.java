package clepto.info.melody;

import android.os.Handler;
import android.util.Log;

public class MediaSliderRunnable implements Runnable {

    private Handler handler;
    private NowPlayingView view;
    private MediaSliderView sliderView;

    public MediaSliderRunnable(NowPlayingView view, Handler handler) {
        this.view = view;
        this.handler = handler;
    }
    
    @Override
    public void run() {
        sliderView = view.getMediaSliderView();
        Log.d("melody", "");
        handler.postDelayed(this, 1000);
    }
}
