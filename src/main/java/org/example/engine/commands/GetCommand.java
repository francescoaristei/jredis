package org.example.engine.commands;

import org.example.database.RedisDatabase;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.RespNull;
import org.example.resp_types.simple.SimpleString;

import java.util.Optional;

public class GetCommand implements RedisCommand{
    private final RedisDatabase redisDatabase;

    public GetCommand(RedisDatabase redisDatabase) {
        this.redisDatabase = redisDatabase;
    }
    @Override
    public RespDataType processCommand(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            Optional<Object> value = redisDatabase.getValueForKey(key.getValue());
            if (value.isEmpty()) {
                response = new RespNull();
            } else if (!(value.get() instanceof String)){
                response = new SimpleError("GET can only retrieve strings.");
            } else {
                response = new SimpleString((String) value.get());
            }
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Key missing");
        }
        return response;
    }
}
