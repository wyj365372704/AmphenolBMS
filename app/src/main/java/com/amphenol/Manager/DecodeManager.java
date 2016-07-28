package com.amphenol.Manager;

import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import com.amphenol.entity.Mater;
import com.amphenol.entity.Purchase;
import com.amphenol.entity.Requisition;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


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
                data.putSerializable("env_list", envArrayList);
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
            data.putSerializable("purchase", purchase);
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
            data.putSerializable("purchaseItem", purchaseItem);

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
            data.putSerializable("requisitionItems", requisitionItems);
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
            data.putSerializable("requisitionItem", requisitionItem);
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
        bundle.putSerializable("params", (HashMap<String, String>) (jsonObject.get("params")));
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
