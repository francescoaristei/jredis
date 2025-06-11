package org.example.resp_types.simple;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.resp_types.RespDataType;

public class SimpleString implements RespDataType {
    private static final Logger log = LogManager.getLogger(SimpleString.class);
    private final String value;

    public SimpleString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public StringBuilder serialize() {
        log.info("serialize SimpleString.");
        return new StringBuilder()
                .append("+")
                .append(this.getValue())
                .append("\r\n");
    }
}
