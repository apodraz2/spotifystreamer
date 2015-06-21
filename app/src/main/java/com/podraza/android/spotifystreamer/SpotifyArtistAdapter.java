package com.podraza.android.spotifystreamer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
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
    private ArrayList<Parcelable> spotifyList;



    SpotifyArtistAdapter(Context context, ArrayList<Parcelable> spotifyList) {
        this.context = context;
        this.spotifyList = spotifyList;

    }

    public ArrayList<Parcelable> getSpotifyList() {
        return this.spotifyList;
    }

    @Override
    public int getCount() {
        return spotifyList.size();
    }

    @Override
    public Parcelable getItem(int position) {
        return spotifyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getArtistId(int position) {
        ParcelableArtist artist = (ParcelableArtist) this.getItem(position);
        return artist.getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View adapterView = convertView;
        ViewHolder holder;

        if(adapterView == null) {
            adapterView = inflater.inflate(R.layout.list_item_artist, null);
            holder = new ViewHolder();
            holder.artistName = (TextView) adapterView.findViewById(R.id.text_artist_view);
            holder.artistPic = (ImageView) adapterView.findViewById(R.id.image_artist_view);

            adapterView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageView imageView = holder.artistPic;
        ParcelableArtist artist = (ParcelableArtist) this.getItem(position);
        if ((artist.getImageUrl() != null)) {
            Picasso.with(context).load(Uri.parse(artist.getImageUrl()))
                    .resize(200, 200)
                    .into(imageView);
        }else {

            Picasso.with(context)
                    .load(R.drawable.question_mark)
                    .resize(200, 200)
                    .into(imageView);
        }
        TextView textView = holder.artistName;
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
        String artistName = artist.name;
        String imageUrl = getCorrectImage(artist.images);
        String artistId = artist.id;
        ParcelableArtist parcelableArtist = new ParcelableArtist(artistName,artistId, imageUrl);

        this.spotifyList.add(parcelableArtist);
    }

    class ViewHolder {
        TextView artistName;
        ImageView artistPic;
    }

}
