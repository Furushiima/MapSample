package com.example.mapsample;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mapsample.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    private final int REQUEST_PERMISSION = 1000;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT >= 23)
            checkPermission();
        else
            start();
    }

    public void start() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // LatLng yu = new LatLng(33.956416, 131.2725288);
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(yu));
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
        }
        // 初期位置を自分の位置にし、カメラをズーム
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng yu = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yu, 16));

        //マップがタップされた時の処理
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng point){
                // 現在地との距離を計測し表示

                // 距離取得
                float[] distance =  new float[3];
                Location.distanceBetween(yu.latitude, yu.longitude, point.latitude, point.longitude, distance);

                // 1000m以上の場合はkm表示
                String displayText;
                if(distance[0]>=1000){
                    displayText = "現在地からの距離：" + String.format("%.03f", (float)distance[0]/1000) + "km";
                }
                else{
                    displayText = "現在地からの距離：" + (int)distance[0] + "m";
                }
                Toast toast= Toast.makeText(getApplicationContext(), displayText, Toast.LENGTH_SHORT);
                toast.show();

                // マーカーを作成
                LatLng latlng = new LatLng(point.latitude, point.longitude);
                mMap.addMarker(new MarkerOptions().position(latlng).title(displayText).draggable(true));

                // 現在地からの直線を引く
                mMap.addPolyline(new PolylineOptions().add(yu, latlng).width(5).color(Color.RED));

            }
        });


        // マップが長押しされたときの処理
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // 距離取得
                float[] distance =  new float[3];
                Location.distanceBetween(yu.latitude, yu.longitude, latLng.latitude, latLng.longitude, distance);
                double rd = distance[0];
                // 1000m以上の場合はkm表示
                String displayText;
                if(distance[0]>=1000){
                    displayText = "半径：" + String.format("%.03f", (float)distance[0]/1000) + "km";
                }
                else {
                    displayText = "半径：" + (int) distance[0] + "m";
                }
                Toast toast= Toast.makeText(getApplicationContext(), displayText, Toast.LENGTH_SHORT);
                toast.show();
                // 円描写
                mMap.addCircle(new CircleOptions().center(yu).radius(rd).strokeColor(Color.BLUE));
            }
        });

        // ボタンが押されたらマップをクリア
        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
            }
        });

    }

    public void checkPermission(){
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED)
                start();
        else
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},REQUEST_PERMISSION);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        if ((requestCode == REQUEST_PERMISSION) && (grantResult[0] == PackageManager.PERMISSION_GRANTED))
            start();
    }

}