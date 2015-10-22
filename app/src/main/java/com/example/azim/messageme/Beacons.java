package com.example.azim.messageme;

/**
 * Created by Azim on 10/3/2015.
 */
public class Beacons {
    String objectId, location, uuid;
    Integer major, minor;

    public Beacons() {
    }

    public Beacons(String objectId, String location, String uuid, Integer major, Integer minor) {
        this.objectId = objectId;
        this.location = location;
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
    }

    public static Beacons getBeacon(final String objectId){
        final Beacons beacon = new Beacons();

        for(Beacons b : ParseApplication.BEACONS_LIST){
            if(b.getObjectId().equals(objectId)){
                return b;
            }
        }
/*
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Beacons");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                // something went wrong
                if (e == null) {
                    // object will be your game score
                    Log.d("demo", "beacon class parsing success");
                    beacon.setLocation(object.getString("location"));
                    beacon.setUuid(object.getString("uuid"));
                    beacon.setMajor(object.getInt("major"));
                    beacon.setMinor(object.getInt("minor"));
                } else
                    Log.d("demo", "beacon class parsing error");
            }
        });
        Log.d("demo", "beacon class: " + beacon.toString() );
*/
        return beacon;
    }

    public String getObjectId() {

        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    @Override
    public String toString() {
        return "Beacons{" +
                "objectId='" + objectId + '\'' +
                ", location='" + location + '\'' +
                ", uuid='" + uuid + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                '}';
    }
}
