package com.podraza.android.spotifystreamer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback {

    //boolean that determines whether the device running the app is a tablet
    private boolean mTwoPane;
    private static final String TRACKSFRAGMENT_TAG = "TFTAG";

    private String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //test if device is tablet
        if(findViewById(R.id.track_list_container) != null) {

            //if device is tablet, set mTwoPane to true
            mTwoPane = true;

            //start new fragment and display it in the second pane
            if(savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.track_list_container, new TopTenTracksActivityFragment(), TRACKSFRAGMENT_TAG)
                        .commit();
            }
        } else {

            //if device is not tablet, set to false
            mTwoPane = false;
        }

        Log.d(LOG_TAG, "Is two-pane: " + mTwoPane);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    //This is the interface callback method
    @Override
    public void onItemSelected(String searchParam) {


        if(mTwoPane) {

            //if the device is a tablet, create argument bundle and send to second pane's
            //fragment
            Bundle args = new Bundle();
            args.putString(Intent.EXTRA_TEXT, searchParam);

            TopTenTracksActivityFragment activity = new TopTenTracksActivityFragment();

            activity.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.track_list_container, activity, TRACKSFRAGMENT_TAG)
                    .commit();

        } else {
            //if the device is not a tablet, simply start a new activity
            Intent intent = new Intent(this, TopTenTracksActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, searchParam);
            startActivity(intent);
        }
    }
}
