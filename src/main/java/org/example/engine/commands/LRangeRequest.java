package org.example.engine.commands;

import org.example.database.RedisDatabase;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.RespNull;

import java.util.List;
import java.util.Optional;

public class LRangeRequest implements RedisCommand{
    private final RedisDatabase redisDatabase;

    public LRangeRequest(RedisDatabase redisDatabase) {
        this.redisDatabase = redisDatabase;
    }
    @Override
    public RespDataType processCommand(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            BulkString startIndex = (BulkString) requestArray.getValue().get(2);
            BulkString endIndex = (BulkString) requestArray.getValue().get(3);
            int startIndexInt = Integer.parseInt(startIndex.getValue());
            int endIndexInt = Integer.parseInt(endIndex.getValue());
            Optional<List<String>> value = redisDatabase.getListElements(key.getValue(), startIndexInt, endIndexInt);
            if (value.isEmpty()) {
                response = new RespNull();
            } else {
                RespArray responseArray = new RespArray();
                for (String str: value.get()) {
                    responseArray.addElement(new BulkString(str));
                }
                response = responseArray;
            }
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Missing key to check or elements to add.");
        }
        return response;
    }
}
