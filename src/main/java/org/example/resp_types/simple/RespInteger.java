package org.example.resp_types.simple;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.resp_types.RespDataType;

public class RespInteger implements RespDataType {
    private static final Logger log = LogManager.getLogger(RespInteger.class);
    private final String value;

    public RespInteger(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public StringBuilder serialize() {
        log.info("serialize RespInteger.");
        return new StringBuilder()
                .append(":")
                .append(this.getValue())
                .append("\r\n");
    }
}