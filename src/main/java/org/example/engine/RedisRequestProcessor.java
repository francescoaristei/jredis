package org.example.engine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.database.RedisDatabase;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.RespInteger;
import org.example.resp_types.simple.RespNull;
import org.example.resp_types.simple.SimpleString;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RedisRequestProcessor {
    RedisDatabase redisDatabase;
    private static final Logger log = LogManager.getLogger(RedisRequestProcessor.class);

    public RedisRequestProcessor(RedisDatabase redisDatabase) {
        this.redisDatabase = redisDatabase;
    }

    public RespDataType processRequest(RespArray requestArray) {
        BulkString commandName = (BulkString) requestArray.getValue().get(0);
        switch(commandName.getValue()) {
            case "PING" -> {
                log.info("processing PING request.");
                return processPingRequest();
            }
            case "ECHO" -> {
                log.info("processing ECHO request.");
                return processEchoRequest(requestArray);
            }
            case "SET" -> {
                log.info("processing SET request.");
                return processSetRequest(requestArray);
            }
            case "SETEX" -> {
                log.info("processing SETEX request.");
                return processSetEXRequest(requestArray);
            }
            case "SETEAXT" -> {
                log.info("processing SETEAXT request.");
                return processSetEAXTRequest(requestArray);
            }
            case "PSETEX" -> {
                log.info("processing PSETEX request.");
                return processPSetEXRequest(requestArray);
            }
            case "GET" -> {
                log.info("processing GET request.");
                return processGetRequest(requestArray);
            }
            case "EXISTS" -> {
                log.info("processing EXISTS request.");
                return processExistsRequest(requestArray);
            }
            case "DEL" -> {
                log.info("processing DEL request.");
                return processDeleteRequest(requestArray);
            }
            case "INCR" -> {
                log.info("processing INCR request.");
                return processIncrRequest(requestArray);
            }
            case "DECR" -> {
                log.info("processing DECR request.");
                return processDecrRequest(requestArray);
            }
            case "LPUSH" -> {
                log.info("processing LPUSH request.");
                return processLpushRequest(requestArray);
            }
            case "RPUSH" -> {
                log.info("processing RPUSH request.");
                return processRpushRequest(requestArray);
            }
            case "LRANGE" -> {
                log.info("processing LRANGE request.");
                return processLrangeRequest(requestArray);
            }
            case "SAVE" -> {
                log.info("processing SAVE request.");
                return processSaveRequest();
            }
            default -> {
                return new SimpleError("Unknown Command"); }
        }
    }

    private RespDataType processGetRequest(RespArray requestArray) {
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

    private RespDataType processSetRequest(RespArray requestArray) {
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

    private RespDataType processSetEXRequest(RespArray requestArray) {
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

    private RespDataType processSetEAXTRequest(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            BulkString timer = (BulkString) requestArray.getValue().get(2);
            BulkString value = (BulkString) requestArray.getValue().get(3);
            redisDatabase.setValueForKeyWithEAXT(key.getValue(), value.getValue(), timer.getValue());
            response = new SimpleString("OK");
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Incomplete command: key, timer and value needed");
        }
        return response;
    }

    private RespDataType processPSetEXRequest(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            BulkString timer = (BulkString) requestArray.getValue().get(2);
            BulkString value = (BulkString) requestArray.getValue().get(3);
            redisDatabase.setValueForKeyWithPEX(key.getValue(), value.getValue(), timer.getValue());
            response = new SimpleString("OK");
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Incomplete command: key, timer and value needed");
        }
        return response;
    }

    private RespDataType processExistsRequest(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            response = new RespInteger(Integer.toString(redisDatabase.checkKey(key.getValue())));
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Missing key to check");
        }
        return response;
    }

    private RespDataType processDeleteRequest(RespArray requestArray) {
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

    private RespDataType processIncrRequest(RespArray requestArray) {
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

    private RespDataType processDecrRequest(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            Optional<String> value = redisDatabase.decrementValue(key.getValue());
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

    private RespDataType processLpushRequest(RespArray requestArray) {
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

    private RespDataType processRpushRequest(RespArray requestArray) {
        RespDataType response;
        try {
            BulkString key = (BulkString) requestArray.getValue().get(1);
            List<String> elementsToAdd = new ArrayList<>();
            for (int i = 2; i < requestArray.getLength(); i++) {
                elementsToAdd.add(requestArray.getValue().get(i).getValue());
            }
            Optional<String> value = redisDatabase.tailPushToList(key.getValue(), elementsToAdd);
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

    private RespDataType processLrangeRequest(RespArray requestArray) {
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

    private RespDataType processSaveRequest() {
        RespDataType response;
        boolean isSaved = redisDatabase.saveToDisk();
        if (isSaved) {
            response = new SimpleString("OK");
        } else {
            response = new SimpleError("ERR: database not saved to disk.");
        }
        return response;
    }


    private RespDataType processEchoRequest(RespArray requestArray) {
        RespDataType response;
        try {
            response = requestArray.getValue().get(1);
        } catch (IndexOutOfBoundsException e) {
            response = new SimpleError("Body of Echo request missing");
        }
        return response;
    }

    private RespDataType processPingRequest() {
        return new BulkString("PONG");
    }
}
