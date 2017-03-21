package com.unated.askincht_beta.Dialog;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.unated.askincht_beta.Activity.SuperActivity;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.View.LoadingDialog;

import butterknife.ButterKnife;


public abstract class SuperDialogFragment extends DialogFragment {
    protected String TAG = getClass().getSimpleName();

    public abstract int onInflateViewFragment();

    public abstract void onCreateFragment(Bundle instance);

    public abstract View onCreateViewFragment(View view);

    public abstract void onAttachFragment(Activity activity);
    private View view;
    ProgressBar mDilatingDotsProgressBar;


    private SuperActivity mActivity;

    private LinearLayout ll;

    private LoadingDialog loadingDialog;

    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.50f;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "* onCreateFragment");
        onCreateFragment(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "* onCreateViewFragment");
        View view = inflater.inflate(onInflateViewFragment(), container, false);

        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        View pbView = inflater.inflate(R.layout.layout_progress_bar, null);
        mDilatingDotsProgressBar = ButterKnife.findById(pbView, R.id.progress);


        ll = (LinearLayout) pbView.findViewById(R.id.llRoot);

        ll.addView(view);

        ButterKnife.bind(this, view);
        view = onCreateViewFragment(view);
        return pbView;
    }
    public void showLoading() {
        mDilatingDotsProgressBar.setVisibility(View.VISIBLE);
        ll.setVisibility(View.GONE);
    }

    public void hideLoading() {
        mDilatingDotsProgressBar.setVisibility(View.GONE);
        ll.setVisibility(View.VISIBLE);
    }

    protected void showProgressDialog() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.setCancelable(false);
        }
        loadingDialog.show();
    }

    protected void closeProgressDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (SuperActivity) activity;
        onAttachFragment(activity);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "* onResumeFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "%* onDestroyFragment");
        ButterKnife.unbind(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "* onPauseFragment");
    }

    protected void showToast(String text) {
        mActivity.showToast(text);
    }

    protected void showToast(int res) {
        mActivity.showToast(res);
    }


}
