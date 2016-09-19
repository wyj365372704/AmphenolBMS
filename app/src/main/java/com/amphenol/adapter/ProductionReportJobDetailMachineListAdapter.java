package com.amphenol.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Employee;
import com.amphenol.entity.Machine;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 */
public class ProductionReportJobDetailMachineListAdapter extends RecyclerView.Adapter<ProductionReportJobDetailMachineListAdapter.ViewHolderBody> {
    private List<Machine> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public ProductionReportJobDetailMachineListAdapter(Context mContext, List<Machine> machines, OnItemClickListener onItemClickListener) {
        this.date = machines;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDate(List<Machine> date) {
        this.date = date;
    }

    @Override
    public ProductionReportJobDetailMachineListAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.product_report_job_detail_machine_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductionReportJobDetailMachineListAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.nameTextView.setText(date.get(position).getName());
        holder.numberTextView.setText(date.get(position).getNumber());
        holder.beginTimeTextView.setText(date.get(position).getStartTime());
    }

    @Override
    public int getItemCount() {
        return date == null ? 0 : date.size();
    }

    class ViewHolderBody extends RecyclerView.ViewHolder {
        int position;
        TextView numberTextView, nameTextView, beginTimeTextView;

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClicked(position);
            }
        };

        public ViewHolderBody(View itemView) {
            super(itemView);
            numberTextView = (TextView) itemView.findViewById(R.id.employee_number);
            nameTextView = (TextView) itemView.findViewById(R.id.employee_name);
            beginTimeTextView = (TextView) itemView.findViewById(R.id.work_order);
            itemView.setOnClickListener(onClickListener);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }
}
