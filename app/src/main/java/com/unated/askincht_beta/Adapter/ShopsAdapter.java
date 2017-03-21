package com.unated.askincht_beta.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Pojo.Shop;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.DateFormater;
import com.unated.askincht_beta.Utils.SharedStore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.BaseHolder> {
    public static final String TAG = "MyLog";
    int edCount;
    int frCount;
    private Context mContext;
    private List<Shop> mShops;
    private View.OnClickListener mOnClickListener;

    String listData = null;

    public void setData(String data) {
        listData = data;
    }
    public ShopsAdapter(Context context, List<Shop> shops) {
        this.mContext = context;
        this.mShops = shops;
    }

    @Override
    public ShopsAdapter.BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_shop_request, parent, false);
        BaseHolder baseHolder = new BaseHolder(view);
        return baseHolder;
    }
    public String numToWord(String num){
        String word = null;
        switch (Integer.valueOf(num)){
            case 1:
                word="Янв";
                break;
            case 2:
                word="Фев";
                break;
            case 3:
                word="Мар";
                break;
            case 4:
                word="Апр";
                break;
            case 5:
                word="Май";
                break;
            case 6:
                word="Июн";
                break;
            case 7:
                word="Июл";
                break;
            case 8:
                word="Авг";
                break;
            case 9:
                word="Сент";
                break;
            case 10:
                word="Окт";
                break;
            case 11:
                word="Дек";
                break;
            case 12:
                word="Дек";
                break;
        }

        return word;
    }

    @Override
    public void onBindViewHolder(final ShopsAdapter.BaseHolder holder, final int position) {
        holder.aElect.setVisibility(View.GONE);
        holder.dElect.setVisibility(View.GONE);
      /*  Call<ElectResponse> call = AppMain.getClient().getElect(SharedStore.getInstance().getSID());
        call.enqueue(new Callback<ElectResponse>() {
            @Override
            public void onResponse(Call<ElectResponse> call, Response<ElectResponse> response) {
                if (response.isSuccessful() && response.body().getData() != null) {
                    holder.mElectShopList.clear();

                    holder.mElectShopList.addAll(response.body().getData().getRequests());
                    for (int i = 0; i < holder.mElectShopList.size(); i++) {
                        if (mShops.get(position).getId() == holder.mElectShopList.get(i).getId())
                            holder.aElect.setVisibility(View.GONE);
                        holder.dElect.setVisibility(View.VISIBLE);
                    }

                }
                else{
                    Log.i(TAG,"null");

                }
            }

            @Override
            public void onFailure(Call<ElectResponse> call, Throwable t) {

            }
        });*/
        Glide.with(holder.mImageView.getContext()).load(mShops.get(position).getAvatar()) .asBitmap().centerCrop().placeholder(R.drawable.mask)

                .into(new BitmapImageViewTarget(holder.mImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(holder.mImageView.getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.mImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });

        Log.i(TAG, "notnulll");
        Log.i(TAG, mShops.get(position).getName());
        if (mShops.get(position).getNew_messages() > 0) {
            holder.llMsgStatus.setActivated(true);
            holder.tvNewMsg.setVisibility(View.VISIBLE);
            holder.tvMsgCount.setText(String.valueOf(mShops.get(position).getNew_messages()));
        } else {
            holder.llMsgStatus.setActivated(false);
            holder.tvNewMsg.setVisibility(View.INVISIBLE);
            holder.tvMsgCount.setText(String.valueOf(mShops.get(position).getCnt_messages()));
        }
        /*Call<MessagesResponse> msg = AppMain.getClient().getRequestMessages(SharedStore.getInstance().getSID(), String.valueOf(mShops.get(position).getId()), listData, "1");
        msg.enqueue(new Callback<MessagesResponse>() {
            @Override
            public void onResponse(Call<MessagesResponse> call, Response<MessagesResponse> response) {
                holder.tvTime.setText(DateFormater.getFormatedDate(response.body().getData().getMessages().get(0).getTime(), "HH:mm"));


            }

            @Override
            public void onFailure(Call<MessagesResponse> call, Throwable t) {
            }
        });*/
        Location loc1 = new Location("");
        loc1.setLatitude(mShops.get(position).getLat());
        loc1.setLongitude(mShops.get(position).getLon());
        Location loc2 = new Location("");

        loc2.setLatitude(Double.parseDouble(SharedStore.getInstance().getMyLat()));
        loc2.setLongitude(Double.parseDouble(SharedStore.getInstance().getMyLng()));


        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        Timestamp stampD = new Timestamp(mShops.get(position).getLastRad() * 1000);

        long daysBetween = TimeUnit.MILLISECONDS.toDays(stamp.getTime()-stampD.getTime());
        Log.i("MyLog",daysBetween+" days");
        if(daysBetween==0)
            holder.tvTime.setText(DateFormater.getFormatedDate(mShops.get(position).getLastRad()  * 1000, "HH:mm"));
        else {
            Log.i("MyLog",DateFormater.getFormatedDate(mShops.get(position).getLastRad()  * 1000, "MM")+"month");
            Log.i("MyLog",DateFormater.getFormatedDate(mShops.get(position).getLastRad()  * 1000, "dd")+"day");

            holder.tvTime.setText(DateFormater.getFormatedDate(mShops.get(position).getLastRad()  * 1000, "dd")+" "+numToWord(DateFormater.getFormatedDate(mShops.get(position).getLastRad()  * 1000, "MM")));
        }        holder.dist.setText(String.format("%.1f", loc1.distanceTo(loc2) / 1000) + "км");
        if(mShops.get(position).getMsg() ==null)
            holder.lastMsg.setText("Фотография");
        else
        if (mShops.get(position).getMsg().length() > 15)
            holder.lastMsg.setText(mShops.get(position).getMsg().substring(0,15) + "...");
        else
            holder.lastMsg.setText(mShops.get(position).getMsg());



        holder.tvMsgText.setText(mShops.get(position).getName());
        holder.more.setTag(mShops.get(position).getId() + ";" + mShops.get(position).getName()
                + ";" + String.format("%.2f", loc1.distanceTo(loc2) / 1000) + ";" + mShops.get(position).getLat() + ";" + mShops.get(position).getLon() + ";" + mShops.get(position).getPhone());
        holder.llParent.setTag(mShops.get(position).getId() + ";" + mShops.get(position).getName()
                + ";" + String.format("%.2f", loc1.distanceTo(loc2) / 1000) + ";" + mShops.get(position).getLat() + ";" + mShops.get(position).getLon() + ";" + mShops.get(position).getPhone());
        holder.llParent.setOnClickListener(getOnClickListener());
        holder.aElect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.aElect.setVisibility(View.GONE);
                holder.dElect.setVisibility(View.VISIBLE);
                Call<SimpleResponse> call = AppMain.getClient().setElect(SharedStore.getInstance().getSID(), mShops.get(position).getId(),SharedStore.getInstance().getToken());
                Log.i(TAG, SharedStore.getInstance().getLastRequestId());
                call.enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {


                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        Log.i(TAG, String.valueOf(t));

                    }
                });


            }
        });
        holder.dElect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.aElect.setVisibility(View.VISIBLE);
                holder.dElect.setVisibility(View.GONE);
                Call<SimpleResponse> call = AppMain.getClient().unSetElect(SharedStore.getInstance().getSID(), mShops.get(position).getId(),SharedStore.getInstance().getToken());
                Log.i(TAG, SharedStore.getInstance().getLastRequestId());
                call.enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {


                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        Log.i(TAG, String.valueOf(t));


                    }
                });


            }
        });
        holder.more.setOnClickListener(getOnClickListener());
        /*holder.more.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreCardDialogFragment moreCardDialogFragment = new   MoreCardDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id", mShops.get(position).getId());
                bundle.putInt("id", mShops.get(position).getId());
                moreCardDialogFragment.setArguments(bundle);
                moreCardDialogFragment.show(((FragmentActivity)mContext).getSupportFragmentManager(), "TAG");
            }
            });

        }*/
        edCount=0;
        if(mShops.get(position).getRating()!=null)
        holder.ratingBarC.setRating(Float.parseFloat(mShops.get(position).getRating()));
        else
        holder.ratingBarC.setVisibility(View.INVISIBLE);
        for (int i=0;i<mShops.get(position).getFriends().size();i++) {
            if (i < 5) {
                final ImageView imageView;
                RelativeLayout.LayoutParams lm = new RelativeLayout.LayoutParams(100, 100);
                imageView = new ImageView(holder.more.getContext());
                imageView.setId(edCount+5);
                Log.i(TAG, String.valueOf(edCount+5));
                Glide.with(holder.more.getContext()).load(mShops.get(position).getFriends().get(i)).asBitmap().centerCrop().placeholder(R.drawable.mask)

                        .into(new BitmapImageViewTarget(imageView) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(holder.more.getContext().getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                imageView.setImageDrawable(circularBitmapDrawable);
                            }
                        });


                if (i > 0) {
                    lm.addRule(RelativeLayout.ALIGN_LEFT, holder.more.getContext().getResources().getIdentifier(String.valueOf((edCount - 1) +5), "id", holder.more.getContext().getPackageName()));
                    lm.setMargins(50, 0, 0, 0);
                }

                imageView.setLayoutParams(lm);

                edCount++;

                Log.i(TAG, "added");
                holder.linear.addView(imageView);
            }
        }
    }




    @Override
    public int getItemCount() {
        return mShops.size();
    }

    public View.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public class BaseHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMsgStatus;
        private TextView tvMsgCount;
        private RatingBar ratingBarC;
        private TextView tvMsgText;
        private TextView tvTime;
        private TextView lastMsg;
        private TextView dist;
        private ImageView aElect;
        private ImageView imageView;
        private ImageView more;
        private ImageView dElect;
        private ImageView tvNewMsg;
        private ImageView mImageView;
        private LinearLayout llParent;
        RelativeLayout linear;

        private List<Shop> mElectShopList;


        public BaseHolder(View itemView) {
            super(itemView);
            linear = ButterKnife.findById(itemView, R.id.images);
            mElectShopList=new ArrayList<>();
            mImageView = ButterKnife.findById(itemView, R.id.imageView5);
            ratingBarC = ButterKnife.findById(itemView, R.id.ratingC);
            llMsgStatus = ButterKnife.findById(itemView, R.id.llMsgStatus);
            tvMsgCount = ButterKnife.findById(itemView, R.id.tvMsgCount);
            tvMsgText = ButterKnife.findById(itemView, R.id.tvMsgText);
            tvTime = ButterKnife.findById(itemView, R.id.tvTime);
            tvNewMsg = ButterKnife.findById(itemView, R.id.newMsg);
            lastMsg = ButterKnife.findById(itemView, R.id.textView6);
            dist = ButterKnife.findById(itemView, R.id.distance);
            aElect = ButterKnife.findById(itemView, R.id.act_h);
            more = ButterKnife.findById(itemView, R.id.more);
            dElect = ButterKnife.findById(itemView, R.id.disact_heart);
            llParent = ButterKnife.findById(itemView, R.id.llParent);
        }


    }

}
