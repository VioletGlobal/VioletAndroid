package com.violet.core.net.encode.utils;

import android.support.annotation.NonNull;

import com.violet.core.util.SNTextUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kan212 on 2018/7/27.
 */

public class ApiConfigController {

    private static final String ENCODED_PARAMS_HEAD = "%s/?r=%s";

    private final Map<String, String> mParamsMap = new LinkedHashMap<>();
    private final boolean mIsApiEncodeSwitchOn = isApiEncodeSwitchOn();

    // 默认编码及压缩方式
    private IEncryptPolicy mEncryptPolicy = new AESEncryptPolicy();
    private ICompressPolicy mCompressPolicy = new ZlibCompressPolicy();
    private ITransportPolicy mTransportPolicy = new Base64EncodePolicy();


    /**
     * 是否启用Api编码压缩流程
     */
    private boolean isApiEncodeSwitchOn() {
        return false;
//        // 配置工具控制
//        final boolean isDisableInConfig = DebugConfig.getInstance().isDisableUrlDecodeInConfig();
//        if (isDisableInConfig){
//            return false;
//        }
//        // 接口配置项控制
//        final int flag = SharedPreferenceUtils.getUrlEncryptFlag();
//        return (flag == UrlEncryptFlagStatus.ENABLE);
    }

    public ApiConfigController() {
    }


    /**
     * 对Url进行编码替换,
     *
     * @param host      主机地址
     * @param originUrl 原始Url
     * @return encodedUrl
     */
    public String encodeRequestUrl(@NonNull String host, @NonNull String originUrl) {
        if (!mIsApiEncodeSwitchOn) {
            return originUrl;
        }
        try {
            final long start = System.currentTimeMillis();
            final StringBuilder totalParams = new StringBuilder();

            // 拼接参数串
            for (Map.Entry<String, String> entry : mParamsMap.entrySet()) {
                final String kParam = entry.getKey();
                final String vParam = entry.getValue();
                if (SNTextUtils.isEmpty(kParam)) {
                    continue;
                }
                totalParams.append(String.format("&%s=%s", kParam, vParam));
            }
            totalParams.deleteCharAt(0);
            //Log.d("<ACC> appendParams = %s", totalParams.toString());
            //1. 加密
            //final byte[] encrypted = mEncryptPolicy.encrypt("MYgGnQE2jDFADSFFDSEWsD", totalParams.toString());
            //Log.d("tianrui2 encrypted= %s", new String(encrypted));
            //2. 压缩
            final byte[] compressed = mCompressPolicy.compress(totalParams.toString().getBytes());
            //3. 编码
            final String encodedParams = mTransportPolicy.transportEncode(compressed);

            final String encodedUrl = String.format(ENCODED_PARAMS_HEAD, host, encodedParams);

            final long delta = System.currentTimeMillis();
            return encodedUrl;
        } catch (Exception e) {
            return originUrl;
        }
    }


    /**
     * 添加GET params参数
     */
    public void addRequestParams(final @NonNull String key, final @NonNull String value) {
        if (!mIsApiEncodeSwitchOn) {
            return;
        }
        final String encoded = getApiParamsEncode(key);
        mParamsMap.put(encoded, value);
    }

    /**
     * 配置文件中存在api编码值时返回编码值,否则返回原值.
     */
    private String getApiParamsEncode(@NonNull String api) {
//        final String encode = ApiEncodeMap.getApiEncode(api);
//        if (!SNTextUtils.isEmpty(encode)) {
//            return encode;
//        }
        return api;
    }


    /**
     * 加密
     */
    public interface IEncryptPolicy {
        byte[] encrypt(String key, String src);
    }

    /**
     * 传输编码
     */
    public interface ITransportPolicy {
        String transportEncode(byte[] src);
    }

    /**
     * 压缩
     */
    public interface ICompressPolicy {
        byte[] compress(byte[] src);
    }

    //---------Utils----------//

    public interface UrlEncryptFlagStatus {
        int ENABLE = 1;
        int DISABLE = 0;
    }
}
