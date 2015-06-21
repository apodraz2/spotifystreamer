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
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private SpotifyArtistAdapter mArtistAdapter;

    private ListView listView;

    private Toast toast;


    public MainActivityFragment() {
    }

    public void updateSpotify (String artist) {
        QuerySpotifyForArtistsTask querySpotifyTask = new QuerySpotifyForArtistsTask();

        querySpotifyTask.execute(artist);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("artistList", mArtistAdapter.getSpotifyList());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Log.d(LOG_TAG, "onCreateView called");

        final Bundle sIS = savedInstanceState;
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.artist_listView);

        if(savedInstanceState != null) {
            //Log.d(LOG_TAG, "savedInstanceState was not null");

            ArrayList<Parcelable> artistList = savedInstanceState.getParcelableArrayList("artistList");

            //Log.d(LOG_TAG, "the artistList size is: " + artistList.size());

            mArtistAdapter = new SpotifyArtistAdapter(
                    getActivity(),
                    artistList
            );


            listView.setAdapter(mArtistAdapter);
        } else {

            mArtistAdapter = new SpotifyArtistAdapter(
                    getActivity(),
                    new ArrayList<Parcelable>());

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

                if((s.length() > 3) && (sIS == null)) {

                    Log.d(LOG_TAG, s.toString());

                    updateSpotify(s.toString());
                }

                else {

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
            ArtistsPager artistsPager = null;

            Log.d(LOG_TAG, "The execute method was called with this parameter: " + params[0]);

            if (params.length == 0) return null;

            api = new SpotifyApi();
            SpotifyService spotifyService = api.getService();

            try {

                artistsPager = spotifyService.searchArtists(params[0]);

            } catch(RetrofitError retrofitError) {
                Log.e(LOG_TAG, retrofitError.toString());
            }

            if(artistsPager != null) {


                Pager<Artist> pager = artistsPager.artists;
                return pager.items;
            }

            else {
                if (toast != null)
                    toast.cancel();

                toast.makeText(getActivity(), "Artist not found, please refine search.", Toast.LENGTH_SHORT);
                toast.show();

                return null;
            }

        }

        //helper method to add artists to the adapter
        private void addArtists(List<Artist> artists) throws MalformedURLException {
            for(Artist artist : artists) {
                mArtistAdapter.addArtist(artist);

            }
        }

        @Override
        protected void onPostExecute(List<Artist> artists) {


            if(artists.size() == 0) {
                if(toast != null) {
                    toast.cancel();
                }
                mArtistAdapter.clear();
                toast = Toast.makeText(getActivity(), "Artist not found, please refine search.", Toast.LENGTH_SHORT);

                toast.show();

            }

            else{
                listView = (ListView) getActivity().findViewById(R.id.artist_listView);
                mArtistAdapter.clear();
                listView.setAdapter(mArtistAdapter);
                try {
                    addArtists(artists);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
