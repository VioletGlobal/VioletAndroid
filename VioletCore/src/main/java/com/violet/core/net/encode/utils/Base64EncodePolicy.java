package com.violet.core.net.encode.utils;


import android.util.Base64;


/**
 */

public class Base64EncodePolicy implements  ApiConfigController.ITransportPolicy {

    @Override
    public String transportEncode(byte[] src) {
        final byte[] encoded = Base64.encode(src, Base64.NO_WRAP | Base64.URL_SAFE);
        return new String(encoded);
    }
}
