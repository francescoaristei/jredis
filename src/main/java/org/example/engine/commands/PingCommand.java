package org.example.engine.commands;

import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;

public class PingCommand implements RedisCommand{
    @Override
    public RespDataType processCommand(RespArray requestArray) {
        return new BulkString("PONG");
    }
}
