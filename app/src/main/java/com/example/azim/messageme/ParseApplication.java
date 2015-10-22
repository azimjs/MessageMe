/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.example.azim.messageme;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class ParseApplication extends Application {
//Ajays changes --2
    private BeaconManager beaconManager;
    static final List<Beacons> BEACONS_LIST = new ArrayList<Beacons>();
    static final Map<String, String> BEACONS_LIST_MAP = new HashMap<>();
    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
            //comment
        }
        return mTracker;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        Parse.initialize(this, "4VzPHycLMTVQHWA5WRPvBwXogmP27iQT9ONvreF4", "MwXELm2tFUTEQBnc7DxXtmBKmCTH2cOeXjnOCFyO");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        //ParseUser.enableAutomaticUser();
        //ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        //defaultACL.setPublicReadAccess(true);
        //ParseACL.setDefaultACL(defaultACL, true);

        loadBeacons();
    }

    private void loadBeacons() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Beacons");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> beacons, ParseException e) {
                //loading.dismiss();
                if (e == null) {
                    Log.d("demo", "Retrieved " + beacons.size() + " beacons");
                    for (ParseObject obj : beacons) {
                        Beacons beacon = new Beacons(obj.getObjectId(), obj.getString("Location"), obj.getString("UUID"), obj.getInt("Major"), obj.getInt("Minor"));
                        BEACONS_LIST.add(beacon);
                        BEACONS_LIST_MAP.put(obj.getInt("Major") + ":" + obj.getInt("Minor"), obj.getString("Location"));
                    }

                } else {
                    Log.d("demo", "Error: " + e.getMessage());
                }
            }
        });
    }
}
