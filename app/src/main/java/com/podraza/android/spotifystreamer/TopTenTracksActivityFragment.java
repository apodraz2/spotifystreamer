package com.podraza.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.RestAdapter;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTenTracksActivityFragment extends Fragment {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private SpotifyTrackAdapter mSpotifyAdapter;
    private ListView listView;

    public TopTenTracksActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trackList", mSpotifyAdapter.getTrackList());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final Bundle sIS = savedInstanceState;

        View rootView = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        listView = (ListView) rootView.findViewById(R.id.tracklist_view);


        if(savedInstanceState != null) {

            ArrayList<Parcelable> trackList = savedInstanceState.getParcelableArrayList("trackList");

            mSpotifyAdapter = new SpotifyTrackAdapter(
                    getActivity(),
                    trackList
            );


            listView.setAdapter(mSpotifyAdapter);
        } else {

            mSpotifyAdapter = new SpotifyTrackAdapter(
                    getActivity(),
                    new ArrayList<Parcelable>());

            listView.setAdapter(mSpotifyAdapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NowPlayingActivity.class);

                ParcelableTrack parcelableTrack = (ParcelableTrack) mSpotifyAdapter.getItem(position);
                intent.putExtra(Intent.EXTRA_TEXT, parcelableTrack.getPreviewUrl());

                startActivity(intent);
            }
        });

        if(sIS == null) {
            updateSpotify(getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT));
        }
        return rootView;
    }



    private void updateSpotify(String artistId) {

        QuerySpotifyForTracksTask querySpotifyForTracksTask = new QuerySpotifyForTracksTask();
        querySpotifyForTracksTask.execute(artistId);
    }


    public class QuerySpotifyForTracksTask extends AsyncTask<String, Void, List<Track>> {
        private final String LOG_TAG = QuerySpotifyForTracksTask.class.getSimpleName();

        private SpotifyApi api = null;



        @Override
        protected List<Track> doInBackground(String... params) {

            if (params.length == 0) return null;

            api = new SpotifyApi();

            SpotifyService spotifyService = api.getService();

            Map<String, Object> paramsMap = new TreeMap<>();
            paramsMap.put("country", "US");
            Tracks tracks = spotifyService.getArtistTopTrack(params[0], paramsMap);

            return tracks.tracks;

        }

        //helper method to add artists to the adapter
        private void addTracks(List<Track> tracks) throws MalformedURLException {

            for(Track track : tracks) {
                mSpotifyAdapter.addTrack(track);

            }
        }


        @Override
        protected void onPostExecute(List<Track> tracks) {
            if(tracks == null) {

                Toast toast = Toast.makeText(getActivity(), "Top tracks not found", Toast.LENGTH_SHORT);

                toast.show();

            }

            else{

                listView.setAdapter(mSpotifyAdapter);
                try {
                    addTracks(tracks);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
