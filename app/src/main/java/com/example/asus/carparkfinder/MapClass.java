package com.example.asus.carparkfinder;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by ASUS on 10/9/2017.
 */
public class MapClass implements GoogleMap.OnInfoWindowClickListener{
    GoogleMap defaultMap;
    Context context;
    final Integer ZOOM = 15;
    private final Integer MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LocationManager mLocationManager;
    private SupportMapFragment mapFragment;
    private AlertDialog dlg;
    private boolean ableToGetLoc;
    private Marker locMarker;
    private Marker searchMarker;
    BitmapDescriptor locIcon, cpIcon, navIcon;
    private int minSpace,maxCPToShow;
    private double maxOccupancy;
    private long maxDist;

    public void changeFilters(int mins,int maxcpshow,double maxoccu,long maxd){
        this.minSpace =mins;
        this.maxCPToShow = maxcpshow;
        this.maxOccupancy =maxoccu;
        this.maxDist=maxd;
    }

    public void create(GoogleMap map, Boolean ableToGetLocB, Context ctx) {
        this.context = ctx;
        this.defaultMap = map;
        this.defaultMap.setOnInfoWindowClickListener(this);
        this.ableToGetLoc = ableToGetLocB;
        this.locMarker=null;
        this.searchMarker=null;
        locIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_cur_loc);
        navIcon = BitmapDescriptorFactory.fromResource(R.mipmap.ic_nav2);
        cpIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_carpark);

        //changeFilters(50, 10,80.0,10);
    }

    public void clearMap(){
        defaultMap.clear();
    }

    public void getCurLocOnMap(LatLng location, boolean zoom, float bearing) {

        if (location != null) {
            onMapReady(defaultMap, location, bearing);
            if (zoom){
                defaultMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM));
            }
        }else{
            //Toast.makeText(context, "Location not detected.", Toast.LENGTH_SHORT).show();
        }
    }

    public void plotCp(Carpark cp){
        String add = cp.address;
        double lat = cp.lat;
        double lng = cp.lng;
        long lotsAvail=cp.avail_lots;
        int availPercen = (int)Math.round(cp.empty_percen);
        int hue = 360-(availPercen*2);

        String desc = "Available lots: " + Long.toString(lotsAvail) +" Empty percentage: "+ Double.toString(availPercen);


        LatLng loc = new LatLng(lat,lng);

        plotPoint(defaultMap, loc, add, desc, hue, cp);
    }

    private void plotPoint(GoogleMap googleMap, LatLng pos, String add, String desc, int hue, Carpark cp){
        try {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(pos)
                    .snippet(desc)
                    .title(add)
                    .alpha(0.9f)
                    .icon(BitmapDescriptorFactory.defaultMarker(hue));

            Marker marker = googleMap.addMarker(markerOptions);
            marker.setTag(cp);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateToSearch(LatLng location){
        defaultMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, ZOOM));
    }

    private void onMapReady(GoogleMap googleMap, LatLng location, float bearing) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.

        LatLng curPos = location;
        if (locMarker!=null){
            locMarker.remove();
        }
        locMarker=googleMap.addMarker(new MarkerOptions()
                .position(curPos)
                .title("Current Location")
                .icon(navIcon)
                .rotation(bearing));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        final Carpark cp = (Carpark) marker.getTag();
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+Double.toString(cp.lat)+","+Double.toString(cp.lng));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }
}

