package com.unated.askincht_beta.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unated.askincht_beta.Pojo.BusMessages.PayToCall;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;


public class PhoneDialogFragment extends SuperActivity {
    final static String DATA_RECEIVE = "data_receive";
    Fragment fragment;
    @Bind(R.id.redProfP)

    LinearLayout redProf;
    @Bind(R.id.phone)

    TextView showReceivedData;
String phone;


    @Bind(R.id.cancelD)
    ImageView img;
String mRequestId;


    @Override
    protected int getContentViewId() {
        return         R.layout.view_phone_dialog;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        showReceivedData = (TextView) findViewById(R.id.phone);
        phone= getIntent().getStringExtra("tel");
        mRequestId= getIntent().getStringExtra("req");
        showReceivedData.setText(phone);


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.back);
        switch( am.getRingerMode() ) {
            case AudioManager.RINGER_MODE_NORMAL:
                mediaPlayer.start();
                break;
        }
    }

    @OnClick({R.id.phone, R.id.cancelD})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelD:
onBackPressed();
                break;
            case R.id.phone:
               onCall();
                break;

        }
    }
    public void onCall() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.CALL_PHONE}, 123);
        } else {
            startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+phone)));
            EventBus.getDefault().post(payToCall());

        }
    }
    @NonNull
    private PayToCall payToCall() {
        PayToCall msg = new PayToCall(SharedStore.getInstance().getSID(),mRequestId);

        return msg;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    onCall();
                } else {
                    Log.d("MyLog", "Call Permission Not Granted");
                }
                break;

            default:
                break;
        }
    }





}
