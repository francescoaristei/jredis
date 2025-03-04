package org.example.resp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.exceptions.IncorrectSyntax;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import java.io.BufferedReader;
import java.io.IOException;

public class Deserializer {
    private static final Logger log = LogManager.getLogger(Deserializer.class);

    public RespDataType deserializeRequest(BufferedReader bufferedReader) throws IOException {
        RespArray requestArray;
        try {
            requestArray = deserializeRespArray(bufferedReader);
        } catch (IncorrectSyntax e) {
            return new SimpleError(String.format("ERR: %s", e.getMessage()));
        }
        return requestArray;
    }

    private RespArray deserializeRespArray (BufferedReader bufferedReader) throws IOException, IncorrectSyntax {
        int arrayLength = checkLength(bufferedReader);
        RespArray resultArray = new RespArray();
        while (--arrayLength >= 0) {
            resultArray.addElement(deserializeBulkString(bufferedReader));
        }
        return resultArray;
    }

    private BulkString deserializeBulkString (BufferedReader bufferedReader) throws IOException, IncorrectSyntax {
        int isBulkString = bufferedReader.read();
        checkType('$', (char)isBulkString);
        int byteLength = checkLength(bufferedReader);
        return new BulkString(deserializeStringBody(bufferedReader, byteLength).toString());
    }

    private StringBuilder deserializeStringBody(BufferedReader bufferedReader, int byteLength) throws IOException, IncorrectSyntax {
        int byteValue;
        StringBuilder bulkStringBody = new StringBuilder();
        while(--byteLength >= 0 && (byteValue = bufferedReader.read()) >= 0) {
            bulkStringBody.append((char)byteValue);
        }
        if (((char)bufferedReader.read()) != '\r' || ((char)bufferedReader.read()) != '\n') {
            log.error("Missing CRLF after data.");
            throw new IncorrectSyntax("Missing CRLF after data.");
        }
        return bulkStringBody;
    }

    private void checkType(char expectedType, char actualType) throws IncorrectSyntax {
        if (actualType != expectedType) {
            log.error("Invalid type char for Resp data type.");
            throw new IncorrectSyntax("Invalid type char for Resp data type.");
        }
    }

    private int checkLength(BufferedReader bufferedReader) throws IOException, IncorrectSyntax {
        char digitOfLength;
        StringBuilder lengthByteValue = new StringBuilder();
        while (Character.isDigit(digitOfLength = (char)bufferedReader.read())) {
            lengthByteValue.append(digitOfLength);
        }
        if (lengthByteValue.isEmpty()) {
            log.error("Missing length of data.");
            throw new IncorrectSyntax("Missing length of data.");
        }
        char cr = digitOfLength;
        char lf = (char)bufferedReader.read();
        if (cr != '\r' || lf != '\n') {
            log.error("Missing CRLF before data.");
            throw new IncorrectSyntax("Missing CRLF before data.");
        }
        return Integer.parseInt(String.valueOf(lengthByteValue));
    }
}
