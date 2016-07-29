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
import com.amphenol.entity.Purchase;

import java.util.List;

/**
 * 采购单适配器
 * Created by Carl on 2016/7/12/012.
 */
public class PurchaseAdapter extends RecyclerView.Adapter {
    private List<Purchase.PurchaseItem> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public void setDate(List<Purchase.PurchaseItem> date) {
        this.date = date;
    }

    public PurchaseAdapter(Context mContext, List<Purchase.PurchaseItem> date, OnItemClickListener onItemClickListener) {
        this.date = date;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.pruchase_receipt_main_item_body, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((ViewHolderBody) holder).position = position;
        ((ViewHolderBody) holder).cgdhxcTextView.setText(date.get(position).getPo());
        ((ViewHolderBody) holder).wlTextView.setText(date.get(position).getMater().getNumber());
        ((ViewHolderBody) holder).slTextView.setText(date.get(position).getMater().getQuantity() + "");
        ((ViewHolderBody) holder).dwTextView.setText(date.get(position).getMater().getUnit());
        if (position % 2 == 0)
            holder.itemView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.table_body_background_o));
        else
            holder.itemView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.table_body_background_e));
    }

    @Override
    public int getItemCount() {
        return date == null ? 0 : date.size();
    }

    class ViewHolderBody extends RecyclerView.ViewHolder {
        int position;
        TextView cgdhxcTextView, wlTextView, slTextView, dwTextView;
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(position);
                }
            }
        };

        public ViewHolderBody(View itemView) {
            super(itemView);
            cgdhxcTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_cgdxc_tv);
            wlTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_wl_tv);
            slTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_sl_tv);
            dwTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_dw_tv);
            itemView.setOnClickListener(mOnClickListener);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }
}
