package com.amphenol.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Shipment;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 * 销售出货页面，列表适配器
 */
public class SaleShipmentMainAdapter extends RecyclerView.Adapter<SaleShipmentMainAdapter.ViewHolderBody>{
    private List<Shipment.ShipmentItem> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public SaleShipmentMainAdapter(Context mContext, List<Shipment.ShipmentItem> shipmentItems, OnItemClickListener onItemClickListener) {
        this.date = shipmentItems;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDate(List<Shipment.ShipmentItem> date) {
        this.date = date;
    }

    @Override
    public SaleShipmentMainAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.sale_shipment_main_item_body, parent, false));
    }

    @Override
    public void onBindViewHolder(SaleShipmentMainAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.pldlnTextView.setText(date.get(position).getPldln().trim());
        holder.clientTextView.setText(date.get(position).getC6cvnb().trim()+"-"+date.get(position).getCdfcnb().trim());
        holder.planQuantity.setText(date.get(position).getQuantity()+""+date.get(position).getUnit());
        holder.materTextView.setText(date.get(position).getMater().getNumber());
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
        TextView pldlnTextView, clientTextView, planQuantity, materTextView;
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
            pldlnTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_mater_tv);
            clientTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_branch_tv);
            planQuantity = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_unit_tv);
            materTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_quantity_tv);
            itemView.setOnClickListener(mOnClickListener);
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }
}
