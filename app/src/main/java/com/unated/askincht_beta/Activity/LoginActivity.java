package com.unated.askincht_beta.Activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.unated.askincht_beta.Fragments.EnterPhoneFragment;
import com.unated.askincht_beta.Fragments.ForgotPasswordFragment;
import com.unated.askincht_beta.Fragments.LoginFragment;
import com.unated.askincht_beta.Fragments.MainFragment;
import com.unated.askincht_beta.Fragments.MyRequestsFragment;
import com.unated.askincht_beta.Fragments.RegistrationFragment;
import com.unated.askincht_beta.Fragments.ShopListFragment;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import java.util.List;


public class LoginActivity extends SuperActivity implements LoginFragment.LoginNavigationInterface, RegistrationFragment.RegistrationInterface,EnterPhoneFragment.EnterNavigationInterface,ForgotPasswordFragment.NavigationInterface {
    private boolean doubleBackToExitPressedOnce = false;
    private Fragment mFragment;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_base;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        if(SharedStore.getInstance().getWas())
            setFragmentAnimated(R.id.container, new LoginFragment());
        else
            setFragmentAnimated(R.id.container, new RegistrationFragment());

    }



    @Override
    public void onRegistration() {
        setFragmentAnimated(R.id.container, new RegistrationFragment());
    }
    @Override
    public void onEnterPhone() {
        setFragmentAnimated(R.id.container, new EnterPhoneFragment());
    }

    @Override
    public void onForgotPassword() {
        setFragmentAnimated(R.id.container, new ForgotPasswordFragment());
    }

    @Override
    public void onBackPressed() {
        ForgotPasswordFragment myFragment = ( ForgotPasswordFragment)   getSupportFragmentManager().findFragmentByTag( ForgotPasswordFragment.class.getName());
        EnterPhoneFragment phFragment = ( EnterPhoneFragment)   getSupportFragmentManager().findFragmentByTag(EnterPhoneFragment.class.getName());

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.back);
        switch( am.getRingerMode() ) {
            case AudioManager.RINGER_MODE_NORMAL:
                mediaPlayer.start();
                break;
        }
        if (phFragment != null && phFragment.isVisible()) {
            onLogin();
        }else if (myFragment != null && myFragment.isVisible()) {
onEnterPhone();        }

            else {
                if (doubleBackToExitPressedOnce) {
                    System.exit(0);
                    return;
                }

                doubleBackToExitPressedOnce = true;
                showToast("Нажмите 'Назад' еще раз для выхода");

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);        }




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

    @Override
    public void onLogin() {
        setFragmentAnimated(R.id.container, new LoginFragment());
    }
}
