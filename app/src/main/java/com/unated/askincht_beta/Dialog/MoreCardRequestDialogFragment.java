package com.unated.askincht_beta.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.Shop;
import com.unated.askincht_beta.Pojo.ShopResponse;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MoreCardRequestDialogFragment extends SuperDialogFragment {

    Fragment fragment;
    @Bind(R.id.redProf)

    LinearLayout redProf;
    List <Shop> mElectShopList;

   /* @Bind(R.id.share_variant)

    LinearLayout share_variant;
    @Bind(R.id.socialConnect)

    LinearLayout socialConnect;*/


    @Bind(R.id.cancel)

    LinearLayout cancel;
    @Bind(R.id.delete)

    LinearLayout delete;

    int myInt;
    int rId;

    @Override
    public int onInflateViewFragment() {
        return R.layout.view_request_dialog;
    }

    @Override
    public void onCreateFragment(Bundle instance) {
        Bundle bundle = this.getArguments();
        myInt = bundle.getInt("id", 0);
        rId = bundle.getInt("rId", 0);
       /* Call<ElectResponse> call = AppMain.getClient().getElect(SharedStore.getInstance().getSID());
        call.enqueue(new Callback<ElectResponse>() {
            @Override
            public void onResponse(Call<ElectResponse> call, Response<ElectResponse> response) {
                if (response.isSuccessful() && response.body().getData() != null) {
                    mElectShopList = new ArrayList<Shop>();
                    mElectShopList.clear();

                    mElectShopList.addAll(response.body().getData().getRequests());
                    for (int i = 0; i < mElectShopList.size(); i++) {
                        if (myInt == mElectShopList.get(i).getId())
*//*
                            elText.setText("Убрать из избранных");
*//*

                    }

                }
                else{
                    Log.i(TAG,"null");

                }
            }

            @Override
            public void onFailure(Call<ElectResponse> call, Throwable t) {

            }
        });*/


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
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                    getActivity().finish();


                } else {
                    showToast(response.body().getError_msg());
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
            }

        });
    }

    private void deleteRequest(final String request_id) {

        Call<SimpleResponse> call = AppMain.getClient().deleteMyRequest(SharedStore.getInstance().getSID(), request_id,SharedStore.getInstance().getToken());
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful()&&response.body().getStatus()==0) {
                    dismiss();
                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                deleteRequest(request_id);
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
            public void onFailure(Call<SimpleResponse> call, Throwable t) {

            }
        });
    }


    @Override
    public View onCreateViewFragment(View view) {



        return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {

    }
    @OnClick({R.id.cancel, R.id.recall, R.id.cryr, R.id.delete/*, R.id.elect*/})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();

                break;

            case R.id.delete:
deleteRequest(String.valueOf(rId));
                break;
           /* case R.id.elect:
                if(elText.getText().toString().equals("Убрать из избранных")){
                    elText.setText("Добавить в избранные");
                    Call<Void> call = AppMain.getClient().unSetElect(SharedStore.getInstance().getSID(),myInt);
                    Log.i(TAG,SharedStore.getInstance().getLastRequestId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {


                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.i(TAG, String.valueOf(t));


                        }
                    });
                }
                else{
                    elText.setText("Убрать из избранных");
                    Call<Void> call = AppMain.getClient().setElect(SharedStore.getInstance().getSID(),myInt);
                    Log.i(TAG,SharedStore.getInstance().getLastRequestId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {


                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.i(TAG, String.valueOf(t));

                        }
                    });

                }



                break;
*/
        }
    }







}
