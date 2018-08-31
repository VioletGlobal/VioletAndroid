package com.violet.imageloader.cache.disc.naming;

/**
 * Created by kan212 on 2018/8/29.
 * 以 uri 的 hashCode 作为文件名。
 */

public class HashCodeFileNameGenerator implements FileNameGenerator {
    @Override
    public String generate(String imageUri) {
        return String.valueOf(imageUri.hashCode());
    }
}
