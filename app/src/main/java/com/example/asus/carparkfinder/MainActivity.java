package com.example.asus.carparkfinder;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Vector;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    GoogleMap defaultMap;
    final Integer ZOOM = 15;
    private final Integer MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private LocationManager mLocationManager;
    private SupportMapFragment mapFragment;
    private AlertDialog dlg;
    private boolean ableToGetLoc = true;
    public static MapClass map;
    public static LatLng globalCurLoc;
    private LocationClass locationfinder;
    private APICall apicall;
    public String result2;
    public Geocoder geocoder;
    CustomInfoWindowGoogleMap gmapinfo;
    public boolean trackOn;
    public DrawerLayout drawer;
    public NavigationView navigationView;
    public View headerLayout;
    public SeekBar availLotsSeek;
    public SeekBar occupancySeek;
    public static SharedPreferences sharedPref;
    public static SharedPreferences.Editor sharedPrefEditor;
    public static Switch nightParkSwitch, freeParkSwitch, mapMoveSwitch;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPref = getSharedPreferences(
                getString(R.string.CPFinder_Shared_Preferences), Context.MODE_PRIVATE);
        sharedPrefEditor = sharedPref.edit();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //final ListView mDrawerListmDrawerList = (ListView) findViewById(R.id.left_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getPermission(this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set the current location button
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ableToGetLoc) {
                    curLocOnMap();
                } else {
                    Toast.makeText(getApplicationContext(), "Location permission not granted. Restart app and allow permissions.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        RadioButton radBut = (RadioButton) findViewById(R.id.searchBut);
        final EditText locEnt = (EditText) findViewById(R.id.location);
        radBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loc = locEnt.getText().toString();
                Toast.makeText(getApplicationContext(), loc, Toast.LENGTH_SHORT).show();
                showSearchLoc(loc);
            }
        });

        headerLayout = navigationView.getHeaderView(0);

        availLotsSeek = (SeekBar) headerLayout.findViewById(R.id.spaceSeek);
        final TextView availLotsText = (TextView) headerLayout.findViewById(R.id.lotsAvail);
        availLotsText.setText(Integer.toString(availLotsSeek.getProgress()) + " lots");
        availLotsSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                availLotsText.setText(Integer.toString(progress) + " Lots");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        occupancySeek = (SeekBar) headerLayout.findViewById(R.id.occupancySeek);
        final TextView occupancyText = (TextView) headerLayout.findViewById(R.id.occupancy);
        occupancyText.setText(Integer.toString(occupancySeek.getProgress()) + "% available");
        occupancySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                occupancyText.setText(Integer.toString(progress) + "% available");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        nightParkSwitch = (Switch) headerLayout.findViewById(R.id.nightParkSwitch);
        freeParkSwitch = (Switch) headerLayout.findViewById(R.id.freeParkSwitch);
        mapMoveSwitch = (Switch) headerLayout.findViewById(R.id.mapMoveSwitch);

        final Button filConfirm = (Button) headerLayout.findViewById(R.id.selConfirm);
        filConfirm.setOnClickListener(new NavigationView.OnClickListener() {

            @Override
            public void onClick(View v) {
                int availPref, freePercenPref;
                boolean nightPark, freePark, mapMove;

                availPref = availLotsSeek.getProgress();
                freePercenPref = occupancySeek.getProgress();

                nightPark = nightParkSwitch.isChecked();
                freePark = freeParkSwitch.isChecked();
                mapMove = mapMoveSwitch.isChecked();

                sharedPrefEditor.clear();
                sharedPrefEditor.putInt(getString(R.string.lotsAvailPref),availPref);
                sharedPrefEditor.putInt(getString(R.string.availPercentagePref),freePercenPref);
                sharedPrefEditor.putBoolean(getString(R.string.night_park),nightPark);
                sharedPrefEditor.putBoolean(getString(R.string.free_park),freePark);
                sharedPrefEditor.putBoolean(getString(R.string.map_move),mapMove);
                sharedPrefEditor.commit();

                drawer.closeDrawer(navigationView);
                curLocOnMap();
            }
        });

        final Button filCancel = (Button) headerLayout.findViewById(R.id.selCancel);
        filCancel.setOnClickListener(new NavigationView.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDrawer();
            }
        });

        try {
            setDrawer();
            setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setDrawer(){
        drawer.closeDrawer(navigationView);
        int availPref, freePercenPref;
        boolean nightPark, freePark, mapMove;

        availPref = sharedPref.getInt(getString(R.string.lotsAvailPref),1);
        freePercenPref = sharedPref.getInt(getString(R.string.availPercentagePref),1);

        nightPark = sharedPref.getBoolean(getString(R.string.night_park),false);
        freePark = sharedPref.getBoolean(getString(R.string.free_park),false);
        mapMove = sharedPref.getBoolean(getString(R.string.map_move),false);

        availLotsSeek.setProgress(availPref);
        occupancySeek.setProgress(freePercenPref);
        nightParkSwitch.setChecked(nightPark);
        freeParkSwitch.setChecked(freePark);
        mapMoveSwitch.setChecked(mapMove);
    }

    private void showSearchLoc(String add){
        List<Address> locations;
        Address address;
        Double lat, lng;
        LatLng loc = null;
        try {
            locations = geocoder.getFromLocationName(add, 1);
            if (locations.size()>0) {
                address = locations.get(0);
                lat = address.getLatitude();
                lng = address.getLongitude();

                loc = new LatLng(lat, lng);

                map.navigateToSearch(loc);
            }else{
                Toast.makeText(getApplicationContext(), "Address not found. Please try again with the exact address", Toast.LENGTH_SHORT).show();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }


    }

    private LatLng getLocation(String add) {
        List<Address> locations;
        Address address;
        Double lat, lng;
        String t;
        LatLng loc = null;
        try {
            locations = geocoder.getFromLocationName(add,1);
            address=locations.get(0);
            lat=address.getLatitude();
            lng=address.getLongitude();

            loc = new LatLng(lat,lng);
            globalCurLoc = loc;
            locationfinder.getLastLocation();
            //map.getCurLocOnMap(loc, true, 0.0f);
            apicall = new APICall();
            apicall.execute("http://sgcarparkfinder.herokuapp.com/carpark");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loc;
    }

    private void resetMap(LatLng loc){
        map.clearMap();

        if (loc!=null) {
            //locationfinder.getLastLocation();
            map.getCurLocOnMap(loc, true, 0.0f);
        }

        apicall = new APICall();
        apicall.execute("http://sgcarparkfinder.herokuapp.com/carpark");
    }

    private void curLocOnMap() {
        if (locationfinder == null){
            try {
                setup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Location location = locationfinder.getLastLocation();
            Double lat, lng;
            LatLng latlng=null;
            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                latlng = new LatLng(lat, lng);
            }

            resetMap(latlng);
        }
    }

    private void setup() throws Exception {

        if (checkGPSOn()) {
            this.trackOn = true;
            locationfinder = new LocationClass(this);
            geocoder = new Geocoder(this, Locale.getDefault());
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    public boolean checkGPSOn() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            //dlg.setMessage("Location function is disable. You will be directed to turn it on.");
            Toast.makeText(getApplicationContext(), "Location function is disabled. You will be directed to turn it on.", Toast.LENGTH_SHORT).show();
            new CountDownTimer(2000, 2000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            }.start();

            return false;
        }
        return true;
    }

    private void getPermission(Activity activity) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        setup();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ableToGetLoc = false;
                    Toast.makeText(getApplicationContext(), "Location permission not granted. Not able to get your location.", Toast.LENGTH_SHORT).show();

                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateResult(String res) {
        this.result2 = res;
        Toast.makeText(getApplicationContext(), this.result2, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.asus.carparkfinder/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.asus.carparkfinder/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.defaultMap = googleMap;
        this.gmapinfo = new CustomInfoWindowGoogleMap(this);
        this.defaultMap.setInfoWindowAdapter(gmapinfo);
        map = new MapClass();
        map.create(defaultMap, ableToGetLoc, this);
        curLocOnMap();
    }

    public class APICall extends AsyncTask<String, Integer, Long> {

        String result = "";
        private Vector<Carpark> cpVec;


        public APICall() {
        }

        private String getStringFromInputStream(InputStream is) {

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return sb.toString();
        }

        private boolean distance_ok(Carpark cp){
            /*LatLng cpLoc= new LatLng(cp.lat,cp.lng);
            double dist = locationfinder.distance(cpLoc, globalCurLoc);

            if (dist<10){
                return true;
            }
            else{
                return false;
            }*/
            return true;
        }

        private boolean lots_ok(Carpark cp){
            if (cp.avail_lots>availLotsSeek.getProgress() && cp.empty_percen>occupancySeek.getProgress()){
                return true;
            }{
                return false;
            }
        }

        private boolean CPApproved(Carpark cp){
            if (distance_ok(cp) && lots_ok(cp)){
                return true;
            }else{
                return false;
            }
        }

        public Vector<Carpark> compileVector(String result) {
            Carpark cp;
            String cp_num, address, cp_type, free_parking, night_park;
            long avail_lots, tot_lots;
            double empty_percen, lat, lng;
            Vector<Carpark> cp_col = new Vector<Carpark>();

            try {
                JSONArray subArray = new JSONArray(result);

                for (int i = 0; i < subArray.length(); i++) {
                    cp_num = subArray.getJSONObject(i).getString("car_park_no").toString();
                    address = subArray.getJSONObject(i).getString("address").toString();
                    cp_type = subArray.getJSONObject(i).getString("car_park_type").toString();
                    free_parking = subArray.getJSONObject(i).getString("free_parking").toString();
                    night_park = subArray.getJSONObject(i).getString("night_parking").toString();
                    avail_lots = Long.parseLong(subArray.getJSONObject(i).getString("avail_lots").toString());
                    tot_lots = Long.parseLong(subArray.getJSONObject(i).getString("tot_lots").toString());
                    empty_percen = Double.parseDouble(subArray.getJSONObject(i).getString("empty_percen").toString());
                    lat = Double.parseDouble(subArray.getJSONObject(i).getString("lat").toString());
                    lng = Double.parseDouble(subArray.getJSONObject(i).getString("lng").toString());

                    cp = new Carpark(cp_num, address, cp_type, free_parking, avail_lots, tot_lots, empty_percen, lat, lng, night_park);

                    if (CPApproved(cp)){
                        cp_col.add(cp);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return cp_col;
        }

        @Override
        protected void onPostExecute(Long result2) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < cpVec.size(); i++) {
                        Carpark cp = cpVec.get(i);
                        map.plotCp(cp);
                    }
                }
            });
        }

        @Override
        protected Long doInBackground(String... params) {
            URL obj = null;
            int tries = 0;
            boolean success = false;
            while (success == false && tries<3) {
                try {
                    tries+=1;
                    obj = new URL(params[0]);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");

                    try {
                        InputStream in = new BufferedInputStream(con.getInputStream());
                        this.result = getStringFromInputStream(in);
                        this.result = this.result.substring(1, this.result.length() - 1);
                        this.result = this.result.replace("\\", "");
                        String res = this.result;
                        cpVec = compileVector(this.result);
                    } catch (Exception e) {
                        String msg = e.getMessage();
                        System.err.println("Caught IOException: " + e.getMessage());
                    }

                    con.disconnect();
                    success = true;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}
