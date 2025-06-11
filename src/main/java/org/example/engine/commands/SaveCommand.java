package org.example.engine.commands;

import org.example.database.RedisDatabase;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.SimpleString;

public class SaveCommand implements RedisCommand{
    private final RedisDatabase redisDatabase;

    public SaveCommand(RedisDatabase redisDatabase) {
        this.redisDatabase = redisDatabase;
    }
    @Override
    public RespDataType processCommand(RespArray requestArray) {
        RespDataType response;
        boolean isSaved = redisDatabase.saveToDisk();
        if (isSaved) {
            response = new SimpleString("OK");
        } else {
            response = new SimpleError("ERR: database not saved to disk.");
        }
        return response;
    }
}
