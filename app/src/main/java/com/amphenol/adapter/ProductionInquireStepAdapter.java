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
public class ProductionInquireStepAdapter extends RecyclerView.Adapter<ProductionInquireStepAdapter.ViewHolderBody> {
    private List<WorkOrder.Step> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public ProductionInquireStepAdapter(Context mContext, List<WorkOrder.Step> steps, OnItemClickListener onItemClickListener) {
        this.date = steps;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDate(List<WorkOrder.Step> date) {
        this.date = date;
    }

    @Override
    public ProductionInquireStepAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.production_inquire_step_body, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductionInquireStepAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.numberTextView.setText(date.get(position).getStepNumber());
        holder.nameTextView.setText(date.get(position).getStepName());
        holder.standardWorkingHoursTextView.setText(date.get(position).getStandardWorkingHours() + "");
        holder.actualWorkingHoursTextView.setText(date.get(position).getActualWorkingHours() + "");
        holder.tbcTextView.setText(date.get(position).getTBC());
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
        TextView numberTextView, nameTextView, standardWorkingHoursTextView, actualWorkingHoursTextView,tbcTextView;
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
            nameTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_wl_tv);
            standardWorkingHoursTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_sl_tv);
            actualWorkingHoursTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_dw_tv);
            tbcTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_main_item_tbc_tv);
            itemView.setOnClickListener(mOnClickListener);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }
}
