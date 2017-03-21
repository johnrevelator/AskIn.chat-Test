package com.unated.askincht_beta.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.unated.askincht_beta.Activity.ChatActivity;
import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.Adapter.UserRequestsAdapter;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Dialog.MoreCardUsersDialogFragment;
import com.unated.askincht_beta.Dialog.RequestOptionsDialogFragment;
import com.unated.askincht_beta.Pojo.BusMessages.CounterMessage;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.BusMessages.MessageItem;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.RequestItem;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.Pojo.UserRequestResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRequestsFragment extends SuperFragment {
    public static final String TAG = "MyLog";

    @Bind(R.id.rvUserRequests)
    RecyclerView rvUserRequests;
    @Bind(R.id.nope)
    RelativeLayout nope;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    private UserRequestsAdapter mUserRequestsAdapter;
    private List<RequestItem> mItemList;

    private String currentReqId = "";

    @Override
    public int onInflateViewFragment() {
        return R.layout.fragment_user_requests;
    }

    @Override
    public void onCreateFragment(Bundle instance) {
        mItemList = new ArrayList<>();
        mUserRequestsAdapter = new UserRequestsAdapter(getActivity(), mItemList);
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
        getRequests();
    }

    @Override
    public View onCreateViewFragment(View view) {
        EventBus.getDefault().post(new CounterMessage(false));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvUserRequests.setLayoutManager(linearLayoutManager);
        rvUserRequests.setAdapter(mUserRequestsAdapter);

        mUserRequestsAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] tmp = view.getTag().toString().split(";");

                switch (view.getId()){
                    case R.id.llParent:
                        String EXTRA_REQUEST = "extra_request";
                        String EXTRA_SHOP = "extra_shop";
                        String EXTRA_TITLE = "extra_title";
                        String EXTRA_ACT = "extra_act";

                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra(EXTRA_REQUEST,tmp[2]);
                        intent.putExtra(EXTRA_SHOP,view.getTag().toString());
                        intent.putExtra(EXTRA_ACT,"no_push");
                        startActivity(intent);
                        getActivity().finish();
                        break;
                    case R.id.more:
                        MoreCardUsersDialogFragment moreCardDialogFragment = new   MoreCardUsersDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", Integer.parseInt(tmp[0]));
                        bundle.putInt("rId",Integer.parseInt(tmp[2]));
                        moreCardDialogFragment.setArguments(bundle);
                        moreCardDialogFragment.show(getFragmentManager(), "TAG");
                        break;

                }

/*
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, ChatFragment.newInstance(tmp[0], tmp[1])).commit();
*/

            }
        });

     /*   mUserRequestsAdapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String[] tmp = view.getTag().toString().split(";");
                currentReqId = tmp[0];
                callOptionsDialog(tmp[2]);
                return false;
            }
        });*/
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh
                getRequests();

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
        requestOptionsDialogFragment.setTargetFragment(UserRequestsFragment.this, 110);
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
        Call<SimpleResponse> call = AppMain.getClient().deleteRequest(SharedStore.getInstance().getSID(), request_id,SharedStore.getInstance().getToken());
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful()&&response.body().getStatus()==0) {
                    getRequests();
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
                }else {
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

    private void getRequests() {
        showProgressDialog();
        Call<UserRequestResponse> call = AppMain.getClient().getUserRequests(SharedStore.getInstance().getSID(),true,SharedStore.getInstance().getToken());
        Log.i(TAG,SharedStore.getInstance().getLastRequestId());
        call.enqueue(new Callback<UserRequestResponse>() {
            @Override
            public void onResponse(Call<UserRequestResponse> call, Response<UserRequestResponse> response) {
                mItemList.clear();
                Log.i(TAG,"1");

                if(response.body().getStatus()==0) {
                    if (response.body().getData().getRequests().size() > 0)
                        mItemList.addAll(response.body().getData().getRequests());
                    if (mItemList.size() == 0)
                        nope.setVisibility(View.VISIBLE);
                    Log.i(TAG, String.valueOf(mItemList.size()));


                    mUserRequestsAdapter.notifyDataSetChanged();
                    closeProgressDialog();
                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                getRequests();
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
            }

            @Override
            public void onFailure(Call<UserRequestResponse> call, Throwable t) {
                closeProgressDialog();

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MessageItem messageItem) {
        getRequests();
    }
}
