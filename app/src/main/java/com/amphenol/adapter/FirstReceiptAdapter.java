package com.amphenol.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 */
public class FirstReceiptAdapter extends RecyclerView.Adapter {
    private List<Mater> maters;
    private OnItemClickListener onItemClickListener ;
    private Context mContext;

    public void setMaters(List<Mater> maters) {
        this.maters = maters;
    }

    public FirstReceiptAdapter(Context mContext, List<Mater> maters, OnItemClickListener onItemClickListener) {
        this.maters = maters;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener ;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.pruchase_receipt_main_item_body, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderBody) {
            ((ViewHolderBody) holder).cgdhxcTextView.setText(maters.get(position).getPo());
            ((ViewHolderBody) holder).wlTextView.setText(maters.get(position).getMate_number());
            ((ViewHolderBody) holder).slTextView.setText(maters.get(position).getPlan_quantity()+"");
            ((ViewHolderBody) holder).dwTextView.setText(maters.get(position).getPurchase_unit());
            if(position%2 == 0)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorTableO));
            else
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorTableE));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return maters == null ? 0 : maters.size();
    }

    class ViewHolderBody extends RecyclerView.ViewHolder {
        TextView cgdhxcTextView, wlTextView, slTextView, dwTextView;
        public ViewHolderBody(View itemView) {
            super(itemView);
            cgdhxcTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_cgdxc_tv);
            wlTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_wl_tv);
            slTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_sl_tv);
            dwTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_dw_tv);
        }
    }
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
}
