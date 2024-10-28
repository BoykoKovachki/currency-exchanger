package com.example.currencyexchanger.models;

import java.util.HashMap;

public class ResponseModel {

    public enum ResponseCode {

        CODE200(200), // Success
        CODE1102(1102), // Not connected to internet
        CODE1103(1103); // Error performing request

        private final int intValue;

        ResponseCode(int intValue) {
            this.intValue = intValue;
        }

        public static ResponseCode fromInt(int intValue) {
            switch (intValue) {
                case 200:
                    return CODE200;
                case 1102:
                    return CODE1102;
                default:
                    return CODE1103;
            }
        }
    }

    public ResponseModel() {
    }

    public ResponseModel(ResponseCode code) {
        this.code = code;
    }

    private ResponseCode code;
    private HashMap<String, Object> data;

    public ResponseCode getCode() {
        return code;
    }

    public void setCode(ResponseCode code) {
        this.code = code;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

}
