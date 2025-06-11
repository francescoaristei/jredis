package org.example.resp_types;

public interface RespDataType {
    <T> T getValue();
    StringBuilder serialize();
}
