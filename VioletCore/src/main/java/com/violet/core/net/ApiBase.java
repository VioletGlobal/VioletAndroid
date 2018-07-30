package com.violet.core.net;


import android.text.TextUtils;

import com.violet.core.net.api.AbsApi;

import java.net.URLEncoder;
import java.security.InvalidParameterException;



/**
 * @author liuqun1
 * create at 2018/6/20 上午11:11
 * biubiu 接口请求
 */
public abstract class ApiBase<T> extends AbsApi<T> {

    /**
     * 新版登录 token 全局错误
     */
    private boolean mTokenError;

    public boolean isTokenError() {
        return mTokenError;
    }

    public void setTokenError(boolean tokenError) {
        mTokenError = tokenError;
    }

    public abstract String getUrlResource();

    public ApiBase() {
        super();
        this.baseUrl = UrlContainer.getApiHost();
    }

    public ApiBase(Class<?> responseClass) {
        super();
        this.baseUrl = UrlContainer.getApiHost();
        this.responseClass = responseClass;
    }

    public ApiBase(String url) {
        super();
        this.baseUrl = url;
    }

    public ApiBase(Class<?> responseClass, String url) {
        super(responseClass);
        this.baseUrl = url;
    }


    /**
     * Generate api's uri based on baseUrl, url resource and request parameters
     * 新闻V5版本api通用uri生成规则，只使用与新闻V5接口uri生成规则（参见本文件头注释）
     * 若：非新闻api拼接规则，请在子类覆盖该方法,进行uri的生成规则。
     *
     * @return api's uri
     */
    public String getUri() {
        if (baseUrl == null) {
            throw new InvalidParameterException("must set baseUrl for API request");
        }
        if (!TextUtils.isEmpty(dnsErrorIpRequestUri)) {
            return dnsErrorIpRequestUri;
        }
        String sPara = null;
        sPara = getParams(sPara);
        String uri = baseUrl;
        if (sPara != null) {
            uri = baseUrl + getUrlResource() + "?" + sPara;
            //  uri = String.format("%s"+  + "%s&%s", baseUrl, urlResource, sPara);
        }
//        uri = getSignUri(uri);
        uri = mApiConfigController.encodeRequestUrl(baseUrl, uri);
        if (UrlContainer.getApiHost().equals(this.baseUrl)) {
            this.originalUri = uri;
        }

        if (!TextUtils.isEmpty(getReplaceRequestUri())) {
            uri = originalUri.replaceFirst(UrlContainer.getApiHost(), getReplaceRequestUri());
        }

        return uri;
    }

    /*
     * 添加uri sign & rand
     *
     */
//    protected String getSignUri(String uri) {
//        int rand = (int) (Math.random() * 1000 + 1);
//        String sign = "";
//        sign = HttpSignUtils.createSign(uri, rand);
////        if (ABTestConfigHelper.isUseNewSign()) {
////            sign = HttpSignUtils.createSignV2(uri, rand);
////        } else {
////            sign = HttpSignUtils.createSign(uri, rand);
////        }
//        uri = String.format("%s&%s=%s&%s=%s", uri, URL_SIGN, sign, RAND, String.valueOf(rand));
//        mApiConfigController.addRequestParams(URL_SIGN, sign);
//        mApiConfigController.addRequestParams(RAND, String.valueOf(rand));
//        return uri;
//    }

    /**
     * 提供外部url：接口拼接方式使用，拼接方式为:基础url?参数
     * 无通用参数，无sign+rand
     * 如：http://api.apps.sina.cn/sdk/recommend.php ? pd=300&num=20&offset=0&vs=5
     *
     * @return url
     */
    public String getExternalUri() {
        String sPara = null;
        sPara = formatParams(sPara);
        String uri = getBaseUrl();
        if (sPara != null)
            uri = String.format("%s?%s", uri, sPara);
        return uri;
    }

    /*
     * 拼接参数
     */
    private String formatParams(String sPara) {
        String result;
        StringBuilder sb = new StringBuilder(1024);
        if (!TextUtils.isEmpty(sPara)) {
            sb.append(sPara);
        }
        // todo 使用keySet遍历会多取一次值,改成entryset遍历
        for (String kParam : params.keySet()) {
            if (TextUtils.isEmpty(kParam) /*|| SNTextUtils.isEmpty(params.get(kParam))*/) {
                continue;
            }
            String vParams = "";
            try {
                vParams = URLEncoder.encode(params.get(kParam));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (sb.length() != 0) {
                sb.append("&");
            }
            sb.append(kParam).append("=").append(vParams);
            mApiConfigController.addRequestParams(kParam, vParams);
        }
        result = sb.toString();
        return result;
    }


    @Override
    public String toString() {
        return "ApiBase{" + "baseUrl='" + baseUrl + '\'' + ", headers=" + headers + ", params="
                + params + '}';
    }


}