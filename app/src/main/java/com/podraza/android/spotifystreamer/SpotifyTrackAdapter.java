package com.podraza.android.spotifystreamer;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
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

import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by adampodraza on 6/18/15.
 */
public class SpotifyTrackAdapter extends BaseAdapter {
    private String LOG_TAG = this.getClass().getSimpleName();

    private Context context;
    private ArrayList<Parcelable> trackList;


    SpotifyTrackAdapter(Context context, ArrayList<Parcelable> trackList) {
        this.context = context;
        this.trackList = trackList;
    }

    public ArrayList<Parcelable> getTrackList() {
        return this.trackList;
    }

    @Override
    public int getCount() {

        return trackList.size();
    }

    @Override
    public Parcelable getItem(int position) {

        return trackList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View adapterView = convertView;
        ViewHolder holder;

        if(adapterView == null) {
            adapterView = inflater.inflate(R.layout.list_item_song, parent, false);
            holder = new ViewHolder();
            holder.albumName = (TextView) adapterView.findViewById(R.id.text_album_view);
            holder.trackName = (TextView) adapterView.findViewById(R.id.text_song_view);
            holder.imageView = (ImageView) adapterView.findViewById(R.id.image_song_view);
            adapterView.setTag(holder);
        } else {
            holder = (ViewHolder) adapterView.getTag();
        }

        ImageView imageView = holder.imageView;
        ParcelableTrack track = (ParcelableTrack) this.getItem(position);

        if(!(track.imageUrl == null)){

            Picasso.with(context).load(Uri.parse(track.imageUrl))
                    .resize(200, 200)
                    .into(imageView);
        } else {
            Picasso.with(context)
                    .load(R.drawable.question_mark)
                    .resize(200, 200)
                    .into(imageView);
        }
        TextView textView = holder.trackName;
        textView.setText(track.getTrackName());

        TextView textView2 = holder.albumName;
        textView2.setText(track.getAlbumName());

        return adapterView;
    }

    private String getCorrectImage(List<Image> images) throws MalformedURLException {
        for(Image image : images) {
            if(image.width < 480) {
                return image.url;
            }
        }

        return null;
    }

    public void clear() {
        this.trackList.clear();
    }

    public void addTrack(String artist, Track track) throws MalformedURLException {
        String artistName = track.artists.get(0).name;
        String trackName = track.name;
        String albumName = track.album.name;
        String previewUrl = track.preview_url;
        String imageUrl = getCorrectImage(track.album.images);

        ParcelableTrack parcelableTrack = new ParcelableTrack(trackName, albumName, imageUrl, previewUrl, artistName);

        this.trackList.add(parcelableTrack);

    }

    class ViewHolder {
        TextView trackName;
        TextView albumName;
        ImageView imageView;
    }
}
