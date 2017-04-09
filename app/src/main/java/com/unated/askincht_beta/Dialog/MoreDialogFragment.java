package com.unated.askincht_beta.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.unated.askincht_beta.Activity.ChatActivity;
import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.BalanceResponse;
import com.unated.askincht_beta.Pojo.BusMessages.CounterMessage;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.MyShopResponse;
import com.unated.askincht_beta.Pojo.ProfileResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.Pojo.RequestSupResponse;
import com.unated.askincht_beta.Pojo.Unread;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.EXTRA_TEXT;


public class MoreDialogFragment extends SuperDialogFragment {

    Fragment fragment;
    @Bind(R.id.redProf)

    LinearLayout redProf;
    @Bind(R.id.main_ph)
    TextView mainPh;
    @Bind(R.id.tvMsgCount1)
    TextView count;

    @Bind(R.id.share)

    LinearLayout share;
    @Bind(R.id.progress_more)
    ProgressBar progressBar;
    @Bind(R.id.retur)

    LinearLayout retur;

    @Bind(R.id.exit)

    LinearLayout exit;
    @Bind(R.id.cancel)

    LinearLayout cancel;
    @Bind(R.id.edit)

    LinearLayout edit;
    @Bind(R.id.llMsgStatus)

    LinearLayout countLayout;
  @Bind(R.id.main_name)

    TextView name;
  @Bind(R.id.main_desc)

  TextView desc;
    @Bind(R.id.balance)

  TextView balance;
    @Bind(R.id.action_shop)

    TextView shop;
    ImageView imageView;
    @Bind(R.id.toggle_switch)
    ToggleSwitch toggleSwitch;
int first=0;
    @Override
    public int onInflateViewFragment() {
        return R.layout.view_more_dialog;
    }

    @Override
    public void onCreateFragment(Bundle instance) {




    }
    private void getBalance(){

            Call<BalanceResponse> call = AppMain.getClient().getShopBalance(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
            call.enqueue(new Callback<BalanceResponse>() {
                @Override
                public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
                    if (response.isSuccessful() && response.body().getData() != null && response.body().getStatus() == 0) {
                        Log.i(TAG, response.body().getData().getBalance() + " баланс магазина");
                        SharedStore.getInstance().setBalance(response.body().getData().getBalance());
                        balance.setText("Баланс: " + response.body().getData().getBalance() + "\u20BD");
                        closeProgressDialog();

                        mainPh.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        name.setVisibility(View.VISIBLE);
                        balance.setVisibility(View.VISIBLE);
                        desc.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                    } else if (response.isSuccessful() && response.body().getStatus() == 1026 && !AppMain.isRefresh) {
                        AppMain.setIsRefresh(true);
                        Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(), SharedStore.getInstance().getToken());
                        callr.enqueue(new Callback<RefreshResponse>() {
                            @Override
                            public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                                closeProgressDialog();
                                if (response.isSuccessful() && response.body().getStatus() == 0) {
                                    SharedStore.getInstance().setToken(response.body().getData().getToken());
                                    getBalance();
                                    AppMain.setIsRefresh(false);


                                } else if (response.body().getStatus() == 1029) {
                                    AppMain.setIsRefresh(false);

                                    logout();
                                }
                            }

                            @Override
                            public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                                closeProgressDialog();
                            }
                        });
                    } else if (response.body().getStatus() == 1027 || response.body().getStatus() == 1028 || response.body().getStatus() == 1029) {


                        logout();
                    }
                }


                @Override
                public void onFailure(Call<BalanceResponse> call, Throwable t) {
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
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        notif();
        toggleSwitch.setCheckedTogglePosition(toggleSwitch.getCheckedTogglePosition());
    }

    private void getMyShop() {
        Log.i("MyLog","getMyShop");

        if (SharedStore.getInstance().getMyShop()!= null) {

            Call<MyShopResponse> call = AppMain.getClient().getMyShop(SharedStore.getInstance().getSID(), SharedStore.getInstance().getToken());
            call.enqueue(new Callback<MyShopResponse>() {
                @Override
                public void onResponse(Call<MyShopResponse> call, Response<MyShopResponse> response) {
                    if (response.isSuccessful() && response.body().getStatus() == 0) {
                        if (response.body().getData().getShop() != null) {
                            if (response.body().getData().getShop().getName() != null) {
                                toggleSwitch.setVisibility(View.VISIBLE);
                                SharedStore.getInstance().setMyShop(response.body().getData().getShop());

                                Log.i("MyLog", "есть");
                                desc.setText(response.body().getData().getShop().getDesc());
                                name.setText(response.body().getData().getShop().getName());
                                Glide.with(getActivity()).load(response.body().getData().getShop().getAvatar()).asBitmap().centerCrop().placeholder(R.drawable.mask)

                                        .into(new BitmapImageViewTarget(imageView) {
                                            @Override
                                            protected void setResource(Bitmap resource) {
                                                RoundedBitmapDrawable circularBitmapDrawable =
                                                        RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                                circularBitmapDrawable.setCircular(true);
                                                imageView.setImageDrawable(circularBitmapDrawable);
                                            }
                                        });
                                getBalance();
                            }
                        } else {
                            Log.i("MyLog", "нет");

                            name.setVisibility(View.VISIBLE);
/*
                        name.setText("Анонимный пользователь");
*/
                            desc.setVisibility(View.GONE);
                            balance.setVisibility(View.INVISIBLE);
                            shop.setText("Добавить магазин/компанию");

                        }

                    } else if (response.isSuccessful() && response.body().getStatus() == 1026 && !AppMain.isRefresh) {
                        AppMain.setIsRefresh(true);
                        Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(), SharedStore.getInstance().getToken());
                        callr.enqueue(new Callback<RefreshResponse>() {
                            @Override
                            public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                                closeProgressDialog();
                                if (response.isSuccessful() && response.body().getStatus() == 0) {
                                    SharedStore.getInstance().setToken(response.body().getData().getToken());
                                    getMyShop();
                                    AppMain.setIsRefresh(false);


                                } else if (response.body().getStatus() == 1029) {
                                    AppMain.setIsRefresh(false);

                                    logout();
                                }
                            }

                            @Override
                            public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                                closeProgressDialog();
                            }
                        });
                    } else if (response.body().getStatus() == 1027 || response.body().getStatus() == 1028 || response.body().getStatus() == 1029) {


                        logout();
                    }

                }

                @Override
                public void onFailure(Call<MyShopResponse> call, Throwable t) {
                    Log.i("MyLog", String.valueOf(t));
                    first = 1;
                    toggleSwitch.setCheckedTogglePosition(0);

                    shop.setText("Добавить магазин/компанию");

                }
            });
        }else{
            toggleSwitch.setCheckedTogglePosition(0);
        }
    }
     public void getProfile(){
         Call<ProfileResponse> call = AppMain.getClient().getProfile(SharedStore.getInstance().getToken());
         call.enqueue(new Callback<ProfileResponse>() {
             @Override
             public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                 if (response.isSuccessful() && response.body().getStatus() == 0) {

                     toggleSwitch.setVisibility(View.VISIBLE);

                     Log.i("MyLog", "есть");
                     name.setText(response.body().getData().getUsername());
                     balance.setText("Баланс: " + response.body().getData().getBalance() + "\u20BD");
                     desc.setVisibility(View.GONE);
                     if(mainPh.getText().toString().equals(response.body().getData().getUsername()))
                     mainPh.setVisibility(View.GONE);
                     Glide.with(getActivity()).load(response.body().getData().getUserAvatar()).asBitmap().centerCrop().placeholder(R.drawable.mask)

                             .into(new BitmapImageViewTarget(imageView) {
                                 @Override
                                 protected void setResource(Bitmap resource) {
                                     RoundedBitmapDrawable circularBitmapDrawable =
                                             RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                                     circularBitmapDrawable.setCircular(true);
                                     imageView.setImageDrawable(circularBitmapDrawable);
                                 }
                             });



                 } else if (response.isSuccessful() && response.body().getStatus() == 1026 && !AppMain.isRefresh) {
                     AppMain.setIsRefresh(true);
                     Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(), SharedStore.getInstance().getToken());
                     callr.enqueue(new Callback<RefreshResponse>() {
                         @Override
                         public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                             closeProgressDialog();
                             if (response.isSuccessful() && response.body().getStatus() == 0) {
                                 SharedStore.getInstance().setToken(response.body().getData().getToken());
getProfile();                                 AppMain.setIsRefresh(false);


                             } else if (response.body().getStatus() == 1029) {
                                 AppMain.setIsRefresh(false);

                                 logout();
                             }
                         }

                         @Override
                         public void onFailure(Call<RefreshResponse> callr, Throwable t) {
                             closeProgressDialog();
                         }
                     });
                 } else if (response.body().getStatus() == 1027 || response.body().getStatus() == 1028 || response.body().getStatus() == 1029) {


                     logout();
                 }

             }

             @Override
             public void onFailure(Call<ProfileResponse> call, Throwable t) {
                 Log.i("MyLog", String.valueOf(t));
                 first = 1;

                 shop.setText("Добавить магазин/компанию");

             }
         });
     }

    @Override
    public View onCreateViewFragment(View view) {
        mainPh= ButterKnife.findById(view,R.id.main_ph);
        imageView=ButterKnife.findById(view,R.id.main_img);
        progressBar.setVisibility(View.VISIBLE);
        mainPh.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        balance.setVisibility(View.GONE);
        desc.setVisibility(View.GONE);
        mainPh.setText(SharedStore.getInstance().getPhone());

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                closeProgressDialog();
            }
        }, 2000);*/
        Log.i("MyLog","start");

        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if(position==0)
                    getProfile();

                if(position==1)
                    getMyShop();


            }
        });
        toggleSwitch.setCheckedTogglePosition(1);
        notif();

        return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {

    }
    @OnClick({R.id.cancel, R.id.exit, R.id.chain, R.id.retur, R.id.share, R.id.edit, R.id.redProf})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
dismiss();
                break;
            case R.id.redProf:
                if(toggleSwitch.getCheckedTogglePosition()==0){
                    MoreRefractorDialogFragment moreRefractorDialogFragment = new MoreRefractorDialogFragment();
                   moreRefractorDialogFragment.show(getFragmentManager(), "TAG");
                }
                break;
            case R.id.edit:
                if (SharedStore.getInstance().getMyShop()!= null) {
                    WebDialogFragment webDialogFragment = new WebDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",1);
                    webDialogFragment.setArguments(bundle);
                    webDialogFragment.show(getActivity().getSupportFragmentManager(), "TAG");
                    Log.i("MyLog","type 1");
                }else{
                    WebDialogFragment webDialogFragment = new WebDialogFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("type",0);
                    webDialogFragment.setArguments(bundle);
                    webDialogFragment.show(getActivity().getSupportFragmentManager(), "TAG");
                    Log.i("MyLog","type 0");

                }
                /*if(first==1)
                    dismiss();*/
                dismiss();
                break;
            case R.id.exit:
               logout();
                break;
            case R.id.chain:
                RequestDialogFragment requestDialogFragment = new  RequestDialogFragment();
                requestDialogFragment.show(getFragmentManager(), "TAG");
                break;
            case R.id.share:

                String shareBody = "http://askin.chat/app";
                String shareText = "Попробуй новый сервис для поиска товаров и услуг. Я больше не трачу время на поисковики и справочники! Сервис ищет за меня и связывает с продавцами в чате.";

                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");


                sharingIntent.putExtra(EXTRA_TEXT, shareBody+"\n"+shareText);
                startActivity(Intent.createChooser(sharingIntent, "Поделиться"));
                break;
            case R.id.retur:
                getChatId();
                break;

        }
    }
    String EXTRA_REQUEST = "extra_request";
    String EXTRA_SHOP = "extra_shop";
    String EXTRA_TITLE = "extra_title";
    String EXTRA_ACT = "extra_act";
    public void getChatId(){

        Call<RequestSupResponse> call = AppMain.getClient().getRequestSupport(SharedStore.getInstance().getToken());
        call.enqueue(new Callback<RequestSupResponse>() {
            @Override
            public void onResponse(Call<RequestSupResponse> call, Response<RequestSupResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra(EXTRA_REQUEST,String.valueOf(response.body().getData().getRequest_id()));
                    intent.putExtra(EXTRA_SHOP,response.body().getData().getShop_id()+ ";AskinChat Support;" +response.body().getData().getRequest_id());
                    Log.i("MyLog",response.body().getData().getShop_id()+ ";AskinChat Support;" +response.body().getData().getRequest_id());
                    intent.putExtra(EXTRA_ACT,"no_push");
                    startActivity(intent);


                } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                    AppMain.setIsRefresh(true);
                    Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                    callr.enqueue(new Callback<RefreshResponse>() {
                        @Override
                        public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                            closeProgressDialog();
                            if (response.isSuccessful() && response.body().getStatus() == 0) {
                                getChatId();
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
            public void onFailure(Call<RequestSupResponse> call, Throwable t) {
                Log.i("MyLog", String.valueOf(t));


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
                    Log.i("msgT",String.valueOf(myReq));
                    Log.i("msgT",String.valueOf(usReq));

                    myReq=response.body().getData().getMessageSupport();
                    if(myReq>0) {
                        countLayout.setVisibility(View.VISIBLE);
                        count.setText(String.valueOf(myReq));
                    } else{
                        countLayout.setVisibility(View.GONE);
                    count.setText(String.valueOf(0));}




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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateToolbarCounter(CounterMessage message) {
        notif();
    }





}
