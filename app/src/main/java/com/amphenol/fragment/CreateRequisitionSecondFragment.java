package com.amphenol.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amphenol.Manager.DecodeManager;
import com.amphenol.Manager.SessionManager;
import com.amphenol.activity.BaseActivity;
import com.amphenol.adapter.PurchaseItemAdapter;
import com.amphenol.amphenol.R;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Purchase;
import com.amphenol.entity.Requisition;
import com.amphenol.ui.LoadingDialog;
import com.amphenol.utils.CommonTools;
import com.amphenol.utils.NetWorkAccessTools;
import com.amphenol.utils.PropertiesUtil;
import com.baoyz.actionsheet.ActionSheet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.amphenol.amphenol.R.id.fragment_purchase_receipt_second_sjdz_in_et;

/**
 * 采购收货_物料明细
 */
public class CreateRequisitionSecondFragment extends Fragment {
    private View rootView = null;
    private TextView materNumberTextView, materDescTextView, materFormatTextView, branchTextView, quantityTextView, unitTextView, currentShardTextView, currentLocationTextView, targetShardTextView, targetLocationTextView;
    private Requisition.RequisitionItem mRequisitionItem;

    public static CreateRequisitionSecondFragment newInstance(Requisition.RequisitionItem mRequisitionItem) {

        Bundle args = new Bundle();
        args.putSerializable("mRequisitionItem",mRequisitionItem);
        CreateRequisitionSecondFragment fragment = new CreateRequisitionSecondFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null){
            mRequisitionItem = (Requisition.RequisitionItem) args.getSerializable("mRequisitionItem");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;
        rootView = inflater.inflate(R.layout.fragment_create_requisition_second, container, false);
        initListeners();
        initViews();
        initData();
        Toolbar mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initListeners() {
    }

    private void initViews() {
        materNumberTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_number_tv);
        materDescTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_desc_tv);
        materFormatTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_mater_format_tv);
        branchTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_branch_number_tv);
        quantityTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_quantity_tv);
        unitTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_unity_tv);
        currentShardTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_current_shard_tv);
        currentLocationTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_current_location_tv);
        targetShardTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_target_shard_tv);
        targetLocationTextView = (TextView) rootView.findViewById(R.id.fragment_create_requisition_second_target_location_tv);
    }

    private void initData() {
        materNumberTextView.setText(mRequisitionItem.getBranch().getMater().getNumber());
        materDescTextView.setText(mRequisitionItem.getBranch().getMater().getDesc());
        materFormatTextView.setText(mRequisitionItem.getBranch().getMater().getFormat());
        branchTextView.setText(mRequisitionItem.getBranch().getPo());
        quantityTextView.setText(mRequisitionItem.getBranch().getQuantity()+"");
        unitTextView.setText(mRequisitionItem.getBranch().getMater().getUnit());
        currentShardTextView.setText(mRequisitionItem.getBranch().getMater().getShard());
        currentLocationTextView.setText(mRequisitionItem.getBranch().getMater().getLocation());
        targetShardTextView.setText(mRequisitionItem.getShard());
        targetLocationTextView.setText(mRequisitionItem.getLocation());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
