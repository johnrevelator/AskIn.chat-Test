package com.unated.askincht_beta.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unated.askincht_beta.Pojo.MyRequestItem;
import com.unated.askincht_beta.R;
import com.unated.askincht_beta.Utils.DateFormater;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;


public class MyRequestsAdapter extends RecyclerView.Adapter<MyRequestsAdapter.BaseHolder> {

    private List<MyRequestItem> mRequests;
    private Context mContext;
    private View.OnClickListener mOnClickListener;
    private View.OnLongClickListener mOnLongClickListener;

    public MyRequestsAdapter(Context context, List<MyRequestItem> requests) {
        this.mRequests = requests;
        this.mContext = context;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_request, null);
        BaseHolder baseHolder = new BaseHolder(view);
        return baseHolder;
    }

    @Override
    public void onBindViewHolder(final BaseHolder holder, int position) {

        int newCount = 0;
        int allCount = 0;
        for (int i = 0; i < mRequests.get(position).getShops().size(); i++) {
            newCount += mRequests.get(position).getShops().get(i).getNew_messages();
            allCount += mRequests.get(position).getShops().get(i).getCnt_messages();
        }
        if(allCount<2){
            long time= System.currentTimeMillis() ;
Log.i("MyLog", String.valueOf(time));
Log.i("MyLog", String.valueOf(mRequests.get(position).getTime()));
            long diff= TimeUnit.MILLISECONDS.toMinutes(time- mRequests.get(position).getTime()*1000);
            Log.i("MyLog", String.valueOf(diff)+"diff");
            long tim;
            if(mRequests.get(position).getAvgTime()==0)
                tim=15;
            else
            tim=TimeUnit.MILLISECONDS.toMinutes(mRequests.get(position).getAvgTime()*1000);

            Log.i("MyLog", String.valueOf(tim)+"tim");

            if (tim>diff) {

                holder.clock.setVisibility(View.VISIBLE);
                if((tim - diff)>60)
                    if((tim - diff)%60!=0)
                    holder.mTextField.setText((tim - diff)/60+" час(-a) " +(tim - diff)%60+ " минут");
                else
                    holder.mTextField.setText((tim - diff)/60+" час(-a) ");
                else
                    holder.mTextField.setText((tim - diff)+ " минут");

            }

        }

        if (newCount > 0) {
            holder.llMsgStatus.setActivated(true);
            holder.tvMsgCount.setText(String.valueOf(newCount));
        } else {
            holder.llMsgStatus.setActivated(false);
            holder.tvMsgCount.setText(String.valueOf(allCount));
        }
      /*  new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                holder.mTextField.setText("Среднее время ожидания: " + DateFormater.getFormatedDate(millisUntilFinished / 1000, "HH:mm"));
            }

            public void onFinish() {
                holder.mTextField.setText("Нет ответа");
            }
        }.start();*/
        if(mRequests.get(position).isClosed()){
            holder.done.setVisibility(View.VISIBLE);

        }

        holder.tvMsgText.setText(mRequests.get(position).getText());
        Log.i("MyLog", String.valueOf(allCount));
        if(allCount>1)
        holder.tvAnsCount.setText(String.valueOf(mRequests.get(position).getShops().size()));
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

       /* Date date1 = format.parse(mRequests.get(position).getTime());
        Date date2 = format.parse(new Date());
        if(toDate((long)mRequests.get(position).getTime()))
*/
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
        holder.llParent.setTag(mRequests.get(position).getId() + ";" + mRequests.get(position).getText());
        holder.more.setTag(mRequests.get(position).getId() + ";" + mRequests.get(position).getText());
        holder.llParent.setOnClickListener(getOnClickListener());
        holder.more.setOnClickListener(getOnClickListener());
        holder.llParent.setOnLongClickListener(getOnLongClickListener());
    }

    private String toDate(long timestamp) {
        Date date = new Date (timestamp * 1000);
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
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
                word="Нояб";
                break;
            case 12:
                word="Дек";
                break;
        }

        return word;
    }

    public class BaseHolder extends RecyclerView.ViewHolder {

        private LinearLayout llMsgStatus;
        private TextView tvMsgCount;
        private ImageView clock;
        private TextView tvAnsCount;
        private TextView done;
        private TextView tvMsgText;
        private TextView tvTime;
        private TextView mTextField;
        private LinearLayout llParent;
        private ImageView more;

        public BaseHolder(View itemView) {
            super(itemView);
            more = ButterKnife.findById(itemView, R.id.moree);
            done = ButterKnife.findById(itemView, R.id.is_done);

            clock = ButterKnife.findById(itemView, R.id.imageView3);
            llMsgStatus = ButterKnife.findById(itemView, R.id.llMsgStatus);
            tvMsgCount = ButterKnife.findById(itemView, R.id.tvMsgCount);
            tvAnsCount = ButterKnife.findById(itemView, R.id.ansCount);
            tvMsgText = ButterKnife.findById(itemView, R.id.tvMsgText);
            tvTime = ButterKnife.findById(itemView, R.id.tvTime);
            llParent = ButterKnife.findById(itemView, R.id.llParent);
            mTextField = ButterKnife.findById(itemView, R.id.textView3);
        }
    }
}
