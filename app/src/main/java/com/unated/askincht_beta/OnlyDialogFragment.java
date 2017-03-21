package com.unated.askincht_beta;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

import com.unated.askincht_beta.Dialog.SuperDialogFragment;


public class OnlyDialogFragment extends SuperDialogFragment {

    Fragment fragment;



   /* @Bind(R.id.cancel)
    ImageView cancel;*/

    @Override
    public int onInflateViewFragment() {
        return R.layout.view_only_dialog;
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();

            }
        }, 5000);

        return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {

    }




   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void vkId(VKId vkId) {
        Log.i("MyLog",vkId.getId());
        Call<Void> call = AppMain.getClient().setSocial(SharedStore.getInstance().getSID(),vkId.getId(),"vk");
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    *//*if(response.body().getData().getShop().getName()!=null) {
                        SharedStore.getInstance().setMyShop(response.body().getData().getShop());
                        desc.setText(response.body().getData().getShop().getDesc());
                        name.setText(response.body().getData().getShop().getName());
                    }
                    else {
                        name.setText("Анонимный пользователь");

                    }*//*


                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.i("MyLog", String.valueOf(t));
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
    }*/








}
