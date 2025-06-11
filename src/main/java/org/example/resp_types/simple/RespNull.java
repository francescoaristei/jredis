package org.example.resp_types.simple;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.resp_types.RespDataType;

public class RespNull implements RespDataType {
    private static final Logger log = LogManager.getLogger(RespNull.class);

    public RespNull() {
    }

    @Override
    public String getValue() {
        return "_";
    }

    @Override
    public StringBuilder serialize() {
        log.info("serialize RespNull.");
        return new StringBuilder()
                .append(this.getValue())
                .append("\r\n");
    }
}
