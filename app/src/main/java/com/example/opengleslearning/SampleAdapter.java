package com.example.opengleslearning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.SampleViewHolder> {
    private ArrayList<String> datas;
    private ItemClickListener listener;
    public SampleAdapter(ArrayList<String> datas) {
        this.datas = datas;
    }

    @NonNull
    @Override
    public SampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample, parent, false);
        return new SampleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SampleViewHolder holder, final int position) {
        holder.tvContent.setText(datas.get(position));
        //点击事件
        holder.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    static class SampleViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;
        SampleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }

    //定义回调接口
    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }
}
