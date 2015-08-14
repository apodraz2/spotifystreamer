package com.podraza.android.spotifystreamer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by adampodraza on 8/11/15.
 */
public class MediaPlayerService extends Service implements MediaPlayer.OnPreparedListener{
    private String LOG_TAG = getClass().getSimpleName();
    static MediaPlayer mMediaPlayer = null;
    private static final String ACTION_PLAY = "PLAY";
    private static final String ACTION_PAUSE = "PAUSE";
    private static final String ACTION_SEEK = "SEEK";
    public static final String CUSTOM_INTENT = "CUSTOM_INTENT";

    private Context ctx;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public static MediaPlayer getmMediaPlayer() {
        return mMediaPlayer;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        ctx = getApplicationContext();
        mp.start();

        UpdateProgress up = new UpdateProgress();
        up.execute();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        if(intent == null) return 0;
        if(intent.getAction().equals(ACTION_PAUSE)) {
            if(mMediaPlayer!= null) {
                mMediaPlayer.pause();
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
        if (intent.getAction().equals(ACTION_PLAY)) {
            String pURL = intent.getStringExtra("pURL");
            if(pURL == null) {
                Toast toast = Toast.makeText(
                        getApplicationContext(),
                        "Something went wrong, please try again",
                        Toast.LENGTH_SHORT);
                toast.show();
                return 0;

            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            try {
                mMediaPlayer.setDataSource(pURL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mMediaPlayer.prepareAsync();

        }

        if(intent.getAction().equals(ACTION_SEEK)) {
            if(mMediaPlayer != null) {
                int mediaPlayerPosition = intent.getIntExtra("mediaPlayerPosition", 0);
                //Log.d(LOG_TAG, "the mediaPlayerPosition is: " + mediaPlayerPosition);
                mMediaPlayer.seekTo(mediaPlayerPosition);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    class UpdateProgress extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            while(mMediaPlayer != null) {
                Intent i = new Intent();
                i.setAction(CUSTOM_INTENT);
                int pos = mMediaPlayer.getCurrentPosition();
                i.putExtra("position", pos);

                ctx.sendBroadcast(i);
            }

            return null;
        }
    }

}
