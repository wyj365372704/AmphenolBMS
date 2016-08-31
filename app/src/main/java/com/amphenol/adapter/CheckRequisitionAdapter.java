package com.amphenol.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Requisition;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 * 审核调拨单页面，列表适配器
 */
public class CheckRequisitionAdapter extends RecyclerView.Adapter<CheckRequisitionAdapter.ViewHolderBody>{
    private List<Requisition.RequisitionItem> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public CheckRequisitionAdapter(Context mContext, List<Requisition.RequisitionItem> requisitionItems, OnItemClickListener onItemClickListener) {
        this.date = requisitionItems;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDate(List<Requisition.RequisitionItem> date) {
        this.date = date;
    }

    @Override
    public CheckRequisitionAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.check_requisition_main_item_body, parent, false));
    }

    @Override
    public void onBindViewHolder(CheckRequisitionAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.materTextView.setText(date.get(position).getBranch().getMater().getNumber());
        holder.branchTextView.setText(date.get(position).getBranch().getPo());
        holder.unitTextView.setText(date.get(position).getBranch().getMater().getUnit());
        holder.quantityTextView.setText(date.get(position).getQuantity()+"");
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
        TextView materTextView, branchTextView, unitTextView ,quantityTextView;
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
            materTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_mater_tv);
            branchTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_branch_tv);
            unitTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_unit_tv);
            quantityTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_quantity_tv);
            itemView.setOnClickListener(mOnClickListener);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }
}
