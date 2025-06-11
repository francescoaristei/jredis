package org.example.engine.commands;

import org.example.database.RedisDatabase;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.RespInteger;
import org.example.resp_types.simple.RespNull;

import java.util.Optional;

public class IncrementCommand implements RedisCommand{
    private final RedisDatabase redisDatabase;

    public IncrementCommand(RedisDatabase redisDatabase) {
        this.redisDatabase = redisDatabase;
    }
    @Override
    public RespDataType processCommand(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            Optional<String> value = redisDatabase.incrementValue(key.getValue());
            if (value.isEmpty()) {
                response = new RespNull();
            } else if (value.get().equals("NPS")){
                response = new SimpleError("Accessed value is not a valid number.");
            } else if(value.get().equals("NAS")) {
                response = new SimpleError("Accessed value is not a string.");
            } else {
                response = new RespInteger(value.get());
            }
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Missing key to check.");
        }
        return response;
    }
}
