package com.amphenol.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.amphenol.amphenol.R;
import com.amphenol.entity.Employee;
import com.amphenol.entity.Machine;

import java.util.List;

/**
 * Created by Carl on 2016/7/12/012.
 */
public class ProductionReportAddJobMachineListAdapter extends RecyclerView.Adapter<ProductionReportAddJobMachineListAdapter.ViewHolderBody> {
    private List<Machine> date;
    private Context mContext;

    public ProductionReportAddJobMachineListAdapter(Context mContext, List<Machine> machines) {
        this.date = machines;
        this.mContext = mContext;
    }

    public void setDate(List<Machine> date) {
        this.date = date;
    }

    @Override
    public ProductionReportAddJobMachineListAdapter.ViewHolderBody onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolderBody(LayoutInflater.from(mContext).inflate(R.layout.add_job_select_employee_body, parent, false));
    }

    @Override
    public void onBindViewHolder(ProductionReportAddJobMachineListAdapter.ViewHolderBody holder, final int position) {
        holder.position = position;
        holder.nameTextView.setText(date.get(position).getName());
        holder.numberTextView.setText(date.get(position).getNumber());
        holder.typeTextView.setText(date.get(position).getType());
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
        TextView numberTextView, nameTextView,typeTextView;
        CheckBox checkBox;
        CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (date.get(position).isChecked() == isChecked)
                    return;
                else {
                    date.get(position).setChecked(isChecked);
                }
            }
        };

        public ViewHolderBody(View itemView) {
            super(itemView);
            numberTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_location_tv);
            nameTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_mater_tv);
            typeTextView = (TextView) itemView.findViewById(R.id.create_requisition_main_item_body_branch_tv);
            checkBox = (CheckBox) itemView.findViewById(R.id.create_requisition_main_item_body_cb);
            checkBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
        }
    }
}
