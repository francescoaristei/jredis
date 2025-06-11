package org.example.engine.commands;

import org.example.database.RedisDatabase;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.RespInteger;
import org.example.resp_types.simple.RespNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LPushCommand implements RedisCommand{
    private final RedisDatabase redisDatabase;

    public LPushCommand(RedisDatabase redisDatabase) {
        this.redisDatabase = redisDatabase;
    }
    @Override
    public RespDataType processCommand(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            List<String> elementsToAdd = new ArrayList<>();
            for (int i = 2; i < requestArray.getLength(); i++) {
                elementsToAdd.add(requestArray.getValue().get(i).getValue());
            }
            Optional<String> value = redisDatabase.headPushToList(key.getValue(), elementsToAdd);
            if (value.isEmpty()) {
                response = new RespNull();
            } else if(value.get().equals("NAL")) {
                response = new SimpleError("Accessed value is not a list.");
            } else {
                response = new RespInteger(value.get());
            }
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Missing key to check or elements to add.");
        }
        return response;
    }
}
