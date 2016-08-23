package com.amphenol.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    private onItemEventCallBack mOnItemEventCallBack;

    public PurchaseItemAdapter(Context mContext, List<Purchase.PurchaseItem.PurchaseItemBranchItem> branches, onItemEventCallBack onItemEventCallBack) {
        this.date = branches;
        this.mContext = mContext;
        mOnItemEventCallBack = onItemEventCallBack;
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
            holder.itemView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.table_body_background_o));
        else
            holder.itemView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.table_body_background_e));
    }

    @Override
    public int getItemCount() {
        return date == null ? 0 : date.size();
    }

    class ViewHolderBody extends RecyclerView.ViewHolder {
        int position ;
        TextView scpcTextView, jhslTextView , ssslEditText;
        ImageView closeImageView;

        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemEventCallBack!=null)
                    mOnItemEventCallBack.onItemClosed(position);
            }
        };

        public ViewHolderBody(View itemView) {
            super(itemView);
            scpcTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_second_item_scpc_tv);
            jhslTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_second_item_jhsl_tv);
            ssslEditText = (TextView) itemView.findViewById(R.id.purchase_receipt_second_item_sssl_et);
            closeImageView = (ImageView) itemView.findViewById(R.id.purchase_receipt_second_item_close_iv);
            closeImageView.setOnClickListener(mOnClickListener);
        }
    }

    public interface onItemEventCallBack {
        void onItemClosed(int position);
    }

}
