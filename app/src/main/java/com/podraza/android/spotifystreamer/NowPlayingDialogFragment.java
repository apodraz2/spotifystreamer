package com.podraza.android.spotifystreamer;

import android.app.DialogFragment;
import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
 * Created by adampodraza on 8/10/15.
 */
public class NowPlayingDialogFragment extends DialogFragment {

    private final String LOG_TAG = this.getClass().getSimpleName();

    private MediaPlayer mediaPlayer;
    private boolean playPause = false;


    private ImageButton playButton;
    private SeekBar seekBar;

    private ArrayList<Parcelable> tracks;
    private int position;
    private ParcelableTrack track;

    private View rootView;


    //Error when playing after a pause
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView");

        //args contains the data from toptentracks
        Bundle args = getArguments();

        rootView = inflater.inflate(R.layout.fragment_now_playing, container, false);

        //instantiate the fields for playback
        if(tracks == null) {
            Log.d(LOG_TAG, "tracks equaled null: " + (tracks == null));
            tracks = args.getParcelableArrayList("tracks");
            position = args.getInt("position", 0);
            track = (ParcelableTrack) tracks.get(position);
        }

        rootView = refreshView(track, rootView);

        final String pURL = track.getPreviewUrl();

        mediaPlayer = new MediaPlayer();

        PlaySongTask playSongTask = new PlaySongTask();
        playSongTask.execute(pURL);

        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer != null) {
                    mediaPlayer.pause();
                }
                playPause = true;
                playButton.setImageResource(android.R.drawable.ic_media_play);
                if(position == (tracks.size()-1)) {

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
                if(mediaPlayer != null) {
                    mediaPlayer.pause();
                }
                playPause = true;
                playButton.setImageResource(android.R.drawable.ic_media_play);
                if(position == 0) {

                    position = tracks.size()-1;
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

    void nullifyMediaPlayer() {
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    View refreshView(ParcelableTrack track, View rootView) {

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

        final String pURL = track.getPreviewUrl();

        playButton = (ImageButton) rootView.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (playPause) {
                    if (mediaPlayer == null) {
                        mediaPlayer = new MediaPlayer();
                    }
                    PlaySongTask playSongTask = new PlaySongTask();
                    playSongTask.execute(pURL);
                    playButton.setImageResource(android.R.drawable.ic_media_pause);
                    playPause = false;


                } else {
                    mediaPlayer.pause();

                    playButton.setImageResource(android.R.drawable.ic_media_play);
                    playPause = true;

                }
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        nullifyMediaPlayer();
    }

    //Should have subtitle controller already set?
    class PlaySongTask extends AsyncTask {
        private final String LOG_TAG = this.getClass().getSimpleName();


        @Override
        protected Object doInBackground(Object[] params) {

            Log.d(LOG_TAG, "The preview url is: " + params[0]);

            //Streaming
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource((String) params[0]);

                if(mediaPlayer != null) {
                    mediaPlayer.prepare();
                    mediaPlayer.start();

                    while (mediaPlayer.isPlaying() && mediaPlayer != null) {

                        seekBar.setProgress(mediaPlayer.getCurrentPosition() / 1000);

                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                                mediaPlayer.seekTo(seekBar.getThumbOffset());
                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });

                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            nullifyMediaPlayer();

        }
    }
}
