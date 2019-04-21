package uk.co.alexmusgrove.applocker.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import uk.co.alexmusgrove.applocker.R;
import uk.co.alexmusgrove.applocker.Helpers.appItem;

public class appAdapter extends RecyclerView.Adapter<appAdapter.appViewHolder> {
    private ArrayList<appItem> appList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class appViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView1;
        TextView mTextView2;
        ImageView mAppIcon;

        appViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mTextView1 = itemView.findViewById(R.id.textView_1);
            mTextView2 = itemView.findViewById(R.id.textView_2);
            mAppIcon = itemView.findViewById(R.id.imageView);


            itemView.setOnClickListener(v -> {
                if (listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }


    public appAdapter(ArrayList<appItem> appList){
        this.appList = appList;
    }
    @NonNull
    @Override
    public appViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_item, viewGroup, false);
        return new appViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull appViewHolder vh, int i) {
        appItem currentItem = appList.get(i);

        vh.mTextView1.setText(currentItem.getmText1());
        vh.mTextView2.setText(currentItem.getmPackageName());
        vh.mAppIcon.setImageDrawable(currentItem.getmAppIcon());
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }
}
