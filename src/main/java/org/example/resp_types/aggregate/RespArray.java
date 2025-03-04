package org.example.resp_types.aggregate;

import org.example.resp_types.RespDataType;
import java.util.ArrayList;
import java.util.List;

public class RespArray implements RespDataType {
    private final List<RespDataType> arrayOfElements;
    private boolean isNullRespArray = false;

    public RespArray() {
        this.arrayOfElements = new ArrayList<>();
    }

    @Override
    public List<RespDataType> getValue() {
        return this.arrayOfElements;
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
