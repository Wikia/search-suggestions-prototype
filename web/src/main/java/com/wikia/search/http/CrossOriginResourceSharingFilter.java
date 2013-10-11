package com.wikia.search.http;

import org.apache.cxf.jaxrs.ext.ResponseHandler;
import org.apache.cxf.jaxrs.model.OperationResourceInfo;
import org.apache.cxf.message.Message;

import javax.ws.rs.core.Response;

public class CrossOriginResourceSharingFilter implements ResponseHandler {

    @Override
    public Response handleResponse(Message message, OperationResourceInfo operationResourceInfo, Response response) {
        response.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        return response;
    }
}
