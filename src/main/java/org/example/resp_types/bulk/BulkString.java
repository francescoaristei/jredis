package org.example.resp_types.bulk;

import org.example.resp_types.RespDataType;

public class BulkString implements RespDataType {
    private final String value;

    public BulkString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
