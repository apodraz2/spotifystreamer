package com.podraza.android.spotifystreamer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adampodraza on 6/21/15.
 */
public class ParcelableTrack implements Parcelable {

    String trackName;
    String albumName;
    String imageUrl;
    String previewUrl;

    ParcelableTrack(String trackName, String albumName, String imageUrl, String previewUrl) {
        this.trackName = trackName;
        this.albumName = albumName;
        this.imageUrl = imageUrl;
        this.previewUrl = previewUrl;
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

    }
}
