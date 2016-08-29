package com.amphenol.Manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import com.amphenol.entity.Mater;
import com.amphenol.entity.Pick;
import com.amphenol.entity.Purchase;
import com.amphenol.entity.Requisition;
import com.amphenol.entity.WorkOrder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
                    double quantity = pickItemJsonObject.optDouble("quantity", 0);
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

            WorkOrder.ProductionBranch productionBranch = new WorkOrder.ProductionBranch();
            productionBranch.setQuantityOrder(quantity_order);
            productionBranch.setQuantityStoraged(quantity_storaged);

            WorkOrder workOrder = new WorkOrder();
            workOrder.setNumber(work_order);
            workOrder.setState(state);

            Mater production = new Mater();
            production.setWarehouse(warehouse);
            production.setNumber(product);
            production.setDesc(product_desc);
            production.setBranchControl(branched);
            production.setUnit(unit);
            production.setShard(shard);
            production.setLocation(location);

            workOrder.setProduction(production);

            productionBranch.setWorkOrder(workOrder);

            data.putParcelable("productionBranch",productionBranch);
        }
        msg.setData(data);
        handler.sendMessage(msg);
    }
    public static void decodeProductionStorageSubmit(JSONObject jsonObject, int messageWhat, Handler handler) throws Exception {
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
