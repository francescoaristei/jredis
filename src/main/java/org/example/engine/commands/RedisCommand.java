package org.example.engine.commands;

import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;

public interface RedisCommand {
    RespDataType processCommand(RespArray requestArray);
}
