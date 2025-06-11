package unit;

import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.RespInteger;
import org.example.resp_types.simple.RespNull;
import org.example.resp_types.simple.SimpleString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class RespSerializerServiceTest {

    @Nested
    class RespSerializerSimpleRespTypesTest {

        @Test
        public void serializeOnSimpleString() {
            String responseString = "+OK\r\n";
            SimpleString simpleStringToSerialize = new SimpleString("OK");
            assertEquals(responseString, simpleStringToSerialize.serialize().toString());
        }

        @Test
        public void serializeOnSimpleError() {
            String responseString = "-Invalid type char for Resp data type\r\n";
            SimpleError simpleErrorToSerialize = new SimpleError("Invalid type char for Resp data type");
            assertEquals(responseString, simpleErrorToSerialize.serialize().toString());
        }

        @Test
        public void serializeOnRespInteger() {
            String responseString = ":1000\r\n";
            RespInteger respIntegerToSerialize = new RespInteger("1000");
            assertEquals(responseString, respIntegerToSerialize.serialize().toString());
        }

        @Test
        public void serializeOnBulkString() {
            String responseString = "$10\r\nhelloworld\r\n";
            BulkString bulkStringToSerialize = new BulkString("helloworld");
            assertEquals(responseString, bulkStringToSerialize.serialize().toString());
        }

        @Test
        public void serializeOnRespNull() {
            String responseString = "_\r\n";
            RespNull respNullToSerialize = new RespNull();
            assertEquals(responseString, respNullToSerialize.serialize().toString());
        }
    }

    @Nested
    class RespSerializerAggregateRespTypesTest {
        @Test
        public void serializeOnRespArray() {
            String responseString = "*2\r\n$5\r\nhello\r\n$5\r\nworld\r\n";
            RespArray respArrayToSerialize = new RespArray();
            respArrayToSerialize.addElement(new BulkString("hello"));
            respArrayToSerialize.addElement(new BulkString("world"));
            assertEquals(responseString, respArrayToSerialize.serialize().toString());
        }

        @Test
        public void serializeOnEmptyArray() {
            String responseString = "*0\r\n";
            RespArray respArrayToSerialize = new RespArray();
            assertEquals(responseString, respArrayToSerialize.serialize().toString());
        }

        @Test
        public void serializeOnArrayOfMixedRespDataTypes() {
            String responseString = "*2\r\n*3\r\n:1\r\n:2\r\n:3\r\n*2\r\n+Hello\r\n-Error\r\n";
            RespArray respArrayToSerialize = new RespArray();
            RespArray firstRespArray = new RespArray();
            firstRespArray.addElement(new RespInteger("1"));
            firstRespArray.addElement(new RespInteger("2"));
            firstRespArray.addElement(new RespInteger("3"));
            RespArray secondRespArray = new RespArray();
            secondRespArray.addElement(new SimpleString("Hello"));
            secondRespArray.addElement(new SimpleError("Error"));
            respArrayToSerialize.addElement(firstRespArray);
            respArrayToSerialize.addElement(secondRespArray);
            assertEquals(responseString, respArrayToSerialize.serialize().toString());
        }

        @Test
        public void serializeOnNullArray() {
            String responseString = "*-1\r\n";
            RespArray respArrayToSerialize = new RespArray();
            respArrayToSerialize.setNull();
            assertEquals(responseString, respArrayToSerialize.serialize().toString());
        }
    }
}
