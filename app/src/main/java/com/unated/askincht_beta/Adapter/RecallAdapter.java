package com.unated.askincht_beta.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.unated.askincht_beta.Pojo.Recall;
import com.unated.askincht_beta.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


public class RecallAdapter extends RecyclerView.Adapter<RecallAdapter.BaseHolder> {
    public static final String TAG = "MyLog";

    private Context mContext;
    private List<Recall> mRecalls;
    private View.OnClickListener mOnClickListener;

    String listData = null;

    public void setData(String data) {
        listData = data;
    }
    public RecallAdapter(Context context, List<Recall> mRecalls) {
        this.mContext = context;
        this.mRecalls = mRecalls;
    }

    @Override
    public RecallAdapter.BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recall, parent, false);
        BaseHolder baseHolder = new BaseHolder(view);
        return baseHolder;
    }

    @Override
    public void onBindViewHolder(final RecallAdapter.BaseHolder holder, final int position) {


        holder.name.setText(mRecalls.get(position).getUser_name());
        holder.text.setText(mRecalls.get(position).getText());
        holder.ratingBar.setRating(Float.parseFloat(mRecalls.get(position).getRating()));

    }






    @Override
    public int getItemCount() {
        return mRecalls.size();
    }

    public View.OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    public class BaseHolder extends RecyclerView.ViewHolder {


        private ImageView avatar;
        private TextView name;
        private TextView text;
        private RatingBar ratingBar;
        private List<Recall> mRecallsList;


        public BaseHolder(View itemView) {
            super(itemView);
            mRecallsList=new ArrayList<>();
            avatar= ButterKnife.findById(itemView, R.id.avatar);
            name = ButterKnife.findById(itemView, R.id.name);
            text = ButterKnife.findById(itemView, R.id.recall_text);
            ratingBar = ButterKnife.findById(itemView, R.id.rating);

        }


    }

}
