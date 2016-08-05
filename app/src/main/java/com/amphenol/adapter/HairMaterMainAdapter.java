package com.amphenol.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Pick;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 * 生产发料页面，列表适配器
 */
public class HairMaterMainAdapter extends RecyclerView.Adapter<HairMaterMainAdapter.ViewHolderBody>{
    private List<Pick.PickItem> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public HairMaterMainAdapter(Context mContext, List<Pick.PickItem> pickItems, OnItemClickListener onItemClickListener) {
        this.date = pickItems;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDate(List<Pick.PickItem> date) {
        this.date = date;
    }

    @Override
    public HairMaterMainAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.hair_mater_main_item_body, parent, false));
    }

    @Override
    public void onBindViewHolder(HairMaterMainAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.materTextView.setText(date.get(position).getBranch().getMater().getNumber());
        holder.defaultShardTextView.setText(date.get(position).getBranch().getMater().getShard());
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
        TextView materTextView, defaultShardTextView, unitTextView ,quantityTextView;
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
            defaultShardTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_branch_tv);
            unitTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_unit_tv);
            quantityTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_quantity_tv);
            itemView.setOnClickListener(mOnClickListener);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }
}
