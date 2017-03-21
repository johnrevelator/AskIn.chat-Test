package com.unated.askincht_beta.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.unated.askincht_beta.Activity.MainActivity;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.AuthResponse;
import com.unated.askincht_beta.Pojo.BusMessages.DisconnectMessage;
import com.unated.askincht_beta.Pojo.MyShopResponse;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import org.greenrobot.eventbus.EventBus;

import java.util.UUID;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends SuperFragment {
    public static final String TAG = "MyLog";

    public interface LoginNavigationInterface {
        void onRegistration();
        void onEnterPhone();

        void onForgotPassword();
    }

    @Bind(R.id.etLogin)
    EditText mEtLogin;
    @Bind(R.id.etPassword)
    EditText mEtPassword;
    @Bind(R.id.btnLogin)
    Button mBtnLogin;
    @Bind(R.id.change)
    TextView change;
    @Bind(R.id.textView5)
    TextView codeC;
    @Bind(R.id.fogot)
    TextView fogot;

/*
    @Bind(R.id.tvForgot)
*/
    TextView mTvForgot;

    private LoginNavigationInterface mInterface;

    @Override
    public int onInflateViewFragment() {

        return R.layout.fragment_login;
    }

    @Override
    public void onCreateFragment(Bundle instance) {

    }

    @Override
    public View onCreateViewFragment(View view) {



        return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {
        mInterface = (LoginNavigationInterface) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        mEtLogin.requestFocus();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED|WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    @OnClick({R.id.btnLogin, R.id.change, R.id.fogot, R.id.textView5})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (mEtLogin.getText().toString().length() == 0) {
                    Log.i(TAG,"не видит логин");
/*
                    mEtLogin.setError(getString(R.string.error_empty));
*/
                    return;
                }
                if (mEtPassword.getText().toString().length() == 0) {
                    Log.i(TAG,"не видит пасс");

/*
                    mEtPassword.setError(getString(R.string.error_empty));
*/
                    return;
                }
                Log.i(TAG,"клик был");
                login();
                break;

            case R.id.fogot:
                mInterface.onEnterPhone();


                break;
            case R.id.textView5:
                final CountryPicker picker = CountryPicker.newInstance("Select Country");
                picker.show(getFragmentManager(), "COUNTRY_PICKER");
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        codeC.setText(dialCode);
                        Log.i(TAG,name+" "+code+" "+dialCode);
                        picker.dismiss();
                    }
                });



                break;
            case R.id.change:
                mInterface.onRegistration();

                break;
        }
    }

    private void login() {

        showLoading();
        Call<AuthResponse> call = AppMain.getClient().login("+7"+mEtLogin.getText().toString(), mEtPassword.getText().toString());
        call.enqueue(new Callback<AuthResponse>() {

            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    SharedStore.getInstance().setToken(response.body().getData().getToken());
                    SharedStore.getInstance().setSID(response.body().getData().getSid());
                    SharedStore.getInstance().setUserId(String.valueOf(response.body().getData().getUser_id()));
                    SharedStore.getInstance().setDeviceAuth(false);
                    SharedStore.getInstance().setPhone(codeC.getText().toString()+mEtLogin.getText().toString());
                    SharedStore.getInstance().setUUID(UUID.randomUUID().toString());
                    Log.i(TAG,UUID.randomUUID().toString());
                    SharedStore.getInstance().clearUserRequests();
                    SharedStore.getInstance().setWas(true);
                    SharedStore.getInstance().clearMyRequests();

                    EventBus.getDefault().post(new DisconnectMessage());
sendToken();
                    getMyShop();
                } else if(response.body().getStatus() == 1005) {
                    Toast.makeText(getActivity(),"Неверный логин или пароль",Toast.LENGTH_SHORT).show();
                    hideLoading();
                }
                else
                 {
                    showToast(response.body().getError_msg());
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                hideLoading();
            }

        });
    }

    private void sendToken() {
        showLoading();

        Call<SimpleResponse> call = AppMain.getClient().saveToken(SharedStore.getInstance().getSID(), FirebaseInstanceId.getInstance().getToken(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                hideLoading();


            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {

            }
        });
        toMain();
    }


    private void getMyShop() {
        showLoading();
        Call<MyShopResponse> call = AppMain.getClient().getMyShop(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<MyShopResponse>() {
            @Override
            public void onResponse(Call<MyShopResponse> call, Response<MyShopResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    hideLoading();
                    SharedStore.getInstance().setMyShop(response.body().getData().getShop());



                } else {
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<MyShopResponse> call, Throwable t) {
                Log.i("MyLog",t.toString());
            }
        });

    }
    public void toMain(){
        startActivity(new Intent(getActivity(), MainActivity.class).putExtra("type",0));
        getActivity().finish();


    }


}
