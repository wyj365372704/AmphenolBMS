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
import com.amphenol.entity.Pick;
import com.amphenol.entity.Requisition;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 */
public class HairMaterSecondOneAdapter extends RecyclerView.Adapter<HairMaterSecondOneAdapter.ViewHolderBody> {
    private List<Pick.PickItem.PickItemBranchItem> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public HairMaterSecondOneAdapter(Context mContext, List<Pick.PickItem.PickItemBranchItem> pickItemBranchItems, OnItemClickListener onItemClickListener) {
        this.date = pickItemBranchItems;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDate(List<Pick.PickItem.PickItemBranchItem> date) {
        this.date = date;
    }

    @Override
    public HairMaterSecondOneAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.hair_mater_mater_list_body, parent, false));
    }

    @Override
    public void onBindViewHolder(HairMaterSecondOneAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.shardTextView.setText(date.get(position).getBranch().getMater().getShard());
        holder.locationTextView.setText(date.get(position).getBranch().getMater().getLocation());
        holder.branchTextView.setText(date.get(position).getBranch().getPo());
        holder.storageQuantityTextView.setText(date.get(position).getBranch().getQuantity()+"");
        holder.quantityEditText.setText(date.get(position).getQuantity() + "");
        holder.checkBox.setChecked(date.get(position).isChecked());
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
        TextView shardTextView,locationTextView, branchTextView, storageQuantityTextView;
        EditText quantityEditText;
        CheckBox checkBox;

        CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onItemClickListener != null) {
                    date.get(position).setChecked(isChecked);
                    onItemClickListener.OnItemCheckedChanged(position, isChecked);
                }
            }
        };
        TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double quantity = 0;
                try {
                    quantity = Double.parseDouble(s.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                date.get(position).setQuantity(quantity);
                if (onItemClickListener != null)
                    onItemClickListener.OnRequisitionQuantityChanged(position, quantity);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        public ViewHolderBody(View itemView) {
            super(itemView);
            shardTextView = (TextView) itemView.findViewById(R.id.hair_mater_shard_tv);
            locationTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_location_tv);
            branchTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_branch_tv);
            storageQuantityTextView = (TextView) itemView.findViewById(R.id.hair_mater_storage_quantity_show_tv);
            quantityEditText = (EditText) itemView.findViewById(R.id.create_requisition_main_item_body_quantity_et);
            quantityEditText.addTextChangedListener(mTextWatcher);
            checkBox = (CheckBox) itemView.findViewById(R.id.create_requisition_main_item_body_cb);
            checkBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
        }
    }

    public interface OnItemClickListener {
        void OnItemCheckedChanged(int position, boolean isChecked);

        void OnRequisitionQuantityChanged(int position, double quantity);
    }
}
