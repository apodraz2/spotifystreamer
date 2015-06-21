package com.podraza.android.spotifystreamer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by adampodraza on 6/18/15.
 */
public class SpotifyTrackAdapter extends BaseAdapter {
    private String LOG_TAG = this.getClass().getSimpleName();

    private Context context;
    private List<Track> trackList;


    SpotifyTrackAdapter(Context context, List<Track> trackList) {
        this.context = context;
        this.trackList = trackList;
    }

    @Override
    public int getCount() {

        return trackList.size();
    }

    @Override
    public Track getItem(int position) {
        return trackList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getTrackId(int position) {
        return this.getItem(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View adapterView = convertView;


        if(adapterView == null) {
            adapterView = inflater.inflate(R.layout.list_item_song, parent, false);
        }

        ImageView imageView = (ImageView) adapterView.findViewById(R.id.image_song_view);
        Track track = this.getItem(position);

        if(!(track.album.images == null)){
            Log.d(LOG_TAG, track.album.images.get(0).url);
            try {
                Picasso.with(context).load(Uri.parse(getCorrectImage(track.album.images))).into(imageView);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            Picasso.with(context)
                    .load(Uri.parse("https://upload.wikimedia.org/wikipedia/commons/5/55/Question_Mark.svg?uselang=gsw"))
                    .into(imageView);
        }
        TextView textView = (TextView) adapterView.findViewById((R.id.text_song_view));
        textView.setText(track.name);

        TextView textView2 = (TextView) adapterView.findViewById(R.id.text_album_view);
        textView2.setText(track.album.name);

        return adapterView;
    }

    private String getCorrectImage(List<Image> images) throws MalformedURLException {
        for(Image image : images) {
            if(image.width < 480) {
                return image.url;
            }
        }

        return "https://upload.wikimedia.org/wikipedia/commons/5/55/Question_Mark.svg?uselang=gsw";
    }

    public void clear() {
        this.trackList.clear();
    }

    public void addTrack(Track track) {
        Log.d(LOG_TAG, "6");
        this.trackList.add(track);

    }
}
