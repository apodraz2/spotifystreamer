package com.podraza.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "1");
        mSpotifyAdapter = new SpotifyTrackAdapter(
                getActivity(),
                new ArrayList<Track>()

        );

        View rootView = inflater.inflate(R.layout.fragment_top_ten_tracks, container, false);

        listView = (ListView) rootView.findViewById(R.id.tracklist_view);

        listView.setAdapter(mSpotifyAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), NowPlayingActivity.class);

                intent.putExtra(Intent.EXTRA_TEXT, mSpotifyAdapter.getItem(position).preview_url);

                startActivity(intent);
            }
        });

                updateSpotify(getActivity().getIntent().getStringExtra(Intent.EXTRA_TEXT));
        return rootView;
    }



    private void updateSpotify(String artistId) {
        Log.d(LOG_TAG, "2");

        QuerySpotifyForTracksTask querySpotifyForTracksTask = new QuerySpotifyForTracksTask();
        querySpotifyForTracksTask.execute(artistId);
    }


    public class QuerySpotifyForTracksTask extends AsyncTask<String, Void, List<Track>> {
        private final String LOG_TAG = QuerySpotifyForTracksTask.class.getSimpleName();

        private SpotifyApi api = null;



        @Override
        protected List<Track> doInBackground(String... params) {

            Log.d(LOG_TAG, "3");

            Log.d(LOG_TAG, "The execute method was called with this parameter: " + params[0]);

            if (params.length == 0) return null;

            api = new SpotifyApi();

            SpotifyService spotifyService = api.getService();

            Map<String, Object> paramsMap = new TreeMap<>();
            paramsMap.put("country", "US");
            Tracks tracks = spotifyService.getArtistTopTrack(params[0], paramsMap);

            return tracks.tracks;

        }

        //helper method to add artists to the adapter
        private void addTracks(List<Track> tracks) {
            Log.d(LOG_TAG, "5");
            for(Track track : tracks) {
                mSpotifyAdapter.addTrack(track);

            }
        }


        @Override
        protected void onPostExecute(List<Track> tracks) {

            Log.d(LOG_TAG, "4");
            if(tracks == null) {

                Toast toast = Toast.makeText(getActivity(), "Top tracks not found", Toast.LENGTH_SHORT);

                toast.show();

            }

            else{

                listView.setAdapter(mSpotifyAdapter);
                addTracks(tracks);
            }
        }
    }
}
