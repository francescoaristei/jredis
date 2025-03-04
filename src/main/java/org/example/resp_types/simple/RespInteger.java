package org.example.resp_types.simple;

import org.example.resp_types.RespDataType;

public class RespInteger implements RespDataType {
    private final String value;

    public RespInteger(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}