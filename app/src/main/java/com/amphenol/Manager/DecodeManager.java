package com.amphenol.Manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import com.amphenol.entity.Branch;
import com.amphenol.entity.Mater;
import com.amphenol.entity.Receipt;
import com.amphenol.fragment.PurchaseReceiptMainFragment;
import com.amphenol.fragment.PurchaseReceiptSecondFragment;

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
            Receipt receipt = new Receipt();
            String firm = jsonObject.optString("firm");
            String receipt_number = jsonObject.optString("receipt_number");
            int status_code = jsonObject.optInt("status_code");
            receipt.setFirm(firm.trim());
            receipt.setReceiptNumber(receipt_number.trim());
            receipt.setStatus(status_code);
            JSONArray materJsonArray = jsonObject.optJSONArray("mater_list");
            ArrayList<Mater> mater_list = new ArrayList<>();
            if (materJsonArray != null) {
                for (int i = 0; i < materJsonArray.length(); i++) {
                    JSONObject materJsonObject1 = materJsonArray.optJSONObject(i);
                    String materPO = materJsonObject1.optString("mater_po");
                    String number = materJsonObject1.optString("number");
                    String mate = materJsonObject1.optString("mate");
                    String unit = materJsonObject1.optString("unit");
                    double quantity = materJsonObject1.optDouble("quantity", 0);
                    Mater mater = new Mater();
                    mater.setPo(materPO);
                    mater.setShdhm(receipt_number.trim());
                    mater.setShdhh(number.trim());
                    mater.setMate_number(mate.trim());
                    mater.setPlan_quantity(quantity);
                    mater.setPurchase_unit(unit.trim());
                    mater_list.add(mater);
                }
            }
            receipt.setMaters(mater_list);
            data.putSerializable("receipt", receipt);
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
            String mateNumber = jsonObject.optString("mate_number");
            String mateDesc = jsonObject.optString("mate_desc");
            String purchaseUnit = jsonObject.optString("purchase_unit");
            double planQuantity = jsonObject.optDouble("plan_quantity");
            int branchControl = jsonObject.optInt("branch_control", Mater.BRANCH_NO_CONTROL);//批次控制默认为不控制
            int status = jsonObject.optInt("status", Mater.STATUS_CLOSED);//收货状态默认为已关闭
            double actualSingle = jsonObject.optDouble("actual_single");
            String actual_unit = jsonObject.optString("actual_unit");
            double actualQuantity = jsonObject.optDouble("actual_quantity");
            String shard = jsonObject.optString("shard");
            String location = jsonObject.optString("location");
            Mater mater = new Mater();
            mater.setMate_number(mateNumber.trim());
            mater.setMate_desc(mateDesc.trim());
            mater.setPurchase_unit(purchaseUnit.trim());
            mater.setPlan_quantity(planQuantity);
            mater.setBranch_control(branchControl);
            mater.setStatus(status);
            mater.setActualSingle(actualSingle);
            mater.setActualQuantity(actualQuantity);
            mater.setShard(shard.trim());
            mater.setLocation(location.trim());
            HashMap<String,String> params = (HashMap<String, String>) jsonObject.get("params");
            mater.setShdhh(params.get("receipt_line").trim());
            mater.setShdhm(params.get("receipt_number").trim());

            JSONArray branchJsonArray = jsonObject.optJSONArray("branch_list");
            if (branchJsonArray != null && branchJsonArray.length() > 0) {
                ArrayList<Branch> branchArrayList = new ArrayList<>();
                for (int i = 0; i < branchJsonArray.length(); i++) {
                    JSONObject branchJsonObject = branchJsonArray.optJSONObject(i);
                    String branch_number = branchJsonObject.optString("branch_number");
                    String branch_desc = branchJsonObject.optString("branch_desc");
                    double plan_quantity = branchJsonObject.optDouble("plan_quantity");
                    double actual_quantity = branchJsonObject.optDouble("actual_quantity");
                    Branch branch = new Branch(branch_number.trim(),branch_desc.trim(),plan_quantity,actual_quantity);
                    branchArrayList.add(branch);
                }
                mater.setBranches(branchArrayList);
            }
            data.putSerializable("mater",mater);
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
        int code = jsonObject.getInt("code");
        String desc = jsonObject.getString("desc");
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
