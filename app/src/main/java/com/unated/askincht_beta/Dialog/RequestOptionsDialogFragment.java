package com.unated.askincht_beta.Dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.unated.askincht_beta.R;

import butterknife.Bind;
import butterknife.OnClick;


public class RequestOptionsDialogFragment extends SuperDialogFragment {

    @Bind(R.id.tvTitle)
    TextView tvTitle;

    private String mTitle;

    public static RequestOptionsDialogFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        RequestOptionsDialogFragment fragment = new RequestOptionsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int onInflateViewFragment() {
        return R.layout.dialog_request_options;
    }

    @Override
    public void onCreateFragment(Bundle instance) {

    }

    @Override
    public View onCreateViewFragment(View view) {
        tvTitle.setText(mTitle);
        return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {
        if (getArguments() != null) {
            mTitle = getArguments().getString("title");
        }
    }

    @OnClick(R.id.btnDeleteRequest)
    void onDeleteClicked() {
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        dismiss();
    }
}
