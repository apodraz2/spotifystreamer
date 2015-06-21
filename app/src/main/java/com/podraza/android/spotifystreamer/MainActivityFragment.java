package com.podraza.android.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();

    SpotifyArtistAdapter mArtistAdapter;


    public MainActivityFragment() {
    }

    public void updateSpotify (String artist) {
        QuerySpotifyForArtistsTask querySpotifyTask = new QuerySpotifyForArtistsTask();

        querySpotifyTask.execute(artist);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mArtistAdapter = new SpotifyArtistAdapter(
                getActivity(),
                new ArrayList<Artist>());

        ListView listView = (ListView) rootView.findViewById(R.id.artist_listView);
        listView.setAdapter(mArtistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Intent intent = new Intent(getActivity(), TopTenTracksActivity.class);

                                                intent.putExtra(Intent.EXTRA_TEXT, mArtistAdapter.getArtistId(position));

                                                startActivity(intent);
                                            }
                                        }

        );

        final EditText editText = (EditText) rootView.findViewById(R.id.edit_artist_text);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if(!(s.length() == 0)) {

                    Log.d(LOG_TAG, s.toString());

                    updateSpotify(s.toString());
                }

                else {
                    Toast toast = Toast.makeText(getActivity(), "Empty query", Toast.LENGTH_SHORT);

                    toast.show();

                }

            }
        });





        return rootView;
    }

    /**
     * Created by adampodraza on 6/12/15.
     * next task: display the spotify json objects
     * in a pretty way!
     */
    public class QuerySpotifyForArtistsTask extends AsyncTask<String, Void, List<Artist>> {
        private final String LOG_TAG = QuerySpotifyForArtistsTask.class.getSimpleName();

        private SpotifyApi api = null;

        @Override
        protected List<Artist> doInBackground(String... params) {

            Log.d(LOG_TAG, "The execute method was called with this parameter: " + params[0]);

            if (params.length == 0) return null;

            api = new SpotifyApi();

            SpotifyService spotifyService = api.getService();

            ArtistsPager artistsPager = spotifyService.searchArtists(params[0]);

            Pager<Artist> pager = artistsPager.artists;



            return pager.items;

        }

        //helper method to add artists to the adapter
        private void addArtists(List<Artist> artists) {
            for(Artist artist : artists) {
                mArtistAdapter.addArtist(artist);

            }
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {


            if(artists == null) {

                Toast toast = Toast.makeText(getActivity(), "Artist not found", Toast.LENGTH_SHORT);

                toast.show();

            }

            else{
                mArtistAdapter.clear();
                addArtists(artists);
            }
        }
    }
}
