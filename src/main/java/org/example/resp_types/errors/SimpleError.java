package org.example.resp_types.errors;

import org.example.resp_types.RespDataType;

public class SimpleError implements RespDataType {
    private final String value;

    public SimpleError(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
