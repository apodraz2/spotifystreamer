package com.podraza.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by adampodraza on 6/21/15.
 */
public class ParcelableTrack implements Parcelable {
    private final String LOG_TAG = this.getClass().getSimpleName();

    String artistName;
    String trackName;
    String albumName;
    String imageUrl;
    String previewUrl;

    ParcelableTrack(String trackName, String albumName, String imageUrl, String previewUrl, String artistName) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.imageUrl = imageUrl;
        this.previewUrl = previewUrl;
        this.artistName = artistName;
    }
    ParcelableTrack (Parcel source){
      /*
       * Reconstruct from the Parcel. Keep same order as in writeToParcel()
       */
        trackName = source.readString();
        Log.d(LOG_TAG, trackName);
        artistName = source.readString();
        Log.d(LOG_TAG, artistName);
        imageUrl = source.readString();
        Log.d(LOG_TAG, imageUrl);
        previewUrl = source.readString();
        Log.d(LOG_TAG, previewUrl);
        albumName = source.readString();
        Log.d(LOG_TAG, albumName);
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackName);
        dest.writeString(albumName);
        dest.writeString(imageUrl);
        dest.writeString(previewUrl);
        dest.writeString(artistName);

    }

    public static final Parcelable.Creator<ParcelableTrack> CREATOR
            = new Parcelable.Creator<ParcelableTrack>() {
        public ParcelableTrack createFromParcel(Parcel in) {
            return new ParcelableTrack(in);
        }

        public ParcelableTrack[] newArray(int size) {
            return new ParcelableTrack[size];
        }
    };
}
