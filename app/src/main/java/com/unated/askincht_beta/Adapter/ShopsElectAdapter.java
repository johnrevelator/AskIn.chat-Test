package com.unated.askincht_beta.Adapter;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unated.askincht_beta.AppMain;
import com.unated.askincht_beta.Dialog.MoreCardDialogFragment;
import com.unated.askincht_beta.Pojo.Shop;
import com.unated.askincht_beta.Pojo.SimpleResponse;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.SharedStore;

import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShopsElectAdapter extends RecyclerView.Adapter<ShopsElectAdapter.BaseHolder> {
    public static final String TAG = "MyLog";

    private Context mContext;
    private List<Shop> mShops;
    private View.OnClickListener mOnClickListener;

    String listData = null;

    public void setData(String data) {
        listData = data;
    }
    public ShopsElectAdapter(Context context, List<Shop> shops) {
        this.mContext = context;
        this.mShops = shops;
    }

    @Override
    public ShopsElectAdapter.BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_shop_request, parent, false);
        BaseHolder baseHolder = new BaseHolder(view);
        return baseHolder;
    }

    @Override
    public void onBindViewHolder(final ShopsElectAdapter.BaseHolder holder, final int position) {
        Log.i(TAG,"notnulll");
        Log.i(TAG,mShops.get(position).getName());


        holder.dElect.setVisibility(View.VISIBLE);
        holder.aElect.setVisibility(View.GONE);
        holder.tvNewMsg.setVisibility(View.INVISIBLE);
        holder.tvMsgCount.setVisibility(View.INVISIBLE);
        holder.tvTime.setVisibility(View.INVISIBLE);
        holder.llMsgStatus.setVisibility(View.INVISIBLE);

        /*if (mShops.get(position).getNew_messages() > 0) {
            holder.llMsgStatus.setActivated(true);
            holder.tvNewMsg.setVisibility(View.VISIBLE);
            holder.tvMsgCount.setText(String.valueOf(mShops.get(position).getNew_messages()));
        } else {
            holder.llMsgStatus.setActivated(false);
            holder.tvNewMsg.setVisibility(View.INVISIBLE);
            holder.tvMsgCount.setText(String.valueOf(mShops.get(position).getCnt_messages()));
        }*/
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


/*
        holder.tvTime.setText(DateFormater.getFormatedDate( mShops.get(position).getLastRad()* 1000, "HH:mm"));
*/
        holder.dist.setText(String.format("%.1f",loc1.distanceTo(loc2)/1000)+"км");
       /* if(mShops.get(position).getMsg().length()>15)
        holder.lastMsg.setText(mShops.get(position).getMsg()+"...");
        else
            holder.lastMsg.setText(mShops.get(position).getMsg());
*/
        holder.tvMsgText.setText(mShops.get(position).getName());
        holder.llParent.setTag(mShops.get(position).getId()+";"+mShops.get(position).getName()
                +";"+String.format("%.2f",loc1.distanceTo(loc2)/1000) +";"+mShops.get(position).getLat() +";"+mShops.get(position).getLon()+";"+mShops.get(position).getPhone());
        holder.llParent.setOnClickListener(getOnClickListener());
        holder.aElect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.aElect.setVisibility(View.GONE);
                holder.dElect.setVisibility(View.VISIBLE);
                Call<SimpleResponse> call = AppMain.getClient().setElect(SharedStore.getInstance().getSID(),mShops.get(position).getId(),SharedStore.getInstance().getToken());
                Log.i(TAG,SharedStore.getInstance().getLastRequestId());
                call.enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {

                       /* try {
                            Log.i(TAG,response.errorBody().string());
                        } catch (IOException e) {
                            Log.i(TAG, String.valueOf(e));
                        }*/

                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        Log.i(TAG, String.valueOf(t));

                    }
                });



            }
        });

        holder.dElect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.aElect.setVisibility(View.VISIBLE);
                holder.dElect.setVisibility(View.GONE);
                Call<SimpleResponse> call = AppMain.getClient().unSetElect(SharedStore.getInstance().getSID(),mShops.get(position).getId(),SharedStore.getInstance().getToken());
                Log.i(TAG,SharedStore.getInstance().getLastRequestId());
                call.enqueue(new Callback<SimpleResponse>() {
                    @Override
                    public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                        mShops.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,mShops.size());
                       /* try {
                            Log.i(TAG,response.errorBody().string());
                        } catch (IOException e) {
                            Log.i(TAG, String.valueOf(e));
                        }*/

                    }

                    @Override
                    public void onFailure(Call<SimpleResponse> call, Throwable t) {
                        Log.i(TAG, String.valueOf(t));


                    }
                });



            }
        });
        holder.more.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoreCardDialogFragment moreCardDialogFragment = new   MoreCardDialogFragment();
                moreCardDialogFragment.show(((FragmentActivity)mContext).getSupportFragmentManager(), "TAG");
            }
            });

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
        private TextView tvMsgText;
        private TextView tvTime;
        private TextView lastMsg;
        private TextView dist;
        private ImageView aElect;
        private ImageView more;
        private ImageView dElect;
        private ImageView tvNewMsg;
        private LinearLayout llParent;

        public BaseHolder(View itemView) {
            super(itemView);

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
