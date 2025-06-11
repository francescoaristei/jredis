package org.example.engine.commands;

import org.example.database.RedisDatabase;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.SimpleString;

public class SetCommand implements RedisCommand{
    private final RedisDatabase redisDatabase;

    public SetCommand(RedisDatabase redisDatabase) {
        this.redisDatabase = redisDatabase;
    }
    @Override
    public RespDataType processCommand(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            BulkString value = (BulkString) requestArray.getValue().get(2);
            redisDatabase.setValueForKey(key.getValue(), value.getValue());
            response = new SimpleString("OK");
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Key or Value missing");
        }
        return response;
    }
}
