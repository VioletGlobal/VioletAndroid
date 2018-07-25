package com.violet.lib.opensource.qr.zixin;

/**
 * Created by kan212 on 2018/7/9.
 */

public final class BlockPair {

    private final byte[] dataBytes;
    private final byte[] errorCorrectionBytes;

    BlockPair(byte[] data, byte[] errorCorrection) {
        dataBytes = data;
        errorCorrectionBytes = errorCorrection;
    }

    public byte[] getDataBytes() {
        return dataBytes;
    }

    public byte[] getErrorCorrectionBytes() {
        return errorCorrectionBytes;
    }
}
