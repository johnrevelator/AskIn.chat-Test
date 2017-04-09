package com.unated.askincht_beta.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Fragments.MyRequestsFragment;
import com.unated.askincht_beta.Fragments.UserRequestsFragment;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MoreCardUsersDialogFragment extends SuperDialogFragment {

    Fragment fragment;
    private Fragment mFragment;


   /* @Bind(R.id.share_variant)

    LinearLayout share_variant;
    @Bind(R.id.socialConnect)

    LinearLayout socialConnect;*/
    @Bind(R.id.cryr)

    LinearLayout cry;

    @Bind(R.id.cancel)

    LinearLayout cancel;
    @Bind(R.id.delete)

    LinearLayout delete;
    int reqId;
    int type;


    @Override
    public int onInflateViewFragment() {
        return R.layout.view_more_users_card_dialog;
    }

    @Override
    public void onCreateFragment(Bundle instance) {


    }

    @Override
    public View onCreateViewFragment(View view) {
        Bundle bundle = this.getArguments();
        reqId = bundle.getInt("rId");
        type= bundle.getInt("type",0);
        cry.setVisibility(View.GONE);


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


                } else {
                    showToast(response.body().getError_msg());
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
            }

        });
    }
    private void deleteMyRequest(final String request_id) {

        Call<SimpleResponse> call = AppMain.getClient().deleteMyRequest(SharedStore.getInstance().getSID(), request_id,SharedStore.getInstance().getToken());
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful()&&response.body().getStatus()==0) {
                    mFragment = new MyRequestsFragment();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment, mFragment.getClass().getName()).commitAllowingStateLoss( );
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
    private void deleteRequest(final String request_id) {
        Log.i(TAG,request_id+ " request_id");
        Call<SimpleResponse> call = AppMain.getClient().deleteRequest(SharedStore.getInstance().getSID(), request_id,SharedStore.getInstance().getToken());
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
    public void onAttachFragment(Activity activity) {

    }
    @OnClick({R.id.cancel, R.id.delete, R.id.cryr})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();

                break;
            case R.id.delete:
                Log.i("MyLog", String.valueOf(reqId));
                if(type==1)

deleteMyRequest(String.valueOf(reqId));
deleteRequest(String.valueOf(reqId));
                break;
            /*case R.id.share_variant:
                RequestDialogFragment requestDialogFragment = new  RequestDialogFragment();
                requestDialogFragment.show(getFragmentManager(), "TAG");
                break;*/
            case R.id.cryr:
                CryDialogFragment cryDialogFragment = new  CryDialogFragment();
               cryDialogFragment.show(getFragmentManager(), "TAG");
                break;


        }
    }







}
