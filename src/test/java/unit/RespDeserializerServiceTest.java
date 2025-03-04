package unit;

import org.example.resp.Deserializer;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

public class RespDeserializerServiceTest {
    private final Deserializer deserializer = new Deserializer();

    @Test
    public void deserializeOnSimpleCorrectRequest() {
        // setup
        String clientRequest = "1\r\n$4\r\nPING\r\n";
        BulkString resultBulkString = new BulkString("PING");
        RespArray resultArray = new RespArray();
        resultArray.addElement(resultBulkString);

        try (BufferedReader clientRequestBufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(clientRequest.getBytes(StandardCharsets.UTF_8))));) {
            // act
            RespArray deserializedRequest = (RespArray) deserializer.deserializeRequest(clientRequestBufferedReader);

            // assert
            String expectedBulkString = resultArray.getValue().get(0).getValue();
            String actualBulkString = deserializedRequest.getValue().get(0).getValue();
            assertEquals(expectedBulkString, actualBulkString);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void deserializeOnComplexCorrectRequest() {
        // setup
        String clientRequest = "6\r\n$5\r\nHMSET\r\n$6\r\nmyhash\r\n$4\r\nname\r\n$4\r\nJohn\r\n$3\r\nage\r\n$2\r\n30\r\n";
        RespArray resultArray = new RespArray();
        resultArray.addElement(new BulkString("HMSET"));
        resultArray.addElement(new BulkString("myhash"));
        resultArray.addElement(new BulkString("name"));
        resultArray.addElement(new BulkString("John"));
        resultArray.addElement(new BulkString("age"));
        resultArray.addElement(new BulkString("30"));

        try (BufferedReader clientRequestBufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(clientRequest.getBytes(StandardCharsets.UTF_8))));) {
            // act
            RespArray deserializedRequest = (RespArray) deserializer.deserializeRequest(clientRequestBufferedReader);

            // assert
            // change to iterate over
            String expectedBulkString = resultArray.getValue().get(0).getValue();
            String actualBulkString = deserializedRequest.getValue().get(0).getValue();
            assertEquals(expectedBulkString, actualBulkString);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void incorrectSyntaxExceptionThrownOnInvalidLengthOnArray() {
        // setup
        String incorrectClientRequest = "c\r\n$4\r\nPING\r\n";
        BulkString resultBulkString = new BulkString("PING");
        RespArray resultArray = new RespArray();
        resultArray.addElement(resultBulkString);

        try (BufferedReader clientRequestBufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(incorrectClientRequest.getBytes(StandardCharsets.UTF_8))));) {
            // act + assert
            RespDataType unknownRespCommand = deserializer.deserializeRequest(clientRequestBufferedReader);

            // assert
            String exceptionMessage = "ERR: Missing length of data.";
            assertEquals(exceptionMessage, unknownRespCommand.getValue());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void incorrectSyntaxExceptionThrownOnMissingCRLFBeforeData() {
        // setup
        String incorrectClientRequest = "1\n$4\r\nPING\r\n";
        BulkString resultBulkString = new BulkString("PING");
        RespArray resultArray = new RespArray();
        resultArray.addElement(resultBulkString);

        try (BufferedReader clientRequestBufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(incorrectClientRequest.getBytes(StandardCharsets.UTF_8))));) {
            // act + assert
            RespDataType unknownRespCommand = deserializer.deserializeRequest(clientRequestBufferedReader);

            // assert
            String exceptionMessage = "ERR: Missing CRLF before data.";
            assertEquals(exceptionMessage, unknownRespCommand.getValue());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void incorrectSyntaxExceptionThrownOnBulkStringMissingCRLFAfterData() {
        // setup
        String incorrectClientRequest = "1\r\n$4\r\nPING\n";
        BulkString resultBulkString = new BulkString("PING");
        RespArray resultArray = new RespArray();
        resultArray.addElement(resultBulkString);

        try (BufferedReader clientRequestBufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(incorrectClientRequest.getBytes(StandardCharsets.UTF_8))));) {
            // act + assert
            RespDataType unknownRespCommand = deserializer.deserializeRequest(clientRequestBufferedReader);

            // assert
            String exceptionMessage = "ERR: Missing CRLF after data.";
            assertEquals(exceptionMessage, unknownRespCommand.getValue());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}