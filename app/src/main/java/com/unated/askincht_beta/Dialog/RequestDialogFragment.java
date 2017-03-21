package com.unated.askincht_beta.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.BusMessages.VKId;
import com.unated.askincht_beta.Pojo.IsSocialResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;
import com.vk.sdk.VKSdk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RequestDialogFragment extends SuperDialogFragment {

    Fragment fragment;


  @Bind(R.id.facebook)
    ImageView facebook;
  @Bind(R.id.vk)
    ImageView vk;
   /* @Bind(R.id.cancel)
    ImageView cancel;*/

    @Override
    public int onInflateViewFragment() {
        return R.layout.view_request_dialog;
    }

    @Override
    public void onCreateFragment(Bundle instance) {



    }
    /*final String[] sMyScope = new String[]{
            VKScope.DIRECT,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS,
            VKScope.MESSAGES,
            VKScope.DOCS
    };*/




    @Override
    public View onCreateViewFragment(View view) {



        return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {

    }
    public void bindSocial(){
        Call<IsSocialResponse> call = AppMain.getClient().isSocial(SharedStore.getInstance().getSID(),"vk",SharedStore.getInstance().getToken());
        call.enqueue(new Callback<IsSocialResponse>() {
            @Override
            public void onResponse(Call<IsSocialResponse> call, Response<IsSocialResponse> response) {
                if (response.isSuccessful()&&response.body().getStatus()==0) {
                    if(!response.body().getData().isBind())
                        VKSdk.login(getActivity());
                    else
                        Toast.makeText(getActivity(),"Вы уже связали эту соц. сеть",Toast.LENGTH_SHORT).show();

                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                bindSocial();
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
            public void onFailure(Call<IsSocialResponse> call, Throwable t) {
                Log.i("MyLog", String.valueOf(t));
            }
        });


    }

    @OnClick({ R.id.facebook, R.id.vk})
    public void onClick(View view) {
        switch (view.getId()) {
         /*   case R.id.cancel:
                dismiss();

                break;*/
            case R.id.vk:
                bindSocial();

               /* Intent intent = new Intent(getActivity(), VKServiceActivity.class);
                intent.putExtra("arg1", "Authorization");
                ArrayList scopes = new ArrayList<>();
                scopes.add(VKScope.OFFLINE);
                intent.putStringArrayListExtra("arg2", scopes);
                intent.putExtra("arg4", VKSdk.isCustomInitialize());
                startActivityForResult(intent, VKServiceActivity.VKServiceType.Authorization.getOuterCode());
*/

                break;
            case R.id.facebook:

                break;

        }

    }
    public void subscribeToVk(final VKId vkId){
        Call<SimpleResponse> call = AppMain.getClient().setSocial(SharedStore.getInstance().getSID(),vkId.getId(),"vk",SharedStore.getInstance().getToken());
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful()&&response.body().getStatus()==0) {
                    /*if(response.body().getData().getShop().getName()!=null) {
                        SharedStore.getInstance().setMyShop(response.body().getData().getShop());
                        desc.setText(response.body().getData().getShop().getDesc());
                        name.setText(response.body().getData().getShop().getName());
                    }
                    else {
                        name.setText("Анонимный пользователь");

                    }*/
                    DoneDialogFragment requestDialogFragment = new DoneDialogFragment();
                    requestDialogFragment.show(getFragmentManager(), "TAG");


                }else  if(response.body().getStatus() == 1026) {

                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                subscribeToVk(vkId);
                            }
                        }

                        @Override
                        public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                            closeProgressDialog();
                        }
                    });
                }else  if(response.body().getStatus() == 1027||response.body().getStatus() == 1028) {


                    logout();
                }

            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.i("MyLog", String.valueOf(t));
            }
        });

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void vkId(VKId vkId) {
        Log.i("MyLog",vkId.getId());
        subscribeToVk(vkId);


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
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }








}
