package com.yc.love.model.engin;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.TypeReference;
import com.kk.securityhttp.engin.HttpCoreEngin;
import com.yc.love.model.base.BaseEngine;
import com.yc.love.model.bean.AResultInfo;
import com.yc.love.model.bean.IdCorrelationLoginBean;
import com.yc.love.model.bean.IdCorrelationSmsBean;
import com.yc.love.model.domain.URLConfig;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

/**
 * Created by mayn on 2019/5/8.
 */
@SuppressWarnings("unchecked")
public class IdCorrelationEngin extends BaseEngine {

    public IdCorrelationEngin(Context context) {
        super(context);
    }

   /* public Observable<IdCorrelationSmsBean> idCorrelationSms(String userId, String mobile) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("mobile", mobile);
        HttpCoreEngin<IdCorrelationSmsBean> httpCoreEngin = HttpCoreEngin.get(mContext);
        Observable<IdCorrelationSmsBean> rxpost = httpCoreEngin.rxpost(URLConfig.ID_INFO_SMS, new TypeReference<IdCorrelationSmsBean>() {
                }.getType(),
                params,
                true,
                true, true);
        return rxpost;
    }*/

    public Observable<AResultInfo<String>> sms(String userId, String mobile) {
        Map<String, String> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("mobile", mobile);
        requestParams(params);
        HttpCoreEngin<AResultInfo<String>> httpCoreEngin = HttpCoreEngin.get(mContext);
        Observable<AResultInfo<String>> rxpost = httpCoreEngin.rxpost(URLConfig.ID_INFO_SMS, new TypeReference<AResultInfo<String>>() {
                }.getType(),
                params,
                true,
                true, true);
        return rxpost;
    }

    private void requestParams(Map<String, String> params) {
        StringBuffer stringBuffer = new StringBuffer();
        for (String s : params.keySet()
                ) {
            stringBuffer.append(s).append(":").append(params.get(s)).append("   ");
        }
        Log.d("mylog", "requestParams: " + stringBuffer.toString());
    }

    public Observable<AResultInfo<IdCorrelationLoginBean>> register(String code, String mobile, String password, String url) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("mobile", mobile);
        params.put("password", password);
        requestParams(params);
        HttpCoreEngin<AResultInfo<IdCorrelationLoginBean>> httpCoreEngin = HttpCoreEngin.get(mContext);
        Observable<AResultInfo<IdCorrelationLoginBean>> rxpost = httpCoreEngin.rxpost(URLConfig.getUrl(url), new TypeReference<AResultInfo<IdCorrelationLoginBean>>() {
                }.getType(),
                params,
                true,
                true, true);
        return rxpost;
    }

    public Observable<AResultInfo<IdCorrelationLoginBean>> login(String mobile, String password, String url) {
        Map<String, String> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("password", password);
        requestParams(params);
        HttpCoreEngin<AResultInfo<IdCorrelationLoginBean>> httpCoreEngin = HttpCoreEngin.get(mContext);
        Observable<AResultInfo<IdCorrelationLoginBean>> rxpost = httpCoreEngin.rxpost(URLConfig.getUrl(url), new TypeReference<AResultInfo<IdCorrelationLoginBean>>() {
                }.getType(),
                params,
                true,
                true, true);
        return rxpost;
    }

    public Observable<AResultInfo<String>> resetPassword(String code, String mobile, String password, String url) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("mobile", mobile);
        params.put("password", password);
        requestParams(params);
        HttpCoreEngin<AResultInfo<String>> httpCoreEngin = HttpCoreEngin.get(mContext);
        Observable<AResultInfo<String>> rxpost = httpCoreEngin.rxpost(URLConfig.getUrl(url), new TypeReference<AResultInfo<String>>() {
                }.getType(),
                params,
                true,
                true, true);
        return rxpost;
    }
}