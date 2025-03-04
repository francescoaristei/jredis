package unit;

import org.example.database.RedisDatabase;
import org.example.engine.RedisRequestProcessor;
import org.example.resp_types.RespDataType;
import org.example.resp_types.aggregate.RespArray;
import org.example.resp_types.bulk.BulkString;
import org.example.resp_types.errors.SimpleError;
import org.example.resp_types.simple.RespInteger;
import org.example.resp_types.simple.RespNull;
import org.example.resp_types.simple.SimpleString;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class RedisResponseGeneratorServiceTest {
    private final RedisDatabase mockedRedisDatabase = Mockito.mock(RedisDatabase.class);
    private final RedisRequestProcessor redisRequestProcessor = new RedisRequestProcessor(mockedRedisDatabase);


    @Test
    public void generateResponseOnPINGRequest() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString pingRequest = new BulkString("PING");
        requestArray.addElement(pingRequest);
        BulkString expectedResponse = new BulkString("PONG");

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnEchoRequest() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString echoRequest = new BulkString("ECHO");
        BulkString echoBody = new BulkString("Hello World");
        requestArray.addElement(echoRequest);
        requestArray.addElement(echoBody);
        BulkString expectedResponse = new BulkString("Hello World");


        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnEchoRequestWithMissingBody() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString echoRequest = new BulkString("ECHO");
        requestArray.addElement(echoRequest);
        SimpleError expectedResponse = new SimpleError("Body of Echo request missing");


        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnSetRequest() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString setRequest = new BulkString("SET");
        BulkString key = new BulkString("key_test_set");
        BulkString value = new BulkString("value_test");
        requestArray.addElement(setRequest);
        requestArray.addElement(key);
        requestArray.addElement(value);
        SimpleString expectedResponse = new SimpleString("OK");

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnSetRequestWithMissingKeyOrValue() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString setRequest = new BulkString("SET");
        BulkString value = new BulkString("value_test");
        requestArray.addElement(setRequest);
        requestArray.addElement(value);
        SimpleError expectedResponse = new SimpleError("Key or Value missing");

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnGetRequest() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString getRequest = new BulkString("GET");
        BulkString key = new BulkString("key_test_get");
        requestArray.addElement(getRequest);
        requestArray.addElement(key);
        SimpleString expectedResponse = new SimpleString("value_test");
        when(mockedRedisDatabase.getValueForKey(key.getValue())).thenReturn(Optional.of("value_test"));

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnGetRequestWithNonExistentKey() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString getRequest = new BulkString("GET");
        BulkString key = new BulkString("missing_key");
        requestArray.addElement(getRequest);
        requestArray.addElement(key);
        RespNull expectedResponse = new RespNull();
        when(mockedRedisDatabase.getValueForKey(key.getValue())).thenReturn(Optional.empty());

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnGetRequestWithNonStringValue() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString getRequest = new BulkString("GET");
        BulkString key = new BulkString("non_string_value");
        requestArray.addElement(getRequest);
        requestArray.addElement(key);
        SimpleError expectedResponse = new SimpleError("GET can only retrieve strings.");
        when(mockedRedisDatabase.getValueForKey(key.getValue())).thenReturn(Optional.of(-1));

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnSetEXRequest() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString setRequest = new BulkString("SETEX");
        BulkString key = new BulkString("test_key_setex");
        BulkString timer = new BulkString("5");
        BulkString value = new BulkString("test_value");
        requestArray.addElement(setRequest);
        requestArray.addElement(key);
        requestArray.addElement(timer);
        requestArray.addElement(value);
        SimpleString expectedResponse = new SimpleString("OK");

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnSetEXRequestWithMissingTimer() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString setRequest = new BulkString("SETEX");
        BulkString key = new BulkString("test_key_setex_missing_timer");
        BulkString value = new BulkString("test_value");
        requestArray.addElement(setRequest);
        requestArray.addElement(key);
        requestArray.addElement(value);
        SimpleString expectedResponse = new SimpleString("Incomplete command: key, timer and value needed");

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnExistingKey() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString existsRequest = new BulkString("EXISTS");
        BulkString key = new BulkString("test_key_exists");
        requestArray.addElement(existsRequest);
        requestArray.addElement(key);
        RespInteger expectedResponse = new RespInteger("1");
        when(mockedRedisDatabase.checkKey(key.getValue())).thenReturn(1);

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnDeleteKeys() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString deleteRequest = new BulkString("DEL");
        BulkString key1 = new BulkString("test_key1");
        BulkString key2 = new BulkString("test_key2");
        requestArray.addElement(deleteRequest);
        requestArray.addElement(key1);
        requestArray.addElement(key2);
        List<String> keysToDelete = new ArrayList<>();
        keysToDelete.add(key1.getValue());
        keysToDelete.add(key2.getValue());
        RespInteger expectedResponse = new RespInteger("2");
        when(mockedRedisDatabase.deleteKeys(keysToDelete)).thenReturn(2);

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnIncrementValue() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString incrRequest = new BulkString("INCR");
        BulkString key = new BulkString("test_key_incr");
        requestArray.addElement(incrRequest);
        requestArray.addElement(key);
        RespInteger expectedResponse = new RespInteger("1");
        when(mockedRedisDatabase.incrementValue(key.getValue())).thenReturn(Optional.of("1"));

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnIncrementValueNotANumber() {
        // setup
        RespArray requestSetArray = new RespArray();
        BulkString setRequest = new BulkString("SET");
        BulkString keySet = new BulkString("test_key_incr_nan");
        BulkString valueSet = new BulkString("NAN");
        requestSetArray.addElement(setRequest);
        requestSetArray.addElement(keySet);
        requestSetArray.addElement(valueSet);
        RespArray requestIncrArray = new RespArray();
        BulkString incrRequest = new BulkString("INCR");
        BulkString keyIncr = new BulkString("test_key_incr_nan");
        requestIncrArray.addElement(incrRequest);
        requestIncrArray.addElement(keyIncr);
        SimpleError expectedResponse = new SimpleError("Accessed value is not a valid number.");
        when(mockedRedisDatabase.incrementValue(keyIncr.getValue())).thenReturn(Optional.of("NPS"));

        // act
        redisRequestProcessor.processRequest(requestSetArray);
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestIncrArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnDecrementValue() {
        // setup
        RespArray requestArray = new RespArray();
        BulkString decrRequest = new BulkString("DECR");
        BulkString key = new BulkString("test_key_decr");
        requestArray.addElement(decrRequest);
        requestArray.addElement(key);
        RespInteger expectedResponse = new RespInteger("-1");
        when(mockedRedisDatabase.decrementValue(key.getValue())).thenReturn(Optional.of("-1"));

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnDecrementValueNotANumber() {
        // setup
        RespArray requestSetArray = new RespArray();
        BulkString setRequest = new BulkString("SET");
        BulkString keySet = new BulkString("test_key_decr_nan");
        BulkString valueSet = new BulkString("NAN");
        requestSetArray.addElement(setRequest);
        requestSetArray.addElement(keySet);
        requestSetArray.addElement(valueSet);
        RespArray requestDecrArray = new RespArray();
        BulkString decrRequest = new BulkString("DECR");
        BulkString keyDecr = new BulkString("test_key_decr_nan");
        requestDecrArray.addElement(decrRequest);
        requestDecrArray.addElement(keyDecr);
        SimpleError expectedResponse = new SimpleError("Accessed value is not a valid number.");
        when(mockedRedisDatabase.decrementValue(keyDecr.getValue())).thenReturn(Optional.of("NPS"));

        // act
        redisRequestProcessor.processRequest(requestSetArray);
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestDecrArray);

        // assert
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
    }

    @Test
    public void generateResponseOnLPUSH() {
        // setup
        RespArray requestLpushArray = new RespArray();
        BulkString lpushRequest = new BulkString("LPUSH");
        BulkString keyLpush = new BulkString("test_key_lpush");
        BulkString valueLpush0 = new BulkString("value_0");
        BulkString valueLpush1 = new BulkString("value_1");
        BulkString valueLpush2 = new BulkString("value_2");
        BulkString valueLpush3 = new BulkString("value_3");
        requestLpushArray.addElement(lpushRequest);
        requestLpushArray.addElement(keyLpush);
        requestLpushArray.addElement(valueLpush0);
        requestLpushArray.addElement(valueLpush1);
        requestLpushArray.addElement(valueLpush2);
        requestLpushArray.addElement(valueLpush3);
        List<String> valuesToAdd = new ArrayList<>();
        valuesToAdd.add(valueLpush0.getValue());
        valuesToAdd.add(valueLpush1.getValue());
        valuesToAdd.add(valueLpush2.getValue());
        valuesToAdd.add(valueLpush3.getValue());
        when(mockedRedisDatabase.headPushToList(keyLpush.getValue(), valuesToAdd)).thenReturn(Optional.of("4"));

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestLpushArray);


        // assert
        assertEquals("4", actualResponse.getValue());
    }

    @Test
    public void generateResponseOnLPUSHNotAList() {
        // setup
        RespArray requestLpushArray = new RespArray();
        BulkString lpushRequest = new BulkString("LPUSH");
        BulkString keyLpush = new BulkString("test_key_lpush_not_a_list");
        BulkString valueLpush0 = new BulkString("value_0");
        requestLpushArray.addElement(lpushRequest);
        requestLpushArray.addElement(keyLpush);
        requestLpushArray.addElement(valueLpush0);
        List<String> valuesToAdd = new ArrayList<>();
        valuesToAdd.add(valueLpush0.getValue());
        when(mockedRedisDatabase.headPushToList(keyLpush.getValue(), valuesToAdd)).thenReturn(Optional.of("NAL"));


        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestLpushArray);


        // assert
        assertEquals("Accessed value is not a list.", actualResponse.getValue());
    }

    @Test
    public void generateResponseOnRPUSH() {
        // setup
        RespArray requestRpushArray = new RespArray();
        BulkString rpushRequest = new BulkString("RPUSH");
        BulkString keyRpush = new BulkString("test_key_rpush");
        BulkString valueRpush0 = new BulkString("value_0");
        BulkString valueRpush1 = new BulkString("value_1");
        BulkString valueRpush2 = new BulkString("value_2");
        BulkString valueRpush3 = new BulkString("value_3");
        requestRpushArray.addElement(rpushRequest);
        requestRpushArray.addElement(keyRpush);
        requestRpushArray.addElement(valueRpush0);
        requestRpushArray.addElement(valueRpush1);
        requestRpushArray.addElement(valueRpush2);
        requestRpushArray.addElement(valueRpush3);
        List<String> valuesToAdd = new ArrayList<>();
        valuesToAdd.add(valueRpush0.getValue());
        valuesToAdd.add(valueRpush1.getValue());
        valuesToAdd.add(valueRpush2.getValue());
        valuesToAdd.add(valueRpush3.getValue());
        when(mockedRedisDatabase.tailPushToList(keyRpush.getValue(), valuesToAdd)).thenReturn(Optional.of("4"));

        // act
        RespDataType actualResponse = redisRequestProcessor.processRequest(requestRpushArray);


        // assert
        assertEquals("4", actualResponse.getValue());
    }

    @Test
    public void generateResponseOnLRANGE() {
        // setup
        RespArray requestRpushArray = new RespArray();
        BulkString lrangeRequest = new BulkString("LRANGE");
        BulkString keyLrange = new BulkString("test_key_lrange");
        BulkString startIndex = new BulkString("1");
        BulkString endIndex = new BulkString("3");
        requestRpushArray.addElement(lrangeRequest);
        requestRpushArray.addElement(keyLrange);
        requestRpushArray.addElement(startIndex);
        requestRpushArray.addElement(endIndex);
        List<String> list = new ArrayList<>();
        list.add("value_0");
        list.add("value_1");
        list.add("value_2");
        list.add("value_3");
        List<String> subList = list.subList(Integer.parseInt(startIndex.getValue()), Integer.parseInt(endIndex.getValue()) + 1);
        when(mockedRedisDatabase.getListElements(keyLrange.getValue(), Integer.parseInt(startIndex.getValue()), Integer.parseInt(endIndex.getValue()))).thenReturn(Optional.of(subList));

        // act
        RespArray actualResponse = (RespArray) redisRequestProcessor.processRequest(requestRpushArray);

        RespArray resultList = new RespArray();
        resultList.addElement(new BulkString("value_1"));
        resultList.addElement(new BulkString("value_2"));
        resultList.addElement(new BulkString("value_3"));

        // assert
        assertEquals(resultList.getValue().size(), actualResponse.getValue().size());
        for (int i = 0; i < resultList.getLength(); i++) {
            assertEquals(resultList.getValue().get(i).getValue(), (String)actualResponse.getValue().get(i).getValue());
        }
    }
}
