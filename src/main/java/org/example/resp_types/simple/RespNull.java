package org.example.resp_types.simple;

import org.example.resp_types.RespDataType;

public class RespNull implements RespDataType {

    public RespNull() {
    }

    @Override
    public String getValue() {
        return "_";
    }
}
