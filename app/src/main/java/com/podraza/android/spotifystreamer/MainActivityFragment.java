package com.podraza.android.spotifystreamer;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Parcelable;
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

import java.net.MalformedURLException;
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

    private SpotifyArtistAdapter mArtistAdapter;
    private Toast toast;
    private ListView listView;


    public MainActivityFragment() {
    }

    public void updateSpotify (String artist) {
        QuerySpotifyForArtistsTask querySpotifyTask = new QuerySpotifyForArtistsTask();

        querySpotifyTask.execute(artist);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        List<ParcelableArtist> artists = mArtistAdapter.getArtists();
        outState.putParcelableArrayList("artists", (ArrayList<? extends Parcelable>) artists);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        Log.d(LOG_TAG, "the saved instance state is null: " + (savedInstanceState == null));
        if(savedInstanceState != null) {
            ArrayList<ParcelableArtist> artists = savedInstanceState.getParcelableArrayList("artists");
            mArtistAdapter = new SpotifyArtistAdapter(
                    getActivity(),
                    artists);
        } else {

            mArtistAdapter = new SpotifyArtistAdapter(
                    getActivity(),
                    new ArrayList<ParcelableArtist>());
            listView = (ListView) rootView.findViewById(R.id.artist_listView);

            listView.setAdapter(mArtistAdapter);
        }
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


                    if (s.length() != 0) {

                        Log.d(LOG_TAG, s.toString());

                        updateSpotify(s.toString());
                    } else {
                        if (toast != null)
                            toast.cancel();

                        toast = Toast.makeText(getActivity(), "Empty query", Toast.LENGTH_SHORT);

                        toast.show();

                    }

                }
            });




        return rootView;
    }

    /**
     * Created by adampodraza on 6/12/15.
     *
     */
    public class QuerySpotifyForArtistsTask extends AsyncTask<String, Void, List<Artist>> {
        private final String LOG_TAG = QuerySpotifyForArtistsTask.class.getSimpleName();

        private SpotifyApi api = null;

        @Override
        protected List<Artist> doInBackground(String... params) {

            if (params.length == 0) return null;

            api = new SpotifyApi();

            SpotifyService spotifyService = api.getService();

            ArtistsPager artistsPager = spotifyService.searchArtists(params[0]);

            Pager<Artist> pager = artistsPager.artists;



            return pager.items;

        }

        //helper method to add artists to the adapter
        private void addArtists(List<Artist> artists) throws MalformedURLException {
            for(Artist artist : artists) {
                mArtistAdapter.addArtist(artist);

            }
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {


            if(artists.isEmpty()) {
                if(toast != null)
                    toast.cancel();

                toast = Toast.makeText(getActivity(), "Artist not found, please refine search.", Toast.LENGTH_SHORT);

                toast.show();

            }

            else{
                mArtistAdapter.clear();
                try {
                    addArtists(artists);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
