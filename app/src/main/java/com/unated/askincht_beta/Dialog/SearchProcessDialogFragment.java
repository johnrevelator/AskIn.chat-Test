package com.unated.askincht_beta.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unated.askincht_beta.Fragments.MyRequestsFragment;
import com.unated.askincht_beta.Pojo.BusMessages.BadMessage;
import com.unated.askincht_beta.Pojo.BusMessages.NotServedMessage;
import com.unated.askincht_beta.Pojo.BusMessages.NothingMessage;
import com.unated.askincht_beta.Pojo.BusMessages.SingleMessage;
import com.unated.askincht_beta.Pojo.MyRequestItem;
import com.unated.askincht_beta.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;


public class SearchProcessDialogFragment extends SuperDialogFragment {
    @Bind(R.id.main_title)
    TextView mMainTitle;

    @Bind(R.id.radar_view)
    ImageView mRadarView;
    @Bind(R.id.sub_title)
    TextView mSubTitle;
    @Bind(R.id.search_container)

    RelativeLayout mSearchContainer;

    private Animation anim;
    private View[] mDots;
    Fragment fragment;

    @Override
    public int onInflateViewFragment() {
        return R.layout.dialog_search;
    }

    @Override
    public void onCreateFragment(Bundle instance) {

    }

    @Override
    public View onCreateViewFragment(View view) {

        initAnimation();
        return view;
    }

    @Override
    public void onAttachFragment(Activity activity) {

    }
    int work=0;
    private void initAnimation() {
        anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        mRadarView.setAnimation(anim);
        anim.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (work==0) {
                    fragment = new MyRequestsFragment();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AppCompatAlertDialogStyle);
                    builder.setMessage("Извините!\n" +
                            "Что-то пошло не так. Попробуйте повторить запрос")
                                    .setCancelable(false)
                                    .setNegativeButton("Ок",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                    dismiss();

                                                }
                                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            }
        }, 10000);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void nothing(NothingMessage nothingMessage) {
        Log.i("MyLog","tcnm");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                work++;
                fragment = new MyRequestsFragment();

                mMainTitle.setText("Извините, сейчас пока нет интересующих вас компаний. Но мы уже работаем над этим и уведомим вас, когда они будут подключены.");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        dismiss();

                    }
                }, 5000);
            }
        }, 3000);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void single(final SingleMessage singleMessage) {
        Log.i("MyLog","tcnm");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                work++;
                fragment = new MyRequestsFragment();

                mMainTitle.setText(singleMessage.getMsg());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        dismiss();

                    }
                }, 5000);
            }
        }, 3000);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void notSreved(final NotServedMessage notServedMessage) {
        Log.i("MyLog","tcnm");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                work++;

                fragment = new MyRequestsFragment();

                mMainTitle.setText(notServedMessage.getText());
                Log.i(TAG,notServedMessage.getText());
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        dismiss();

                    }
                }, 5000);
            }
        }, 3000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void badEWord(BadMessage badMessage) {
        Log.i("MyLog","tcnm");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                work++;
                fragment = new MyRequestsFragment();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AppCompatAlertDialogStyle);
                builder.setMessage("Простите, я вся краснею, но с этим я не могу вам помочь...")
                        .setCancelable(false)
                        .setNegativeButton("Ок",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        dismiss();

                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();



            }
        }, 3000);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showCompanies(final MyRequestItem myRequestItem) {
        mMainTitle.setText("Ищу необходимые компании в заданном радиусе.");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                if (myRequestItem.getShops() != null && myRequestItem.getShops().size() >4) {
                    work++;
                    fragment=new MyRequestsFragment();
                    if((TimeUnit.MILLISECONDS.toMinutes(myRequestItem.getAvgTime()*1000))>60)
                        mMainTitle.setText("Я нашла "+myRequestItem.getShops().size()+" компаний.  Скоро они появятся в чате и я приглашу вас. Среднее время ожидания "+(TimeUnit.MILLISECONDS.toMinutes(myRequestItem.getAvgTime()*1000))/60+" час(-ов) " +(TimeUnit.MILLISECONDS.toMinutes(myRequestItem.getAvgTime()*1000))%60+ " минут");
                    else
                        mMainTitle.setText("Я нашла "+myRequestItem.getShops().size()+" компаний.  Скоро они появятся в чате и я приглашу вас. Среднее время ожидания "+ (TimeUnit.MILLISECONDS.toMinutes(myRequestItem.getAvgTime()*1000))+" минут");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();
                            dismiss();

                        }
                    }, 5000);
                } else
                if (myRequestItem.getShops().size()==0) {
                    work++;

                    fragment=new MyRequestsFragment();
                    mMainTitle.setText("Извините, сейчас пока нет интересующих вас компаний. Но мы уже работаем над этим и уведомим вас, когда они будут подключены.");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            dismiss();

                        }
                    }, 5000);}
                else
                if (myRequestItem.getShops().size()>0&&myRequestItem.getShops().size()<5) {
                    work++;

                    fragment=new MyRequestsFragment();
                    if((TimeUnit.MILLISECONDS.toMinutes(myRequestItem.getAvgTime()*1000))>60)
                        mMainTitle.setText("Я нашла "+myRequestItem.getShops().size()+" компаний.  Скоро они появятся в чате и я приглашу вас. Среднее время ожидания "+(TimeUnit.MILLISECONDS.toMinutes(myRequestItem.getAvgTime()*1000))/60+" час(-ов) " +(TimeUnit.MILLISECONDS.toMinutes(myRequestItem.getAvgTime()*1000))%60+ " минут");
                    else
                        mMainTitle.setText("Я нашла "+myRequestItem.getShops().size()+" компаний.  Скоро они появятся в чате и я приглашу вас. Среднее время ожидания "+(TimeUnit.MILLISECONDS.toMinutes(myRequestItem.getAvgTime()*1000))+" минут");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();

                            dismiss();

                        }
                    }, 5000);}


            }
        }, 3000);

    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                // Hyi!!.
            }
        };
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
}
