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
import com.amphenol.entity.Job;
import com.amphenol.entity.Pick;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 */
public class ProductionReportJobListAdapter extends RecyclerView.Adapter<ProductionReportJobListAdapter.ViewHolderBody> {
    private List<Job> date;
    private OnItemClickListener onItemClickListener;
    private Context mContext;

    public ProductionReportJobListAdapter(Context mContext, List<Job> jobs, OnItemClickListener onItemClickListener) {
        this.date = jobs;
        this.mContext = mContext;
        this.onItemClickListener = onItemClickListener;
    }

    public void setDate(List<Job> date) {
        this.date = date;
    }

    @Override
    public ProductionReportJobListAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.product_report_job_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductionReportJobListAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.jobNumberTextView.setText(date.get(position).getJobNumber());
        holder.workOrderTextView.setText(date.get(position).getWorkOrder().getNumber());
        holder.stepNameTextView.setText(date.get(position).getStepName());
        holder.proprNameTextView.setText(date.get(position).getProprName());
    }

    @Override
    public int getItemCount() {
        return date == null ? 0 : date.size();
    }

    class ViewHolderBody extends RecyclerView.ViewHolder {
        int position;
        TextView jobNumberTextView, workOrderTextView, stepNameTextView, proprNameTextView;

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClicked(position);
            }
        };

        public ViewHolderBody(View itemView) {
            super(itemView);
            jobNumberTextView = (TextView) itemView.findViewById(R.id.job_number);
            workOrderTextView = (TextView) itemView.findViewById(R.id.work_order);
            stepNameTextView = (TextView) itemView.findViewById(R.id.step_number);
            proprNameTextView = (TextView) itemView.findViewById(R.id.propr_name);
            itemView.setOnClickListener(onClickListener);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }
}
