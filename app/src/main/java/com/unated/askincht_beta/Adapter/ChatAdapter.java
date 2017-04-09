package com.unated.askincht_beta.Adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.unated.askincht_beta.Activity.SimpleSampleActivity;
import com.unated.askincht_beta.Pojo.BusMessages.MessageItem;
import com.unated.askincht_beta.Pojo.ChatInterface;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.DateFormater;
import com.unated.askincht_beta.Utils.SharedStore;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.content.Context.CLIPBOARD_SERVICE;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageHolder> {
    public static final String TAG = "MyLog";

    private List<ChatInterface> mMessages;
    private Context mContext;
    public class MessageHolder extends RecyclerView.ViewHolder {

        private TextView tvTime;
        private LinearLayout left;
        private LinearLayout right;
        private ProgressBar progressBar;
        private ProgressBar progressBar1;
        private TextView tvMessage;
        private ImageView ivAvatar;
        private ImageView leftT;
        private ImageView rightT;
        private ImageView ivReaded;
        private TextView tvTimer;
        private TextView tvMessager;
        private TextView tvName;
        private TextView tvNamer;
        private ImageView ivAvatarr;
        private ImageView ivImg;
        private ImageView lPl;
        private ImageView rPl;
        private ImageView ivImg1;
        private ImageView ivReadedr;
        private RelativeLayout msg;
        private RelativeLayout msg1;

        public MessageHolder(View itemView) {
            super(itemView);
            tvTime = ButterKnife.findById(itemView, R.id.tvTime);
            lPl = ButterKnife.findById(itemView, R.id.l_pl);
            rPl = ButterKnife.findById(itemView, R.id.r_pl);
            progressBar = ButterKnife.findById(itemView, R.id.progress);
            progressBar1 = ButterKnife.findById(itemView, R.id.progress1);
            ivImg= ButterKnife.findById(itemView, R.id.msg_img);
            ivImg1= ButterKnife.findById(itemView, R.id.msg_img1);
            msg= ButterKnife.findById(itemView, R.id.msg);
            msg1= ButterKnife.findById(itemView, R.id.msg1);
            left = ButterKnife.findById(itemView, R.id.left);
            leftT = ButterKnife.findById(itemView, R.id.imageView7);
            right = ButterKnife.findById(itemView, R.id.right);
            rightT = ButterKnife.findById(itemView, R.id.imageView71);
            tvMessage = ButterKnife.findById(itemView, R.id.tvMsgText);
            tvName = ButterKnife.findById(itemView, R.id.tvMsgName);
            tvNamer = ButterKnife.findById(itemView, R.id.tvMsgName1);
            ivAvatar = ButterKnife.findById(itemView, R.id.ivAvatar);
            ivReaded = ButterKnife.findById(itemView, R.id.ivReaded);
            tvTimer = ButterKnife.findById(itemView, R.id.tvTime1);
            tvMessager = ButterKnife.findById(itemView, R.id.tvMsgText1);
            ivAvatarr = ButterKnife.findById(itemView, R.id.ivAvatar1);
            ivReadedr = ButterKnife.findById(itemView, R.id.ivReaded1);
        }
    }

    public ChatAdapter(Context context, List<ChatInterface> messageItems) {
        this.mMessages = messageItems;
        this.mContext = context;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layout = R.layout.list_item_message;

        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);
        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(final MessageHolder holder, final int position) {
        Log.i(TAG, String.valueOf(((MessageItem) mMessages.get(position)).getUserId())+" "+SharedStore.getInstance().getUserId());
        Log.i(TAG, String.valueOf(((MessageItem) mMessages.get(position)).getReaded()));
        if (!((MessageItem) mMessages.get(position)).getUserId().equals(SharedStore.getInstance().getUserId())) {
            holder.right.setVisibility(View.GONE);
            /*
            holder.ivReaded.setVisibility(View.GONE);


*/          holder.tvMessage.setText(((MessageItem) mMessages.get(position)).getText());
            holder.tvName.setText(((MessageItem) mMessages.get(position)).getUserName());
            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            Timestamp stampD = new Timestamp(((MessageItem) mMessages.get(position)).getTime()* 1000);

            long daysBetween = TimeUnit.MILLISECONDS.toDays(stamp.getTime()-stampD.getTime());
            Log.i("MyLog",daysBetween+" days");
            if(daysBetween==0)
                holder.tvTime.setText(DateFormater.getFormatedDate(((MessageItem) mMessages.get(position)).getTime() * 1000, "HH:mm"));
            else {
                Log.i("MyLog",DateFormater.getFormatedDate(((MessageItem) mMessages.get(position)).getTime()  * 1000, "MM")+"month");
                Log.i("MyLog",DateFormater.getFormatedDate(((MessageItem) mMessages.get(position)).getTime()  * 1000, "dd")+"day");

                holder.tvTime.setText(DateFormater.getFormatedDate(((MessageItem) mMessages.get(position)).getTime() * 1000, "dd")+" "+numToWord(DateFormater.getFormatedDate(((MessageItem) mMessages.get(position)).getTime()  * 1000, "MM")));
            }            if(((MessageItem) mMessages.get(position)).isIs_file()){
                holder.tvMessage.setVisibility(View.GONE);
                final Context context=holder.right.getContext();

                holder.tvTime.setVisibility(View.GONE);
                Log.i("MyLog","prepare");
                holder.leftT.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
                Log.i("MyLog", "http://beta.api.askin.chat/api/v1/storage/loadChatFile/" + ((MessageItem) mMessages.get(position)).getRequest_id() + "/" + ((MessageItem) mMessages.get(position)).getShop_id() + "/" + ((MessageItem) mMessages.get(position)).getFile_name());
                holder.msg.setBackgroundColor(Color.parseColor("#00000000"));
                holder.ivReadedr.setVisibility(View.GONE);
                holder.ivImg1.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("image", "http://beta.api.askin.chat/api/v1/storage/loadChatFile/" + ((MessageItem) mMessages.get(position)).getRequest_id() + "/" + ((MessageItem) mMessages.get(position)).getShop_id() + "/" + ((MessageItem) mMessages.get(position)).getFile_name());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(mContext, "Скопированно", Toast.LENGTH_LONG).show();

                        return false;
                    }
                });
                holder.ivImg1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(context, SimpleSampleActivity.class);
                        intent.putExtra("ph","http://beta.api.askin.chat/api/v1/storage/loadChatFile/" + ((MessageItem) mMessages.get(position)).getRequest_id() + "/" + ((MessageItem) mMessages.get(position)).getShop_id() + "/" + ((MessageItem) mMessages.get(position)).getFile_name());
                        Activity activity = (Activity) context;
                        activity.startActivity(intent);

                        activity.overridePendingTransition( android.R.anim.fade_in,android.R.anim.fade_out);                   }
                });
                Log.i(TAG, Resources.getSystem().getDisplayMetrics().density+" density");


                    int width;
             /*   width= (int) (250*Resources.getSystem().getDisplayMetrics().density*((MessageItem) mMessages.get(position)).getFileChat().getImg_height()/((MessageItem) mMessages.get(position)).getFileChat().getImg_width());
                FrameLayout.LayoutParams layoutParams = new  FrameLayout.LayoutParams((int) (250*Resources.getSystem().getDisplayMetrics().density),width );
                    holder.ivImg1.setLayoutParams(layoutParams);
*/
                Glide.with(holder.right.getContext()).load("http://beta.api.askin.chat/api/v1/storage/loadChatFile/" + ((MessageItem) mMessages.get(position)).getRequest_id() + "/" + ((MessageItem) mMessages.get(position)).getShop_id() + "/" + ((MessageItem) mMessages.get(position)).getFile_name()).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.ivImg1);
                holder.ivImg1.setImageURI(null);
               /* holder.ivImg1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent fullScreenIntent = new Intent(context, FullScreenImageActivity.class);
                        holder.ivImg1.buildDrawingCache();
                        Bitmap image= holder.ivImg1.getDrawingCache();

                        Bundle extras = new Bundle();
                        extras.putParcelable("imagebitmap", image);
                        fullScreenIntent.putExtras(extras);
                        context.startActivity(fullScreenIntent);
                    }
                });*/

            }
            else{
                holder.ivImg1.setVisibility(View.GONE);
                holder.lPl.setVisibility(View.GONE);

            }

            } else {
            holder.left.setVisibility(View.GONE);
            holder.tvMessager.setText(((MessageItem) mMessages.get(position)).getText());
            Timestamp stamp = new Timestamp(System.currentTimeMillis());
            Timestamp stampD = new Timestamp(((MessageItem) mMessages.get(position)).getTime()* 1000);

            long daysBetween = TimeUnit.MILLISECONDS.toDays(stamp.getTime()-stampD.getTime());
            Log.i("MyLog",daysBetween+" days");
            if(daysBetween==0)
                holder.tvTimer.setText(DateFormater.getFormatedDate(((MessageItem) mMessages.get(position)).getTime() * 1000, "HH:mm"));
            else {
                Log.i("MyLog",DateFormater.getFormatedDate(((MessageItem) mMessages.get(position)).getTime()  * 1000, "MM")+"month");
                Log.i("MyLog",DateFormater.getFormatedDate(((MessageItem) mMessages.get(position)).getTime()  * 1000, "dd")+"day");

                holder.tvTimer.setText(DateFormater.getFormatedDate(((MessageItem) mMessages.get(position)).getTime() * 1000, "dd")+" "+numToWord(DateFormater.getFormatedDate(((MessageItem) mMessages.get(position)).getTime()  * 1000, "MM")));
            }                        if (((MessageItem) mMessages.get(position)).getReaded() != null) {
                if (((MessageItem) mMessages.get(position)).getReaded().equals("true")) {
                    holder.ivReadedr.setImageResource(R.drawable.read);
                }

            }
            if (((MessageItem) mMessages.get(position)).isIs_file()) {
                holder.rightT.setVisibility(View.GONE);
                holder.progressBar1.setVisibility(View.VISIBLE);

                holder.tvMessager.setVisibility(View.GONE);
                holder.tvTimer.setVisibility(View.GONE);
                Log.i("MyLog", "prepare");
               /* Call<Void> call = AppMain.getClient().getFile(((MessageItem) mMessages.get(position)).getShop_id(),((MessageItem) mMessages.get(position)).getRequest_id(),((MessageItem) mMessages.get(position)).getFile_name());
                Log.i(TAG, SharedStore.getInstance().getLastRequestId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {


                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.i(TAG, String.valueOf(t));


                    }
                });
*/
                Log.i("MyLog", "http://beta.api.askin.chat/api/v1/storage/loadChatFile/" + ((MessageItem) mMessages.get(position)).getRequest_id() + "/" + ((MessageItem) mMessages.get(position)).getShop_id() + "/" + ((MessageItem) mMessages.get(position)).getFile_name());
                holder.msg1.setBackgroundColor(Color.parseColor("#00000000"));
                holder.ivReadedr.setVisibility(View.GONE);
                Log.i(TAG, Resources.getSystem().getDisplayMetrics().density+" density");

                final Context context = holder.right.getContext();

                /*    int width;
                    width= (int) (250*Resources.getSystem().getDisplayMetrics().density*((MessageItem) mMessages.get(position)).getFileChat().getImg_height()/((MessageItem) mMessages.get(position)).getFileChat().getImg_width());
                    FrameLayout.LayoutParams layoutParams = new  FrameLayout.LayoutParams((int) (250*Resources.getSystem().getDisplayMetrics().density),width );
                    holder.ivImg.setLayoutParams(layoutParams);
*/
                Glide.with(holder.right.getContext()).load("http://beta.api.askin.chat/api/v1/storage/loadChatFile/" + ((MessageItem) mMessages.get(position)).getRequest_id() + "/" + ((MessageItem) mMessages.get(position)).getShop_id() + "/" + ((MessageItem) mMessages.get(position)).getFile_name()).centerCrop().listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        holder.progressBar1.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        holder.progressBar1.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.ivImg);
                holder.ivImg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("image", "http://beta.api.askin.chat/api/v1/storage/loadChatFile/" + ((MessageItem) mMessages.get(position)).getRequest_id() + "/" + ((MessageItem) mMessages.get(position)).getShop_id() + "/" + ((MessageItem) mMessages.get(position)).getFile_name());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(mContext, "Скопированно", Toast.LENGTH_LONG).show();
                        return false;
                    }
                });
                holder.ivImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(context, SimpleSampleActivity.class);

                        intent.putExtra("ph","http://beta.api.askin.chat/api/v1/storage/loadChatFile/" + ((MessageItem) mMessages.get(position)).getRequest_id() + "/" + ((MessageItem) mMessages.get(position)).getShop_id() + "/" + ((MessageItem) mMessages.get(position)).getFile_name());
                        Activity activity = (Activity) context;
                        activity.startActivity(intent);

                        activity.overridePendingTransition( android.R.anim.fade_in,android.R.anim.fade_out);

                    }
                });
                holder.ivImg.setImageURI(null);
              /*  holder.ivImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent fullScreenIntent = new Intent(context, FullScreenImageActivity.class);
                        holder.ivImg.buildDrawingCache();
                        Bitmap image= holder.ivImg.getDrawingCache();

                        Bundle extras = new Bundle();
                        extras.putParcelable("imagebitmap", image);
                        fullScreenIntent.putExtras(extras);
                        context.startActivity(fullScreenIntent);
                    }
                });*/

            } else{
                    holder.ivImg.setVisibility(View.GONE);
                    holder.rPl.setVisibility(View.GONE);

                }

        }

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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    /*public class BaseHolder extends  {

        public BaseHolder(View itemView) {
            super(itemView);
        }
    }
*/


   /* public class WritingHolder extends BaseHolder {

        ImageView dot1;
        ImageView dot2;
        ImageView dot3;

        List<View> mDots;

        public WritingHolder(View itemView) {
            super(itemView);

            mDots = new ArrayList<>();

            dot1 = ButterKnife.findById(itemView, R.id.dot1);
            dot2 = ButterKnife.findById(itemView, R.id.dot2);
            dot3 = ButterKnife.findById(itemView, R.id.dot3);

            mDots.add(dot1);
            mDots.add(dot2);
            mDots.add(dot3);
        }

        public void animateDots() {
            Random r = new Random();
            for (final View dot : mDots) {
                final AlphaAnimation in = new AlphaAnimation(0.0f, 1.0f);
                final AlphaAnimation out = new AlphaAnimation(1.0f, 0.0f);
                in.setDuration(1000);
                in.setStartOffset(r.nextInt(3000));
                out.setStartOffset(2000);
                out.setDuration(1000);


                in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dot.startAnimation(out);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                out.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        dot.startAnimation(in);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                dot.startAnimation(in);
            }
        }
    }*/
}
