package com.project.treasurehunt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeoFenceBroadcastReceiver extends BroadcastReceiver {

    String fenceId;
    Integer index;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d("BroadCast", "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList) {
            Log.d("BroadCast", "onReceive: " + geofence.getRequestId());
            fenceId = geofence.getRequestId();
        }

        int transitionType = geofencingEvent.getGeofenceTransition();

        for (int iterator = 0; iterator < GeoFenceUtils.getInstance().addClues().size(); iterator++) {
            if (GeoFenceUtils.getInstance().addClues().get(iterator).getId().equals(fenceId)) {
                index = iterator;
                break;
            }
        }

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                NotificationHelper.getInstance(context).sendHighPriorityNotification("Treasure hunt", "You found a clue" + GeoFenceUtils.getInstance().addClues().get(index).getName(), MainActivity.class);
                break;
        }
    }
}
