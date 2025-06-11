package org.example.resp_types.bulk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.resp_types.RespDataType;

public class BulkString implements RespDataType {
    private static final Logger log = LogManager.getLogger(BulkString.class);
    private final String value;

    public BulkString(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public StringBuilder serialize() {
        log.info("serialize BulkString.");
        return new StringBuilder()
                .append("$")
                .append(this.getValue().length())
                .append("\r\n")
                .append(this.getValue())
                .append("\r\n");
    }
}
