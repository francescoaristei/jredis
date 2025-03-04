package org.example.resp_types.simple;

import org.example.resp_types.RespDataType;

public class SimpleString implements RespDataType {
    private final String value;

    public SimpleString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
