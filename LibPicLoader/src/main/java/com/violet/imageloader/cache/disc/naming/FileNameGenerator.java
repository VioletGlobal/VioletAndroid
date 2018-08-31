package com.violet.imageloader.cache.disc.naming;

/**
 * Created by kan212 on 2018/8/29.
 */

public interface FileNameGenerator {
    /** Generates unique file name for image defined by URI */
    String generate(String imageUri);
}
