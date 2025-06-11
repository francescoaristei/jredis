package org.example.engine.commands;

import org.example.database.RedisDatabase;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.RespInteger;

public class ExistsCommand implements RedisCommand{
    private final RedisDatabase redisDatabase;

    public ExistsCommand(RedisDatabase redisDatabase) {
        this.redisDatabase = redisDatabase;
    }
    @Override
    public RespDataType processCommand(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            response = new RespInteger(Integer.toString(redisDatabase.checkKey(key.getValue())));
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Missing key to check");
        }
        return response;
    }
}
