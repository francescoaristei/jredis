package org.example.engine.commands;

import org.example.database.RedisDatabase;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.RespInteger;

import java.util.ArrayList;
import java.util.List;

public class DeleteCommand implements RedisCommand{
    private final RedisDatabase redisDatabase;

    public DeleteCommand(RedisDatabase redisDatabase) {
        this.redisDatabase = redisDatabase;
    }
    @Override
    public RespDataType processCommand(RespArray requestArray) {
        RespDataType response;
        try {
            List<String> keysToDelete = new ArrayList<>();
            for (int i = 1; i < requestArray.getLength(); i++) {
                keysToDelete.add(requestArray.getValue().get(i).getValue());
            }
            response = new RespInteger(Integer.toString(redisDatabase.deleteKeys(keysToDelete)));
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Missing key to check");
        }
        return response;
    }
}
