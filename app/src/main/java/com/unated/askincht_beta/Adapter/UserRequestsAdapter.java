package com.unated.askincht_beta.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.unated.askincht_beta.Pojo.RequestItem;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.DateFormater;
import com.unated.askincht_beta.Utils.SharedStore;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

public class UserRequestsAdapter extends RecyclerView.Adapter<UserRequestsAdapter.BaseHolder> {
    public static final String TAG = "MyLog";

    private List<RequestItem> mRequests;
    private Context mContext;
    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;

    public UserRequestsAdapter(Context context, List<RequestItem> requests) {
        this.mRequests = requests;
        this.mContext = context;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_shop_request, null);
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
    public void onBindViewHolder(final BaseHolder holder, final int position) {
        holder.ratingBarC.setVisibility(View.GONE);
        holder.aElect.setVisibility(View.GONE);
        holder.dElect.setVisibility(View.GONE);
        if (mRequests.get(position).getNew_messages() > 0) {
            holder.llMsgStatus.setActivated(true);
            holder.tvNewMsg.setVisibility(View.VISIBLE);
            holder.tvMsgCount.setText(String.valueOf(mRequests.get(position).getNew_messages()));
        } else {
            holder.llMsgStatus.setActivated(false);
            holder.tvNewMsg.setVisibility(View.INVISIBLE);
            holder.tvMsgCount.setText(String.valueOf(mRequests.get(position).getCnt_messages()));
        }
       /* Glide.with(holder.mImageView.getContext()).load(mShops.get(position).getAvatar()) .asBitmap().centerCrop().placeholder(R.drawable.mask)

                .into(new BitmapImageViewTarget(holder.mImageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(holder.mImageView.getContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.mImageView.setImageDrawable(circularBitmapDrawable);
                    }
                });*/
        holder.tvCity.setText(mRequests.get(position).getCity());
        holder.tvDist.setVisibility(View.GONE);
        if(mRequests.get(position).isClosed()){
            holder.lastMsg.setText("Сделка завершена");
            holder.lastMsg.setTextColor(Color.parseColor("#00CC00"));
        }
        else {
            if (mRequests.get(position).getLastMsg() == null)
                holder.lastMsg.setText("Фотография");
            else if (mRequests.get(position).getLastMsg().length() > 40)
                holder.lastMsg.setText(mRequests.get(position).getLastMsg().substring(0, 40) + "...");
            else
                holder.lastMsg.setText(mRequests.get(position).getLastMsg());
        }
        holder.tvMsgText.setText(mRequests.get(position).getText());
        holder.more.setTag(SharedStore.getInstance().getMyShop().getId() + ";" +mRequests.get(position).getText()
                +";"+mRequests.get(position).getId()/*mRequests.get(position).getShop_id()*/ );
        Timestamp stamp = new Timestamp(System.currentTimeMillis());
        Timestamp stampD = new Timestamp(mRequests.get(position).getTime() * 1000);

        long daysBetween = TimeUnit.MILLISECONDS.toDays(stamp.getTime()-stampD.getTime());
        Log.i("MyLog",daysBetween+" days");
        if(daysBetween==0)
            holder.tvTime.setText(DateFormater.getFormatedDate(mRequests.get(position).getTime() * 1000, "HH:mm"));
        else {
            Log.i("MyLog",DateFormater.getFormatedDate(mRequests.get(position).getTime() * 1000, "MM")+"month");
            Log.i("MyLog",DateFormater.getFormatedDate(mRequests.get(position).getTime() * 1000, "dd")+"day");

            holder.tvTime.setText(DateFormater.getFormatedDate(mRequests.get(position).getTime() * 1000, "dd")+" "+numToWord(DateFormater.getFormatedDate(mRequests.get(position).getTime() * 1000, "MM")));
        }
        holder.llParent.setTag(SharedStore.getInstance().getMyShop().getId() + ";" +mRequests.get(position).getText()
                +";"+mRequests.get(position).getId()+ ";" + mRequests.get(position).isClosed() );
        holder.llParent.setOnClickListener(getOnClickListener());
        holder.llParent.setOnLongClickListener(getOnLongClickListener());
       /* holder.aElect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.aElect.setVisibility(View.GONE);
                holder.dElect.setVisibility(View.VISIBLE);
                Call<Void> call = AppMain.getClient().setElect(SharedStore.getInstance().getSID(),mRequests.get(position).getUser_id());
                Log.i(TAG,SharedStore.getInstance().getLastRequestId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.i(TAG,response.body().toString());
                        try {
                            Log.i(TAG,response.errorBody().string());
                        } catch (IOException e) {
                            Log.i(TAG, String.valueOf(e));
                        }

                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
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
                Call<Void> call = AppMain.getClient().unSetElect(SharedStore.getInstance().getSID(),mRequests.get(position).getUser_id());
                Log.i(TAG,SharedStore.getInstance().getLastRequestId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.i(TAG,response.body().toString());
                        try {
                            Log.i(TAG,response.errorBody().string());
                        } catch (IOException e) {
                            Log.i(TAG, String.valueOf(e));
                        }

                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.i(TAG, String.valueOf(t));


                    }
                });


            }
        });
*/
        holder.more.setOnClickListener(getOnClickListener());


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mRequests.size();
    }

    public View.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public View.OnLongClickListener getOnLongClickListener() {
        return mOnLongClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
    }

    public class BaseHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMsgStatus;
        private TextView tvMsgCount;
        private TextView tvMsgText;
        private TextView tvCity;
        private TextView tvTime;
        private TextView tvDist;
        private TextView lastMsg;
        private ImageView aElect;
        private ImageView more;
        private ImageView dElect;
        private ImageView mImageView;
        private RatingBar ratingBarC;

        private ImageView tvNewMsg;
        private LinearLayout llParent;

        public BaseHolder(View itemView) {
            super(itemView);
            more = ButterKnife.findById(itemView, R.id.more);
            mImageView = ButterKnife.findById(itemView, R.id.imageView5);
            ratingBarC = ButterKnife.findById(itemView, R.id.ratingC);

            llMsgStatus = ButterKnife.findById(itemView, R.id.llMsgStatus);
            tvMsgCount = ButterKnife.findById(itemView, R.id.tvMsgCount);
            tvCity = ButterKnife.findById(itemView, R.id.tvCity);
            tvMsgText = ButterKnife.findById(itemView, R.id.tvMsgText);
            tvDist = ButterKnife.findById(itemView, R.id.distance);
            tvTime = ButterKnife.findById(itemView, R.id.tvTime);
            tvNewMsg = ButterKnife.findById(itemView, R.id.newMsg);
            lastMsg = ButterKnife.findById(itemView, R.id.textView6);
            aElect = ButterKnife.findById(itemView, R.id.act_h);
            dElect = ButterKnife.findById(itemView, R.id.disact_heart);
            llParent = ButterKnife.findById(itemView, R.id.llParent);
        }
    }
}