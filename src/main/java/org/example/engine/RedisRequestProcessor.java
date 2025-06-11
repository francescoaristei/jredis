package org.example.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.RedisDatabase;
import org.example.engine.commands.*;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;

public class RedisRequestProcessor {
    private final RedisDatabase redisDatabase;
    private static final Logger log = LogManager.getLogger(RedisRequestProcessor.class);

    public RedisRequestProcessor(RedisDatabase redisDatabase) {
        this.redisDatabase = redisDatabase;
    }

    public RespDataType processRequest(RespArray requestArray) {
        BulkString commandName = (BulkString) requestArray.getValue().get(0);
        switch(commandName.getValue()) {
            case "PING" -> {
                log.info("processing PING request.");
                return new PingCommand().processCommand(requestArray);
            }
            case "ECHO" -> {
                log.info("processing ECHO request.");
                return new EchoCommand().processCommand(requestArray);
            }
            case "SET" -> {
                log.info("processing SET request.");
                return new SetCommand(redisDatabase).processCommand(requestArray);
            }
            case "SETEX" -> {
                log.info("processing SETEX request.");
                return new SetEXCommand(redisDatabase).processCommand(requestArray);
            }
            case "SETEAXT" -> {
                log.info("processing SETEAXT request.");
                return new SetEAXTCommand(redisDatabase).processCommand(requestArray);
            }
            case "PSETEX" -> {
                log.info("processing PSETEX request.");
                return new PSetEXCommand(redisDatabase).processCommand(requestArray);
            }
            case "GET" -> {
                log.info("processing GET request.");
                return new GetCommand(redisDatabase).processCommand(requestArray);
            }
            case "EXISTS" -> {
                log.info("processing EXISTS request.");
                return new ExistsCommand(redisDatabase).processCommand(requestArray);
            }
            case "DEL" -> {
                log.info("processing DEL request.");
                return new DeleteCommand(redisDatabase).processCommand(requestArray);
            }
            case "INCR" -> {
                log.info("processing INCR request.");
                return new IncrementCommand(redisDatabase).processCommand(requestArray);
            }
            case "DECR" -> {
                log.info("processing DECR request.");
                return new DecrementCommand(redisDatabase).processCommand(requestArray);
            }
            case "LPUSH" -> {
                log.info("processing LPUSH request.");
                return new LPushCommand(redisDatabase).processCommand(requestArray);
            }
            case "RPUSH" -> {
                log.info("processing RPUSH request.");
                return new RPushCommand(redisDatabase).processCommand(requestArray);
            }
            case "LRANGE" -> {
                log.info("processing LRANGE request.");
                return new LRangeRequest(redisDatabase).processCommand(requestArray);
            }
            case "SAVE" -> {
                log.info("processing SAVE request.");
                return new SaveCommand(redisDatabase).processCommand(requestArray);
            }
            default -> {
                return new SimpleError("Unknown Command"); }
        }
    }
}
