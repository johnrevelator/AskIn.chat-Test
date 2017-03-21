package com.unated.askincht_beta.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.BusMessages.SomeMessage;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgotPasswordFragment extends SuperFragment {
    @Bind(R.id.etPasswordFog)
    EditText etPasswordFog;
    @Bind(R.id.etPasswordConfFog)
    EditText etPasswordConfFog;
    @Bind(R.id.btnFgt)
    Button fogot;
    String token;
    private NavigationInterface mInterface;

    public interface NavigationInterface {
        void onLogin();

    }
    @Override
    public int onInflateViewFragment() {
        return R.layout.fragment_forgot_password;
    }

    @Override
    public void onCreateFragment(Bundle instance) {

    }

    @Override
    public View onCreateViewFragment(View view) {

        Bundle bundle = this.getArguments();
        token = bundle.getString("tkn");return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {
        mInterface = (ForgotPasswordFragment.NavigationInterface) activity;


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
    private void preparePhone() {
        showLoading();
        Call<SimpleResponse> call = AppMain.getClient().setNewPass(etPasswordFog.getText().toString(),token, SharedStore.getInstance().getToken());
        call.enqueue(new Callback<SimpleResponse>() {

            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if(response.body().getStatus()==0&&response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Пароль восстановлен", Toast.LENGTH_SHORT).show();
                    mInterface.onLogin();

                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                preparePhone();
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
                Toast.makeText(getActivity(),"Пароль не восстановлен, попробуйте снова",Toast.LENGTH_SHORT).show();
                mInterface.onLogin();

                Log.i("MyLog", String.valueOf(t));
            }

        });
    }
    @OnClick({R.id.btnFgt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFgt:
                if (etPasswordFog.getText().toString().length() > 5) {

                    if (etPasswordFog.getText().toString().equals(etPasswordFog.getText().toString())) {
                        Log.i("MyLog", "не видит логин");
                        preparePhone();
                        break;
                    } else {
                        Toast.makeText(getActivity(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                        break;


                    }
                }else{
                    Toast.makeText(getActivity(), "Пароль должен быть минимум 6 символов длинной", Toast.LENGTH_SHORT).show();

                }



        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showCompanies(SomeMessage msg) {
        token=msg.getMsg();
        Log.i("MyLog",token+ " токен");
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
