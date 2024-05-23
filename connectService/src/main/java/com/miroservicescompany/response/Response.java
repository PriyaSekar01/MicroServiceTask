package com.miroservicescompany.response;

import java.util.List;


@lombok.Data
public class Response {

	private Data data;
    private Error error;
    private String timeStamp;
    private String message;
    private List<String> errorMessages;
}
