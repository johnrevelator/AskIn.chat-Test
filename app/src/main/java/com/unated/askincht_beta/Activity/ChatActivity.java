package com.unated.askincht_beta.Activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Fragments.ChatFragment;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.RequestResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChatActivity extends SuperActivity {

    private static final String EXTRA_REQUEST = "extra_request";
    private static final String EXTRA_SHOP = "extra_shop";
    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_ACT = "extra_act";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_base;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
       /* setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        Log.i(TAG,this.getIntent().getStringExtra("extra_request")+" reqId in Act");
        getFragmentManager().beginTransaction().addToBackStack(null).commit();

        setFragment(R.id.container, ChatFragment.newInstance(getIntent().getStringExtra(EXTRA_REQUEST), getIntent().getStringExtra(EXTRA_SHOP),getIntent().getStringExtra(EXTRA_TITLE)));
    }


    int id;

    @Override
    public void onBackPressed() {
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.back);
        switch( am.getRingerMode() ) {
            case AudioManager.RINGER_MODE_NORMAL:

                mediaPlayer.start();
                break;
        }
Log.i(TAG,getIntent().getStringExtra(EXTRA_REQUEST)+" reqId");
         sendToBack();



    }
    public void sendToBack(){
        Call<RequestResponse> call = AppMain.getClient().getRequest(SharedStore.getInstance().getSID(), Integer.valueOf(getIntent().getStringExtra(EXTRA_REQUEST)),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<RequestResponse>() {
            @Override
            public void onResponse(Call<RequestResponse> call, Response<RequestResponse> response) {
                if (response.isSuccessful()&&response.body().getStatus()==0) {

                    Log.i(TAG, SharedStore.getInstance().getUserId() + " grerher " + response.body().getData().getRequests().getUser_id());
                    if(SharedStore.getInstance().getMyShop()==null){
                        id= Integer.parseInt(SharedStore.getInstance().getUserId());
                    }
                    else{
                        id=SharedStore.getInstance().getMyShop().getId();
                    }
                    Log.i(TAG, id + " dsvdd " + response.body().getData().getRequests().getShop_id());

                    if (Integer.valueOf(SharedStore.getInstance().getUserId())==response.body().getData().getRequests().getUser_id()) {

                        startActivity(new Intent(getBaseContext(), MainActivity.class).putExtra("type",4).putExtra("extra_item",response.body().getData().getRequests().getId()));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        finish();


                    }
                    else{
                        startActivity(new Intent(getBaseContext(), MainActivity.class).putExtra("type",2).putExtra("extra_item",response.body().getData().getRequests().getId()));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        finish();

                    }

            } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                AppMain.setIsRefresh(true);
                Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                callr.enqueue(new Callback<RefreshResponse>() {
                    @Override
                    public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                        closeProgressDialog();
                        if (response.isSuccessful() && response.body().getStatus() == 0) {
                            SharedStore.getInstance().setToken(response.body().getData().getToken());
                            sendToBack();
                            AppMain.setIsRefresh(false);


                        }else if(response.body().getStatus() == 1029){
                            AppMain.setIsRefresh(false);

                            logout();
                        }
                    }

                    @Override
                    public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                        closeProgressDialog();
                    }
                });
            }else  if(response.body().getStatus() == 1027||response.body().getStatus() == 1028||response.body().getStatus() == 1029) {


                logout();
            }



        }

            @Override
            public void onFailure(Call<RequestResponse> call, Throwable t) {
                Log.i("MyLog", String.valueOf(t));
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
    public void logout(){
        Call<LogoutResponse> call = AppMain.getClient().logout(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<LogoutResponse>() {

            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    SharedStore.getInstance().setSID(null);
                    SharedStore.getInstance().setUserId(null);
                    SharedStore.getInstance().setMyShop(null);
                    SharedStore.getInstance().setDeviceAuth(true);
                    startActivity(new Intent(getBaseContext(),LoginActivity.class));
                    finish();


                } else {
                    showToast(response.body().getError_msg());
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
            }

        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }
}
