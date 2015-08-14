package com.podraza.android.spotifystreamer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class NowPlayingActivityFragment extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private static final String ACTION_PLAY = "PLAY";
    private static final String ACTION_PAUSE = "PAUSE";
    private static final String ACTION_SEEK = "SEEK";

    private boolean playPause = false;
    int mediaPlayerPosition = 0;

    private ImageButton playButton;
    private SeekBar seekBar;

    private ArrayList<Parcelable> tracks;
    private int position;
    private ParcelableTrack track;

    private View rootView;

    String pURL = null;

    // Flag if receiver is registered
    private boolean mReceiversRegistered = false;
    // Define a handler and a broadcast receiver
    private final Handler mHandler = new Handler();

    public NowPlayingActivityFragment() {


    }

    //Prevent errors when orientation changes
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");
        outState.putParcelableArrayList("tracks", tracks);
        outState.putInt("position", position);
        outState.putInt("mediaPlayerPosition", mediaPlayerPosition);
        //nullifyMediaPlayer();
        super.onSaveInstanceState(outState);
    }

    private void manageMediaPlayer(String action) {
        Intent intent = new Intent(getActivity(), MediaPlayerService.class);
        intent.setAction(action);
        intent.putExtra("pURL", pURL);
        intent.putExtra("mediaPlayerPosition", mediaPlayerPosition);
        getActivity().startService(intent);
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(MediaPlayerService.CUSTOM_INTENT)) {
                seekBar = (SeekBar) getActivity().findViewById(R.id.now_playing_seek_bar);

                mediaPlayerPosition = intent.getIntExtra("position", 0);

                seekBar.setProgress(mediaPlayerPosition/1000);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        // Register Sync Recievers
        IntentFilter intentToReceiveFilter = new IntentFilter();
        intentToReceiveFilter.addAction(MediaPlayerService.CUSTOM_INTENT);
        getActivity().registerReceiver(mIntentReceiver, intentToReceiveFilter, null, mHandler);
        mReceiversRegistered = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);

        if(!(savedInstanceState == null)) {
            tracks = savedInstanceState.getParcelableArrayList("tracks");
            position = savedInstanceState.getInt("position", 0);
            mediaPlayerPosition = savedInstanceState.getInt("mediaPlayerPosition");
            track = (ParcelableTrack) tracks.get(position);
        }

        if(tracks == null) {
            Log.d(LOG_TAG, "tracks equaled null: " + (tracks == null));
            tracks = getActivity().getIntent().getParcelableArrayListExtra("tracks");
            position = getActivity().getIntent().getIntExtra("position", 0);
            track = (ParcelableTrack) tracks.get(position);
            pURL = track.getPreviewUrl();
            manageMediaPlayer(ACTION_PLAY);
        }

        rootView = refreshView(track, rootView);

        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageMediaPlayer(ACTION_PAUSE);
                mediaPlayerPosition = 0;
                playPause = true;
                playButton.setImageResource(android.R.drawable.ic_media_play);

                if (position == (tracks.size() - 1)) {

                    position = 0;
                    track = (ParcelableTrack) tracks.get(position);

                    rootView = refreshView(track, rootView);
                } else {

                    position += 1;
                    track = (ParcelableTrack) tracks.get(position);

                    rootView = refreshView(track, rootView);
                }
            }
        });

        ImageButton prevButton = (ImageButton) rootView.findViewById((R.id.prev_button));
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageMediaPlayer(ACTION_PAUSE);
                mediaPlayerPosition = 0;
                playPause = true;
                playButton.setImageResource(android.R.drawable.ic_media_play);

                if (position == 0) {

                    position = tracks.size() - 1;
                    track = (ParcelableTrack) tracks.get(position);

                    rootView = refreshView(track, rootView);

                } else {

                    position -= 1;
                    track = (ParcelableTrack) tracks.get(position);

                    rootView = refreshView(track, rootView);
                }
            }
        });

        return rootView;
    }

    View refreshView(ParcelableTrack track, View rootView) {
        final MediaPlayer mediaPlayer = MediaPlayerService.getmMediaPlayer();

        TextView artistText = (TextView) rootView.findViewById(R.id.now_playing_artist_name);
        artistText.setText(track.artistName);

        TextView albumText = (TextView) rootView.findViewById(R.id.now_playing_album_name);
        albumText.setText(track.albumName);

        ImageView albumImage = (ImageView) rootView.findViewById(R.id.now_playing_album_art);
        if(!(track.imageUrl == null)){

            Picasso.with(getActivity().getApplicationContext()).load(Uri.parse(track.imageUrl))
                    .resize(1000, 1000)
                    .into(albumImage);
        } else {
            Picasso.with(getActivity().getApplicationContext())
                    .load(R.drawable.question_mark)
                    .resize(1000, 1000)
                    .into(albumImage);
        }

        TextView trackText = (TextView) rootView.findViewById(R.id.now_playing_track_name);
        trackText.setText(track.getTrackName());

        seekBar = (SeekBar) rootView.findViewById(R.id.now_playing_seek_bar);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                mediaPlayerPosition = seekBar.getThumbOffset();
                manageMediaPlayer(ACTION_SEEK);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pURL = track.getPreviewUrl();

        playButton = (ImageButton) rootView.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (playPause) {

                    manageMediaPlayer(ACTION_PLAY);
                    Log.d(LOG_TAG, "pressed play");

                    playButton.setImageResource(android.R.drawable.ic_media_pause);
                    playPause = false;


                } else {
                    manageMediaPlayer(ACTION_PAUSE);

                    playButton.setImageResource(android.R.drawable.ic_media_play);
                    playPause = true;

                }
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        Log.d(LOG_TAG, "onPause");
        MediaPlayer mediaPlayer = MediaPlayerService.getmMediaPlayer();
        if(mediaPlayer != null) {
            mediaPlayerPosition = mediaPlayer.getCurrentPosition();
        }
        //manageMediaPlayer(ACTION_PAUSE);

        if(mReceiversRegistered) {
            getActivity().unregisterReceiver(mIntentReceiver);
            mReceiversRegistered = false;
        }

        super.onPause();

    }
}
