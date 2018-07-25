package com.violet.lib.opensource.qr.zixin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kan212 on 2018/7/9.
 */

public final class ReedSolomonEncoder {

    private final List<GenericGFPoly> cachedGenerators;
    private final GenericGF field;

    public ReedSolomonEncoder(GenericGF field) {
        this.field = field;
        this.cachedGenerators = new ArrayList<>();
        cachedGenerators.add(new GenericGFPoly(field, new int[]{1}));
    }

    private GenericGFPoly buildGenerator(int degree) {
        if (degree >= cachedGenerators.size()) {
            GenericGFPoly lastGenerator = cachedGenerators.get(cachedGenerators.size() - 1);
            for (int d = cachedGenerators.size(); d <= degree; d++) {
                GenericGFPoly nextGenerator = lastGenerator.multiply(
                        new GenericGFPoly(field, new int[] { 1, field.exp(d - 1 + field.getGeneratorBase()) }));
                cachedGenerators.add(nextGenerator);
                lastGenerator = nextGenerator;
            }
        }
        return cachedGenerators.get(degree);
    }

    public void encode(int[] toEncode, int ecBytes) {
        if (ecBytes == 0) {
            throw new IllegalArgumentException("No error correction bytes");
        }
        int dataBytes = toEncode.length - ecBytes;
        if (dataBytes <= 0) {
            throw new IllegalArgumentException("No data bytes provided");
        }
        GenericGFPoly generator = buildGenerator(ecBytes);
        int[] infoCoefficients = new int[dataBytes];
        System.arraycopy(toEncode, 0, infoCoefficients, 0, dataBytes);
        GenericGFPoly info = new GenericGFPoly(field, infoCoefficients);
        info = info.multiplyByMonomial(ecBytes, 1);
        GenericGFPoly remainder = info.divide(generator)[1];
        int[] coefficients = remainder.getCoefficients();
        int numZeroCoefficients = ecBytes - coefficients.length;
        for (int i = 0; i < numZeroCoefficients; i++) {
            toEncode[dataBytes + i] = 0;
        }
        System.arraycopy(coefficients, 0, toEncode, dataBytes + numZeroCoefficients, coefficients.length);
    }
}
