package com.amphenol.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NetWorkAccessTools {
    public static final int ERROR_CODE_ACCESS_FAILED = 0, ERROR_CODE_RETURN_ERROR = 1;

    private static final int BITMAP_MAX_SIZE = 60;
    private static NetWorkAccessTools netWorkAccessTools;
    private RequestQueue requestQueue;
    private LruCache<String, Bitmap> lruCache;
    private ImageLoader imageLoader;
    private ImageLoader.ImageCache imageCache;
    private FinalHttp finalHttp;
    private Context mContext;

    private NetWorkAccessTools(Context context) {
        mContext = context;
        requestQueue = Volley.newRequestQueue(context);
        lruCache = new LruCache<String, Bitmap>(BITMAP_MAX_SIZE);
        imageCache = new ImageLoader.ImageCache() {
            @Override
            public void putBitmap(String key, Bitmap value) {
                lruCache.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return lruCache.get(key);
            }
        };
        imageLoader = new ImageLoader(requestQueue, imageCache);
        finalHttp = new FinalHttp();
        finalHttp.configTimeout(Integer.parseInt(PropertiesUtil.getInstance(mContext).getValue(PropertiesUtil.NETWORK_TIMEOUT_MS, "5000")));
        finalHttp.configRequestExecutionRetryCount(Integer.parseInt(PropertiesUtil.getInstance(mContext).getValue(PropertiesUtil.NETWORK_RETRY_COUNT, "5")));
    }

    public synchronized static NetWorkAccessTools getInstance(Context context) {
        if (netWorkAccessTools == null) {
            netWorkAccessTools = new NetWorkAccessTools(context);
        }
        return netWorkAccessTools;
    }

    /**
     * get 方式发起异步网络请求
     *
     * @param url         访问地址,带上http://头, 不要跟上传递参数文本,不要跟上"?"
     * @param params      参数集合
     * @param requestCode 请求码，回调中用于区分请求
     * @param listener    回调接受者，UI线程执行
     */
    public void getAsyn(final String url, Map<String, String> params, final int requestCode, final RequestTaskListener listener) {
        if (params == null) {
            params = new HashMap<>();
        }
        final AjaxParams ajaxParams = new AjaxParams(params);
        final Map<String, String> finalParams = params;
        finalHttp.get(url, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onStart() {
                Log.d("NetWorkAccessTools-->", "onStart :" + FinalHttp.getUrlWithQueryString(url, ajaxParams) + " params is "+finalParams.toString());
                if (listener != null)
                    listener.onRequestStart(requestCode);

            }

            @Override
            public void onLoading(long count, long current) {
                Log.d("NetWorkAccessTools-->", "onLoading -count:" + count + " -current:" + current);
                if (listener != null)
                    listener.onRequestLoading(requestCode, current, count);
            }

            @Override
            public void onSuccess(String t) {
                Log.d("NetWorkAccessTools-->", "onSuccess :" + t.trim());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(t);
                    jsonObject.put("params", finalParams);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(e, ERROR_CODE_RETURN_ERROR, e.getMessage());
                    return;
                }
                if (listener != null)
                    listener.onRequestSuccess(jsonObject, requestCode);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
//                Log.d("NetWorkAccessTools-->", "onFailure : -errorNo:" + errorNo + " -message:" + strMsg.trim() + " -exception:" + t.getMessage());
                if (listener != null)
                    listener.onRequestFail(requestCode, errorNo);
            }
        });
    }

    /**
     * @param url         访问地址,带上http://头, 不要跟上传递参数文本,不要跟上"?"
     * @param params      参数集合
     * @param files       需要上传的文件集合，可以置空
     * @param requestCode 请求码，回调中用于区分请求
     * @param listener    回调接受者 , UI线程执行
     */
    public void postAsyn(final String url, Map<String, String> params, Map<String, File> files, final int requestCode, final RequestTaskListener listener) {
        if (params == null) {
            params = new HashMap<>();
        }
        AjaxParams ajaxParams = new AjaxParams(params);
        if (files != null) {
            Set<Entry<String, File>> entrySet2 = files.entrySet();
            for (Entry<String, File> entry : entrySet2) {
                String name = entry.getKey();
                File value = entry.getValue();
                try {
                    ajaxParams.put(name, value);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        final Map<String, String> finalParams = params;
        finalHttp.post(url, ajaxParams, new AjaxCallBack<String>() {
            @Override
            public void onLoading(long count, long current) {
                Log.d("NetWorkAccessTools-->", "onLoading -count:" + count + " current:" + current);
                if (listener != null)
                    listener.onRequestLoading(requestCode, current, count);
            }

            @Override
            public void onSuccess(String t) {
                Log.d("NetWorkAccessTools-->", "onSuccess :" + t);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(t);
                    jsonObject.put("params", finalParams);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(e, ERROR_CODE_RETURN_ERROR, e.getMessage());
                    return;
                }
                if (listener != null)
                    listener.onRequestSuccess(jsonObject, requestCode);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                Log.d(NetWorkAccessTools.class.getSimpleName(), "onFailure :" + strMsg);
                if (listener != null)
                    listener.onRequestFail(requestCode, errorNo);
            }

            @Override
            public void onStart() {
                Log.d(NetWorkAccessTools.class.getSimpleName(), "onStart :" + url);
                if (listener != null)
                    listener.onRequestStart(requestCode);
            }
        });
    }

    public void downLoadFile(final String url, Map<String, String> params, String savePath, final int requestCode, final RequestTaskListener listener) {
        if (params == null) {
            params = new HashMap<>();
        }
        final Map<String, String> finalParams = params;
        finalHttp.download("", new AjaxParams(params), "", new AjaxCallBack<File>() {
            @Override
            public void onStart() {
                Log.d(NetWorkAccessTools.class.getSimpleName(), "onStart :" + url);
                listener.onRequestStart(requestCode);
            }

            @Override
            public void onLoading(long count, long current) {
                Log.d("NetWorkAccessTools-->", "onLoading -count:" + count + " current:" + current);
                listener.onRequestLoading(requestCode, current, count);
            }

            @Override
            public void onSuccess(File file) {
                Log.d("NetWorkAccessTools-->", "onSuccess -fileName:" + file.getName());
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("params", finalParams);
                } catch (Exception e) {
                    e.printStackTrace();
                    onFailure(e, 0, e.getMessage());
                    return;
                }
                listener.onRequestSuccess(jsonObject, requestCode);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                Log.d("NetWorkAccessTools-->", "onFailure : -errorNo:" + errorNo + " -message:" + strMsg + " -exception:" + t.getMessage());
                listener.onRequestFail(requestCode, errorNo);
            }
        });
    }

    /**
     * 加载图片的工具方法
     *
     * @param imageURL
     * @param imageContainer
     * @param defaultImageResId
     * @param errorImageResId
     */
    public void toLoadImage(String imageURL, ImageView imageContainer, int defaultImageResId, int errorImageResId) {
        if (TextUtils.isEmpty(imageURL)) {
            imageContainer.setImageResource(errorImageResId);
        } else {
            ImageLoader.ImageListener listener = imageLoader.getImageListener(imageContainer, defaultImageResId, errorImageResId);
            imageLoader.get(imageURL, listener);
        }
    }

    /**
     *
     */
    public interface RequestTaskListener {
        void onRequestStart(int requestCode);

        void onRequestLoading(int requestCode, long current, long count);

        void onRequestSuccess(JSONObject jsonObject, int requestCode);

        void onRequestFail(int requestCode, int errorNo);
    }
}
