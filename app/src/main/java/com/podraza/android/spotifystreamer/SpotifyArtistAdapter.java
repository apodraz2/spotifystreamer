package com.podraza.android.spotifystreamer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by adampodraza on 6/16/15.
 */
public class SpotifyArtistAdapter extends BaseAdapter {
    private String LOG_TAG = SpotifyArtistAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<Artist> spotifyList;



    SpotifyArtistAdapter(Context context, ArrayList<Artist> spotifyList) {
        this.context = context;
        this.spotifyList = spotifyList;

    }

    @Override
    public int getCount() {
        return spotifyList.size();
    }

    @Override
    public Artist getItem(int position) {
        return spotifyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getArtistId(int position) {
        Artist artist = this.getItem(position);
        return artist.id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View adapterView = convertView;

        if(adapterView == null) {
                adapterView = inflater.inflate(R.layout.list_item_artist, null);
        }

        ImageView imageView = (ImageView) adapterView.findViewById(R.id.image_artist_view);
        Artist artist = this.getItem(position);
        if (!(artist.images == null)) {
            try {
                Picasso.with(context).load(Uri.parse(getCorrectImage(artist.images))).into(imageView);
            } catch (MalformedURLException e) {
                    e.printStackTrace();
            }
        }
        TextView textView = (TextView) adapterView.findViewById(R.id.text_artist_view);
        textView.setText(artist.name);


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
        spotifyList.clear();
    }

    public void addArtist(Artist artist) {

        this.spotifyList.add(artist);
    }

}
