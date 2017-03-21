package com.unated.askincht_beta.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.unated.askincht_beta.Activity.LoginActivity;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.BalanceResponse;
import com.unated.askincht_beta.Pojo.BusMessages.LogoutResponse;
import com.unated.askincht_beta.Pojo.MyShopResponse;
import com.unated.askincht_beta.Pojo.RefreshResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

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
    private void getBalance(final boolean isShop){
        if (SharedStore.getInstance().getMyShop()== null||isShop) {

            Call<BalanceResponse> call = AppMain.getClient().getBalance(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
            call.enqueue(new Callback<BalanceResponse>() {
                @Override
                public void onResponse(Call<BalanceResponse> call, Response<BalanceResponse> response) {
                    if (response.isSuccessful() && response.body().getData() != null&&response.body().getStatus()==0) {
                        Log.i(TAG, response.body().getData().getBalance() + " баланс пользователя");
                        SharedStore.getInstance().setBalance(response.body().getData().getBalance());
                        balance.setText("Баланс: "+response.body().getData().getBalance()+"\u20BD");

                        mainPh.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        name.setVisibility(View.GONE);
                        desc.setVisibility(View.GONE);
                        balance.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                    } else  if(response.isSuccessful() &&response.body().getStatus() == 1026&&! AppMain.isRefresh) {
                        AppMain.setIsRefresh(true);
                        Call<RefreshResponse> callr = AppMain.getClient().refresh(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
                        callr.enqueue(new Callback<RefreshResponse>() {
                            @Override
                            public void onResponse(Call<RefreshResponse> callr, Response<RefreshResponse> response) {
                                closeProgressDialog();
                                if (response.isSuccessful() && response.body().getStatus() == 0) {
                                    SharedStore.getInstance().setToken(response.body().getData().getToken());
                                    getBalance(isShop);
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
                public void onFailure(Call<BalanceResponse> call, Throwable t) {
                    Log.i("MyLog", String.valueOf(t));
                }
            });
        }else{
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
                                    getBalance(isShop);
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
    }



    @Override
    public void onResume() {
        super.onResume();
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
        getMyShop(SharedStore.getInstance().getShop());
    }

    private void getMyShop(final boolean isShop) {


        Call<MyShopResponse> call = AppMain.getClient().getMyShop(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<MyShopResponse>() {
            @Override
            public void onResponse(Call<MyShopResponse> call, Response<MyShopResponse> response) {
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    if (response.body().getData().getShop() != null) {
                        if (response.body().getData().getShop().getName() != null) {
                            toggleSwitch.setVisibility(View.VISIBLE);
                            toggleSwitch.setCheckedTogglePosition(1);
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
                            getBalance(isShop);
                        }
                    } else {
                        Log.i("MyLog","нет");

                        name.setVisibility(View.VISIBLE);
/*
                        name.setText("Анонимный пользователь");
*/
                        desc.setVisibility(View.GONE);
                        balance.setVisibility(View.INVISIBLE);
                        shop.setText("Добавить магазин/компанию");

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
                                getMyShop(isShop);
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
            public void onFailure(Call<MyShopResponse> call, Throwable t) {
                Log.i("MyLog", String.valueOf(t));
                first=1;
                getBalance(false);

                shop.setText("Добавить магазин/компанию");

            }
        });
    }

    @Override
    public View onCreateViewFragment(View view) {
        mainPh= ButterKnife.findById(view,R.id.main_ph);
        imageView=ButterKnife.findById(view,R.id.main_img);

        toggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                if(position==0)
                getBalance(true);
                else
                    getBalance(false);


            }
        });

        return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {

    }
    @OnClick({R.id.cancel, R.id.exit, R.id.chain, R.id.retur, R.id.share, R.id.edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
dismiss();
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
                Toast.makeText(getActivity(),"Чуть позже",Toast.LENGTH_LONG).show();

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






}
