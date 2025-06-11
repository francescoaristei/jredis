package org.example.engine.commands;

import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.errors.SimpleError;

public class EchoCommand implements RedisCommand{
    @Override
    public RespDataType processCommand(RespArray requestArray) {
        RespDataType response;
        try {
            response = requestArray.getValue().get(1);
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Body of Echo request missing");
        }
        return response;
    }
}
