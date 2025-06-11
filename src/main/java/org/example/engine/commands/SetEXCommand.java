package org.example.engine.commands;

import org.example.database.RedisDatabase;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.SimpleString;

public class SetEXCommand implements RedisCommand{
    private final RedisDatabase redisDatabase;

    public SetEXCommand(RedisDatabase redisDatabase) {
        this.redisDatabase = redisDatabase;
    }
    @Override
    public RespDataType processCommand(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            BulkString timer = (BulkString) requestArray.getValue().get(2);
            BulkString value = (BulkString) requestArray.getValue().get(3);
            redisDatabase.setValueForKeyWithEX(key.getValue(), value.getValue(), timer.getValue());
            response = new SimpleString("OK");
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Incomplete command: key, timer and value needed");
        }
        return response;
    }
}
