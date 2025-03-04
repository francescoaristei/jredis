package org.example.resp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.RespInteger;
import org.example.resp_types.simple.RespNull;
import org.example.resp_types.simple.SimpleString;

public class Serializer {
    private static final Logger log = LogManager.getLogger(Serializer.class);

    public Serializer() {}

    public StringBuilder serializeRespArray(RespArray respArrayToSerialize) {
        StringBuilder resultStringBuilder = new StringBuilder();
        if (respArrayToSerialize.isNull()) {
            return resultStringBuilder.append("*").append("-1").append("\r\n");
        }
        resultStringBuilder.append("*");
        resultStringBuilder.append(respArrayToSerialize.getValue().size());
        resultStringBuilder.append("\r\n");
        for(RespDataType respDataType: respArrayToSerialize.getValue()) {
            resultStringBuilder.append(serializeRespDataType(respDataType));
        }
        return resultStringBuilder;
    }

    public StringBuilder serializeSimpleString(SimpleString simpleStringToSerialize) {
        return new StringBuilder()
                .append("+")
                .append(simpleStringToSerialize.getValue())
                .append("\r\n");
    }

    public StringBuilder serializeSimpleError(SimpleError simpleErrorToSerialize) {
        return new StringBuilder()
                .append("-")
                .append(simpleErrorToSerialize.getValue())
                .append("\r\n");
    }

    public StringBuilder serializeRespInteger(RespInteger respIntegerToSerialize) {
        return new StringBuilder()
                .append(":")
                .append(respIntegerToSerialize.getValue())
                .append("\r\n");
    }

    public StringBuilder serializeBulkString(BulkString bulkStringToSerialize) {
        return new StringBuilder()
                .append("$")
                .append(bulkStringToSerialize.getValue().length())
                .append("\r\n")
                .append(bulkStringToSerialize.getValue())
                .append("\r\n");
    }

    public StringBuilder serializeRespNull(RespNull respNullToSerialize) {
        return new StringBuilder()
                .append(respNullToSerialize.getValue())
                .append("\r\n");
    }

    public StringBuilder serializeRespDataType(RespDataType respDataType) {
        StringBuilder serializedRespDataType = new StringBuilder();
        if (respDataType instanceof SimpleString) {
            log.info("serialize SimpleString.");
            serializedRespDataType.append(serializeSimpleString((SimpleString) respDataType));
        } else if (respDataType instanceof SimpleError) {
            log.info("serialize SimpleError.");
            serializedRespDataType.append(serializeSimpleError((SimpleError) respDataType));
        } else if (respDataType instanceof RespNull) {
            log.info("serialize RespNull.");
            serializedRespDataType.append(serializeRespNull((RespNull) respDataType));
        } else if (respDataType instanceof RespInteger) {
            log.info("serialize RespInteger.");
            serializedRespDataType.append(serializeRespInteger((RespInteger) respDataType));
        } else if(respDataType instanceof BulkString) {
            log.info("serialize BulkString.");
            serializedRespDataType.append(serializeBulkString((BulkString) respDataType));
        } else if (respDataType instanceof RespArray) {
            log.info("serialize RespArray.");
            serializedRespDataType.append(serializeRespArray((RespArray) respDataType));
        }
        return serializedRespDataType;
    }
}
