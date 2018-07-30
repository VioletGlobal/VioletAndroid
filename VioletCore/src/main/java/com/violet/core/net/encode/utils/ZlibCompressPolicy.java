package com.violet.core.net.encode.utils;


import com.violet.core.util.ZipUtils;

/**
 * Created by shixi_tianrui1 on 17-3-29.
 */

public class ZlibCompressPolicy implements ApiConfigController.ICompressPolicy {
    @Override
    public byte[] compress(byte[] src) {
        return ZipUtils.compress(src);
    }
}
