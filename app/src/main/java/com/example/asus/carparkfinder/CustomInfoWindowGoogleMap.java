package com.example.asus.carparkfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by ASUS on 21/1/2018.
 */
public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter,GoogleMap.OnInfoWindowClickListener {
    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx){
        this.context=ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view=null;
        try {
            view = ((Activity) context).getLayoutInflater().inflate(R.layout.info_window, null);

            final Carpark cp = (Carpark) marker.getTag();

            String occup= Integer.toString((int)Math.floor(cp.empty_percen))+"% available";

            TextView add = (TextView) view.findViewById(R.id.cpAdd);
            TextView avail = (TextView) view.findViewById(R.id.availLots);
            TextView occupancy = (TextView) view.findViewById(R.id.occupancy);
            TextView nightPark = (TextView) view.findViewById(R.id.night_park);
            TextView freePark = (TextView) view.findViewById(R.id.free_park);

            view.setOnClickListener(new View.OnClickListener() {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+Double.toString(cp.lat)+","+Double.toString(cp.lng));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);
                }
            });

            ImageButton NavigateBut = (ImageButton) view.findViewById(R.id.NavigateButton);

            NavigateBut.setOnClickListener(new View.OnClickListener() {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+Double.toString(cp.lat)+","+Double.toString(cp.lng));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
                    mapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(mapIntent);
                }
            });

            add.setText(cp.address);
            avail.setText(Long.toString(cp.avail_lots) + " lots available");
            occupancy.setText(occup);
            if (cp.night_park) {
                nightPark.setText("Night parking available");
            }else{
                nightPark.setText("Night parking not available");
            }
            freePark.setText("Free parking: "+ cp.free_parking);

        }catch (Exception e){
            String t = e.getMessage();
        }
        return view;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        final Carpark cp = (Carpark) marker.getTag();
        if (cp != null) {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Double.toString(cp.lat) + "," + Double.toString(cp.lng));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show();
            mapIntent.setPackage("com.google.android.apps.maps");
            context.startActivity(mapIntent);
        }
    }
}
