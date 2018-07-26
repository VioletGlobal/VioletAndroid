package com.violet.net.http.request;


import com.violet.net.http.model.HttpMethod;

public class GetRequest extends Request<GetRequest> {

    public GetRequest(String url) {
        super(url);
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.GET;
    }

}
