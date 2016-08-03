package com.amphenol.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Requisition;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 * 创建调拨单页面，列表适配器
 */
public class StockSearchAdapter extends RecyclerView.Adapter<StockSearchAdapter.ViewHolderBody> {
    private ArrayList<Mater.Branch> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public StockSearchAdapter(Context mContext, ArrayList<Mater.Branch> branches, OnItemClickListener onItemClickListener) {
        this.date = branches;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDate(ArrayList<Mater.Branch> date) {
        this.date = date;
    }

    @Override
    public StockSearchAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.stock_search_main_item_body, parent, false));
    }

    @Override
    public void onBindViewHolder(StockSearchAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.locationTextView.setText(date.get(position).getMater().getLocation());
        holder.materTextView.setText(date.get(position).getMater().getNumber());
        holder.branchTextView.setText(date.get(position).getPo());
        holder.unitTextView.setText(date.get(position).getMater().getUnit());
        holder.quantityTextView.setText(date.get(position).getQuantity() + "");
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
        TextView locationTextView, materTextView, branchTextView,quantityTextView, unitTextView;

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
            locationTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_location_tv);
            materTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_mater_tv);
            branchTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_branch_tv);
            unitTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_unit_tv);
            quantityTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_quantity_et);
            itemView.setOnClickListener(mOnClickListener);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }
}
