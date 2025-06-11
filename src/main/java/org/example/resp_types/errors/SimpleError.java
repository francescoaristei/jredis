package org.example.resp_types.errors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.resp_types.RespDataType;

public class SimpleError implements RespDataType {
    private final String value;
    private static final Logger log = LogManager.getLogger(SimpleError.class);


    public SimpleError(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public StringBuilder serialize() {
        log.info("serialize SimpleError.");
        return new StringBuilder()
                .append("-")
                .append(this.getValue())
                .append("\r\n");
    }
}
