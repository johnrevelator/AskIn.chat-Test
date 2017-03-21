package com.unated.askincht_beta.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.unated.askincht_beta.Adapter.ShopsElectAdapter;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.ElectResponse;
import com.unated.askincht_beta.Pojo.MyRequestItem;
import com.unated.askincht_beta.Pojo.Shop;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ElectFragment extends SuperFragment {
    private MyRequestItem mMyRequestItem;
    public static final String TAG = "MyLog";
    public TextView nope;
    private ShopsElectAdapter mAdapter;
    private List<Shop> mShopList;
    @Bind(R.id.rvShop)
    RecyclerView rvShops;
    @Override
    public int onInflateViewFragment() {
        return R.layout.fragment_elect;
    }

    @Override
    public void onCreateFragment(Bundle instance) {

            mShopList = new ArrayList<>();
    }
    private void getShops() {
        showProgressDialog();
        Call<ElectResponse> call = AppMain.getClient().getElect(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<ElectResponse>() {
            @Override
            public void onResponse(Call<ElectResponse> call, Response<ElectResponse> response) {
                closeProgressDialog();
                if (response.isSuccessful()) {
                    Log.i(TAG,"notnull");
                    Log.i(TAG,response.body().getData().toString());
                    mShopList.clear();
/*
                    tvSearchResult.setText("Нашлось "+response.body().getData().size()+" компаний");
*/
                    mShopList.addAll(response.body().getData().getRequests());
                    if(response.body().getData().getRequests().size()==0)
                        nope.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                }
                else{
                    Log.i(TAG,"null");

                }
            }

            @Override
            public void onFailure(Call<ElectResponse> call, Throwable t) {
                closeProgressDialog();
            }
        });}


    @Override
    public View onCreateViewFragment(View view) {

        mAdapter = new ShopsElectAdapter(getActivity(), mShopList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvShops.setLayoutManager(linearLayoutManager);
        nope=(TextView)view.findViewById(R.id.textView2);
        rvShops.setAdapter(mAdapter);return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        getShops();
    }

    @Override
    public void onAttachFragment(Activity activity) {

    }
}
