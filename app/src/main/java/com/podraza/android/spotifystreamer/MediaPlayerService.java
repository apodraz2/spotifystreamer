package com.podraza.android.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by adampodraza on 8/11/15.
 */
public class MediaPlayerService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
