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
import com.amphenol.entity.Returns;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 * 采购退货选择物料退货页面，列表适配器
 */
public class ReturnsSecondAdapter extends RecyclerView.Adapter<ReturnsSecondAdapter.ViewHolderBody> {
    private List<Returns.ReturnsItemSource> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public ReturnsSecondAdapter(Context mContext, List<Returns.ReturnsItemSource> returnsItemSources, OnItemClickListener onItemClickListener) {
        this.date = returnsItemSources;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDate(List<Returns.ReturnsItemSource> date) {
        this.date = date;
    }

    @Override
    public ReturnsSecondAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.pruchase_return_second_item_body, parent, false));
    }

    @Override
    public void onBindViewHolder(ReturnsSecondAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.locationTextView.setText(date.get(position).getMater().getLocation());
        holder.branchTextView.setText(date.get(position).getPo());
        holder.quantityTextView.setText(date.get(position).getQuantity()+"");
        holder.quantityEditText.setText(date.get(position).getQuantity() + "");
        holder.checkBox.setChecked(date.get(position).isChecked());
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
        TextView locationTextView, quantityTextView, branchTextView;
        EditText quantityEditText;
        CheckBox checkBox;

        CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (date.get(position).isChecked() == isChecked)
                    return;
                else {
                    date.get(position).setChecked(isChecked);
                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemCheckedChanged(position, isChecked);
                    }
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
                date.get(position).setEnableQuantity(quantity);
                if (onItemClickListener != null)
                    onItemClickListener.OnRequisitionQuantityChanged(position, quantity);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        public ViewHolderBody(View itemView) {
            super(itemView);
            locationTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_location_tv);
            branchTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_mater_tv);
            quantityTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_branch_tv);
            quantityEditText = (EditText) itemView.findViewById(R.id.create_requisition_main_item_body_quantity_et);
            quantityEditText.addTextChangedListener(mTextWatcher);
            checkBox = (CheckBox) itemView.findViewById(R.id.create_requisition_main_item_body_cb);
            checkBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
            itemView.setTag(position);
        }
    }

    public interface OnItemClickListener {
        void OnItemCheckedChanged(int position, boolean isChecked);

        void OnRequisitionQuantityChanged(int position, double quantity);
    }
}
