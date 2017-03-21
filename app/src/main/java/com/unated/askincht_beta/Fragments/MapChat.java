package com.unated.askincht_beta.Fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.Language;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapChat extends AppCompatActivity {
    String TAG="MyLog";
    double value;
    double lat;
    private GoogleMap gMap;
    private SupportMapFragment mSupportMapFragment;
    private String[] colors = {"#7fff7272", "#7f31c7c5", "#7fff8a00"};
    @Bind(R.id.zoombutton)
    CircleButton zoom;
    @Bind(R.id.unzoombutton)
    CircleButton unzoom;
    @Bind(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    double lng;
    TextView tvWalkTime;
    TextView tvDriveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_map_dialog);
        ButterKnife.bind(this);

        tvWalkTime=(TextView)findViewById(R.id.tvManM);
        tvDriveTime=(TextView)findViewById(R.id.tvCarM);
        value=getIntent().getDoubleExtra("value",0);
        Log.i("MyLog", String.valueOf(value));
        lat=Double.valueOf(getIntent().getStringExtra("lat"));
        Log.i("MyLog", String.valueOf(lat));

        lng=Double.valueOf(getIntent().getStringExtra("lng"));
        Log.i("MyLog", String.valueOf(lng));

        FragmentManager fmanager = getSupportFragmentManager();
        Fragment fragment = fmanager.findFragmentById(R.id.mapM);
        SupportMapFragment supportmapfragment = (SupportMapFragment)fragment;
        supportmapfragment.getMapAsync(mOnMapReadyCallback);
    }
   private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

            gMap = googleMap;
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 17));
            int height = 100;
            int width = 100;
            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.point);
            Bitmap b=bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            gMap.addMarker(new MarkerOptions().position(new LatLng(lat,lng)));
            gMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(SharedStore.getInstance().getMyLat()), Double.valueOf(SharedStore.getInstance().getMyLng()))).title("Вы").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

            GoogleDirection.withServerKey("AIzaSyDqEgRCf3qXDdfBuahMGQgwToUpXBX2Ozw")
                    .from(new LatLng(Double.valueOf(SharedStore.getInstance().getMyLat()), Double.valueOf(SharedStore.getInstance().getMyLng())))
                    .to(new LatLng(lat,lng))
                    .language(Language.RUSSIAN)
                    .alternativeRoute(true)
                    .transportMode(TransportMode.DRIVING)

                    .avoid(AvoidType.FERRIES)
                    .avoid(AvoidType.FERRIES)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if(direction.isOK()) {

                                for (int i = 0; i < direction.getRouteList().size(); i++) {
                                    Route route = direction.getRouteList().get(i);
                                    String color = colors[i % colors.length];
                                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                    Log.i(TAG, String.valueOf(route.getLegList().get(0).getDuration()));
                                    Log.i(TAG, String.valueOf(route.getLegList().get(0).getArrivalTime()));
                                    Log.i(TAG, String.valueOf("2"+route.getLegList().get(0).getDepartureTime()));
                                    Log.i(TAG, String.valueOf("3"+route.getLegList().get(0).getDuration()+"3"));
                                    Log.i(TAG, String.valueOf("4"+route.getLegList().get(0).getDurationInTraffic()+"4"));
                                    gMap.addPolyline(DirectionConverter.createPolyline(getBaseContext(), directionPositionList, 5,Color.parseColor(color)));
                                    Log.i(TAG, String.valueOf("5 "+direction.getRouteList().get(0).getLegList().get(0).getDuration().getText()));
                                    tvDriveTime.setText(direction.getRouteList().get(0).getLegList().get(0).getDuration().getText());

                                    Log.i(TAG, String.valueOf(rawBody)+"5");


                                }                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            Log.i(TAG, String.valueOf(t));

                        }
                    });
            GoogleDirection.withServerKey("AIzaSyDqEgRCf3qXDdfBuahMGQgwToUpXBX2Ozw")
                    .from(new LatLng(Double.valueOf(SharedStore.getInstance().getMyLat()), Double.valueOf(SharedStore.getInstance().getMyLng())))
                    .to(new LatLng(lat,lng))
                    .language(Language.RUSSIAN)
                    .alternativeRoute(true)
                    .transportMode(TransportMode.WALKING)

                    .avoid(AvoidType.FERRIES)
                    .avoid(AvoidType.FERRIES)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if(direction.isOK()) {

                                for (int i = 0; i < direction.getRouteList().size(); i++) {
                                    Route route = direction.getRouteList().get(i);

                                    Log.i(TAG, String.valueOf("6 "+direction.getRouteList().get(0).getLegList().get(0).getDuration().getText()));

                                    tvWalkTime.setText(direction.getRouteList().get(0).getLegList().get(0).getDuration().getText());


                                }                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            Log.i(TAG, String.valueOf(t));

                        }
                    });



        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R.id.floatingActionButton,R.id.zoombutton,R.id.unzoombutton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton:
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(SharedStore.getInstance().getMyLat()), Double.valueOf(SharedStore.getInstance().getMyLng())), 14));

                break;
            case R.id.zoombutton:
                gMap.animateCamera(CameraUpdateFactory.zoomIn());

                break;
            case R.id.unzoombutton:
                gMap.animateCamera(CameraUpdateFactory.zoomOut());

                break;
        }}

}
