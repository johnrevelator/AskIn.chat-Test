package com.unated.askincht_beta.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.unated.askincht_beta.Pojo.BusMessages.SomeMessage;
import com.unated.askincht_beta.R;

import butterknife.Bind;
import butterknife.OnClick;


public class EnterPhoneFragment extends SuperFragment {
    @Bind(R.id.etPhone)
    EditText etPhone;
    @Bind(R.id.btnFogot)
    Button btnFogot;

    public interface EnterNavigationInterface {
        void onForgotPassword();


    }
    private EnterNavigationInterface mInt;

    @Override
    public int onInflateViewFragment() {
        return R.layout.fragment_phone_edit;
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
        mInt = (EnterPhoneFragment.EnterNavigationInterface) activity;


    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    private void requestContactPermission() {

        int hasContactPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECEIVE_SMS);

/*
        if(hasContactPermission != PackageManager.PERMISSION_GRANTED ) {
*/
            ActivityCompat.requestPermissions(getActivity(), new String[]   {Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS}, REQUEST_CODE_ASK_PERMISSIONS);
        /*}else {
            Log.i("MyLog", "Contact permission already granted 2.");

            enter();
            //Toast.makeText(AddContactsActivity.this, "Contact Permission is already granted", Toast.LENGTH_LONG).show();
        }*/
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[]           permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                // Check if the only required permission has been granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("MyLog", "Contact permission has now been granted. Showing result.");
                    enter();
                } else {
                    Log.i("MyLog", "Contact permission was NOT granted.");
                }
                break;
        }
    }
    public void enter(){
        final Intent intent = new Intent(getActivity(), AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,

                        AccountKitActivity.ResponseType.CODE); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        PhoneNumber phoneNumber=new  PhoneNumber("+7",etPhone.getText().toString(),"RU");
        configurationBuilder.setInitialPhoneNumber(phoneNumber);
        configurationBuilder.setReadPhoneStateEnabled(true);

        configurationBuilder.setReceiveSMS(true);

        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,

                configurationBuilder.build());
        startActivityForResult(intent, 1);
    }


    @OnClick({R.id.btnFogot})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFogot:
                if(Build.VERSION.SDK_INT < 23){
                    Log.i("MyLog", "Contact permission already granted.");


                    enter();
                }else {
                    requestContactPermission();
                }





        }
    }
    String token;
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
                    Log.i("MyLog","Success stange");
                    token=loginResult.getAuthorizationCode();
                    ForgotPasswordFragment fragment = new ForgotPasswordFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("tkn", token);
                    fragment.setArguments(bundle);
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    ft.replace(R.id.container, fragment, fragment.getClass().getName());
                    ft.addToBackStack(null);
                    ft.commit();

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

    }
    @NonNull
    public SomeMessage someMessage(){
        SomeMessage someMessage=new SomeMessage();
        someMessage.setMsg(token);

        return someMessage;
    }






}
