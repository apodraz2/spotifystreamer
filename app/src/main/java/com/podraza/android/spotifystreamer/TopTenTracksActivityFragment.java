package com.podraza.android.spotifystreamer;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import java.net.UnknownHostException;
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
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class TopTenTracksActivityFragment extends Fragment {

    private final String LOG_TAG = this.getClass().getSimpleName();
    private SpotifyTrackAdapter mSpotifyAdapter;
    private ListView listView;
    static final String TRACKS_URI = "URI";

    private Uri mUri;

    public TopTenTracksActivityFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trackList", mSpotifyAdapter.getTrackList());
        super.onSaveInstanceState(outState);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(!getResources().getBoolean(R.bool.isTablet)) {
            Intent intent = new Intent(getActivity(), MediaPlayerService.class);
            intent.setAction("PAUSE");
            getActivity().startService(intent);
        }
        String searchParam = null;
        Bundle args = getArguments();

        Log.d(LOG_TAG, "args is null: " + (args == null));

        if(args != null) {

            searchParam = args.getString(Intent.EXTRA_TEXT);
            Log.d(LOG_TAG, "searchParam is: " + searchParam);
        }


        View rootView = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        listView = (ListView) rootView.findViewById(R.id.tracklist_view);

        Log.d(LOG_TAG, "savedInstanceState is null: " + (savedInstanceState == null));

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

                if(!isNetworkAvailable()) {
                    Toast toast = Toast.makeText(
                            getActivity().getApplicationContext(),
                            "No network connection, please try again",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {

                    //detect whether device is tablet
                    if (getResources().getBoolean(R.bool.isTablet)) {

                        //create a new bundle and put the data necessary for playback in it
                        Bundle args = new Bundle();
                        ArrayList<Parcelable> tracks = mSpotifyAdapter.getTrackList();
                        args.putParcelableArrayList("tracks", tracks);
                        args.putInt("position", position);

                        //instantiate new dialog fragment
                        DialogFragment dialogFragment = new NowPlayingDialogFragment();

                        dialogFragment.setArguments(args);

                        //show the dialog fragment on a tablet
                        dialogFragment.show(getActivity().getFragmentManager(), "dialog");

                    } else {
                        //if the device is not a tablet, simply start and create a new activity
                        Intent intent = new Intent(getActivity(), NowPlayingActivity.class);

                        ArrayList<Parcelable> tracks = mSpotifyAdapter.getTrackList();
                        intent.putParcelableArrayListExtra("tracks", tracks);
                        intent.putExtra("position", position);

                        startActivity(intent);
                    }
                }
            }
        });

        if(getResources().getBoolean(R.bool.isTablet)) {
            if(searchParam == null && savedInstanceState == null) {
                //if the device is a tablet but there is no searchparam do nothing.
            } else if(savedInstanceState == null && searchParam != null)  {
                //otherwise, call the updatespotify method with the searchparam
                updateSpotify(searchParam);
            } else {
                //if savedInstanceState is not equal to null do nothing
            }
        } else {
            if (savedInstanceState == null) {
                //if the device is not a tablet, call update spotify with the passed intent
                updateSpotify(getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT));

            }
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

        String artist = null;

        @Override
        protected List<Track> doInBackground(String... params) {
            Tracks tracks = null;
            if (params.length == 0) return null;

            api = new SpotifyApi();

            SpotifyService spotifyService = api.getService();

            Map<String, Object> paramsMap = new TreeMap<>();
            paramsMap.put("country", "US");

            if(params[0] == null) {
                artist = "4gzpq5DPGxSnKTe4SA8HAU";
            } else {
                artist = params[0];
            }
            try {
                 tracks = spotifyService.getArtistTopTrack(artist, paramsMap);
            } catch (RuntimeException error) {
                Log.e(LOG_TAG, error.toString());
            }

            return tracks.tracks;

        }

        //helper method to add artists to the adapter
        private void addTracks(List<Track> tracks) throws MalformedURLException {

            for(Track track : tracks) {
                mSpotifyAdapter.addTrack(artist, track);

            }
        }


        @Override
        protected void onPostExecute(List<Track> tracks) {
            if(tracks == null || tracks.size() == 0) {

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
