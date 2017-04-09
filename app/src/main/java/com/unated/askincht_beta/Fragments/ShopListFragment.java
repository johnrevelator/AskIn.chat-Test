package com.unated.askincht_beta.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.unated.askincht_beta.Activity.ChatActivity;
import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.Adapter.ShopsAdapter;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Dialog.MoreCardDialogFragment;
import com.unated.askincht_beta.Pojo.BusMessages.CounterMessage;
import com.unated.askincht_beta.Pojo.BusMessages.IncreaseRadius;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.BusMessages.MessageItem;
import com.unated.askincht_beta.Pojo.BusMessages.rId;
import com.unated.askincht_beta.Pojo.ElectResponse;
import com.unated.askincht_beta.Pojo.MyRequestItem;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.Shop;
import com.unated.askincht_beta.Pojo.ShopsResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopListFragment extends SuperFragment {

    public static final String EXTRA = "extra_search_item";
    public static final String TAG = "MyLog";
    ProgressDialog progress;
    AlertDialog.Builder ad;
    Context context;
    @Bind(R.id.rvShops)
    RecyclerView rvShops;
    @Bind(R.id.tvSearchResult)
    TextView tvSearchResult;

    TextView moreB;

    private int mMyRequestItem;
    private ShopsAdapter mAdapter;
    private List<Shop> mShopList;
    private List<Shop> mElectShopList;

    public static ShopListFragment newInstance(int searchItem) {
        Bundle args = new Bundle();
        args.putInt(EXTRA, searchItem);
        ShopListFragment fragment = new ShopListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int onInflateViewFragment() {
        return R.layout.fragment_shop_list;
    }

    @Override
    public void onCreateFragment(Bundle instance) {
        mShopList = new ArrayList<>();
        mElectShopList = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        getShops();
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

    private void getShops() {
        showProgressDialog();
        Call<ShopsResponse> call = AppMain.getClient().getShops(SharedStore.getInstance().getSID(), String.valueOf(mMyRequestItem),true,SharedStore.getInstance().getToken());
        call.enqueue(new Callback<ShopsResponse>() {
            @Override
            public void onResponse(Call<ShopsResponse> call, Response<ShopsResponse> response) {
                closeProgressDialog();
                if (response.isSuccessful() && response.body().getStatus()==0) {
                    if (response.isSuccessful() && response.body().getData() != null) {
                        Log.i(TAG, "notnull");
                        Log.i(TAG, response.body().getData().toString());
                        mShopList.clear();
/*
                    tvSearchResult.setText("Нашлось "+response.body().getData().size()+" компаний");
*/
                        mShopList.addAll(response.body().getData());
                        mAdapter.setData(String.valueOf(mMyRequestItem));
                        mAdapter.notifyDataSetChanged();
                        setLast();
                        setTimer();

                    } else {
                        Log.i(TAG, "null");

                    }
                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                getShops();
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
            public void onFailure(Call<ShopsResponse> call, Throwable t) {
                closeProgressDialog();
            }
        });
    }

    private void getElectShops() {
        Call<ElectResponse> call = AppMain.getClient().getElect(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<ElectResponse>() {
            @Override
            public void onResponse(Call<ElectResponse> call, Response<ElectResponse> response) {
                closeProgressDialog();
                if (response.isSuccessful() && response.body().getData() != null) {
Log.i(TAG,response.body().getData().getRequests().get(0).getName() );
                    mElectShopList.clear();

                    mElectShopList.addAll(response.body().getData().getRequests());

                }
                else{
                    Log.i(TAG,"null");

                }
            }

            @Override
            public void onFailure(Call<ElectResponse> call, Throwable t) {

            }
        });}

    @Override
    public View onCreateViewFragment(View view) {
        EventBus.getDefault().post(new CounterMessage(false));
        tvSearchResult.setText("Результаты поиска");
       /* if (mMyRequestItem.getShops() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvSearchResult.setText(Html.fromHtml(String.format(getString(R.string.title_search_result), getResources().getQuantityString(R.plurals.find, mMyRequestItem.getShops().size()), mMyRequestItem.getShops().size(), getResources().getQuantityString(R.plurals.shops, mMyRequestItem.getShops().size())), Html.FROM_HTML_MODE_LEGACY));
            } else {
                tvSearchResult.setText(Html.fromHtml(String.format(getString(R.string.title_search_result), getResources().getQuantityString(R.plurals.find, mMyRequestItem.getShops().size()), mMyRequestItem.getShops().size(), getResources().getQuantityString(R.plurals.shops, mMyRequestItem.getShops().size()))));
            }
        }*/
        mAdapter = new ShopsAdapter(getActivity(), mShopList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvShops.setLayoutManager(linearLayoutManager);
        rvShops.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] sid=view.getTag().toString().split(";");

                switch (view.getId()){
                    case R.id.llParent:
                        if(SharedStore.getInstance().getMyLat()!=null)
                            Log.d(TAG, "Message Notification");
                  String EXTRA_REQUEST = "extra_request";
                        String EXTRA_SHOP = "extra_shop";
                       String EXTRA_TITLE = "extra_title";
                         String EXTRA_ACT = "extra_act";

                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra(EXTRA_REQUEST,String.valueOf(mMyRequestItem));
                        intent.putExtra(EXTRA_SHOP,view.getTag().toString());
                        intent.putExtra(EXTRA_ACT,"no_push");
                        startActivity(intent);
                    break;
                    case R.id.more:
                        MoreCardDialogFragment moreCardDialogFragment = new   MoreCardDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", Integer.parseInt(sid[0]));
                        bundle.putInt("rId", mMyRequestItem);
                        moreCardDialogFragment.setArguments(bundle);
                        moreCardDialogFragment.show(getFragmentManager(), "TAG");
                    break;

                }
            }
        });
        moreB=(TextView)view.findViewById(R.id.moreB);
        return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mMyRequestItem = bundle.getInt("extra_item",0);
            Log.i("MyLog", String.valueOf(mMyRequestItem));


        }

    }
    public void setLast(){
        SharedStore.getInstance().setLastRequestId(String.valueOf(mMyRequestItem));

    }
    public void dialShow(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Расширение радиуса");
        builder.setMessage("Я  расширила радиус и ваш запрос ушел компаниям в радиусе "+ (lastRadius/10000+1)*10+" км. Я приглашу вас когда компании ответят в чате. В среднем это займет 15 минут.");
        builder.setCancelable(true);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { // Кнопка ОК
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    int lastRadius;
    public void increaseRadius(){

        lastRadius=0;
        int newRadius = 0;
        if (lastRadius!=50000){
            progress= new ProgressDialog(getActivity(),R.style.AppCompatAlertDialogStyle);
            progress.setTitle("Расширение радиуса");
            progress.setMessage("Расширяю радиус до "+ (lastRadius/10000+1)*10+" км");
            progress.show();
            newRadius=(lastRadius/10000+1)*10000;
            Log.i(TAG, String.valueOf(newRadius));
        EventBus.getDefault().post(new IncreaseRadius(mMyRequestItem,newRadius));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    moreB.setVisibility(View.INVISIBLE);
                    progress.dismiss();
                    dialShow();
                }
            }, 4000);


                }
    }


    @NonNull
    private rId getRid() {
        rId msg = new  rId();
        msg.setrId(String.valueOf(mMyRequestItem));

        return msg;
    }
    public void visiable() {
        if(mShopList.size()% 6== 0) {
            moreB.setText("Еще");
            moreB.setVisibility(View.VISIBLE);
        moreB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(getRid());
                moreB.setVisibility(View.INVISIBLE);

            }
        });
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showCompanies(final MyRequestItem myRequestItem) {
        getShops();


    }


        @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessage(MessageItem messageItem) {
        getShops();
    }
    public void setTimer(){
      /*  Timer updateTimer = new Timer();
        updateTimer.schedule(new TimerTask()
        {
            public void run()
            {
                try
                {
                *//*    int seconds = (int) (System.currentTimeMillis()/1000);
                    Log.i("Timer", String.valueOf(seconds));

                    int shopSeconds= (int)mMyRequestItem.getRadTime();
                    if (shopSeconds==0)
                        shopSeconds=(int)mMyRequestItem.getTime();
                    Log.i("Timer", String.valueOf(shopSeconds));
                    int minutes = (int)TimeUnit.MILLISECONDS.toMinutes((seconds-shopSeconds)*1000);
                    Log.i("Timer", String.valueOf(minutes));

                    if (minutes>4&& mMyRequestItem.getRadius()<50000){
                        Log.i("Timer", String.valueOf(minutes)+"rds");*/
visiable();




                   /* SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa");
                    Date Date1 = format.parse("08:00:12 pm");
                    Date Date2 = format.parse("05:30:12 pm");
                    long mills = Date1.getTime() - Date2.getTime();
                    Log.v("Data1", ""+Date1.getTime());
                    Log.v("Data2", ""+Date2.getTime());
                    int Hours = (int) (mills/(1000 * 60 * 60));
                    int Mins = (int) (mills/(1000*60)) % 60;

                    String diff = Hours + ":" + Mins; // updated value every1 second
                    txtCurrentTime.setText(diff);*/
          /*      }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }, 0, 1000);
    }
*/
    }
}

