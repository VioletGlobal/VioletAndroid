package com.violet.dagger.model.api;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by kan212 on 2018/6/29.
 */

public class StringConverter implements Converter<ResponseBody,String>{
    @Override
    public String convert(ResponseBody value) throws IOException {
        return value.string();
    }
}
