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

/**
 * Created by adampodraza on 6/16/15.
 */
public class SpotifyArtistAdapter extends BaseAdapter {
    private String LOG_TAG = SpotifyArtistAdapter.class.getSimpleName();

    private Context context;
    private ArrayList<ParcelableArtist> spotifyList;



    SpotifyArtistAdapter(Context context, ArrayList<ParcelableArtist> spotifyList) {
        this.context = context;
        this.spotifyList = spotifyList;

    }

    public ArrayList<ParcelableArtist> getArtists() {
        return this.spotifyList;
    }

    public void setArtists(ArrayList<ParcelableArtist> artists) {
        this.spotifyList = artists;
    }

    @Override
    public int getCount() {
        return spotifyList.size();
    }

    @Override
    public ParcelableArtist getItem(int position) {
        return spotifyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getArtistId(int position) {
        ParcelableArtist artist = this.getItem(position);
        return artist.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View adapterView = convertView;

        if(adapterView == null) {
                adapterView = inflater.inflate(R.layout.list_item_artist, null);
        }

        ImageView imageView = (ImageView) adapterView.findViewById(R.id.image_artist_view);
        ParcelableArtist artist = this.getItem(position);
        if ((artist.getImageUrl() != null)) {
                Picasso.with(context).load(Uri.parse(artist.getImageUrl())).into(imageView);

        }else {
            Log.d(LOG_TAG, "the url was null");
            Picasso.with(context)
                    .load(R.drawable.question_mark)
                    .resize(200, 200)
                    .into(imageView);
        }
        TextView textView = (TextView) adapterView.findViewById(R.id.text_artist_view);
        textView.setText(artist.getName());


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
        spotifyList.clear();
    }

    public void addArtist(Artist artist) throws MalformedURLException {
        String url;
        if(artist.images != null)
             url = getCorrectImage(artist.images);
        else
            url = null;

        this.spotifyList.add(new ParcelableArtist(artist.name, artist.id, url));
    }

}
