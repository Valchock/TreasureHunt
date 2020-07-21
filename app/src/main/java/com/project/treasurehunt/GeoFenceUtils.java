package com.project.treasurehunt;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class GeoFenceUtils {

    private static GeoFenceUtils instance;
    PendingIntent pendingIntent;

    public static GeoFenceUtils getInstance() {
        if (instance == null) {
            instance = new GeoFenceUtils();
        }
        return instance;
    }

    public ArrayList<GeoFenceClueData> addClues() {
        ArrayList<GeoFenceClueData> landMarkData = new ArrayList<>();
        GeoFenceClueData clueOne = new GeoFenceClueData("golden_gate_bridge", "Go to a bridge with a name that does not match the color that it is!", "at the Golden Gate bridge", 37.819927, -122.478256);
        GeoFenceClueData clueTwo = new GeoFenceClueData("ferry_building", "Go to a market with an amazing assortment of delicious foods. It is a San Francisco classic!", "in the Ferry Building", 37.795490, -122.394276);
        GeoFenceClueData clueThree = new GeoFenceClueData("pier_39", "Go to a pier popular for tourist attractions, carnival games, fresh fish, and delicious sourdough bread!", "at Pier 39", 37.808674, -122.409821);
        GeoFenceClueData clueFour = new GeoFenceClueData("union_square", "Go to a square named after Civil War rallies held there, but now known for its amazing shopping!", "in Union Square", 37.788151, -122.407570);
        GeoFenceClueData clueFive = new GeoFenceClueData("", "Congratulations !!! won", "", 0, 0);
        landMarkData.add(clueOne);
        landMarkData.add(clueTwo);
        landMarkData.add(clueThree);
        landMarkData.add(clueFour);
        landMarkData.add(clueFive);
        return landMarkData;
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    public Geofence getGeofence(String ID, LatLng latLng, float radius, int transitionTypes) {
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(ID)
                .setTransitionTypes(transitionTypes)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();
    }

    public PendingIntent getPendingIntent(Context context) {
        if (pendingIntent != null) {
            return pendingIntent;
        }
        Intent intent = new Intent(context, GeoFenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 2607, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    public String getErrorString(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            }
        }
        return e.getLocalizedMessage();
    }
}
