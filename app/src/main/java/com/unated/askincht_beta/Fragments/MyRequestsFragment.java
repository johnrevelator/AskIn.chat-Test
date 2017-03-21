package com.unated.askincht_beta.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.Activity.ShopListActivity;
import com.unated.askincht_beta.Adapter.MyRequestsAdapter;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Dialog.RequestOptionsDialogFragment;
import com.unated.askincht_beta.Pojo.BusMessages.CounterMessage;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.BusMessages.MessageItem;
import com.unated.askincht_beta.Pojo.MyRequestItem;
import com.unated.askincht_beta.Pojo.MyRequestResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyRequestsFragment extends SuperFragment {

    @Bind(R.id.rvRequests)
    RecyclerView rvRequests;
    @Bind(R.id.nope)
    RelativeLayout nope;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private List<MyRequestItem> mRequestItems;
    private MyRequestsAdapter mAdapter;

    AlertDialog.Builder ad;
    private String currentReqId;

    @Override
    public int onInflateViewFragment() {
        return R.layout.fragment_my_requests;
    }

    @Override
    public void onCreateFragment(Bundle instance) {
        mRequestItems = new ArrayList<>();
        mAdapter = new MyRequestsAdapter(getActivity(), mRequestItems);


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
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyRequests();
    }

    @Override
    public View onCreateViewFragment(View view) {
        EventBus.getDefault().post(new CounterMessage(false));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvRequests.setLayoutManager(linearLayoutManager);
        rvRequests.setAdapter(mAdapter);

        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] tmp = view.getTag().toString().split(";");
                for (int i = 0; i < mRequestItems.size(); i++) {
                    if(mRequestItems.get(i).getShops().size()>0){
                    if (mRequestItems.get(i).getId() == Integer.valueOf(tmp[0]).intValue()&&mRequestItems.get(i).getShops().get(0).getCnt_messages() >1 ) {
                        ShopListFragment shopListFragment=new ShopListFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("extra_item", mRequestItems.get(i).getId() );
                        shopListFragment.setArguments(bundle);
Log.i("MyLog", String.valueOf(mRequestItems.get(i).getId()));
                        getFragmentManager().beginTransaction().replace(R.id.container,shopListFragment, shopListFragment.getClass().getName()).commit();


                        break;
                    }
                }
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh
                getMyRequests();

                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
        return view;

    }

    @Override
    public void onAttachFragment(Activity activity) {

    }

    private void callOptionsDialog(String title) {
        RequestOptionsDialogFragment requestOptionsDialogFragment = RequestOptionsDialogFragment.newInstance(title);
        requestOptionsDialogFragment.setTargetFragment(MyRequestsFragment.this, 110);
        requestOptionsDialogFragment.show(getActivity().getSupportFragmentManager(), "TAG");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 110:
                    deleteRequest(currentReqId);
                    break;
            }
        }
    }

    private void deleteRequest(final String request_id) {
        showProgressDialog();
        Call<SimpleResponse> call = AppMain.getClient().deleteMyRequest(SharedStore.getInstance().getSID(), request_id,SharedStore.getInstance().getToken());
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful()&&response.body().getStatus()==0) {
                    getMyRequests();
                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                deleteRequest(request_id);
                                AppMain.setIsRefresh(false);


                            }else if(response.body().getStatus() == 1029){
                                AppMain.setIsRefresh(false);

                                logout();
                            }
                        }

                        @Override
                        public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                            closeProgressDialog();
                        }
                    });
                }else  if(response.body().getStatus() == 1027||response.body().getStatus() == 1028||response.body().getStatus() == 1029) {


                    logout();
                }
            else {
                    try {
                        showToast(response.errorBody().string());
                        closeProgressDialog();
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                closeProgressDialog();
            }
        });
    }

    public class MsgTimeComparator implements Comparator<MyRequestItem> {
        @Override
        public int compare(MyRequestItem myRequestItem, MyRequestItem t1) {
            if (myRequestItem.getId() > t1.getId())
                return -1;
            else if (myRequestItem.getId() < t1.getId())
                return +1;
            return 0;
        }
    }

    private void getMyRequests() {
        showProgressDialog();
        Call<MyRequestResponse> myRequestsCall = AppMain.getClient().getMyRequestList(SharedStore.getInstance().getSID(),true,SharedStore.getInstance().getToken());
        myRequestsCall.enqueue(new Callback<MyRequestResponse>() {
            @Override
            public void onResponse(Call<MyRequestResponse> call, Response<MyRequestResponse> response) {
                closeProgressDialog();
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    mRequestItems.clear();
                    mRequestItems.addAll(response.body().getData().getRequests());

                    //Collections.sort(mRequestItems, new MsgTimeComparator());
                    mAdapter.notifyDataSetChanged();
                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                AppMain.setIsRefresh(true);
                Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                callr.enqueue(new Callback<RefreshResponse>() {
                    @Override
                    public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                        closeProgressDialog();
                        if (response.isSuccessful() && response.body().getStatus() == 0) {
                            SharedStore.getInstance().setToken(response.body().getData().getToken());
                            getMyRequests();
                            AppMain.setIsRefresh(false);


                        }else if(response.body().getStatus() == 1029){
                            AppMain.setIsRefresh(false);

                            logout();
                        }
                    }

                    @Override
                    public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                        closeProgressDialog();
                    }
                });
            }else  if(response.body().getStatus() == 1027||response.body().getStatus() == 1028||response.body().getStatus() == 1029) {


                logout();
            }
                if(response.body().getData().getRequests().size()==0)
                    nope.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<MyRequestResponse> call, Throwable t) {
                closeProgressDialog();
            }

        });
    }
    public void logout(){
        Call<LogoutResponse> call = AppMain.getClient().logout(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<LogoutResponse>() {

            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    SharedStore.getInstance().setSID(null);
                    SharedStore.getInstance().setUserId(null);
                    SharedStore.getInstance().setMyShop(null);
                    SharedStore.getInstance().setDeviceAuth(true);
                    startActivity(new Intent(getActivity(),LoginActivity.class));
                    getActivity().finish();


                } else {
                    showToast(response.body().getError_msg());
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
            }

        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageItem messageItem) {
        getMyRequests();
    }
}
