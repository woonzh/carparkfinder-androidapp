package com.example.asus.carparkfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ASUS on 11/9/2017.
 */
public class LocationClass implements LocationListener{
    LocationManager locationManager;
    Context appContext;
    String providerName;
    Location oldLoc;

    LocationClass(Context ctx) {
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        this.appContext = ctx;

        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }else {
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {
            } else {
                if (isNetworkEnabled && isGPSEnabled) {
                    Criteria locationCritera = new Criteria();
                    locationCritera.setAccuracy(Criteria.ACCURACY_COARSE);
                    locationCritera.setAltitudeRequired(false);
                    locationCritera.setBearingRequired(true);
                    locationCritera.setPowerRequirement(Criteria.NO_REQUIREMENT);

                    providerName = locationManager.getBestProvider(locationCritera, true);
                    locationManager.requestLocationUpdates(providerName, 1000, 0, this);

                } else {
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                    }
                    if (isGPSEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
                    }
                }
            }
        }
    }

    public Location getLastLocation() {
        Location location = null;
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return location;
        }else {
            location = locationManager.getLastKnownLocation(providerName);
            if (location ==null){
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            this.oldLoc=location;
        }
                    //location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            return location;
    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng loc = new LatLng(location.getLatitude(),location.getLongitude());

        float bearing = oldLoc.bearingTo(location);

        MainActivity.globalCurLoc =loc;
        oldLoc=location;

        MainActivity.map.getCurLocOnMap(loc, MainActivity.mapMoveSwitch.isChecked(), bearing);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLastLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(appContext, "Location function is disabled. You will be directed to turn it on.", Toast.LENGTH_SHORT).show();
        new CountDownTimer(2000, 2000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                appContext.startActivity(intent);
            }
        }.start();
    }

    public double distance(LatLng loc1, LatLng loc2) {
        double theta = loc1.longitude - loc2.longitude;
        double dist = Math.sin(deg2rad(loc1.latitude))
                * Math.sin(deg2rad(loc2.latitude))
                + Math.cos(deg2rad(loc1.latitude))
                * Math.cos(deg2rad(loc2.latitude))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
