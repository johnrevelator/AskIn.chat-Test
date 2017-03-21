package com.unated.askincht_beta.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Dialog.SearchProcessDialogFragment;
import com.unated.askincht_beta.Pojo.BusMessages.ExactReq;
import com.unated.askincht_beta.Pojo.BusMessages.NotificationMessage;
import com.unated.askincht_beta.Pojo.BusMessages.SearchMessage;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;
import com.unated.askincht_beta.Utils.SphericalUtil;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Random;

import at.markushi.ui.CircleButton;
import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainFragment extends SuperFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {
    @Bind(R.id.etSearch)
    AutoCompleteTextView mEtSearch;
    @Bind(R.id.seekBar)
    DiscreteSeekBar mSeekBar;
    @Bind(R.id.tvCarTime)
    TextView tvCarTime;
    @Bind(R.id.tvWalkTime)
    TextView tvWalkTime;
    @Bind(R.id.tvWalkTime3)
    TextView tvWalkTime3;
    @Bind(R.id.zoombutton)
    CircleButton zoom;
    @Bind(R.id.unzoombutton)
    CircleButton unzoom;
    @Bind(R.id.tvWalk)
    TextView tvWalk;
    @Bind(R.id.tvCar)
    TextView tvCar;
    @Bind(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;

    private SupportMapFragment mSupportMapFragment;
    private Location mLocation;
    private GoogleMap gMap;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static final String TAG = "MyLog";
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private LatLng latLng;
    private int valueM;
    LocationManager locationManager;
    private int first = 0;
    double constantZoom=8.681171  ;
    ArrayList<Spanned> helpers;

    final String[] mCats = {"Нужен адвокат при разводе", "Нужен семейный адвокат", "Нужен оценщик", "Нужно организовать праздник под ключ",
            "Нужно отремонтировать диван"};


    @Override
    public int onInflateViewFragment() {
        return R.layout.fragment_main;
    }

    @Override
    public void onCreateFragment(Bundle instance) {

    }


    @Override
    public View onCreateViewFragment(View view) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(MainFragment.this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        mEtSearch.setAdapter(new ArrayAdapter<>(getActivity(),
                R.layout.item_search, mCats));
        mEtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Log.i(TAG,mEtSearch.getText().toString());
                Call<ExactReq> myRequestsCall = AppMain.getClientArt().getExactRequests(SharedStore.getInstance().getSID(),"getExactRequests",mEtSearch.getText().toString());
                myRequestsCall.enqueue(new Callback<ExactReq>() {
                    @Override
                    public void onResponse(Call<ExactReq> call, Response<ExactReq> response) {
                        closeProgressDialog();
                        if (response.isSuccessful() && response.body().getStatus() == 0) {
                            Log.i(TAG, String.valueOf(response.body().getData().getText().size()));
                            helpers=new ArrayList<>();

                            for(int i=0;i<response.body().getData().getText().size();i++)
                            helpers.add(Html.fromHtml(response.body().getData().getText().get(i)));
                            mEtSearch.setAdapter(new ArrayAdapter<>(getActivity(),
                                    R.layout.item_search, helpers));

                        }
                    }

                    @Override
                    public void onFailure(Call<ExactReq> call, Throwable t) {
                        closeProgressDialog();
                    }

                });

            }
        });
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(mOnMapReadyCallback);
        tvWalkTime3.setText(String.valueOf(10));
        final MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.song);

        mSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

                            mediaPlayer.start();

                int needVal = 0;

                if (value <= 25) {
                    needVal = (int) (value / 2.5);
                    Log.i(TAG, String.valueOf(needVal));

                }
                if(value > 25&&value<28.125){
                    needVal = 15;
                    Log.i(TAG, String.valueOf(needVal));
                }
                if(value > 28.125&&value<31.25){
                    needVal = 20;
                    Log.i(TAG, String.valueOf(needVal));
                }
                if(value > 31.25&&value<34.375){
                    needVal = 25;
                    Log.i(TAG, String.valueOf(needVal));
                }
                if(value > 34.375&&value<37.5){
                    needVal = 30;
                    Log.i(TAG, String.valueOf(needVal));
                }
                if(value > 37.5&&value<40.625){
                    needVal = 35;
                    Log.i(TAG, String.valueOf(needVal));
                }
                if(value > 40.625&&value<43.75){
                    needVal = 40;
                    Log.i(TAG, String.valueOf(needVal));
                }
                if(value > 43.75&&value<46.875){
                    needVal = 45;
                    Log.i(TAG, String.valueOf(needVal));
                }
                if(value > 46.875&&value<=50){
                    needVal = 50;
                    Log.i(TAG, String.valueOf(needVal));
                }
                valueM = value * 1000;
                Log.i(TAG, String.valueOf(valueM));
                int min = ((60*needVal/5));
                Log.i(TAG, String.valueOf(60*needVal/5)+" man");
                Log.i(TAG, String.valueOf(60*needVal/40)+" car");
                if (min < 60) {
                    tvWalkTime.setText(String.valueOf(min));
                    tvWalk.setText("мин");
                } else {
                    int h = min / 60;
                    min = min % 60;
                    if (min > 9) {
                        tvWalkTime.setText(h + ":" + min);
                        tvWalk.setText("ч");
                    } else {
                        tvWalkTime.setText(h + ":0" + min);
                        tvWalk.setText("ч");
                    }
                }

                min = (60*needVal/40);
                if (min < 60) {
                    tvCarTime.setText(String.valueOf(min));
                    tvCar.setText("мин");
                } else {
                    int h = min / 60;
                    min = min % 60;
                    if (min > 9) {
                        tvCarTime.setText(h + ":" + min);
                        tvCar.setText("ч");
                    } else {
                        tvCarTime.setText(h + ":0" + min);
                        tvCar.setText("ч");
                    }

                }
                tvWalkTime3.setText(String.valueOf(needVal));
/*
               animateToMeters(value * 1000);
*/
                if(!isMap)
                    sBtoZoom(needVal);

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
        mSeekBar.setFocusable(true);

        mEtSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (mEtSearch.getRight() - mEtSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        EventBus.getDefault().post(getSearchMessage());

                        Log.i(TAG, "поиск");
                        callSearchDialog();

                        return true;
                    }
                }
                return false;
            }
        });

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Log.i(TAG, "поиск c клавы");


                    EventBus.getDefault().post(getSearchMessage());

                    callSearchDialog();
                    return true;
                }
                return false;
            }
        });
        return view;
    }


    @NonNull
    private SearchMessage getSearchMessage() {
        SearchMessage msg = new SearchMessage();
        msg.setText(mEtSearch.getText().toString());
        msg.setLat(gMap.getCameraPosition().target.latitude);
        msg.setLon(gMap.getCameraPosition().target.longitude);
        msg.setUniq(random());
        msg.setRadius(valueM);
        return msg;
    }

    public static String random() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 13; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        Log.i(TAG, output);
        return output;
    }

    double zoomForSeekBar;
    boolean isSeek=false;

    public void sBtoZoom(int value){
        isSeek=true;
        zooming= (float) (15.8 - Math.log(value* 5.508)/Math.log(2));


        Log.i(TAG, (float) (15.8 - Math.log(value* 5.508)/Math.log(2))+" true     may be");
                gMap.animateCamera( CameraUpdateFactory.zoomTo(zooming) );
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
        isSeek=false;
            }
        }, 500);


    }
    double zoomLevel;



    private void callSearchDialog() {
        SearchProcessDialogFragment searchProcessDialogFragment = new SearchProcessDialogFragment();
        searchProcessDialogFragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    private int metersTmp = 5;

    private void animateToMeters(int meters) {
        if (latLng != null) {
            LatLng point = gMap.getCameraPosition().target;
            LatLngBounds latLngBounds = calculateBounds(point, meters);
            if (latLngBounds != null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, 0);
                if (gMap != null) {
                    gMap.moveCamera(cameraUpdate);
                }
            }
        }

    }


    private LatLngBounds calculateBounds(LatLng center, double radius) {
        return new LatLngBounds.Builder().
                include(SphericalUtil.computeOffset(center, radius, 0)).
                include(SphericalUtil.computeOffset(center, radius, 90)).
                include(SphericalUtil.computeOffset(center, radius, 180)).
                include(SphericalUtil.computeOffset(center, radius, 270)).build();
    }

    @Override
    public void onAttachFragment(Activity activity) {

    }
    float zooming;
    int seekBarLevel;

    private OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

            gMap = googleMap;
            gMap.animateCamera( CameraUpdateFactory.zoomTo((float) constantZoom) );
            gMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                @Override
                public void onCameraMove() {
                    zooming = gMap.getCameraPosition().zoom;

                    Log.d(TAG, zooming+"  zoomOnMapReady");
                    if(!isSeek)
                    conpert();



                }
            });

            /*gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                private float currentZoom = -1;

                @Override
                public void onCameraChange(CameraPosition pos) {
                    if (pos.zoom != currentZoom){
                        currentZoom = pos.zoom;
                        LatLngBounds bounds = gMap.getProjection().getVisibleRegion().latLngBounds;
                        VisibleRegion vr = gMap.getProjection().getVisibleRegion();
                        double bottom = vr.latLngBounds.southwest.latitude;

                        Location center = new Location("center");
                        center.setLatitude(vr.latLngBounds.getCenter().latitude);
                        center.setLongitude(vr.latLngBounds.getCenter().longitude);

                        Location middleLeftCornerLocation = new Location("center");
                        middleLeftCornerLocation.setLatitude(center.getLatitude());
                        middleLeftCornerLocation.setLongitude(bottom);

                        float dis = center.distanceTo(middleLeftCornerLocation);
                        Log.i(TAG, String.valueOf(dis));
                        mSeekBar.setProgress((int) (dis/1000));

                    }
                }
            });*/
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

        super.onDestroy();
        mGoogleApiClient.disconnect();

    }


    @Override
    public void onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i(TAG, "connect");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && this.getActivity().checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && this.getActivity().checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            Log.i(TAG, "permcheckM");

        } else {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "permcheck");

                return;
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && getActivity().checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        } else {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);


            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, oneShotNetworkLocationListener);
            Location locationn = (locationManager.getLastKnownLocation(provider));
            try {
                if (locationn != null) {
                    latLng = new LatLng(locationn.getLatitude(), locationn.getLongitude());
                    SharedStore.getInstance().setMyLat(locationn.getLatitude());
                    SharedStore.getInstance().setMyLng(locationn.getLongitude());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationn.getLatitude(), locationn.getLongitude()), (float) constantZoom));
                }
            } catch (NullPointerException e) {
                Log.i(TAG, String.valueOf(e));
            }
        }
        // Begin polling for new location updates.
        startLocationUpdates();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 123
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            String provider = locationManager.getBestProvider(criteria, true);


            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, oneShotNetworkLocationListener);

            Location locationn = (locationManager.getLastKnownLocation(provider));
            try {
                if(locationn!=null) {
                    latLng = new LatLng(locationn.getLatitude(), locationn.getLongitude());
                    SharedStore.getInstance().setMyLat(locationn.getLatitude());
                    SharedStore.getInstance().setMyLng(locationn.getLongitude());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationn.getLatitude(), locationn.getLongitude()),(float) constantZoom));
                }
            } catch (NullPointerException e) {
                Log.i(TAG, String.valueOf(e));
            }                }
    }


    protected void startLocationUpdates() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);

    }

    public void onLocationChanged(Location location) {
        if (SharedStore.getInstance().getMyLat() != null || !SharedStore.getInstance().getMyLat().equals("")) {
            if (location != null) {
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
if(first<1)
    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), (float) constantZoom
    ));

                SharedStore.getInstance().setMyLat(location.getLatitude());
                SharedStore.getInstance().setMyLng(location.getLongitude());
                first++;

            }
        }
        else {
            first++;

            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            SharedStore.getInstance().setMyLat(location.getLatitude());
            SharedStore.getInstance().setMyLng(location.getLongitude());

        }
    }
    @OnClick({R.id.floatingActionButton,R.id.zoombutton,R.id.unzoombutton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton:

                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) constantZoom));
                mSeekBar.setProgress(10);

                break;
            case R.id.zoombutton:
                if(mSeekBar.getProgress()>2) {
                    if(gMap.getCameraPosition().zoom<12.538471 ) {


                        gMap.animateCamera(CameraUpdateFactory.zoomIn());


                        conpert();


                    }
                }


                break;
            case R.id.unzoombutton:
                if(mSeekBar.getProgress()<50) {
                    if (6.894615< gMap.getCameraPosition().zoom) {
                        gMap.animateCamera(CameraUpdateFactory.zoomOut());




                        conpert();


                    }
                }

                break;
        }
    }



boolean isMap=false;
public void conpert(){
    isMap=true;
    zooming = gMap.getCameraPosition().zoom;

    Log.d(TAG, Math.pow(2,(15.8 -zooming))/5.508+"  seek");
    seekBarLevel= Double.valueOf(Math.pow(2,(15.8 -zooming))/5.508).intValue();
        mSeekBar.setProgress(seekBarLevel);

    isMap=false;
}

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Subscribe
    public void onNotificationReceived(NotificationMessage notificationMessage) {

    }
    private final LocationListener oneShotNetworkLocationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            locationManager.removeUpdates(this);
        }

        @Override
        public void onProviderEnabled(String arg0) {
            locationManager.removeUpdates(this);
        }

        @Override
        public void onProviderDisabled(String arg0) {
            locationManager.removeUpdates(this);
        }

        @Override
        public void onLocationChanged(Location location) {
            locationManager.removeUpdates(this);
        }
    };
}
