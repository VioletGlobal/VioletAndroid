package com.violet.core.net.api;

import android.text.TextUtils;

import com.violet.core.net.Method;
import com.violet.core.net.cache.CacheEntry;
import com.violet.core.net.config.DnsConfig;
import com.violet.core.net.encode.utils.ApiConfigController;
import com.violet.core.util.MD5;
import com.violet.net.dispatcher.VtPriority;
import com.violet.net.http.model.Response;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Created by kan212 on 2018/7/26.
 */

public class AbsApi<T> {

    public static final String API_STRING = "http://10.210.136.100:15679/";

    public interface IApiResultDispatcher<T> {
        void onResponseOK(T data, AbsApi api);

        void onResponseError(Object o, AbsApi api, Throwable throwable);
    }

    public interface ApiStatusCode {
        int Uknown = -1;
        int OK = 200;
        int Timeout = 100;
    }

    public interface IApiExecutor {
        void execute(AbsApi api, IApiResultDispatcher dispatcher, VtPriority priority);

        Response executeSync(AbsApi api);

        void executeForReplaceUri(AbsApi api, IApiResultDispatcher dispatcher, boolean isMinPriority, String replaceUri);

        void cancel(Object tag);

        void cancelAll();
    }

    // API头信息
    protected HashMap<String, String> headers;
    protected Map<String, String> responseHeaders;// used for http log upload
    protected ConcurrentMap<String, String> params;
    protected HashMap<String, String> mPostParams;
    protected Class<?> responseClass;
    protected String baseUrl;
    protected int statusCode;
    protected int httpCode;
    protected T data;
    protected Object error;
    //    protected int ownerId;
    protected int mMethod = Method.GET;

    /**
     * v6 add  zhoulibin for dns error
     */
    protected int changeServerTimes;
    protected DnsConfig dnsConfig;
    protected String originalUri;
    protected String dnsErrorIpRequestUri;
    // replace IP, （IP绑host用）
    protected String replaceRequestUri;
    protected VtPriority priority;

    /**
     * Api编码压缩模块.
     */
    protected ApiConfigController mApiConfigController = new ApiConfigController();

    public String getDnsErrorIpRequestUri() {
        return dnsErrorIpRequestUri;
    }

    public void setDnsErrorIpRequestUri(String dnsErrorIpRequestUri) {
        this.dnsErrorIpRequestUri = dnsErrorIpRequestUri;
    }

    public String getReplaceRequestUri() {
        return replaceRequestUri;
    }

    public void setReplaceRequestUri(String uri) {
        this.replaceRequestUri = uri;
    }

    public String getOriginalUri() {
        return originalUri;
    }

    public void setOriginalUri(String originalUri) {
        this.originalUri = originalUri;
    }

    public DnsConfig getDnsConfig() {
        if (null == dnsConfig) {
            dnsConfig = new DnsConfig();
        }
        return dnsConfig;
    }

    public void setDnsConfig(DnsConfig dnsConfig) {
        this.dnsConfig = dnsConfig;
    }

    public int getChangeServerTimes() {
        return changeServerTimes;
    }

    public void setChangeServerTimes(int changeServerTimes) {
        this.changeServerTimes = changeServerTimes;
    }

    public void addChangeServerTimes() {
        this.changeServerTimes = changeServerTimes++;
    }


    public static final String RESOURCE = String.format("/?%s=", /*GeneralRequestParameter.RES_PARAMS*/"");

    public int getRequestMethod() {
        return mMethod;
    }

    public void setRequestMethod(int mMethod) {
        this.mMethod = mMethod;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public boolean isStatusOK() {
        return statusCode == ApiStatusCode.OK;
    }

    public boolean hasData() {
        if (!isStatusOK() || data == null) {
            return false;
        }

        return !(responseClass != null && !responseClass.isInstance(data));

    }

    public boolean isStatusAndResponseClassOk() {
        return isStatusOK() && !(responseClass != null && !responseClass.isInstance(data));
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Class<?> getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(Class<?> responseClass) {
        this.responseClass = responseClass;
    }


    public void setPriority(VtPriority priority) {
        this.priority = priority;
    }

    public VtPriority getPriority() {
        return this.priority;
    }

    public AbsApi() {
        headers = new HashMap<>();
        params = new ConcurrentHashMap<>();
        mPostParams = new HashMap<>();
        this.statusCode = ApiStatusCode.Uknown;
    }

    public AbsApi(Class<?> responseClass) {
        this();
        this.responseClass = responseClass;
    }

    public AbsApi(String url) {
        this();
        this.baseUrl = url;
    }

    public AbsApi(Class<?> responseClass, String url) {
        this(responseClass);
        this.baseUrl = url;
    }


    public void addCommonRequestParams(Map<String, String> map) {
        if (map != null && !map.isEmpty()) {
            params.putAll(map);
        }
    }

    /**
     * Generate api's uri based on baseUrl, url resource and request parameters
     * 新闻V5版本api通用uri生成规则，只使用与新闻V5接口uri生成规则（参见本文件头注释）
     * 若：非新闻api拼接规则，请在子类覆盖该方法,进行uri的生成规则。
     *
     * @return api's uri
     */
    public String getUri() {
        return getExternalUri();
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
     * 获取参数串，
     * 先添加通用参数, 然后进行拼接参数。
     */
    protected String getParams(String sPara) {
        addCommonRequestParams(null);
        return formatParams(sPara);
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

    public HashMap<String, String> getPostParams() {
        return mPostParams;
    }

//    public int getOwnerId() {
//        return ownerId;
//    }
//
//    public void setOwnerId(int ownerId) {
//        this.ownerId = ownerId;
//    }

    /**
     * Add api parameter
     */
    public void addUrlParameter(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        params.put(key, value);
    }

    /**
     * Add api parameter if not exist valid value
     */
    public void addUrlParameterIfNecessary(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        if (this.params.containsKey(key) && !TextUtils.isEmpty(this.params.get(key))) {
            return;
        }
        params.put(key, value);
    }

    public void addPostParameter(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        this.mPostParams.put(key, value);
    }

    /**
     * Add api header
     */
    public void addRequestHeader(String header, String value) {
        this.headers.put(header, value);
    }

    public Map<String, String> getRequestHeader() {
        return headers;
    }

    /**
     * @return params
     */
    public ConcurrentMap<String, String> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "ApiBase{" + "baseUrl='" + baseUrl + '\'' + ", headers=" + headers + ", params="
                + params + '}';
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    protected CacheEntry mCacheEntry;

    public void setCacheEntry(CacheEntry cacheEntry) {
        mCacheEntry = cacheEntry;
    }

    public CacheEntry getCacheEntry() {
        return mCacheEntry;
    }

    public String getUriKey() {
        return MD5.hexdigest(getUri());
    }

    /**
     * 获取请求的key， 可以用来标志是否是同一个请求
     * @return
     */
    public String getRequestKey() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(baseUrl)
                .append("&&")
                .append((headers == null || headers.isEmpty()) ? "headers" : headers.toString())
                .append("&&")
                .append((params == null || params.isEmpty()) ? "params" : params.toString())
                .append("&&")
                .append((mPostParams == null || mPostParams.isEmpty()) ? "post_params" : mPostParams.toString());

        return stringBuilder.toString();
    }
}
