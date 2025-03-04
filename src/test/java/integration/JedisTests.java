package integration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


// integration tests using jedis: https://github.com/redis/jedis
public class JedisTests {
    private static final Logger log = LogManager.getLogger(JedisTests.class);

    @Test
    public void testSetAndGetCommands() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String setResult = jedis.set("test_set", "Jedis");
            log.info("processed SET command: {}.", setResult);
            String value = jedis.get("test_set");
            assertEquals("Jedis", value);
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testGetCommandOnNewKey() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            String actualValue = jedis.get("missingValue");
            log.info("processed GET command: {}.", actualValue);
            assertNull(actualValue);
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testSetEXAndGetCommands() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.setex("test_setex", 10, "Jedis");
            String value = jedis.get("test_setex");
            assertEquals("Jedis", value);
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testSetEXAndGetOnExpiredTimer() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.setex("test_setex_expired", 1, "Jedis");
            Thread.sleep(2000);
            String value = jedis.get("test_setex_expired");
            assertNull(value);
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testPSetEXAndGetCommands() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.psetex("test_psetex", 1000, "Jedis");
            String value = jedis.get("test_psetex");
            assertEquals("Jedis", value);
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testPSetEXAndGetOnExpiredKey() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.psetex("test_psetex_expired", 500, "Jedis");
            Thread.sleep(1000);
            String value = jedis.get("test_psetex_expired");
            assertNull(value);
        } catch (Exception e) {
            log.error(e.getMessage());
            fail();
        }
    }

    @Test
    public void testIfKeyExists() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.set("test_key_exists", "test_value");
            boolean keyExists = jedis.exists("test_key_exists");
            assertTrue(keyExists);
        }
    }

    @Test
    public void testIfKeyDoesNotExist() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            boolean keyExists = jedis.exists("test_key_does_not_exist");
            assertFalse(keyExists);
        }
    }

    @Test
    public void testDeleteSetOfKeys() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.set("test_key1", "test_value1");
            jedis.set("test_key2", "test_value2");
            jedis.set("test_key3", "test_value3");
            long numberOfDeletedKeys = jedis.del("test_key1", "test_key2", "test_key3");
            assertEquals(3, numberOfDeletedKeys);
        }
    }

    @Test
    public void testDeleteNonExistingKeys() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            long numberOfDeletedKeys = jedis.del("test_key_delete_non_existing");
            assertEquals(0, numberOfDeletedKeys);
        }
    }

    // cannot test edge cases like incr/decr of non-parsable (into int) string as jedis.incr/decr doesn't accept resp SimpleError
    @Test
    public void testIncrementOfNonExistingKey() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            long numberAfterIncrease = jedis.incr("test_key_incr_non_existing");
            assertEquals(1, numberAfterIncrease);
        }
    }

    @Test
    public void testIncrementOnExistingKey() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.set("test_key_incr_existing", "2");
            long numberAfterIncrease = jedis.incr("test_key_incr_existing");
            assertEquals(3, numberAfterIncrease);
        }
    }

    @Test
    public void testIncrementNotAValidNumber() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            Exception exception = assertThrows(JedisDataException.class, () -> {
                    jedis.set("test_key_incr_nan", "NAN");;
                    jedis.incr("test_key_incr_nan");
                }
            );
            assertEquals("Accessed value is not a valid number.", exception.getMessage());
        }
    }

    @Test
    public void testIncrementNotAString() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            Exception exception = assertThrows(JedisDataException.class, () -> {
                    jedis.lpush("test_incr_not_string", "value");
                    jedis.incr("test_incr_not_string");
                }
            );
            assertEquals("Accessed value is not a string.", exception.getMessage());
        }
    }

    @Test
    public void testDecrementOfNonExistingKey() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            long numberAfterIncrease = jedis.decr("test_key_decr_non_existing");
            assertEquals(-1, numberAfterIncrease);
        }
    }

    @Test
    public void testDecrementOnExistingKey() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.set("test_key_decr_existing", "2");
            long numberAfterIncrease = jedis.decr("test_key_decr_existing");
            assertEquals(1, numberAfterIncrease);
        }
    }

    @Test
    public void testHeadPushElementsOnListWithNonExistingKey() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            long lengthOfList = jedis.lpush("test_hlist_key_new", "value0", "value1");
            assertEquals(2, lengthOfList);
        }
    }

    @Test
    public void testHeadPushElementsOnListWithExistingKey() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.lpush("test_hlist_key_existing", "value0");
            long lengthOfList = jedis.lpush("test_hlist_key_existing", "value1");
            assertEquals(2, lengthOfList);
        }
    }

    @Test
    public void testHeadPushElementOnNonListValue() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            Exception exception = assertThrows(JedisDataException.class, () -> {
                jedis.set("test_list_key_not_list", "value");
                jedis.lpush("test_list_key_not_list", "value1");
                }
            );

            assertEquals("Accessed value is not a list.", exception.getMessage());
        }
    }

    @Test
    public void testTailPushElementsOnListWithNonExistingKey() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            long lengthOfList = jedis.rpush("test_tlist_key_new", "value0", "value1");
            assertEquals(2, lengthOfList);
        }
    }

    @Test
    public void testTailPushElementsOnListWithExistingKey() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.rpush("test_rlist_key_existing", "value0");
            long lengthOfList = jedis.rpush("test_rlist_key_existing", "value1");
            assertEquals(2, lengthOfList);
        }
    }

    @Test
    public void testLrangePositiveValidIndexes() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.rpush("test_lrange_positive_valid_indexes", "value0", "value1", "value2", "value3", "value4");
            List<String> actualResult = jedis.lrange("test_lrange_positive_valid_indexes", 1, 4);
            List<String> expectedResult = new ArrayList<>();
            expectedResult.add("value1");
            expectedResult.add("value2");
            expectedResult.add("value3");
            expectedResult.add("value4");
            assertEquals(expectedResult.size(), actualResult.size());
            for (int i = 0; i < expectedResult.size(); i++) {
                assertEquals(expectedResult.get(i), actualResult.get(i));
            }
        }
    }

    @Test
    public void testSave() {
        try(Jedis jedis = new Jedis("localhost", 6379)) {
            String result = jedis.save();
            assertEquals("OK", result);
        }
    }
}
