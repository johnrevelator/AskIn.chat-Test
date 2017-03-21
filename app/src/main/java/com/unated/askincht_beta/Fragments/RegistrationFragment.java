package com.unated.askincht_beta.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mukesh.countrypicker.fragments.CountryPicker;
import com.mukesh.countrypicker.interfaces.CountryPickerListener;
import com.unated.askincht_beta.Activity.MainActivity;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.AuthRegister;
import com.unated.askincht_beta.Pojo.AuthResponse;
import com.unated.askincht_beta.Pojo.BusMessages.DisconnectMessage;
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


public class RegistrationFragment extends SuperFragment {

    public interface RegistrationInterface{
        void onLogin();
    }

    @Bind(R.id.etLogin)
    EditText mEtEmail;
    @Bind(R.id.registr)
    RelativeLayout register;
    @Bind(R.id.regProg)
    ProgressBar regProg;
    @Bind(R.id.etPassword)
    EditText mEtPwd;
    @Bind(R.id.etPasswordConf)
    EditText mEtPwdRepeat;
    @Bind(R.id.btnLogin)
    Button mBtnRegister;
    @Bind(R.id.change1)
    TextView change;
    @Bind(R.id.textView5)
    TextView codeC;
AccessToken accessToken;
    private RegistrationInterface mInterface;

    @Override
    public int onInflateViewFragment() {
        return R.layout.fragment_registration;
    }

    @Override
    public void onCreateFragment(Bundle instance) {
        AccountKit.initialize(getContext());


    }

    @Override
    public View onCreateViewFragment(View view) {
       return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mEtEmail.requestFocus();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED|WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    @Override
    public void onAttachFragment(Activity activity) {
        mInterface = (RegistrationInterface) activity;
    }

    @OnClick({R.id.btnLogin,R.id.change1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (mEtPwd.getText().toString().length() > 5){
                    if (mEtPwd.getText().toString().equals(mEtPwdRepeat.getText().toString())) {

                        final Intent intent = new Intent(getActivity(), AccountKitActivity.class);
                        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                                        LoginType.PHONE,

                                        AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
                        // ... perform additional configuration ...
                        PhoneNumber phoneNumber = new PhoneNumber("+7", mEtEmail.getText().toString(), "RU");
                        configurationBuilder.setInitialPhoneNumber(phoneNumber);
                        intent.putExtra(
                                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                                configurationBuilder.build());
                        startActivityForResult(intent, 1);
                    } else {
                        Toast.makeText(getActivity(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();

                    }
        }else {
                    Toast.makeText(getActivity(), "Пароль должен быть минимум 6 символов длинной", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.change1:
                mInterface.onLogin();
                break;
            case R.id.textView5:
                final CountryPicker picker = CountryPicker.newInstance("Select Country");
                picker.show(getFragmentManager(), "COUNTRY_PICKER");
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                        codeC.setText(dialCode);
                        Log.i("MyLog",name+" "+code+" "+dialCode);
                        picker.dismiss();
                    }
                });



                break;

        }
    }
    @Override
    public void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==1) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage;
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
                Log.i("MyLog", String.valueOf(loginResult.getError()));
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {

                if (loginResult.getAccessToken() != null) {
                    Log.i("MyLog","Success simple");

                    toastMessage = "Success:" + loginResult.getAccessToken().getAccountId();


                } else {
                    regProg.setVisibility(View.VISIBLE);
                    register.setVisibility(View.GONE);
                    Log.i("MyLog","Success stange");

                    toastMessage = String.format(
                            "Success:%s...",
                            loginResult.getAuthorizationCode().substring(0,10));
                    Call<AuthRegister> call = AppMain.getClient().registerUser(codeC.getText().toString()+mEtEmail.getText().toString(), mEtPwd.getText().toString(), loginResult.getAuthorizationCode());
                    call.enqueue(new Callback<AuthRegister>() {

                        @Override
                        public void onResponse(Call<AuthRegister> call, Response<AuthRegister> response) {
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                Log.i("MyLog","Success");

                                login();
                            }
                            else{
                                if(response.body().getStatus()==1004){
                                    regProg.setVisibility(View.GONE);
                                register.setVisibility(View.VISIBLE);
                                    showToast("Номер телефона уже занят");}
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthRegister> call, Throwable t) {
                                               }

                    });
                }

                // If you have an authorization code, retrieve it from
                // loginResult.getAuthorizationCode()
                // and pass it to your server and exchange it for an access token.

                // Success! Start your next activity...
/*
                goToMyLoggedInActivity();
*/
            }


        }
    }
    private void login() {
        showLoading();
        Call<AuthResponse> call = AppMain.getClient().login("+7"+mEtEmail.getText().toString(), mEtPwd.getText().toString());
        call.enqueue(new Callback<AuthResponse>() {

            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    SharedStore.getInstance().setToken(response.body().getData().getToken());

                    SharedStore.getInstance().setSID(response.body().getData().getSid());
                    SharedStore.getInstance().setUserId(String.valueOf(response.body().getData().getUser_id()));
                    SharedStore.getInstance().setDeviceAuth(false);
                    SharedStore.getInstance().setPhone("+7"+mEtEmail.getText().toString());
                    SharedStore.getInstance().setUUID(UUID.randomUUID().toString());
                    Log.i("MyLog",UUID.randomUUID().toString());
                    SharedStore.getInstance().setWas(true);

                    SharedStore.getInstance().clearUserRequests();
                    SharedStore.getInstance().clearMyRequests();

                    EventBus.getDefault().post(new DisconnectMessage());
                    sendToken();
                } else {
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


    /*private void getMyShop() {
        showLoading();
        Call<MyShopResponse> call = AppMain.getClient().getMyShop(SharedStore.getInstance().getSID());
        call.enqueue(new Callback<MyShopResponse>() {
            @Override
            public void onResponse(Call<MyShopResponse> call, Response<MyShopResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    hideLoading();
                    SharedStore.getInstance().setMyShop(response.body().getData().getShop());
sendToken();



                } else {
                    hideLoading();
                }
            }

            @Override
            public void onFailure(Call<MyShopResponse> call, Throwable t) {
                Log.i("MyLog",t.toString());
            }
        });

    }*/
    public void toMain(){
        startActivity(new Intent(getActivity(), MainActivity.class).putExtra("type",0));
        getActivity().finish();


    }

}
