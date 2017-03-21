package com.unated.askincht_beta.Dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.unated.askincht_beta.R;

import butterknife.Bind;
import butterknife.OnClick;


public class CryDialogFragment extends SuperDialogFragment {

    Fragment fragment;


   /* @Bind(R.id.share_variant)

    LinearLayout share_variant;
    @Bind(R.id.socialConnect)

    LinearLayout socialConnect;*/

  @Bind(R.id.cry)

  TextView cry;

    @Override
    public int onInflateViewFragment() {
        return R.layout.view_cry_dialog;
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

    }
    @OnClick({R.id.cry})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cry:
                dismiss();

                break;


        }
    }







}
