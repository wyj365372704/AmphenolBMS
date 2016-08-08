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
import android.widget.ImageView;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Pick;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 */
public class HairMaterSecondTwoAdapter extends RecyclerView.Adapter<HairMaterSecondTwoAdapter.ViewHolderBody> {
    private List<Pick.PickItem.PickItemBranchItem> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public HairMaterSecondTwoAdapter(Context mContext, List<Pick.PickItem.PickItemBranchItem> pickItemBranchItems, OnItemClickListener onItemClickListener) {
        this.date = pickItemBranchItems;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDate(List<Pick.PickItem.PickItemBranchItem> date) {
        this.date = date;
    }

    @Override
    public HairMaterSecondTwoAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.hair_mater_mater_list_second_body, parent, false));
    }

    @Override
    public void onBindViewHolder(HairMaterSecondTwoAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.shardTextView.setText(date.get(position).getBranch().getMater().getShard());
        holder.locationTextView.setText(date.get(position).getBranch().getMater().getLocation());
        holder.branchTextView.setText(date.get(position).getBranch().getPo());
        holder.quantityEditText.setText(date.get(position).getQuantity() + "");
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
        TextView shardTextView, locationTextView, branchTextView;
        EditText quantityEditText;
        ImageView mImageView;
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.purchase_receipt_second_item_close_iv:
                        if (onItemClickListener != null) {
                            onItemClickListener.OnItemClosed(position);
                        }
                        break;
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
            quantityEditText = (EditText) itemView.findViewById(R.id.create_requisition_main_item_body_quantity_et);
            quantityEditText.addTextChangedListener(mTextWatcher);
            mImageView = (ImageView) itemView.findViewById(R.id.purchase_receipt_second_item_close_iv);
            mImageView.setOnClickListener(onClickListener);
        }
    }

    public interface OnItemClickListener {
        void OnItemClosed(int positio);

        void OnRequisitionQuantityChanged(int position, double quantity);
    }
}
