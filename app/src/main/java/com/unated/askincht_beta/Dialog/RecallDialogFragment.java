package com.unated.askincht_beta.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.Adapter.RecallAdapter;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.OnlyDialogFragment;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.Recall;
import com.unated.askincht_beta.Pojo.RecallsResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecallDialogFragment extends SuperDialogFragment {

    Fragment fragment;
    @Bind(R.id.redProf)

    LinearLayout redProf;
    @Bind(R.id.rv_recall)
    RecyclerView recallView;
    @Bind(R.id.main_send)
    TextView textView;
    @Bind(R.id.main_name)
    EditText text;
    @Bind(R.id.ratingBar)
    RatingBar ratingBar;

    private List<Recall> mRequestItems;
    private RecallAdapter mAdapter;

   /* @Bind(R.id.share_variant)

    LinearLayout share_variant;
    @Bind(R.id.socialConnect)

    LinearLayout socialConnect;*/
   /* @Bind(R.id.cry)

    LinearLayout cry;
    @Bind(R.id.elect)

    LinearLayout elect;
    @Bind(R.id.recall)

    LinearLayout recall;
    @Bind(R.id.cancel)

    LinearLayout cancel;
    @Bind(R.id.delete)

    LinearLayout delete;
  @Bind(R.id.main_name)

    TextView name;
  @Bind(R.id.main_desc)

  TextView desc;*/
    int myInt;
    int rId;

    @Override
    public int onInflateViewFragment() {
        return R.layout.view_recall_dialog;
    }

    @Override
    public void onCreateFragment(Bundle instance) {
        Bundle bundle = this.getArguments();
        myInt = bundle.getInt("id", 0);
       rId = bundle.getInt("rId", 0);
        mRequestItems = new ArrayList<>();
        mAdapter = new RecallAdapter(getActivity(), mRequestItems);


    }

    @Override
    public void onResume() {
        super.onResume();
        getMyRecalls();
    }


    @Override
    public View onCreateViewFragment(View view) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recallView.setLayoutManager(linearLayoutManager);
        recallView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {

    }
    @OnClick({R.id.main_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_send:
setMyRecalls();
                break;


        }
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
    private void setMyRecalls() {
        if(!text.getText().toString().equals("")&&ratingBar.getRating()!=0.0) {
            Call<RecallsResponse> myRequestsCall = AppMain.getClient().setRecall(SharedStore.getInstance().getSID(), myInt, rId, text.getText().toString(), ratingBar.getRating(),SharedStore.getInstance().getToken());
            myRequestsCall.enqueue(new Callback<RecallsResponse>() {
                @Override
                public void onResponse(Call<RecallsResponse> call, Response<RecallsResponse> response) {
                    if (response.isSuccessful() && response.body().getStatus() == 0) {
                   /* mRequestItems.clear();
                    mRequestItems.addAll(response.body().getData().getRecalls());*/

                        //Collections.sort(mRequestItems, new MsgTimeComparator());
/*
                    mAdapter.notifyDataSetChanged();
*/
                        getMyRecalls();
                        text.setText("");
                    } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                        AppMain.setIsRefresh(true);
                        Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                        callr.enqueue(new Callback<RefreshResponse>() {
                            @Override
                            public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                                closeProgressDialog();
                                if (response.isSuccessful() && response.body().getStatus() == 0) {
                                    SharedStore.getInstance().setToken(response.body().getData().getToken());
                                    setMyRecalls();
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
                    }else if (response.body().getStatus() > 0) {

                        OnlyDialogFragment requestDialogFragment = new OnlyDialogFragment();
                        requestDialogFragment.show(getFragmentManager(), "TAG");
                    }

                }

                @Override
                public void onFailure(Call<RecallsResponse> call, Throwable t) {
                }

            });
        }
        else{
            Toast.makeText(getActivity(),"Оставьте отзыв и рейтинг",Toast.LENGTH_SHORT).show();
        }
    }

    private void getMyRecalls() {
        Call<RecallsResponse> myRequestsCall = AppMain.getClient().getRecall(SharedStore.getInstance().getSID(), String.valueOf(myInt),SharedStore.getInstance().getToken());
        myRequestsCall.enqueue(new Callback<RecallsResponse>() {
            @Override
            public void onResponse(Call<RecallsResponse> call, Response<RecallsResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    mRequestItems.clear();
                    if(response.body().getData().getShop_rating()!=null){
                    mRequestItems.addAll(response.body().getData().getRecalls());

                    //Collections.sort(mRequestItems, new MsgTimeComparator());
                    mAdapter.notifyDataSetChanged();}
                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                getMyRecalls();
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
            public void onFailure(Call<RecallsResponse> call, Throwable t) {
            }

        });
    }






}
