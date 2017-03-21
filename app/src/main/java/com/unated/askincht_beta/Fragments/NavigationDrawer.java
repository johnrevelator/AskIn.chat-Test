package com.unated.askincht_beta.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.BusMessages.RequestCounter;
import com.unated.askincht_beta.Pojo.ProfileResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dmitryabramichev on 11.09.16.
 */
public class NavigationDrawer extends SuperFragment {

    public interface NavigationDrawerInterface {
        void onSearchClicked();

        void onMyRequestsClicked();

        void onFavClicked();

        void onUserRequestClicked();

        void onAddCompanyClicked();

        void onSettingsClicked();

        void onShareClicked();
    }

    @Bind(R.id.tvUserName)
    TextView tvUserName;
    @Bind(R.id.ivAsk)
    ImageView mIvAsk;
    @Bind(R.id.ivNotifications)
    ImageView mIvNotifications;
    @Bind(R.id.relativeLayout)
    RelativeLayout mRelativeLayout;
    @Bind(R.id.llSearch)
    LinearLayout mLlSearch;
    @Bind(R.id.llMyRequests)
    LinearLayout mLlMyRequests;
    @Bind(R.id.llFav)
    LinearLayout mLlFav;
    @Bind(R.id.llUserReq)
    LinearLayout mLlUserReq;
    @Bind(R.id.llAddCompany)
    LinearLayout mLlAddCompany;
    @Bind(R.id.llSettings)
    LinearLayout mLlSettings;
    @Bind(R.id.llShare)
    LinearLayout mLlShare;
    @Bind(R.id.llUserReqCounter)
    LinearLayout llUserReqCounter;
    @Bind(R.id.tvUserReqCounter)
    TextView tvUserReqCounter;
    @Bind(R.id.llMyRequestsConter)
    LinearLayout llMyRequestsConter;
    @Bind(R.id.tvMyRequestsCounter)
    TextView tvMyRequestsCounter;

    private View mView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View mContainerView;

    private NavigationDrawerInterface mInterface;

    @Override
    public int onInflateViewFragment() {
        return R.layout.fragment_navigation_drawer;
    }

    @Override
    public void onCreateFragment(Bundle instance) {

    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCounters();
    }

    private void refreshCounters() {
        if (SharedStore.getInstance().getUserRequestsCount() > 0) {
            llUserReqCounter.setVisibility(View.VISIBLE);
            tvUserReqCounter.setText(String.valueOf(SharedStore.getInstance().getUserRequestsCount()));
        } else {
            llUserReqCounter.setVisibility(View.GONE);
            tvUserReqCounter.setText("");
        }

        if (SharedStore.getInstance().getMyRequestsCount() > 0) {
            llMyRequestsConter.setVisibility(View.VISIBLE);
            tvMyRequestsCounter.setText(String.valueOf(SharedStore.getInstance().getMyRequestsCount()));
        } else {
            llMyRequestsConter.setVisibility(View.GONE);
            tvMyRequestsCounter.setText("");
        }
    }

    @Override
    public View onCreateViewFragment(View view) {
        return view;
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onAttachFragment(Activity activity) {
        mInterface = (NavigationDrawerInterface) activity;
    }

    private void getProfileInfo() {
        showLoading();
        Call<ProfileResponse> call = AppMain.getClient().getProfile(SharedStore.getInstance().getSID(),SharedStore.getInstance().getToken());
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body().getStatus() == 0) {
                    tvUserName.setText(response.body().getData().getUsername());
                } else {
                    showToast(response.body().getError_msg());
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                hideLoading();
            }
        });
    }

    public void setUp(int viewId, DrawerLayout viewById, Toolbar toolbar) {
        mContainerView = getActivity().findViewById(viewId);
        mDrawerLayout = viewById;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), viewById, toolbar, R.string.title_nav_drawer_open, R.string.title_nav_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        if (SharedStore.getInstance().getUserRequestsCount() > 0) {
            llUserReqCounter.setVisibility(View.VISIBLE);
            tvUserReqCounter.setText(String.valueOf(SharedStore.getInstance().getUserRequestsCount()));
        }

        if (!SharedStore.getInstance().isDeviceAuth()) {
            mLlUserReq.setVisibility(View.VISIBLE);
            getProfileInfo();
        }

        if (SharedStore.getInstance().getMyRequestsCount() > 0) {
            llMyRequestsConter.setVisibility(View.VISIBLE);
            tvMyRequestsCounter.setVisibility(View.VISIBLE);
            tvMyRequestsCounter.setText(String.valueOf(SharedStore.getInstance().getMyRequestsCount()));
        }
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mContainerView);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mContainerView);
    }

    @OnClick({R.id.ivAsk, R.id.ivNotifications, R.id.llSearch, R.id.llMyRequests, R.id.llFav, R.id.llUserReq, R.id.llAddCompany, R.id.llSettings, R.id.llShare})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivAsk:
                break;
            case R.id.ivNotifications:
                break;
            case R.id.llSearch:
                mInterface.onSearchClicked();
                break;
            case R.id.llMyRequests:
                SharedStore.getInstance().clearMyRequests();
                llMyRequestsConter.setVisibility(View.GONE);
                mInterface.onMyRequestsClicked();
                break;
            case R.id.llFav:
                mInterface.onFavClicked();
                break;
            case R.id.llUserReq:
                SharedStore.getInstance().clearUserRequests();
                llUserReqCounter.setVisibility(View.GONE);
                mInterface.onUserRequestClicked();
                break;
            case R.id.llAddCompany:
                mInterface.onAddCompanyClicked();
                break;
            case R.id.llSettings:
                mInterface.onSettingsClicked();
                break;
            case R.id.llShare:
                mInterface.onShareClicked();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateRequestCounter(RequestCounter count) {
        Log.d("TAG", "updateRequestCounter");
        if (!count.isMy()) {
            if (count.getRequestCount() > 0) {
                mLlUserReq.setVisibility(View.VISIBLE);
                llUserReqCounter.setVisibility(View.VISIBLE);
                tvUserReqCounter.setText(String.valueOf(SharedStore.getInstance().getUserRequestsCount()));
            } else {
                llUserReqCounter.setVisibility(View.GONE);
            }
        } else {
            if (count.getRequestCount() > 0) {
                llMyRequestsConter.setVisibility(View.VISIBLE);
                tvMyRequestsCounter.setText(String.valueOf(SharedStore.getInstance().getMyRequestsCount()));
            } else {
                llMyRequestsConter.setVisibility(View.GONE);
            }
        }
    }
}
