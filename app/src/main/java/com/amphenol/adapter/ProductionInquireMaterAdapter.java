package com.amphenol.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.WorkOrder;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 * 生产订单查询-材料，列表适配器
 */
public class ProductionInquireMaterAdapter extends RecyclerView.Adapter<ProductionInquireMaterAdapter.ViewHolderBody> {
    private List<WorkOrder.Mater> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public ProductionInquireMaterAdapter(Context mContext, List<WorkOrder.Mater> maters, OnItemClickListener onItemClickListener) {
        this.date = maters;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDate(List<WorkOrder.Mater> date) {
        this.date = date;
    }

    @Override
    public ProductionInquireMaterAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.production_inquire_mater_body, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductionInquireMaterAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.numberTextView.setText(date.get(position).getNumber());
        holder.descTextView.setText(date.get(position).getDesc());
        if (position % 2 == 0)
            holder.itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.table_body_background_o));
        else
            holder.itemView.setBackground(ContextCompat.getDrawable(mContext, R.drawable.table_body_background_e));
    }

    @Override
    public int getItemCount() {
        return date == null ? 0 : date.size();
    }

    class ViewHolderBody extends RecyclerView.ViewHolder {
        int position;
        TextView numberTextView, descTextView, planUsageAmountTextView, actualUsageAmountTextView;
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
            numberTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_cgdxc_tv);
            descTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_wl_tv);
            planUsageAmountTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_sl_tv);
            actualUsageAmountTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_dw_tv);
            itemView.setOnClickListener(mOnClickListener);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }
}
