package com.unated.askincht_beta.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.PermissionUtil;
import com.unated.askincht_beta.Utils.SharedStore;

public class AdviceScreen extends AppCompatActivity {
    /* public static int SPLASH_DISPLAY_LENGTH = 8000;
     private String code = "";*/
    int currentIndex = 0;
    int ok;
    final String strings[] = {"«Хочу помыть машину на мойке самообслуживания»\n\n\n" +

            "«Надо аккуратно подшить шелковую блузку»\n\n\n" +

            "«Срочно надо вывести пятно с пиджака»\n\n","«Желаю вкусных крупных раков вареных с доставкой домой сегодня вечером к футболу»\n\n\n" +

            "«Нужна красивая раковина в ванную фирму не помню французская какая-то»\n\n\n" +

            "«Ищу зеленые кроссовки нью бэлэнс 44 размер»\n"};

    int messageCount;
    private TextSwitcher simpleTextSwitcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);
        messageCount = strings.length;

        simpleTextSwitcher = (TextSwitcher) findViewById(R.id.simpleTextSwitcher);
        // Set the ViewFactory of the TextSwitcher that will create TextView object when asked
        simpleTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // TODO Auto-generated method stub
                // create a TextView
                TextView t = new TextView(AdviceScreen.this);
                // set the gravity of text to top and center horizontal
                t.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
                t.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                // set displayed text size
                t.setTextColor(Color.WHITE);
                t.setTextSize(20);
                return t;
            }
        });

        // Declare in and out animations and load them using AnimationUtils class
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);

        // set the animation type to TextSwitcher
        simpleTextSwitcher.setInAnimation(in);
        simpleTextSwitcher.setOutAnimation(out);
        recursion();
        //text appear on start
        // ClickListener for NEXT button
        // When clicked on Button TextSwitcher will switch between labels
        // The current label will go out and next label will come in with specified animation


    }

    public void doing(){
        ok = 1;


        LocationManager mlocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.i("MyLog","disabled");
            displayLocationSettingsRequest(this);

        }else {
            Log.i("MyLog","enabled");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && getApplicationContext().checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && getApplicationContext().checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION},
                            123);
                }
            } else {
                if (!TextUtils.isEmpty(SharedStore.getInstance().getSID())) {

                    if (PermissionUtil.isHavePermission(this, PermissionUtil.ACCESS_FINE_LOCATION) && PermissionUtil.isHavePermission(this, PermissionUtil.ACCESS_COARSE_LOCATION)) {
                        if (TextUtils.isEmpty(SharedStore.getInstance().getSID())) {
                            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                            intent.putExtra("flag", 1);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            startActivity(new Intent(this, MainActivity.class).putExtra("type",0));
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{PermissionUtil.ACCESS_COARSE_LOCATION}, 100);
                        } else {
                            PermissionUtil.requestPermissions(this, PermissionUtil.ACCESS_FINE_LOCATION, 100);

                        }
                    }

                } else {
                    if (TextUtils.isEmpty(SharedStore.getInstance().getSID())) {
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        intent.putExtra("flag", 1);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        startActivity(new Intent(this, MainActivity.class).putExtra("type",0));
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
            }

        }
    }


    public void ok(View view) {
        doing();

    }
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        doing();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        finish();
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        finish();
                        break;
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults){
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (TextUtils.isEmpty(SharedStore.getInstance().getSID())) {
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        intent.putExtra("flag", 1);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        startActivity(new Intent(this, MainActivity.class).putExtra("type",0));
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                }
            case 123:
                if (!TextUtils.isEmpty(SharedStore.getInstance().getSID())) {

                    if (PermissionUtil.isHavePermission(this, PermissionUtil.ACCESS_FINE_LOCATION) && PermissionUtil.isHavePermission(this, PermissionUtil.ACCESS_COARSE_LOCATION)) {
                        if (TextUtils.isEmpty(SharedStore.getInstance().getSID())) {
                            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                            intent.putExtra("flag", 1);
                            startActivity(intent);
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            startActivity(new Intent(this, MainActivity.class).putExtra("type",0));
                            finish();
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{PermissionUtil.ACCESS_COARSE_LOCATION}, 100);
                        } else {
                            PermissionUtil.requestPermissions(this, PermissionUtil.ACCESS_FINE_LOCATION, 100);

                        }
                    }

                } else {
                    if (TextUtils.isEmpty(SharedStore.getInstance().getSID())) {
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        intent.putExtra("flag", 1);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        startActivity(new Intent(this, MainActivity.class).putExtra("type",0));
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
        }

    }

    public void recursion(){
        currentIndex++;
        // If index reaches maximum then reset it
        if (currentIndex == messageCount)
            currentIndex = 0;
        simpleTextSwitcher.setText(strings[currentIndex]); // set Text in TextSwitcher
        if(ok!=1)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    recursion();

                }
            }, 3000);


    }
}
