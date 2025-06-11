package org.example.resp_types.aggregate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.resp_types.RespDataType;
import java.util.ArrayList;
import java.util.List;

public class RespArray implements RespDataType {
    private static final Logger log = LogManager.getLogger(RespArray.class);
    private final List<RespDataType> arrayOfElements;
    private boolean isNullRespArray = false;

    public RespArray() {
        this.arrayOfElements = new ArrayList<>();
    }

    @Override
    public List<RespDataType> getValue() {
        return this.arrayOfElements;
    }

    @Override
    public StringBuilder serialize() {
        log.info("serialize RespArray.");
        StringBuilder resultStringBuilder = new StringBuilder();
        if (this.isNull()) {
            return resultStringBuilder.append("*").append("-1").append("\r\n");
        }
        resultStringBuilder.append("*");
        resultStringBuilder.append(this.getValue().size());
        resultStringBuilder.append("\r\n");
        for(RespDataType respDataType: this.getValue()) {
            resultStringBuilder.append(respDataType.serialize());
        }
        return resultStringBuilder;
    }

    public int getLength() {
        return this.arrayOfElements.size();
    }

    public void addElement(RespDataType element) {
        this.arrayOfElements.add(element);
    }

    public void setNull() {
        this.isNullRespArray = true;
    }

    public boolean isNull() {
        return this.isNullRespArray;
    }
}
