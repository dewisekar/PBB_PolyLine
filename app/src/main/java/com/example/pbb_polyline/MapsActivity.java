package com.example.pbb_polyline;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LocationManager lm;
    private LocationListener ll;
    private LatLng current = null;
    private LatLng before = null;
    private LatLng lokasiTujuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ll = new lokasiListener();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED){
            return;
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 2, ll);

        Button cari = (Button) findViewById(R.id.idCari);
        cari.setOnClickListener(op);
    }

    View.OnClickListener op = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.idCari:sembunyikanKeyBoard(view);
                    goCari();break;
            }
        }
    };

    private void sembunyikanKeyBoard(View v){
        InputMethodManager a = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        a.hideSoftInputFromWindow(v.getWindowToken(),0);
    }

    private void goCari(){

        EditText tempat = (EditText) findViewById(R.id.input_cari);

        if(tempat.length()==0){
            tempat.setError("Tempat tidak boleh kosong!");
        }
        else{
            Geocoder g = new Geocoder(getBaseContext());
            List<Address> daftar = null;
            try {
                daftar = g.getFromLocationName(tempat.getText().toString(), 1);
                Address alamat = daftar.get(0);

                String nemuAlamat = alamat.getAddressLine(0);
                Double lintang = alamat.getLatitude();
                Double bujur = alamat.getLongitude();

                Toast.makeText(getBaseContext(), "Ketemu" + nemuAlamat, Toast.LENGTH_LONG).show();

                Toast.makeText(this,"Move to "+ nemuAlamat +" Lat:" +
                        lintang + " Long:" +bujur,Toast.LENGTH_LONG).show();
                gotoPeta(lintang,bujur,15);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void gotoPeta(Double lat, Double lng, float z){
        lokasiTujuan = new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().
                position(lokasiTujuan).
                title("Tujuan to  " +lat +":" +lng));
        mMap.moveCamera(CameraUpdateFactory.
                newLatLngZoom(lokasiTujuan,z));

    }

    private void tambahGaris(){
        Polyline poli = mMap.addPolyline(new PolylineOptions().add(current).add(lokasiTujuan));
        poli.setWidth(2f);
        poli.setColor(Color.RED);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng ITS = new LatLng(-7.28, 112.79);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ITS, 2));
    }

    private class lokasiListener implements LocationListener{
        @Override
        public void onLocationChanged(Location location) {
            if(current==null){
                before = null;
                current = new LatLng(location.getLatitude(), location.getLongitude());
                gotoPeta(location.getLatitude(), location.getLongitude(), 15);
                Toast.makeText(getBaseContext(), "GPS Capture:" + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
            }
            else{
                before = new LatLng(current.latitude, current.longitude);
                current = new LatLng(location.getLatitude(), location.getLongitude());
                tambahGaris();
                Toast.makeText(getBaseContext(), "GPS Capture:" + location.getLatitude() + " " + location.getLongitude() + " before" + before.latitude + " " + before.longitude, Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
