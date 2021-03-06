package com.amphenol.Manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;


import com.amphenol.entity.Dict;
import com.amphenol.entity.Employee;
import com.amphenol.entity.Job;
import com.amphenol.entity.Machine;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Pick;
import com.amphenol.entity.Purchase;
import com.amphenol.entity.Requisition;
import com.amphenol.entity.Returns;
import com.amphenol.entity.Shipment;
import com.amphenol.entity.WorkOrder;
import com.amphenol.fragment.ProductionReportJobListFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Carl on 2016/7/15/015.
 */
public class DecodeManager {
    private static final int RETURN_CODE_SUCCESS = 1;

    /**
     * 解码json 获取登录环境
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     */
    public static void decodeGetEnv(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            JSONArray envList = jsonObject.getJSONArray("env_list");
            if (envList != null && envList.length() > 0) {
                ArrayList<String> envArrayList = new ArrayList<>();
                for (int i = 0; i < envList.length(); i++) {
                    envArrayList.add(envList.getString(i).trim());
                }
                data.putStringArrayList("env_list", envArrayList);
            }
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 解码json 获取功能菜单
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     */
    public static void decodeGetMenu(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            JSONArray jsonArray = jsonObject.getJSONArray("menu_list");
            if (jsonArray != null) {
                ArrayList<HashMap<String, String>> menuList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    HashMap<String, String> menu = new HashMap<>();
                    JSONObject menuObject = jsonArray.getJSONObject(i);
                    Iterator<String> iterator = menuObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String value = menuObject.getString(key);
                        menu.put(key.trim(), value.trim());
                    }
                    menuList.add(menu);
                }
                data.putSerializable("menu_list", menuList);
            }

        }
        msg.setData(data);
        handler.sendMessage(msg);
    }


    public static void decodeLoginCheck(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 查询送货单
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws Exception
     */
    public static void decodeQueryReceipt(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Purchase purchase = new Purchase();
            String firm = jsonObject.optString("firm");//送货厂商
            String receipt_number = jsonObject.optString("receipt_number");//送货单号
            int status_code = jsonObject.optInt("status_code");//送货单状态
            purchase.setFirm(firm.trim());
            purchase.setNumber(receipt_number.trim());
            purchase.setStatus(status_code);
            JSONArray materJsonArray = jsonObject.optJSONArray("mater_list");
            ArrayList<Purchase.PurchaseItem> purchaseItems = new ArrayList<>();
            if (materJsonArray != null) {
                for (int i = 0; i < materJsonArray.length(); i++) {
                    JSONObject materJsonObject1 = materJsonArray.optJSONObject(i);
                    String materPO = materJsonObject1.optString("mater_po");//采购单项次
                    String number = materJsonObject1.optString("number");//采购单行号
                    String mate = materJsonObject1.optString("mate");//物料编号
                    String unit = materJsonObject1.optString("unit");//单位
                    double quantity = materJsonObject1.optDouble("quantity", 0);//数量
                    Purchase.PurchaseItem purchaseItem = new Purchase.PurchaseItem();
                    purchaseItem.setPurchase(purchase);
                    purchaseItem.setPo(materPO.trim());
                    purchaseItem.setNumber(number.trim());
                    Mater mater = new Mater();
                    mater.setNumber(mate.trim());
                    mater.setUnit(unit.trim());
                    mater.setQuantity(quantity);
                    purchaseItem.setMater(mater);
                    purchaseItems.add(purchaseItem);
                }
            }
            purchase.setPurchaseItems(purchaseItems);
            data.putParcelable("purchase", purchase);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }


    /**
     * 查询退货单
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws Exception
     */
    public static void decodeQueryReturn(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            HashMap<String, String> params = (HashMap<String, String>) jsonObject.get("params");
            Returns returns = new Returns();
            String firm = jsonObject.optString("firm");//退货厂商
            String receipt_number = params.get("return_number").trim();
            int status_code = jsonObject.optInt("status_code");//退货单状态
            returns.setFirm(firm.trim());
            returns.setNumber(receipt_number.trim());
            returns.setStatus(status_code);
            JSONArray materJsonArray = jsonObject.optJSONArray("mater_list");
            ArrayList<Returns.ReturnsItem> returnsItems = new ArrayList<>();
            if (materJsonArray != null) {
                for (int i = 0; i < materJsonArray.length(); i++) {
                    JSONObject materJsonObject1 = materJsonArray.optJSONObject(i);
                    String materPO = materJsonObject1.optString("mater_po");//退货单项次
                    String number = materJsonObject1.optString("number");//退货单行号
                    String mater_number = materJsonObject1.optString("mater");//物料编号
                    String unit = materJsonObject1.optString("unit");//单位
                    double quantity = materJsonObject1.optDouble("quantity", 0);//数量
                    Returns.ReturnsItem returnsItem = new Returns.ReturnsItem();
                    returnsItem.setReturns(returns);
                    returnsItem.setPo(materPO.trim());
                    returnsItem.setNumber(number.trim());
                    Mater mater = new Mater();
                    mater.setNumber(mater_number.trim());
                    mater.setUnit(unit.trim());
                    returnsItem.setQuantity(quantity);
                    returnsItem.setMater(mater);
                    returnsItems.add(returnsItem);
                }
            }
            returns.setReturnsItems(returnsItems);
            data.putParcelable("returns", returns);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 查询送货单物料明细
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws Exception
     */
    public static void decodeQueryReceiptItem(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            HashMap<String, String> params = (HashMap<String, String>) jsonObject.get("params");

            Purchase purchase = new Purchase();
            purchase.setNumber(params.get("receipt_number").trim());

            String mateNumber = jsonObject.optString("mate_number");//物料编号
            String mateDesc = jsonObject.optString("mate_desc");//物料描述
            String purchaseUnit = jsonObject.optString("purchase_unit");//采购单位
            double planQuantity = jsonObject.optDouble("plan_quantity");//采购数量
            int branchControl = jsonObject.optInt("branch_control", Mater.BRANCH_NO_CONTROL);//批次控制  默认为不控制
            int status = jsonObject.optInt("status", Purchase.PurchaseItem.STATUS_CLOSED);//收货状态   默认为已关闭
            double actualSingle = jsonObject.optDouble("actual_single");//实际单重
            String actual_unit = jsonObject.optString("actual_unit");//实际单重单位
            double actualQuantity = jsonObject.optDouble("actual_quantity");//实际总数
            String shard = jsonObject.optString("shard");//收货子库
            String location = jsonObject.optString("location");//收货库位

            Purchase.PurchaseItem purchaseItem = new Purchase.PurchaseItem();
            purchaseItem.setUnit(purchaseUnit.trim());
            purchaseItem.setQuantity(planQuantity);
            purchaseItem.setState(status);
            purchaseItem.setNumber(params.get("receipt_line").trim());

            Mater mater = new Mater();
            mater.setNumber(mateNumber.trim());
            mater.setDesc(mateDesc.trim());
            mater.setBranchControl(branchControl);
            mater.setUnit(actual_unit.trim());
            mater.setSingle(actualSingle);
            mater.setQuantity(actualQuantity);
            mater.setShard(shard.trim());
            mater.setLocation(location.trim());

            JSONArray branchJsonArray = jsonObject.optJSONArray("branch_list");
            ArrayList<Purchase.PurchaseItem.PurchaseItemBranchItem> purchaseItemBranchItems = new ArrayList<>();
            if (branchJsonArray != null && branchJsonArray.length() > 0) {
                for (int i = 0; i < branchJsonArray.length(); i++) {
                    JSONObject branchJsonObject = branchJsonArray.optJSONObject(i);
                    String branch_number = branchJsonObject.optString("branch_number");//批次行号
                    String branch_desc = branchJsonObject.optString("branch_desc");//批次号
                    double plan_quantity = branchJsonObject.optDouble("plan_quantity");//数量
                    Mater.Branch branch = new Mater.Branch();
                    branch.setNumber(branch_number);
                    branch.setPo(branch_desc);
                    branch.setQuantity(plan_quantity);
                    Purchase.PurchaseItem.PurchaseItemBranchItem purchaseItemBranchItem = new Purchase.PurchaseItem.PurchaseItemBranchItem(branch, plan_quantity);
                    purchaseItemBranchItems.add(purchaseItemBranchItem);
                }
            }
            purchaseItem.setMater(mater);
            purchaseItem.setPurchase(purchase);
            purchaseItem.setPurchaseItemBranchItems(purchaseItemBranchItems);
            data.putParcelable("purchaseItem", purchaseItem);

        }
        msg.setData(data);
        handler.sendMessage(msg);
    }


    /**
     * 查询退货单物料明细
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws Exception
     */
    public static void decodeQueryReturnItem(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Returns.ReturnsItem returnsItem = new Returns.ReturnsItem();
            Mater mater = new Mater();
            returnsItem.setMater(mater);

            HashMap<String, String> params = (HashMap<String, String>) jsonObject.get("params");
            data.putInt("position", Integer.parseInt(params.get("position")));
            String mater_desc = jsonObject.optString("mate_desc");//物料描述
            mater.setDesc(mater_desc);

            ArrayList<Returns.ReturnsItemSource> returnsItemSources = new ArrayList<>();
            returnsItem.setReturnsItemSources(returnsItemSources);

            JSONArray locationJsonArray = jsonObject.optJSONArray("location_list");
            if (locationJsonArray != null && locationJsonArray.length() > 0) {
                for (int i = 0; i < locationJsonArray.length(); i++) {
                    JSONObject branchJsonObject = locationJsonArray.optJSONObject(i);
                    String shard = branchJsonObject.optString("shard");//子库
                    String location = branchJsonObject.optString("location");//库位
                    String branch_number = branchJsonObject.optString("branch");//批次号
                    double quantity = branchJsonObject.optDouble("quantity");//数量
                    Returns.ReturnsItemSource returnsItemSource = new Returns.ReturnsItemSource();
                    returnsItemSource.setPo(branch_number);
                    returnsItemSource.setQuantity(quantity);
                    returnsItemSource.setEnableQuantity(quantity);
                    Mater sourceMater = new Mater();
                    sourceMater.setShard(shard);
                    sourceMater.setLocation(location);
                    returnsItemSource.setMater(sourceMater);
                    returnsItemSources.add(returnsItemSource);
                }
            }
            data.putParcelable("returnsItem", returnsItem);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeReceiptConfirm(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeReceiptClose(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeReturnConfirm(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeReturnClose(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeQueryWarehouse(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            String warehouse = jsonObject.optString("warehouse");
            JSONArray warehouseJsonArray = jsonObject.optJSONArray("warehouse_list");
            ArrayList<String> wareHouseStringList = new ArrayList<>();
            for (int i = 0; i < warehouseJsonArray.length(); i++) {
                wareHouseStringList.add(warehouseJsonArray.getJSONObject(i).optString("name").trim());
            }
            data.putString("warehouse", warehouse.trim());
            data.putStringArrayList("warehouse_list", wareHouseStringList);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeQueryShardList(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            ArrayList<String> shardList = new ArrayList<>();
            JSONArray jsonArray = jsonObject.optJSONArray("shard_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                String shardName = jsonObject1.optString("name");
                shardList.add(shardName.trim());
            }
            data.putStringArrayList("shardList", shardList);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeCreaetRequisitionGetMaterList(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            HashMap<String, String> params = (HashMap<String, String>) jsonObject.get("params");
            String warehouse = params.get("warehouse");
            ArrayList<Requisition.RequisitionItem> requisitionItems = new ArrayList<>();
            JSONArray jsonArray = jsonObject.optJSONArray("mater_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                Requisition.RequisitionItem requisitionItem = new Requisition.RequisitionItem();
                JSONObject requisitionItemJsonObject = jsonArray.optJSONObject(i);
                String mate = requisitionItemJsonObject.optString("mate");
                String shard = requisitionItemJsonObject.optString("shard");
                String location = requisitionItemJsonObject.optString("location");
                String branchPo = requisitionItemJsonObject.optString("branch");
                Double quantity = requisitionItemJsonObject.optDouble("quantity");
                String unit = requisitionItemJsonObject.optString("unit");
                String target_shard = requisitionItemJsonObject.optString("target_shard");
                String target_location = requisitionItemJsonObject.optString("target_location");
                requisitionItem.setQuantity(quantity);
                Mater.Branch branch = new Mater.Branch();
                branch.setQuantity(quantity);
                branch.setPo(branchPo.trim());
                Mater mater = new Mater();
                mater.setNumber(mate.trim());
                mater.setShard(shard.trim());
                mater.setWarehouse(warehouse.trim());
                mater.setLocation(location.trim());
                mater.setUnit(unit.trim());
                branch.setMater(mater);
                requisitionItem.setBranch(branch);
                requisitionItem.setShard(target_shard);
                requisitionItem.setLocation(target_location);
                requisitionItems.add(requisitionItem);
            }
            data.putParcelableArrayList("requisitionItems", requisitionItems);
        }
        msg.setData(data);
//        handler.sendMessage(msg);
        handler.sendMessageAtFrontOfQueue(msg);
    }

    public static void decodeStockSearchGetMaterList(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            HashMap<String, String> params = (HashMap<String, String>) jsonObject.get("params");
            String warehouse = params.get("warehouse");
            ArrayList<Mater.Branch> branches = new ArrayList<>();
            JSONArray jsonArray = jsonObject.optJSONArray("mater_list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject requisitionItemJsonObject = jsonArray.optJSONObject(i);
                String mate = requisitionItemJsonObject.optString("mate");
                String shard = requisitionItemJsonObject.optString("shard");
                String location = requisitionItemJsonObject.optString("location");
                String branchPo = requisitionItemJsonObject.optString("branch");
                Double quantity = requisitionItemJsonObject.optDouble("quantity");
                String unit = requisitionItemJsonObject.optString("unit");
                Mater.Branch branch = new Mater.Branch();
                branch.setQuantity(quantity);
                branch.setPo(branchPo.trim());
                Mater mater = new Mater();
                mater.setNumber(mate.trim());
                mater.setShard(shard.trim());
                mater.setWarehouse(warehouse.trim());
                mater.setLocation(location.trim());
                mater.setUnit(unit.trim());
                branch.setMater(mater);
                branches.add(branch);
            }
            data.putParcelableArrayList("branches", branches);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }


    public static void decodeCreaetRequisitionGetMater(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            HashMap<String, String> params = (HashMap<String, String>) jsonObject.get("params");
            String from_warehouse = params.get("warehouse");
            String from_shard = params.get("shard");
            String from_location = params.get("location");
            String mate = params.get("mate");
            String branch_po = params.get("branch");
            double quantity = 0;
            try {
                quantity = Double.parseDouble(params.get("quantity"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String unit = params.get("unit");
            String mater_desc = jsonObject.optString("mater_desc");
            String mater_format = jsonObject.optString("mater_format");
            String target_shard = jsonObject.optString("target_shard");
            String target_location = jsonObject.optString("target_location");
            Requisition.RequisitionItem requisitionItem = new Requisition.RequisitionItem();
            Mater mater = new Mater();
            mater.setWarehouse(from_warehouse);
            mater.setShard(from_shard);
            mater.setLocation(from_location);
            mater.setNumber(mate);
            mater.setDesc(mater_desc);
            mater.setFormat(mater_format);
            mater.setUnit(unit);
            Mater.Branch branch = new Mater.Branch();
            branch.setPo(branch_po);
            branch.setQuantity(quantity);
            branch.setMater(mater);
            requisitionItem.setBranch(branch);
            requisitionItem.setShard(target_shard);
            requisitionItem.setLocation(target_location);
            data.putParcelable("requisitionItem", requisitionItem);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeStockSearchGetMater(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            HashMap<String, String> params = (HashMap<String, String>) jsonObject.get("params");
            String from_warehouse = params.get("warehouse");
            String from_shard = params.get("shard");
            String from_location = params.get("location");
            String mate = params.get("mate");
            String branch_po = params.get("branch");
            double quantity = 0;
            try {
                quantity = Double.parseDouble(params.get("quantity"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String unit = params.get("unit");
            String mater_desc = jsonObject.optString("mater_desc");
            String mater_format = jsonObject.optString("mater_format");
            Mater mater = new Mater();
            mater.setWarehouse(from_warehouse);
            mater.setShard(from_shard);
            mater.setLocation(from_location);
            mater.setNumber(mate);
            mater.setDesc(mater_desc);
            mater.setFormat(mater_format);
            mater.setUnit(unit);
            Mater.Branch branch = new Mater.Branch();
            branch.setPo(branch_po);
            branch.setQuantity(quantity);
            branch.setMater(mater);
            data.putParcelable("branch", branch);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeCreaetRequisitionCommit(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            String number = jsonObject.optString("number");
            data.putString("number", number.trim());
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeCheckRequisitionGetMaterList(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Requisition requisition = new Requisition();
            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            String requisitionNumber = params.get("requisition");
            requisition.setNumber(requisitionNumber);
            int state = jsonObject.optInt("state", Requisition.STATUS_NO_REQUISITION);
            String founder = jsonObject.optString("founder");
            String department = jsonObject.optString("department");
            String date = jsonObject.optString("date");
            requisition.setStatus(state);
            requisition.setFounder(founder);
            requisition.setDepartment(department);
            requisition.setDate(date);
            ArrayList<Requisition.RequisitionItem> requisitionItems = new ArrayList<>();
            JSONArray materJsonArray = jsonObject.optJSONArray("mater_list");
            if (materJsonArray != null) {

                for (int i = 0; i < materJsonArray.length(); i++) {
                    Requisition.RequisitionItem requisitionItem = new Requisition.RequisitionItem();
                    JSONObject materObject = materJsonArray.optJSONObject(i);
                    String requisition_line = materObject.optString("requisition_line");
                    String materNumber = materObject.optString("mater");
                    String branchPo = materObject.optString("branch");
                    double quantity = materObject.optDouble("quantity");
                    String unit = materObject.optString("unit");
                    requisitionItem.setNumber(requisition_line.trim());
                    requisitionItem.setQuantity(quantity);
                    Mater mater = new Mater();
                    mater.setNumber(materNumber.trim());
                    mater.setUnit(unit.trim());
                    Mater.Branch branch = new Mater.Branch();
                    branch.setPo(branchPo.trim());
                    branch.setMater(mater);
                    requisitionItem.setBranch(branch);
                    requisitionItems.add(requisitionItem);
                }

            }
            requisition.setRequisitionItems(requisitionItems);
            data.putParcelable("requisition", requisition);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeCheckRequisitionGetMater(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Requisition requisition = new Requisition();
            HashMap<String, String> params = (HashMap<String, String>) jsonObject.get("params");
            String requisitionNumber = params.get("requisition");
            requisition.setNumber(requisitionNumber.trim());
            String requisitionItemNumber = params.get("requisition_line");
            String mate = params.get("mate");
            String branch_po = params.get("branch");
            double quantity = 0;
            try {
                quantity = Double.parseDouble(params.get("quantity"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            String unit = params.get("unit");
            String mater_desc = jsonObject.optString("mater_desc");
            String mater_format = jsonObject.optString("mater_format");
            String from_warehouse = jsonObject.optString("from_warehouse");
            String from_shard = jsonObject.optString("form_shard");
            String from_location = jsonObject.optString("form_location");
            String target_shard = jsonObject.optString("target_shard");
            String target_location = jsonObject.optString("target_location");
            Requisition.RequisitionItem requisitionItem = new Requisition.RequisitionItem();
            Mater mater = new Mater();
            mater.setWarehouse(from_warehouse.trim());
            mater.setShard(from_shard.trim());
            mater.setLocation(from_location.trim());
            mater.setNumber(mate.trim());
            mater.setDesc(mater_desc.trim());
            mater.setFormat(mater_format.trim());
            mater.setUnit(unit.trim());
            Mater.Branch branch = new Mater.Branch();
            branch.setPo(branch_po.trim());
            branch.setMater(mater);
            requisitionItem.setBranch(branch);
            requisitionItem.setShard(target_shard.trim());
            requisitionItem.setLocation(target_location.trim());
            requisitionItem.setQuantity(quantity);
            requisitionItem.setActualQuantity(quantity);
            requisitionItem.setNumber(requisitionItemNumber.trim());
            requisitionItem.setRequisition(requisition);
            data.putParcelable("requisitionItem", requisitionItem);
            ArrayList<String> shardStrings = new ArrayList<>();
            JSONArray shardJsonArray = jsonObject.optJSONArray("target_shard_list");
            for (int i = 0; i < shardJsonArray.length(); i++) {
                JSONObject shardJsonObject = shardJsonArray.optJSONObject(i);
                String shard = shardJsonObject.optString("shard");
                shardStrings.add(shard.trim());
            }
            data.putStringArrayList("target_shard_list", shardStrings);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeCheckRequisitionSure(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeCheckRequisitionCancel(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeHairMaterGetPickList(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        Pick pick = new Pick();
        HashMap<String, String> params = (HashMap<String, String>) jsonObject.get("params");
        String pickNumber = params.get("pick_number");
        pick.setNumber(pickNumber);
        if (isRequestOK(jsonObject)) {
            String work_order = jsonObject.optString("work_order");
            String founder = jsonObject.optString("founder");
            String department = jsonObject.optString("department");
            String date = jsonObject.optString("date");
            int type = jsonObject.optInt("type", Pick.TYPE_NORMAL);
            int state = jsonObject.optInt("state", Pick.STATE_FINISHED);
            pick.setWorkOrder(work_order.trim());
            pick.setFounder(founder.trim());
            pick.setDepartment(department.trim());
            pick.setDate(date.trim());
            pick.setType(type);
            pick.setState(state);
            JSONArray pickItemJsonArray = jsonObject.optJSONArray("picking_list");
            if (pickItemJsonArray != null && pickItemJsonArray.length() > 0) {
                ArrayList<Pick.PickItem> pickItems = new ArrayList<>();
                for (int i = 0; i < pickItemJsonArray.length(); i++) {
                    JSONObject pickItemJsonObject = pickItemJsonArray.getJSONObject(i);
                    Pick.PickItem pickItem = new Pick.PickItem();
                    Mater.Branch branch = new Mater.Branch();
                    Mater mater = new Mater();
                    branch.setMater(mater);
                    String pick_line = pickItemJsonObject.optString("pick_line");
                    String sequence = pickItemJsonObject.optString("sequence");
                    String materNumber = pickItemJsonObject.optString("mater");
                    double quantity = Math.abs(pickItemJsonObject.optDouble("quantity", 0));
                    String unit = pickItemJsonObject.optString("unit");
                    String warehouse = pickItemJsonObject.optString("warehouse");
                    String defaultShard = pickItemJsonObject.optString("shard");
                    String location = pickItemJsonObject.optString("location");
                    int itemState = pickItemJsonObject.optInt("state", Pick.PickItem.STATE_CLOSED);
                    int branched = pickItemJsonObject.optInt("branched", Pick.PickItem.BRANCHED_NO);
                    pickItem.setPickLine(pick_line.trim());
                    pickItem.setSequence(sequence.trim());
                    pickItem.setQuantity(quantity);
                    pickItem.setState(itemState);
                    pickItem.setBranched(branched);
                    pickItem.setBranch(branch);
                    mater.setNumber(materNumber.trim());
                    mater.setUnit(unit.trim());
                    mater.setWarehouse(warehouse.trim());
                    mater.setShard(defaultShard.trim());
                    mater.setLocation(location.trim());
                    pickItems.add(pickItem);
                }
                pick.setPickItems(pickItems);
            }
        }
        data.putParcelable("pick", pick);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeHairMaterSubmit(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeHairMaterCancel(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeHairMaterGetMaterList(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        ArrayList<String> shards = new ArrayList<>();
        Pick pick = new Pick();
        Pick.PickItem pickItem = new Pick.PickItem();
        pickItem.setPick(pick);
        Mater.Branch branch = new Mater.Branch();
        Mater mater = new Mater();
        branch.setMater(mater);
        HashMap<String, String> params = (HashMap<String, String>) jsonObject.get("params");
        double plantQuantity = 0;
        try {
            plantQuantity = Double.parseDouble(params.get("quantity"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String department = params.get("department");
        String workOrder = params.get("workOrder");
        String sequence = params.get("sequence");
        String warehouse = params.get("warehouse");
        String mateNumber = params.get("mater");
        String shard = params.get("shard");
        String location = params.get("location");
        String branchPo = params.get("branch");
        String pick_number = params.get("pick_number");
        String pick_line = params.get("pick_line");
        String unit = params.get("unit");
        String type = params.get("type");
        String branched = params.get("branched");
        pick.setType(Integer.valueOf(type));
        branch.setPo(branchPo.trim());
        mater.setShard(shard.trim());
        mater.setLocation(location.trim());
        mater.setWarehouse(warehouse.trim());
        mater.setNumber(mateNumber.trim());
        mater.setUnit(unit.trim());
        pickItem.setBranched(Integer.valueOf(branched));
        pickItem.setBranch(branch);
        pickItem.setSequence(sequence.trim());
        pickItem.setQuantity(plantQuantity);
        pickItem.setPickLine(pick_line.trim());
        pick.setDepartment(department);
        pick.setWorkOrder(workOrder);
        pick.setNumber(pick_number);
        if (isRequestOK(jsonObject)) {
            String mater_desc = jsonObject.optString("mater_desc");
            String mater_format = jsonObject.optString("mater_format");
            mater.setDesc(mater_desc.trim());
            mater.setFormat(mater_format.trim());

            JSONArray shardJsonArray = jsonObject.optJSONArray("shard_list");
            if (shardJsonArray != null && shardJsonArray.length() > 0) {
                for (int position = 0; position < shardJsonArray.length(); position++) {
                    JSONObject shardObject = shardJsonArray.getJSONObject(position);
                    String shardName = shardObject.optString("shard");
                    shards.add(shardName.trim());
                }
            }

            JSONArray materJsonArray = jsonObject.optJSONArray("mater_list");
            if (materJsonArray != null && materJsonArray.length() > 0) {
                ArrayList<Pick.PickItem.PickItemBranchItem> pickItemBranchItems = new ArrayList<>();
                for (int i = 0; i < materJsonArray.length(); i++) {
                    Pick.PickItem.PickItemBranchItem pickItemBranchItem = new Pick.PickItem.PickItemBranchItem();
                    Mater.Branch branchSecond = new Mater.Branch();
                    Mater materSecond = new Mater();
                    JSONObject branchObject = materJsonArray.getJSONObject(i);
                    String branchPoSecond = branchObject.optString("branch");
                    String shardSecond = branchObject.optString("shard");
                    String locationSecond = branchObject.optString("location");
                    double quantity = branchObject.optDouble("quantity");
                    String mater_unit = branchObject.optString("unit");
                    materSecond.setNumber(mateNumber.trim());
                    materSecond.setWarehouse(warehouse.trim());
                    materSecond.setShard(shardSecond.trim());
                    materSecond.setLocation(locationSecond.trim());
                    materSecond.setUnit(mater_unit.trim());
                    branchSecond.setPo(branchPoSecond.trim());
                    branchSecond.setQuantity(quantity);
                    branchSecond.setMater(materSecond);
                    pickItemBranchItem.setBranch(branchSecond);
                    pickItemBranchItems.add(pickItemBranchItem);
                }
                pickItem.setPickItemBranchItems(pickItemBranchItems);
            }
        }
        data.putParcelable("pickItem", pickItem);
        data.putStringArrayList("shards", shards);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionStorageInquire(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            int state = jsonObject.optInt("order_state");
            String product_desc = jsonObject.optString("product_desc").trim();
            String product = jsonObject.optString("product").trim();
            int branched = jsonObject.optInt("branched");
            double quantity_order = jsonObject.optDouble("quantity_order");
            double quantity_storaged = jsonObject.optDouble("quantity_storaged");
            String unit = jsonObject.optString("unit").trim();
            String shard = jsonObject.optString("shard").trim();
            String location = jsonObject.optString("location").trim();

            Map<String, String> params = (Map<String, String>) jsonObject.opt("params");
            String work_order = params.get("work_order");
            String warehouse = params.get("warehouse");

            WorkOrder workOrder = new WorkOrder();
            workOrder.setNumber(work_order);
            workOrder.setState(state);
            workOrder.setQuantityOrderProduct(quantity_order);
            workOrder.setQuantityFinishedProduct(quantity_storaged);

            Mater production = new Mater();
            production.setWarehouse(warehouse);
            production.setNumber(product);
            production.setDesc(product_desc);
            production.setBranchControl(branched);
            production.setUnit(unit);
            production.setShard(shard);
            production.setLocation(location);

            workOrder.setProduction(production);

            data.putParcelable("workOrder", workOrder);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionStorageSubmit(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        data.putDouble("max_remain", jsonObject.optDouble("max_remain"));
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 生产订单查询
     *
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws Exception
     */
    public static void decodeProductionOrderInquire(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            WorkOrder workOrder = new WorkOrder();
            Mater production = new Mater();
            ArrayList<WorkOrder.MaterProduct> materProducts = new ArrayList<>();//材料集合
            ArrayList<WorkOrder.Step> steps = new ArrayList<>();//工序集合
            workOrder.setProduction(production);
            workOrder.setMaterProducts(materProducts);
            workOrder.setSteps(steps);

            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            String warehouse = params.get("warehouse").trim();
            String work_order = params.get("work_order").trim();
            String department = jsonObject.optString("department").trim();
            int orderState = jsonObject.optInt("order_state");
            String productName = jsonObject.optString("product_name").trim();
            String productDesc = jsonObject.optString("product_desc").trim();
            String productForm = jsonObject.optString("product_form").trim();
            double quantityOrderProduct = jsonObject.optDouble("quantity_order_product", 0);
            double quantityFinishedProduct = jsonObject.optDouble("quantity_finished_product", 0);
            double quantityRemainProduct = jsonObject.optDouble("quantity_remain_product", 0);
            String plainStartDate = jsonObject.optString("plain_start_date").trim();
            String plainFinishedDate = jsonObject.optString("plain_finish_date").trim();
            String actualStartDate = jsonObject.optString("actual_start_date").trim();
            String sale_order_number = jsonObject.optString("sale_order_number").trim();
            String customerName = jsonObject.optString("customer_name").trim();
            String customerCode = jsonObject.optString("customer_code").trim();
            String customerPurchaseOrderNumber = jsonObject.optString("customer_purchase_order_number").trim();
            double quantityOrderSale = jsonObject.optDouble("quantity_order_sale", 0);
            double quantityShipmentSale = jsonObject.optDouble("quantity_shipmented_sale", 0);
            double quantityRemainSale = jsonObject.optDouble("quantity_remain_sale", 0);
            String complianceDate = jsonObject.optString("compliance_date").trim();

            production.setWarehouse(warehouse);
            workOrder.setNumber(work_order);
            workOrder.setDepartment(department);
            workOrder.setState(orderState);
            production.setNumber(productName);
            production.setDesc(productDesc);
            production.setFormat(productForm);
            workOrder.setQuantityOrderProduct(quantityOrderProduct);
            workOrder.setQuantityRemainProduct(quantityRemainProduct);
            workOrder.setQuantityFinishedProduct(quantityFinishedProduct);
            workOrder.setPlanStartDate(plainStartDate);
            workOrder.setPlanFinishDate(plainFinishedDate);
            workOrder.setActualStartDate(actualStartDate);
            workOrder.setSaleOrderNumber(sale_order_number);
            workOrder.setCustomerName(customerName);
            workOrder.setCustomerCode(customerCode);
            workOrder.setCustomerPurchaseOrderNumber(customerPurchaseOrderNumber);
            workOrder.setQuantityOrderSale(quantityOrderSale);
            workOrder.setQuantityShipmentSale(quantityShipmentSale);
            workOrder.setQuantityRemainSale(quantityRemainSale);
            workOrder.setComplianceDate(complianceDate);

//            functionCreateMaterProductionTexst(materProducts);

            JSONArray materJsonArray = jsonObject.optJSONArray("mater_list");
            if (materJsonArray != null) {
                for (int i = 0; i < materJsonArray.length(); i++) {
                    WorkOrder.MaterProduct materProduct = new WorkOrder.MaterProduct();
                    materProducts.add(materProduct);

                    JSONObject materJsonObject = materJsonArray.getJSONObject(i);
                    String materNumber = materJsonObject.optString("mater_number").trim();
                    String materName = materJsonObject.optString("mater_name").trim();
                    String materDesc = materJsonObject.optString("mater_desc").trim();
                    String materForm = materJsonObject.optString("mater_form").trim();
                    String last_hair_mater_date = materJsonObject.optString("last_hair_mater_date").trim();
                    double plan_usage_amount = materJsonObject.optDouble("plan_usage_amount", 0);
                    double actual_usage_amount = materJsonObject.optDouble("actual_usage_amount", 0);
                    String mater_unit = materJsonObject.optString("mater_unit").trim();

                    materProduct.setSequenceNumber(materNumber);
                    materProduct.setNumber(materName);
                    materProduct.setDesc(materDesc);
                    materProduct.setFormat(materForm);
                    materProduct.setLastHairMaterDate(last_hair_mater_date);
                    materProduct.setPlanUsageAmount(plan_usage_amount);
                    materProduct.setActualUsageAmount(actual_usage_amount);
                    materProduct.setUnit(mater_unit);
                }
            }

//            functionCreateStepTexst(steps);

            JSONArray stepJsonArray = jsonObject.optJSONArray("step_list");
            if (stepJsonArray != null) {
                for (int i = 0; i < stepJsonArray.length(); i++) {
                    WorkOrder.Step step = new WorkOrder.Step();
                    steps.add(step);

                    JSONObject stepJsonObject = stepJsonArray.getJSONObject(i);
                    String step_number = stepJsonObject.optString("step_number").trim();
                    String step_name = stepJsonObject.optString("step_name").trim();
                    String outsourcing_supplier = stepJsonObject.optString("outsourcing_supplier").trim();
                    String outsourcing_purchase_order_number = stepJsonObject.optString("outsourcing_purchase_order_number").trim();
                    double standard_working_hours = stepJsonObject.optDouble("standard_working_hours", 0);
                    double actual_working_hours = stepJsonObject.optDouble("actual_working_hours", 0);
                    double outsourcing_costs = stepJsonObject.optDouble("outsourcing_costs", 0);
                    String tbc = stepJsonObject.optString("tbc").trim();

                    step.setStepNumber(step_number);
                    step.setStepName(step_name);
                    step.setOutsourcingSupplier(outsourcing_supplier);
                    step.setOutsourcingPurchaseOrderNumber(outsourcing_purchase_order_number);
                    step.setStandardWorkingHours(standard_working_hours);
                    step.setActualWorkingHours(actual_working_hours);
                    step.setOutsourcingCosts(outsourcing_costs);
                    step.setTBC(tbc);
                }
            }
            data.putParcelable("workOrder", workOrder);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionOrderInquireGetMater(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            String mater_desc = jsonObject.optString("mater_desc");

            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            int position = Integer.parseInt(params.get("position"));

            data.putString("mater_desc", mater_desc);
            data.putInt("position", position);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionOrderInquireGetStepOutsourceInfo(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            String outsourcing_supplier = jsonObject.optString("outsourcing_supplier").trim();
            double outsourcing_costs = jsonObject.optDouble("outsourcing_costs", 0);
            String outsourcing_purchase_order_number = jsonObject.optString("outsourcing_purchase_order_number").trim();
            String outsourcing_costs_unit = jsonObject.optString("outsourcing_costs_unit").trim();

            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            int position = Integer.parseInt(params.get("position"));

            data.putString("outsourcing_supplier", outsourcing_supplier);
            data.putDouble("outsourcing_costs", outsourcing_costs);
            data.putString("outsourcing_costs_unit", outsourcing_costs_unit);
            data.putString("outsourcing_purchase_order_number", outsourcing_purchase_order_number);

            data.putInt("position", position);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodePrintMaterLabelInquire(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            String mater_format = jsonObject.optString("mater_format");
            String mater_desc = jsonObject.optString("mater_desc");
            int branched = jsonObject.optInt("branched", Mater.BRANCH_NORMAL);
            String storage_unit = jsonObject.optString("storage_unit");
            double single = jsonObject.optDouble("single", 0);
            String single_unit = jsonObject.optString("single_unit");

            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            String materNumber = params.get("mater");
            String branchName = params.get("branch");

            Mater.Branch branch = new Mater.Branch();
            branch.setPo(branchName);
            Mater mater = new Mater();
            branch.setMater(mater);
            mater.setNumber(materNumber);
            mater.setFormat(mater_format);
            mater.setDesc(mater_desc);
            mater.setBranchControl(branched);
            mater.setUnit(storage_unit);
            mater.setSingle(single);
            mater.setSingleUnit(single_unit);
            data.putParcelable("branch", branch);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionReportGetJobList(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            ArrayList<Job> jobs = new ArrayList<>();
            JSONArray jobListJsonArray = jsonObject.optJSONArray("job_list");
            if (jobListJsonArray != null) {
                for (int i = 0; i < jobListJsonArray.length(); i++) {
                    JSONObject JobInfo = jobListJsonArray.getJSONObject(i);
                    String job_number = JobInfo.optString("job_number").trim();
                    String work_order = JobInfo.optString("work_order").trim();
                    String step_name = JobInfo.optString("step_name").trim();
                    String step_number = JobInfo.optString("step_number").trim();
                    String proper_name = JobInfo.optString("proper_name").trim();
                    String proper_number = JobInfo.optString("proper_number").trim();
                    Job job = new Job();
                    job.setJobNumber(job_number);
                    WorkOrder workOrder = new WorkOrder();
                    workOrder.setNumber(work_order);
                    job.setWorkOrder(workOrder);
                    job.setStepName(step_name);
                    job.setStepNumber(step_number);
                    job.setProprName(proper_name);
                    job.setProprNumber(proper_number);
                    jobs.add(job);
                }
            }


            data.putParcelableArrayList("jobs", jobs);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionReportGetJobDetail(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Job job = new Job();

            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            String job_number = params.get("job_number");
            String step_name = params.get("step_name");
            String step_number = params.get("step_number");
            String proper_name = params.get("proper_name");
            String proper_number = params.get("proper_number");

            job.setJobNumber(job_number);
            job.setStepName(step_name);
            job.setStepNumber(step_number);
            job.setProprName(proper_name);
            job.setProprNumber(proper_number);

            String work_order = jsonObject.optString("work_order").trim();
            String department = jsonObject.optString("department").trim();
            String create_time = jsonObject.optString("create_time").trim();
            WorkOrder workOrder = new WorkOrder();
            workOrder.setNumber(work_order);
            workOrder.setDepartment(department);
            job.setWorkOrder(workOrder);
            job.setCreateTime(create_time);

            ArrayList<Employee> employees = new ArrayList<>();
            JSONArray employeeJsonArray = jsonObject.optJSONArray("employee_list");
            if (employeeJsonArray != null) {
                for (int i = 0; i < employeeJsonArray.length(); i++) {
                    JSONObject employeeJsonObject = employeeJsonArray.getJSONObject(i);
                    String employee_number = employeeJsonObject.optString("employee_number").trim();
                    String employee_name = employeeJsonObject.optString("employee_name").trim();
                    String begin_time = employeeJsonObject.optString("begin_time").trim();
                    Employee employee = new Employee();
                    employee.setNumber(employee_number);
                    employee.setName(employee_name);
                    employee.setStartTime(begin_time);
                    employees.add(employee);
                }
            }

            ArrayList<Machine> machines = new ArrayList<>();
            JSONArray machineJsonArray = jsonObject.optJSONArray("machine_list");
            if (machineJsonArray != null) {
                for (int i = 0; i < machineJsonArray.length(); i++) {
                    JSONObject machineJsonObject = machineJsonArray.getJSONObject(i);
                    String machine_number = machineJsonObject.optString("machine_number").trim();
                    String machine_name = machineJsonObject.optString("machine_name").trim();
                    String begin_time = machineJsonObject.optString("begin_time").trim();
                    Machine machine = new Machine();
                    machine.setNumber(machine_number);
                    machine.setName(machine_name);
                    machine.setStartTime(begin_time);
                    machines.add(machine);
                }
            }
            job.setEmployees(employees);
            job.setMachines(machines);

            data.putParcelable("job", job);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionReportEmployeeInquire(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {

            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            String number = params.get("employee_number");
            String name = jsonObject.optString("name").trim();
            String type = jsonObject.optString("type").trim();
            int state = jsonObject.optInt("state", Employee.STATE_CODE_DEFAULT);
            String department = jsonObject.optString("department").trim();

            Employee employee = new Employee();
            employee.setNumber(number);
            employee.setName(name);
            employee.setType(type);
            employee.setState(state);
            employee.setDepartment(department);
            data.putParcelable("employee", employee);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionReportAddOrRemoveOption(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionReportMachineInquire(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            String number = params.get("machine_number");
            String name = jsonObject.optString("name").trim();
            String type = jsonObject.optString("type").trim();
            int state = jsonObject.optInt("state", Employee.STATE_CODE_DEFAULT);
            String department = jsonObject.optString("department").trim();

            Machine machine = new Machine();
            machine.setNumber(number);
            machine.setName(name);
            machine.setType(type);
            machine.setState(state);
            machine.setDepartment(department);
            data.putParcelable("machine", machine);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionReportAddNewJobWorkOrderInquire(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            String workOrder = params.get("work_order");
            data.putString("workOrder", workOrder);

            ArrayList<Dict> stepDictList = new ArrayList<>();
            ArrayList<Dict> proprDictList = new ArrayList<>();

            JSONArray stepJsonArray = jsonObject.optJSONArray("step_list");
            if (stepJsonArray != null) {
                for (int i = 0; i < stepJsonArray.length(); i++) {
                    JSONObject stepJsonObject = stepJsonArray.optJSONObject(i);
                    if (stepJsonObject != null) {
                        String step_number = stepJsonObject.optString("step_number").trim();
                        String step_name = stepJsonObject.optString("step_name").trim();
                        Dict dict = new Dict(step_number, step_name);
                        stepDictList.add(dict);
                    }
                }
            }

            JSONArray proprJsonArray = jsonObject.optJSONArray("propr_list");
            if (proprJsonArray != null) {
                for (int i = 0; i < proprJsonArray.length(); i++) {
                    JSONObject proprJsonObject = proprJsonArray.optJSONObject(i);
                    if (proprJsonObject != null) {
                        String propr_number = proprJsonObject.optString("propr_number").trim();
                        String propr_name = proprJsonObject.optString("propr_name").trim();
                        Dict dict = new Dict(propr_number, propr_name);
                        proprDictList.add(dict);
                    }
                }
            }

            data.putParcelableArrayList("stepDictList", stepDictList);
            data.putParcelableArrayList("proprDictList", proprDictList);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionReportAddNewJobEmployeeInquire(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            String work_order = params.get("work_order");
            String step_number = params.get("step_number");
            String propr_number = params.get("propr_number");

            data.putString("work_order", work_order);
            data.putString("step_number", step_number);
            data.putString("propr_number", propr_number);

            ArrayList<Employee> employees = new ArrayList<>();
            JSONArray employeeJsonArray = jsonObject.optJSONArray("employee_list");
            if (employeeJsonArray != null) {
                for (int i = 0; i < employeeJsonArray.length(); i++) {
                    JSONObject employeeJsonObject = employeeJsonArray.optJSONObject(i);
                    if (employeeJsonObject != null) {
                        String employee_number = employeeJsonObject.optString("employee_number").trim();
                        String employee_name = employeeJsonObject.optString("employee_name").trim();
                        String employee_dept = employeeJsonObject.optString("employee_dept").trim();
                        int default_checked = employeeJsonObject.optInt("default_checked", 0);
                        Employee employee = new Employee();
                        employee.setNumber(employee_number);
                        employee.setName(employee_name);
                        employee.setDepartment(employee_dept);
                        employee.setChecked(default_checked == 1 ? true : false);
                        employees.add(employee);
                    }
                }
            }
            data.putParcelableArrayList("employees", employees);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionReportAddNewJobMachineInquire(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            String work_order = params.get("work_order");
            String step_number = params.get("step_number");
            String propr_number = params.get("propr_number");

            data.putString("work_order", work_order);
            data.putString("step_number", step_number);
            data.putString("propr_number", propr_number);

            ArrayList<Machine> machines = new ArrayList<>();
            JSONArray machineJsonArray = jsonObject.optJSONArray("machine_list");
            if (machineJsonArray != null) {
                for (int i = 0; i < machineJsonArray.length(); i++) {
                    JSONObject machineJsonObject = machineJsonArray.optJSONObject(i);
                    if (machineJsonObject != null) {
                        String machine_number = machineJsonObject.optString("machine_number").trim();
                        String machine_name = machineJsonObject.optString("machine_name").trim();
                        String machine_dept = machineJsonObject.optString("machine_dept").trim();
                        int default_checked = machineJsonObject.optInt("default_checked");
                        Machine machine = new Machine();
                        machine.setNumber(machine_number);
                        machine.setName(machine_name);
                        machine.setDepartment(machine_dept);
                        machine.setChecked(default_checked == 1 ? true : false);
                        machines.add(machine);
                    }
                }
            }
            data.putParcelableArrayList("machines", machines);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionReportAddNewJobSubmit(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {


        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeProductionReportJobFinishInquire(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            double artificial_hours = jsonObject.optDouble("artificial_hours", 0);
            double machine_hours = jsonObject.optDouble("machine_hours", 0);

            data.putDouble("artificial_hours", artificial_hours);
            data.putDouble("machine_hours", machine_hours);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeInventoryUpdateSubmit(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeInventoryAddQuery(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Map<String, String> params = (Map<String, String>) jsonObject.get("params");
            String mater_number = params.get("mater");
            String mater_desc = jsonObject.optString("mater_desc");
            String mater_format = jsonObject.optString("mater_format");
            String unit = jsonObject.optString("unit");
            String branched = jsonObject.optString("branched");

            Mater mater = new Mater();
            mater.setNumber(mater_number.trim());
            mater.setDesc(mater_desc.trim());
            mater.setFormat(mater_format.trim());
            mater.setUnit(unit.trim());
            mater.setBranchControl(TextUtils.equals(branched, "1") ? Mater.BRANCH_CONTROL : TextUtils.equals(branched, "0") ? Mater.BRANCH_NO_CONTROL : Mater.BRANCH_NORMAL);

            data.putParcelable("mater", mater);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeGetShipmentList(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        Map<String, String> params = (Map<String, String>) jsonObject.get("params");
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Shipment shipment = new Shipment();
            shipment.setNumber(params.get("pldno"));
            shipment.setWarehouse(params.get("warehouse"));
            shipment.setClientNumber(jsonObject.optString("client_number").trim());
            shipment.setClientName(jsonObject.optString("client_name").trim());
            shipment.setDepartment(jsonObject.optString("department").trim());
            shipment.setExpectedDate(jsonObject.optString("expected_data").trim());
            ArrayList<Shipment.ShipmentItem> shipmentItems = new ArrayList<>();
            shipment.setShipmentItems(shipmentItems);

            JSONArray zpldtlJsonArray = jsonObject.optJSONArray("zpldtl_list");

            if (zpldtlJsonArray != null) {
                for (int i = 0; i < zpldtlJsonArray.length(); i++) {
                    JSONObject zpldtlObject = zpldtlJsonArray.optJSONObject(i);
                    if (zpldtlObject != null) {
                        Shipment.ShipmentItem shipmentItem = new Shipment.ShipmentItem();
                        shipmentItems.add(shipmentItem);
                        shipmentItem.setPldln(zpldtlObject.optString("pldln").trim());
                        shipmentItem.setC6cvnb(zpldtlObject.optString("c6cvnb").trim());
                        shipmentItem.setCdfcnb(zpldtlObject.optString("cdfcnb").trim());
                        shipmentItem.setQuantity(zpldtlObject.optDouble("plan_quantity"));
                        shipmentItem.setUnit(zpldtlObject.optString("plan_quantity_unit").trim());
                        Mater mater = new Mater();
                        shipmentItem.setMater(mater);
                        mater.setNumber(zpldtlObject.optString("mater").trim());
                        mater.setDesc(zpldtlObject.optString("mater_dese").trim());
                        mater.setFormat(zpldtlObject.optString("mater_format").trim());
                        mater.setShard(zpldtlObject.optString("shard").trim());
                        mater.setLocation(zpldtlObject.optString("location").trim());
                    }
                }
            }
            data.putParcelable("shipment", shipment);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeSaleShipmentGetDetail(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        Map<String, String> params = (Map<String, String>) jsonObject.get("params");
        insertRecInformation(data, jsonObject);
        if (isRequestOK(jsonObject)) {
            Shipment.ShipmentItem shipmentItem = new Shipment.ShipmentItem();
            shipmentItem.setQuantity(Double.parseDouble(params.get("plan_quantity")));
            shipmentItem.setC6cvnb(params.get("c6cvnb"));
            shipmentItem.setCdfcnb(params.get("cdfcnb"));
            Shipment shipment = new Shipment();
            shipment.setNumber(params.get("pldno"));
            shipment.setWarehouse(params.get("warehouse"));
            shipmentItem.setPldln(params.get("pldln"));
            shipmentItem.setShipment(shipment);
            Mater mater = new Mater();
            shipmentItem.setMater(mater);
            mater.setNumber(params.get("mater"));
            mater.setShard(params.get("shard"));
            mater.setLocation(params.get("location"));

            shipmentItem.setBoxNumber(jsonObject.optString("boxnm").trim());
            shipmentItem.setBoxQuantity(jsonObject.optDouble("boxes"));
            shipmentItem.setBoxln(jsonObject.optString("boxln").trim());

            ArrayList<String> shards = new ArrayList<>();
            JSONArray shardJsonArray = jsonObject.optJSONArray("shard_list");
            if (shardJsonArray != null && shardJsonArray.length() > 0) {
                for (int position = 0; position < shardJsonArray.length(); position++) {
                    JSONObject shardObject = shardJsonArray.getJSONObject(position);
                    String shardName = shardObject.optString("shard");
                    shards.add(shardName.trim());
                }
            }

            JSONArray materJsonArray = jsonObject.optJSONArray("mater_list");
            if (materJsonArray != null && materJsonArray.length() > 0) {
                ArrayList<Shipment.ShipmentItem.ShipmentItemBranchItem> shipmentItemBranchItems = new ArrayList<>();
                shipmentItem.setShipmentItemBranchItems(shipmentItemBranchItems);
                for (int i = 0; i < materJsonArray.length(); i++) {
                    Shipment.ShipmentItem.ShipmentItemBranchItem shipmentItemBranchItem = new Shipment.ShipmentItem.ShipmentItemBranchItem();
                    Mater.Branch branchSecond = new Mater.Branch();
                    Mater materSecond = new Mater();
                    JSONObject branchObject = materJsonArray.getJSONObject(i);
                    String branchPoSecond = branchObject.optString("branch");
                    String shardSecond = branchObject.optString("shard");
                    String locationSecond = branchObject.optString("location");
                    double quantity = branchObject.optDouble("quantity");
                    String mater_unit = branchObject.optString("unit");
                    materSecond.setNumber(params.get("mater"));
                    materSecond.setWarehouse(params.get("warehouse"));
                    materSecond.setShard(shardSecond.trim());
                    materSecond.setLocation(locationSecond.trim());
                    materSecond.setUnit(mater_unit.trim());
                    branchSecond.setPo(branchPoSecond.trim());
                    branchSecond.setQuantity(quantity);
                    branchSecond.setMater(materSecond);
                    shipmentItemBranchItem.setBranch(branchSecond);
                    shipmentItemBranchItems.add(shipmentItemBranchItem);
                }

            }

            data.putInt("code", 1);
            data.putStringArrayList("shards", shards);
            data.putParcelable("shipmentItem", shipmentItem);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 确认出货
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws Exception
     */
    public static void decodeSaleShipmentEnsure(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    /**
     * 出货过账
     * @param jsonObject
     * @param messageWhat
     * @param handler
     * @throws Exception
     */
    public static void decodeSaleShipmentCommit(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void decodeSaleShipmentCancel(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
        Message msg = new Message();
        Bundle data = new Bundle();
        msg.what = messageWhat;
        insertRecInformation(data, jsonObject);
        msg.setData(data);
        handler.sendMessage(msg);
    }
//通用模板
//    public static void decodeReceiptConfirm(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
//        Message msg = new Message();
//        Bundle data = new Bundle();
//        msg.what = messageWhat;
//Map<String, String> params = (Map<String, String>) jsonObject.get("params");
//        insertRecInformation(data, jsonObject);
//        if (isRequestOK(jsonObject)) {
//
//
//        }
//    msg.setData(data);
//    handler.sendMessage(msg);
//    }

    /**
     * 完成code ,desc,params(Map) 响应字段的读取,存入bundle
     *
     * @param bundle
     * @param jsonObject
     */
    private static void insertRecInformation(Bundle bundle, JSONObject jsonObject) throws Exception {
        int code = jsonObject.optInt("code");
        String desc = jsonObject.optString("desc");
        bundle.putInt("code", code);
        bundle.putString("desc", desc.trim());
        bundle.putSerializable("params", (HashMap<String, String>) (jsonObject.opt("params")));
    }

    /**
     * jsonobject 返回结果校验 ,校验retcode
     *
     * @param jsonObject
     * @return
     */
    private static boolean isRequestOK(JSONObject jsonObject) {
        int retcode = jsonObject.optInt("code");
        if (retcode == RETURN_CODE_SUCCESS) {
            return true;
        } else {
            return false;
        }
    }

}
