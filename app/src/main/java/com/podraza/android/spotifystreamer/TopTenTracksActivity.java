package com.podraza.android.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class TopTenTracksActivity extends ActionBarActivity {
    private String LOG_TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_ten_tracks);

        Log.d(LOG_TAG, "savedInstanceState is null: " + (savedInstanceState == null));

        //if device is a tablet, activity creates new bundle with the artistId argument passed
        //from main activity and updates the fragment with the artistID
        if(savedInstanceState == null && getResources().getBoolean(R.bool.isTablet)) {

            Bundle arguments = new Bundle();
            arguments.putString(Intent.EXTRA_TEXT, getIntent().getStringExtra(Intent.EXTRA_TEXT));

            TopTenTracksActivityFragment fragment = new TopTenTracksActivityFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.track_list_container, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_ten_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
