package com.gans.vk.dashboard.controller.model;

public class ResponseStatus {

    public static final ResponseStatus OK = new ResponseStatus("ok");

    private String _status;

    public ResponseStatus(String status) {
        _status = status;
    }

    public String getStatus() {
        return _status;
    }

    public void setStatus(String status) {
        _status = status;
    }

}
