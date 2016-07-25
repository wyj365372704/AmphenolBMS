package com.amphenol.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Purchase;

import java.util.List;

/**
 * 采购单项详细页面适配器
 * Created by Carl on 2016/7/12/012.
 */
public class PurchaseItemAdapter extends RecyclerView.Adapter {
    private List<Purchase.PurchaseItem.PurchaseItemBranchItem> date;
    private Context mContext;
    private OnBranchItemActualQuantityChangedListener mOnBranchItemActualQuantityChangedListener;

    public PurchaseItemAdapter(Context mContext, List<Purchase.PurchaseItem.PurchaseItemBranchItem> branches, OnBranchItemActualQuantityChangedListener onBranchItemActualQuantityChangedListener) {
        this.date = branches;
        this.mContext = mContext;
        mOnBranchItemActualQuantityChangedListener = onBranchItemActualQuantityChangedListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.pruchase_receipt_second_item_body, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((ViewHolderBody) holder).position = position;
        ((ViewHolderBody) holder).scpcTextView.setText(date.get(position).getBranch().getPo());
        ((ViewHolderBody) holder).jhslTextView.setText(date.get(position).getBranch().getQuantity() + ""); //计划数量
        ((ViewHolderBody) holder).ssslEditText.setText(date.get(position).getActualQuantity() + "");//如果是新增批次
        if (position % 2 == 0)
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTableO));
        else
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTableE));
    }

    @Override
    public int getItemCount() {
        return date == null ? 0 : date.size();
    }

    class ViewHolderBody extends RecyclerView.ViewHolder {
        int position ;
        TextView scpcTextView, jhslTextView;
        EditText ssslEditText;
        ImageView closeImageView;

        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("删除批次").setMessage("将对该批次物料实收数量置为0，确认收货后以删除该批次信息？");
                builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("wyj", "delete " + position);
                        date.get(position).setActualQuantity(0);
                        notifyItemChanged(position);
                        if (mOnBranchItemActualQuantityChangedListener != null)
                            mOnBranchItemActualQuantityChangedListener.onBranchActualQuantityChanged();
                    }
                });
                builder.create().show();
            }
        };

        TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("wyj","onTextChanged  the position is "+position +" , the s is "+s);
                double quantity = 0;
                try {
                    quantity = Double.parseDouble(s.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                date.get(position).setActualQuantity(quantity);
                if (mOnBranchItemActualQuantityChangedListener != null)
                    mOnBranchItemActualQuantityChangedListener.onBranchActualQuantityChanged();
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        public ViewHolderBody(View itemView) {
            super(itemView);
            scpcTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_second_item_scpc_tv);
            jhslTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_second_item_jhsl_tv);
            ssslEditText = (EditText) itemView.findViewById(R.id.purchase_receipt_second_item_sssl_et);
            closeImageView = (ImageView) itemView.findViewById(R.id.purchase_receipt_second_item_close_iv);
            closeImageView.setOnClickListener(mOnClickListener);
            ssslEditText.addTextChangedListener(mTextWatcher);
        }
    }

    public interface OnBranchItemActualQuantityChangedListener {
        void onBranchActualQuantityChanged();
    }

}
