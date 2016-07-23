package com.amphenol.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Branch;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 * adapter会自动将表头添加进入数据集合中，使用者不需要手动添加
 */
public class SecondReceiptAdapter extends RecyclerView.Adapter {
    private List<Branch> branches;
    private Context mContext;

    public SecondReceiptAdapter(Context mContext, List<Branch> branches) {
        this.branches = branches;
        this.mContext = mContext;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.pruchase_receipt_second_item_body, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolderBody) {
            ((ViewHolderBody) holder).scpcTextView.setText(branches.get(position).getScpc());
            ((ViewHolderBody) holder).jhslTextView.setText(branches.get(position).getJhsl() + "");
            ((ViewHolderBody) holder).ssslTextView.setText(branches.get(position).getSssl() + "");
            if (position % 2 == 0)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTableO));
            else
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorTableE));
            ((ViewHolderBody) holder).closeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("删除批次").setMessage("将对该批次物料实收数量置为0，确认收货后以删除该批次信息？");
                    builder.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((ViewHolderBody) holder).ssslTextView.setText("0.0");
                        }
                    });
                    builder.create().show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return branches == null ? 0 : branches.size();
    }

    class ViewHolderBody extends RecyclerView.ViewHolder {
        TextView scpcTextView, jhslTextView, ssslTextView;
        ImageView closeImageView;

        public ViewHolderBody(View itemView) {
            super(itemView);
            scpcTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_second_item_scpc_tv);
            jhslTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_second_item_jhsl_tv);
            ssslTextView = (TextView) itemView.findViewById(R.id.purchase_receipt_second_item_sssl_et);
            closeImageView = (ImageView) itemView.findViewById(R.id.purchase_receipt_second_item_close_iv);
        }
    }
}
