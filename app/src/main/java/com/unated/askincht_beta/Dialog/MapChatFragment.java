package com.unated.askincht_beta.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.Activity.MainActivity;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.MyShopResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapChatFragment extends SuperDialogFragment {

    Fragment fragment;
    @Bind(R.id.redProf)

    LinearLayout redProf;
    @Bind(R.id.main_ph)
    TextView mainPh;

   /* @Bind(R.id.share_variant)

    LinearLayout share_variant;
    @Bind(R.id.socialConnect)

    LinearLayout socialConnect;*/
    @Bind(R.id.exit)

    LinearLayout exit;
    @Bind(R.id.cancel)

    LinearLayout cancel;

    @Override
    public int onInflateViewFragment() {
        return R.layout.view_more_dialog;
    }

    @Override
    public void onCreateFragment(Bundle instance) {


    }
    public void getPhoneNumber(){
        Call<MyShopResponse> call = AppMain.getClient().getMyShop(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<MyShopResponse>() {

            @Override
            public void onResponse(Call<MyShopResponse> call, Response<MyShopResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    mainPh.setText(response.body().getData().getShop().getPhone());



                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                getPhoneNumber();
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
            public void onFailure(Call<MyShopResponse> call, Throwable t) {
            }

        });
    }

    @Override
    public View onCreateViewFragment(View view) {

        return view;
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


                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
            }

        });
    }

    @Override
    public void onAttachFragment(Activity activity) {

    }
    @OnClick({R.id.cancel, R.id.exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();

                break;
            case R.id.exit:
                Call<LogoutResponse> call = AppMain.getClient().logout(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                call.enqueue(new Callback<LogoutResponse>() {

                    @Override
                    public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                        if (response.isSuccessful() && response.body().getStatus() == 0) {
                            SharedStore.getInstance().setSID(null);
                            SharedStore.getInstance().setUserId(null);
                            SharedStore.getInstance().setDeviceAuth(true);
                            startActivity(MainActivity.getIntent(getActivity()));



                        } else {
                            showToast(response.body().getError_msg());
                        }
                    }

                    @Override
                    public void onFailure(Call<LogoutResponse> call, Throwable t) {
                    }

                });
                break;

        }
    }







}
