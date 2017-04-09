package com.unated.askincht_beta.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.google.firebase.iid.FirebaseInstanceId;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Dialog.MoreDialogFragment;
import com.unated.askincht_beta.Fragments.ElectFragment;
import com.unated.askincht_beta.Fragments.MainFragment;
import com.unated.askincht_beta.Fragments.MyRequestsFragment;
import com.unated.askincht_beta.Fragments.NavigationDrawer;
import com.unated.askincht_beta.Fragments.ShopListFragment;
import com.unated.askincht_beta.Fragments.UserRequestsFragment;
import com.unated.askincht_beta.Pojo.AuthResponse;
import com.unated.askincht_beta.Pojo.BusMessages.ConnectMessage;
import com.unated.askincht_beta.Pojo.BusMessages.CounterMessage;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.BusMessages.NotificationMessage;
import com.unated.askincht_beta.Pojo.BusMessages.VKId;
import com.unated.askincht_beta.Pojo.MyShopResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.Pojo.Unread;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.UUID;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends SuperActivity implements NavigationDrawer.NavigationDrawerInterface {

    public static final String TAG = "MyLog";
    private boolean doubleBackToExitPressedOnce = false;
    int entered=0;
    @Bind(R.id.toolbar_counter)
    LinearLayout llToolbarCounter;

    AHBottomNavigation bottomNavigation;
    private Fragment mFragment;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }
    protected void setScreen(int pos){
        bottomNavigation.setCurrentItem(pos);
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);



        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottomBar);

        bottomNavigation.setAccentColor(Color.parseColor("#5e8be2"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);


    }


    private void getDeviceRegistration() {
        showProgressDialog("");
        if (TextUtils.isEmpty(SharedStore.getInstance().getUUID())) {
            SharedStore.getInstance().setUUID(UUID.randomUUID().toString());
        }
        Call<AuthResponse> call = AppMain.getClient().devRegistration(SharedStore.getInstance().getUUID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                closeProgressDialog();
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    SharedStore.getInstance().setSID(response.body().getData().getSid());
                    SharedStore.getInstance().setUserId(String.valueOf(response.body().getData().getUser_id()));
                    SharedStore.getInstance().setDeviceAuth(true);
                    EventBus.getDefault().post(new ConnectMessage());
                    getMyShop();                } else  if(response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.isRefresh=true;
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                getDeviceRegistration();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppMain.isRefresh=false;
                                        Log.i("MyLog"," isRefresh=false;");

                                    }
                                }, 30000);

                            }else if(response.body().getStatus() == 1029||response.body().getStatus() == 1028){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppMain.isRefresh=false;
                                        Log.i("MyLog"," isRefresh=false;");

                                    }
                                }, 30000);
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
            public void onFailure(Call<AuthResponse> call, Throwable t) {

            }
        });
    }

    private void sendToken() {

        Call<SimpleResponse> call = AppMain.getClient().saveToken(SharedStore.getInstance().getSID(), FirebaseInstanceId.getInstance().getToken(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {


                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.isRefresh=true;
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                sendToken();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppMain.isRefresh=false;
                                        Log.i("MyLog"," isRefresh=false;");

                                    }
                                }, 30000);

                            }else if(response.body().getStatus() == 1029||response.body().getStatus() == 1028){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppMain.isRefresh=false;
                                        Log.i("MyLog"," isRefresh=false;");

                                    }
                                }, 30000);
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
            public void onFailure(Call<SimpleResponse> call, Throwable t) {


            }
        });
    }


    @Override
    public void onSearchClicked() {
        mFragment = new MainFragment();
        setFragment(R.id.container, mFragment);
    }
    boolean shopIs;
    private void getMyShop() {
        Log.i(TAG,SharedStore.getInstance().getToken()+" token");
        Call<MyShopResponse> call = AppMain.getClient().getMyShop(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<MyShopResponse>() {

            @Override
            public void onResponse(Call<MyShopResponse> call, Response<MyShopResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    if(response.body().getData().getShop()!=null) {
                        if (response.body().getData().getShop().getName() != null && !response.body().getData().getShop().getName().equals("")) {
                            Log.i(TAG, "name" + response.body().getData().getShop().getName());

                            shopIs = true;


                        }
                        sendToken();
                        setUpItems();
                        notif();

                    }




                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.isRefresh=true;
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                getMyShop();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppMain.isRefresh=false;
                                        Log.i("MyLog"," isRefresh=false;");

                                    }
                                }, 30000);

                            }else if(response.body().getStatus() == 1029||response.body().getStatus() == 1028){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppMain.isRefresh=false;
                                        Log.i("MyLog"," isRefresh=false;");

                                    }
                                }, 30000);
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
            public void onFailure(Call<MyShopResponse> call, Throwable t) {
                Log.i("OkHttp:",t.toString());
                setUpItems();
                notif();
            }

        });



    }



    public void setUpItems(){
        bottomNavigation.removeAllItems();
        bottomNavigation.addItem(new AHBottomNavigationItem("Поиск",(R.drawable.ic_searching)));
        bottomNavigation.addItem(new AHBottomNavigationItem("Мои запросы",R.drawable.ic_person));
/*
        bottomNavigation.addItem(new AHBottomNavigationItem("Избранное",R.drawable.ic_heart));
*/

        if (!shopIs){
            bottomNavigation.addItem(new AHBottomNavigationItem("Настройки",R.drawable.ic_more));

        }
        else{
            bottomNavigation.addItem(new AHBottomNavigationItem("Клиенты",R.drawable.ic_users_req));
            bottomNavigation.addItem(new AHBottomNavigationItem("Настройки",R.drawable.ic_more ));
            entered++;
            Log.i(TAG, String.valueOf(entered));

        }
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position){
                    case 0:
                        onSearchClicked();

                        break;
                    case 1:
                        SharedStore.getInstance().clearMyRequests();
                        onMyRequestsClicked();
                        break;
                   /* case 2:
                        onFavClicked();

                        break;*/
                    case 2:
                        if( !shopIs){
                            MoreDialogFragment searchProcessDialogFragment = new MoreDialogFragment();
                            searchProcessDialogFragment.show(getSupportFragmentManager(), "TAG");
                        }
                        else {
                            SharedStore.getInstance().clearUserRequests();
                            onUserRequestClicked();
                        }
                        break;
                    case 3:

                        MoreDialogFragment searchProcessDialogFragment = new MoreDialogFragment();
                        searchProcessDialogFragment.show(getSupportFragmentManager(), "TAG");

                        break;

                }
                return true;
            }
        });
        if(getIntent().hasExtra("type")) {
            Log.i("type_intent","is");
            Bundle bd = getIntent().getExtras();
            Log.i("type_intent",getIntent().getIntExtra("type",0)+" type");

            if(getIntent().getIntExtra("type",0)==1) {
                Log.i("type_intent","done 1");

                getIntent().removeExtra("type");

                bottomNavigation.setCurrentItem(1);
            } else if(getIntent().getIntExtra("type",0)==2) {
                Log.i("type_intent","done 2");
                getIntent().removeExtra("type");

                bottomNavigation.setCurrentItem(2);
            } else if(getIntent().getIntExtra("type",0)==0) {
                Log.i("type_intent","done 0");
                getIntent().removeExtra("type");

                bottomNavigation.setCurrentItem(0);
            }
            else if(getIntent().getIntExtra("type",0)==3) {
                Log.i("type_intent","done 3");
                getIntent().removeExtra("type");
                onSearchClicked();
                bottomNavigation.setCurrentItem(3);

            }
            else if(getIntent().getIntExtra("type",0)==4) {
                Log.i("type_intent","done 4");
                getIntent().removeExtra("type");
                setFragment(R.id.container, ShopListFragment.newInstance(getIntent().getIntExtra("extra_item",0)));


            }



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
                    startActivity(new Intent(getBaseContext(),LoginActivity.class));
                    finish();


                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {
            }

        });
    }

    @Override
    public void onMyRequestsClicked() {
        EventBus.getDefault().post(new CounterMessage(false));
/*
        if (!TextUtils.isEmpty(SharedStore.getInstance().getSID())) {
*/
        mFragment = new MyRequestsFragment();
        setFragment(R.id.container, mFragment);
       /* } else {
            startActivity(new Intent(this, LoginActivity.class));
        }*/
    }

    @Override
    public void onFavClicked() {
        mFragment = new ElectFragment();
        setFragment(R.id.container, mFragment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("msgT","Resumed");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(SharedStore.getInstance().getSID())) {
                    EventBus.getDefault().post(new ConnectMessage());
                    android.app.Fragment prev = getFragmentManager().findFragmentByTag("MoreDialog");
                    if(prev == null){
                        Log.i("MyLog","Getting shop");

                        getMyShop();}
                } else {
                    getDeviceRegistration();
                }

            }
        }, 1000);


    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onUserRequestClicked() {
        EventBus.getDefault().post(new CounterMessage(false));
        mFragment = new UserRequestsFragment();
        setFragment(R.id.container, mFragment);
    }

    @Override
    public void onAddCompanyClicked() {
        if (SharedStore.getInstance().isDeviceAuth()) {
            startActivity(new Intent(getBaseContext(), LoginActivity.class));

        } else {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
                Log.i("MyLog", "ok");

                Log.i("MyLog", res.userId);
                EventBus.getDefault().post(new VKId(res.userId));
            }

            @Override
            public void onError(VKError error) {
                Log.i("MyLog", String.valueOf(error));
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data,callback)) {
            super.onActivityResult(requestCode, resultCode, data);
            mFragment.onActivityResult(requestCode, resultCode, data);


        }

    }
    @Override
    public void onSettingsClicked() {

    }

    @Override
    public void onShareClicked() {

    }

    @Override
    public void onBackPressed() {
        MainFragment myFragment = (MainFragment)   getSupportFragmentManager().findFragmentByTag(MainFragment.class.getName());
        ShopListFragment resultFragment = (ShopListFragment)   getSupportFragmentManager().findFragmentByTag(ShopListFragment.class.getName());

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.back);
        switch( am.getRingerMode() ) {
            case AudioManager.RINGER_MODE_NORMAL:
                mediaPlayer.start();
                break;
        }
        if (myFragment != null && myFragment.isVisible()) {
            if (doubleBackToExitPressedOnce) {
                System.exit(0);
                return;
            }

            doubleBackToExitPressedOnce = true;
            showToast("Нажмите 'Назад' еще раз для выхода");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);        }
        else if (resultFragment != null && resultFragment.isVisible()) {
            mFragment = new MyRequestsFragment();
            setFragment(R.id.container, mFragment);
        }

        else {
            bottomNavigation.setCurrentItem(0);
            mFragment = new MainFragment();
            setFragment(R.id.container, mFragment);
        }

    }
    public void notif(){
        Log.i("msgT","new");
        Call<Unread> call = AppMain.getClient().getUnreadCounts(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<Unread>() {
            @Override
            public void onResponse(Call<Unread> call, Response<Unread> response) {
                closeProgressDialog();
                if (response.isSuccessful()&& response.body().getStatus() == 0 ) {
                    int myReq = 0;
                    int usReq=0;
                    int supReq=0;
                    Log.i("msgT",String.valueOf(myReq));
                    Log.i("msgT",String.valueOf(usReq));

                    myReq=response.body().getData().getMessageShops()-response.body().getData().getMessageSupport();
                    if(myReq>0) {
                        AHNotification notification = new AHNotification.Builder()
                                .setText(String.valueOf(myReq))
                                .setBackgroundColor(Color.parseColor("#e7567c"))
                                .setTextColor(Color.parseColor("#ffffff"))
                                .build();
                        bottomNavigation.setNotification(notification, 1);
                    }
                    else{

                        bottomNavigation.setNotification("", 1);

                    }
                    supReq=response.body().getData().getMessageSupport();

                    if(supReq>0) {
                        AHNotification notification = new AHNotification.Builder()
                                .setText(String.valueOf(supReq))
                                .setBackgroundColor(Color.parseColor("#e7567c"))
                                .setTextColor(Color.parseColor("#ffffff"))
                                .build();
                        if( !shopIs)
                        bottomNavigation.setNotification(notification, 2);
                        else
                            bottomNavigation.setNotification(notification, 3);

                    }


                        else{
                        if( !shopIs)
                            bottomNavigation.setNotification("", 2);
                        else
                            bottomNavigation.setNotification("", 3);

                    }


                    usReq=response.body().getData().getMessageClients();

                    if(usReq>0) {
                        AHNotification notificationr = new AHNotification.Builder()
                                .setText(String.valueOf(usReq))
                                .setBackgroundColor(Color.parseColor("#e7567c"))
                                .setTextColor(Color.parseColor("#ffffff"))
                                .build();
                        bottomNavigation.setNotification(notificationr, 2);
                    }
                    else{

                        bottomNavigation.setNotification("", 2);

                    }

                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.isRefresh=true;
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                SharedStore.getInstance().setToken(response.body().getData().getToken());
                                notif();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppMain.isRefresh=false;
                                        Log.i("MyLog"," isRefresh=false;");

                                    }
                                }, 30000);

                            }else if(response.body().getStatus() == 1029||response.body().getStatus() == 1028){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppMain.isRefresh=false;
                                        Log.i("MyLog"," isRefresh=false;");

                                    }
                                }, 30000);
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
            public void onFailure(Call<Unread> call, Throwable t) {
                closeProgressDialog();
            }
        });
    }


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        return intent;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateToolbarCounter(CounterMessage message) {
        notif();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void push(NotificationMessage message) {
    }

}




