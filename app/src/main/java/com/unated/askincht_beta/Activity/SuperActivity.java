package com.unated.askincht_beta.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.testfairy.TestFairy;
import com.unated.askincht_beta.R;

import butterknife.ButterKnife;


public abstract class SuperActivity extends AppCompatActivity {
    protected String TAG = "MyLog";
    private ProgressDialog mProgressDialog;
    protected Toolbar mToolbar;

    protected abstract int getContentViewId();

    protected abstract void onCreateActivity(Bundle savedInstanceState);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TestFairy.begin(this, "58cf3592c89bdbf8fa1266af0500da802fb10f8d");

        Log.d(TAG, "* onCreateActivity");
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        mToolbar = ButterKnife.findById(this, R.id.appBar);
        if (mToolbar != null)
            setSupportActionBar(mToolbar);
        onCreateActivity(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "* onResumeActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "* onDestroyActivity");
    }
    protected void finAct(){
        finish();
    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "* onPause");
        hideSoftKeyboard();
    }

    public void showToast(String text) {
        if (text == null)
            return;
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "toast: " + text);
    }

    public void showToast(int res) {
        showToast(getString(res));
    }

    protected void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (!(this instanceof MainActivity) && id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void setFragment(int containerId, Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(containerId, fragment, fragment.getClass().getName()).commitAllowingStateLoss( );
    }


    protected void setFragment(int containerId, Fragment fragment, boolean b) {
        getSupportFragmentManager().beginTransaction().replace(containerId, fragment).addToBackStack("fragment").commitAllowingStateLoss( );
    }

    protected void setFragmentAnimated(int containerId, Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.container, fragment, fragment.getClass().getName());
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss( );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void showProgressDialog(String s) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.setMessage(s);
        mProgressDialog.show();
    }


    protected void showProgressDialog(int res) {
        showProgressDialog(getString(res));
    }

    protected Boolean getProgressDialogState() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    protected void closeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}

