package com.project.treasurehunt;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int FINE_LOCATION_ACCESS_REQUEST_CODE = 1;
    private static final int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 2;
    @BindView(R.id.clue_txt)
    TextView clueTxt;
    @BindView(R.id.img_id)
    ImageView imgId;
    ArrayList<GeoFenceClueData> geoFenceClueDataArrayList;
    GeoFenceViewModel geoFenceViewModel;
    private GeofencingClient geofencingClient;
    private float radius = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        geofencingClient = LocationServices.getGeofencingClient(this);
        geoFenceViewModel = ViewModelProviders.of(this).get(GeoFenceViewModel.class);
        observeGeoFenceLiveData();
        System.out.println("oncreate");
    }

    private void observeGeoFenceLiveData() {
        System.out.println("onObserve");
        geoFenceViewModel.getCurrentClue().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer < 4) {
                    createGeoFenceRequestForCurrentClue(geoFenceClueDataArrayList.get(integer));
                } else {
                    clueTxt.setText(geoFenceClueDataArrayList.get(integer).getHint());
                    imgId.setImageResource(R.drawable.android_treasure);
                }

            }
        });
    }

    private void createGeoFenceRequestForCurrentClue(GeoFenceClueData geoFenceClueData) {
        clueTxt.setText(geoFenceClueData.getHint());
        LatLng locationLatLng = new LatLng(geoFenceClueData.getLat(), geoFenceClueData.getLng());
        Geofence geofence = GeoFenceUtils.getInstance().getGeofence(geoFenceClueData.getId(), locationLatLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER);
        GeofencingRequest geofencingRequest = GeoFenceUtils.getInstance().getGeofencingRequest(geofence);
        PendingIntent pendingIntent = GeoFenceUtils.getInstance().getPendingIntent(this);

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("MainActivity", "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = GeoFenceUtils.getInstance().getErrorString(e);
                        Log.d("MainActivity", "onFailure: " + errorMessage);
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        requestForeGroundPermission();
    }

    private void requestForeGroundPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            boolean versionQ = checkIfVersionGreaterThanQ();
            if (versionQ) {
                requestBackGroundPermission();
            } else {
                addGeoFenceClue();
            }
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    private void requestBackGroundPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            addGeoFenceClue();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                //We show a dialog and ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                boolean versionQ = checkIfVersionGreaterThanQ();
                if (versionQ) {
                    requestBackGroundPermission();
                } else {
                    addGeoFenceClue();
                }
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addGeoFenceClue();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addGeoFenceClue() {
        geoFenceClueDataArrayList = GeoFenceUtils.getInstance().addClues();
    }

    private boolean checkIfVersionGreaterThanQ() {
        return Build.VERSION.SDK_INT >= 29;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        System.out.println("onnew");
        super.onNewIntent(intent);
        geoFenceViewModel.updateClue();
    }
}
