package unit;

import org.example.database.RedisDatabase;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class RedisDatabaseTest {
    // unit test works on the shared data structure, no issue with concurrency accesses.
    private final RedisDatabase redisDatabase = RedisDatabase.getInstance();

    @Test
    public void testGetValueForNonExpiredKeyEX() {
        try {
            // setup
            redisDatabase.setValueForKeyWithEX("test_key_setex", "test_value", "1");
            Thread.sleep(500);

            // act
            assertNotEquals(Optional.empty(), redisDatabase.getValueForKey("test_key_setex"));

            Optional<?> actualValue = redisDatabase.getValueForKey("test_key_setex");

            // assert
            assertEquals("test_value", actualValue.orElseThrow());

        } catch (InterruptedException | NoSuchElementException e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testGetValueForKeyOnExpiredKeyEX() {
        try {
            // setup
            redisDatabase.setValueForKeyWithEX("test_key_setex_expired", "test_value", "1");
            Thread.sleep(1000);

            // act
            Optional<?> actualValue = redisDatabase.getValueForKey("test_key_setex_expired");

            // assert
            assertEquals(Optional.empty(), actualValue);

        } catch (InterruptedException e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testGetValueForKeyOnExpiredKeyEAXT() {
        try {
            // setup
            redisDatabase.setValueForKeyWithEAXT("test_key_get_eaxt_expired", "test_value", String.valueOf(Instant.now().plus(Duration.ofSeconds(1)).getEpochSecond()));
            Thread.sleep(1001);

            // act
            Optional<?> actualValue = redisDatabase.getValueForKey("test_key_eaxt_expired");

            // assert
            assertEquals(Optional.empty(), actualValue);
        } catch(InterruptedException e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testGetValueForKeyOnNonExpiredKeyEAXT() {
        try {
            // setup
            redisDatabase.setValueForKeyWithEAXT("test_key_get_eaxt_non_expired", "test_value", String.valueOf(Instant.now().plus(Duration.ofSeconds(2)).getEpochSecond()));
            Thread.sleep(500);

            // act
            Optional<?> actualValue = redisDatabase.getValueForKey("test_key_get_eaxt_non_expired");

            // assert
            assertEquals("test_value", actualValue.orElseThrow());

        } catch(InterruptedException | NoSuchElementException e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testGetValueForNonExpiredKeyPEX() {
        try {
            // setup
            redisDatabase.setValueForKeyWithPEX("test_key_get_psetex_non_expired", "test_value", String.valueOf(1000));
            Thread.sleep(500);

            // act
            Optional<?> actualValue = redisDatabase.getValueForKey("test_key_get_psetex_non_expired");

            // assert
            assertEquals("test_value", actualValue.orElseThrow());
        } catch (InterruptedException | NoSuchElementException e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testIfKeyExist() {
        // setup
        redisDatabase.setValueForKey("test_key_exists", "test_value");

        // act
        int checkResult = redisDatabase.checkKey("test_key_exists");

        // assert
        assertEquals(1, checkResult);
    }

    @Test
    public void testDeleteKeysIfExist() {
        // setup
        List<String> keysToDelete = new ArrayList<>();
        keysToDelete.add("test_key1");
        keysToDelete.add("test_key2");
        redisDatabase.setValueForKey(keysToDelete.get(0), "test_value1");
        redisDatabase.setValueForKey(keysToDelete.get(1), "test_value2");

        // act
        int numberOfDeletedKeys = redisDatabase.deleteKeys(keysToDelete);

        // assert
        assertEquals(2, numberOfDeletedKeys);
        int checkKey1 = redisDatabase.checkKey("test_key1");
        int checkKey2 = redisDatabase.checkKey("test_key2");
        assertEquals(0, checkKey1);
        assertEquals(0, checkKey2);
    }

    @Test
    public void testIncrementStoredValue() {
        try {
            // setup
            String key = "test_key_incr_stored";
            redisDatabase.setValueForKey(key, "0");

            // act
            Optional<String> value = redisDatabase.incrementValue(key);

            // assert
            assertEquals("1", value.orElseThrow());
        } catch (NoSuchElementException e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testIncrementNonStoredValue() {
        try {
            // act
            Optional<String> value = redisDatabase.incrementValue("test_key_incr_non_stored");

            // assert
            assertEquals("1", value.orElseThrow());
        } catch(NoSuchElementException e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testIncrementNonParsableString() {
        try {
            // setup
            redisDatabase.setValueForKey("test_key_incr_np", "NON_PARSABLE");

            // act
            Optional<String> value = redisDatabase.incrementValue("test_key_incr_np");

            // assert
            assertEquals("NPS", value.orElseThrow());
        } catch (NoSuchElementException e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testIncrementNonAString() {
        // TODO once lpush has been done
    }

    @Test
    public void testDecrementStoredValue() {
        try {
            // setup
            String key = "test_key_decr_stored";
            redisDatabase.setValueForKey(key, "0");

            // act
            Optional<String> value = redisDatabase.decrementValue(key);

            // assert
            assertEquals("-1", value.orElseThrow());
        } catch (NoSuchElementException e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testDecrementNonStoredValue() {
        // act
        Optional<String> value = redisDatabase.decrementValue("test_key_decr_non_stored");

        // assert
        value.ifPresent(s -> assertEquals("-1", s));
    }

    @Test
    public void testDecrementNonParsableString() {
        // setup
        redisDatabase.setValueForKey("test_key_decr_np", "NON_PARSABLE");

        // act
        Optional<String> value = redisDatabase.decrementValue("test_key_decr_np");

        // assert
        value.ifPresent(s -> assertEquals("NPS", s));
    }

    @Test
    public void testDecrementNonAString() {
        // TODO once lpush has been done
    }

    @Test
    public void testPushElementToHeadOfListWithNonExistingKey() {
        try {
            // setup + act
            ArrayList<String> expectedValue = new ArrayList<>();
            expectedValue.add("test_value");
            redisDatabase.headPushToList("test_list_key_non_existing", Collections.singletonList("test_value"));

            // assert
            Optional<?> list = redisDatabase.getValueForKey("test_list_key_non_existing");

            assertEquals(expectedValue, list.orElseThrow());

        } catch (Exception e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testPushElementToHeadOfListWithExistingKey() {
        try {
            // setup + act
            ArrayList<String> expectedValue = new ArrayList<>();
            expectedValue.add("test_value_0");
            expectedValue.add("test_value_1");
            redisDatabase.headPushToList("test_list_key_existing", Collections.singletonList("test_value_0"));
            redisDatabase.headPushToList("test_list_key_existing", Collections.singletonList("test_value_1"));

            // act
            Optional<?> list = redisDatabase.getValueForKey("test_list_key_existing");

            // assert
            assertEquals(expectedValue, list.orElseThrow());
        } catch(Exception e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testPushElementToHeadOfListNotAList() {
        try {
            // setup
            redisDatabase.setValueForKey("test_list_not_a_list", "test_value");

            // act
            Optional<String> opResult = redisDatabase.headPushToList("test_list_not_a_list", Collections.singletonList("test_value_1"));

            // assert
            assertEquals("NAL", opResult.orElseThrow());

        } catch (Exception e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testPushElementsToHeadOfList() {
        try {
            // setup
            List<String> expectedList = new ArrayList<>();
            expectedList.add("test_value_2");
            expectedList.add("test_value_1");
            expectedList.add("test_value_0");

            List<String> valuesToAdd = new ArrayList<>();
            valuesToAdd.add("test_value_0");
            valuesToAdd.add("test_value_1");
            valuesToAdd.add("test_value_2");

            // act
            redisDatabase.headPushToList("test_hlist_multiple_elements", valuesToAdd);
            Optional<?> list = redisDatabase.getValueForKey("test_hlist_multiple_elements");

            // assert
            assertEquals(expectedList, list.orElseThrow());
        } catch (Exception e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testPushElementsToTailOfList() {
        try {
            // setup
            List<String> expectedList = new ArrayList<>();
            expectedList.add("test_value_0");
            expectedList.add("test_value_1");
            expectedList.add("test_value_2");

            List<String> valuesToAdd = new ArrayList<>();
            valuesToAdd.add("test_value_0");
            valuesToAdd.add("test_value_1");
            valuesToAdd.add("test_value_2");

            // act
            redisDatabase.tailPushToList("test_tlist_multiple_elements", valuesToAdd);
            Optional<?> list = redisDatabase.getValueForKey("test_tlist_multiple_elements");

            // assert
            assertEquals(expectedList, list.orElseThrow());
        } catch (Exception e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testGetSublistValidPositiveRange() {
        try {
            // setup
            List<String> initialValues = new ArrayList<>();
            initialValues.add("value_0");
            initialValues.add("value_1");
            initialValues.add("value_2");
            initialValues.add("value_3");
            initialValues.add("value_4");
            String key = "test_lrange_positive";
            int startIndex = 1;
            int endIndex = 4;
            List<String> expectedValues = new ArrayList<>();
            expectedValues.add("value_1");
            expectedValues.add("value_2");
            expectedValues.add("value_3");
            expectedValues.add("value_4");
            redisDatabase.tailPushToList(key, initialValues);

            // act
            Optional<List<String>> actualValues = redisDatabase.getListElements(key, startIndex, endIndex);

            // assert
            assertEquals(expectedValues, actualValues.orElseThrow());

        } catch (Exception e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testGetSublistValidNegativeRange() {
        try {
            // setup
            List<String> initialValues = new ArrayList<>();
            initialValues.add("value_0");
            initialValues.add("value_1");
            initialValues.add("value_2");
            initialValues.add("value_3");
            initialValues.add("value_4");
            String key = "test_lrange_negative";
            int startIndex = -4;
            int endIndex = -1;
            List<String> expectedValues = new ArrayList<>();
            expectedValues.add("value_1");
            expectedValues.add("value_2");
            expectedValues.add("value_3");
            expectedValues.add("value_4");
            redisDatabase.tailPushToList(key, initialValues);

            // act
            Optional<List<String>> actualValues = redisDatabase.getListElements(key, startIndex, endIndex);

            // assert
            assertEquals(expectedValues, actualValues.orElseThrow());
        } catch (Exception e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testGetSublistStartLargerThenEndOfList() {
        try {
            // setup
            List<String> initialValues = new ArrayList<>();
            initialValues.add("value_0");
            initialValues.add("value_1");
            initialValues.add("value_2");
            initialValues.add("value_3");
            initialValues.add("value_4");
            String key = "test_lrange_start_index_greater";
            int startIndex = 5;
            int endIndex = 4;
            List<String> expectedValues = new ArrayList<>();
            redisDatabase.tailPushToList(key, initialValues);

            // act
            Optional<List<String>> actualValues = redisDatabase.getListElements(key, startIndex, endIndex);

            // assert
            assertEquals(expectedValues, actualValues.orElseThrow());
        } catch (Exception e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testGetSublistEndIndexGreaterThanLength() {
        try {
            // setup
            List<String> initialValues = new ArrayList<>();
            initialValues.add("value_0");
            initialValues.add("value_1");
            initialValues.add("value_2");
            initialValues.add("value_3");
            initialValues.add("value_4");
            String key = "test_lrange_endindex_greater";
            int startIndex = 1;
            int endIndex = 5;
            List<String> expectedValues = new ArrayList<>();
            expectedValues.add("value_1");
            expectedValues.add("value_2");
            expectedValues.add("value_3");
            expectedValues.add("value_4");
            redisDatabase.tailPushToList(key, initialValues);

            // act
            Optional<List<String>> actualValues = redisDatabase.getListElements(key, startIndex, endIndex);

            // assert
            assertEquals(expectedValues, actualValues.orElseThrow());
        } catch (Exception e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testGetSublistOfNonExistingKey() {
        try {
            // setup
            String key = "test_lrange_missing_key";
            int startIndex = 1;
            int endIndex = 5;

            // act
            Optional<List<String>> actualValues = redisDatabase.getListElements(key, startIndex, endIndex);

            // assert
            assertEquals(Optional.empty(), actualValues);
        } catch (Exception e) {
            fail(e.getCause());
        }
    }

    @Test
    public void testGetSublistOfNonListValue() {
        try {
            // setup
            String key = "test_lrange_nonlist_value";
            int startIndex = 1;
            int endIndex = 5;
            redisDatabase.setValueForKey(key, "value");

            // act
            Optional<List<String>> actualValues = redisDatabase.getListElements(key, startIndex, endIndex);

            // assert
            assertEquals(Optional.empty(), actualValues);
        } catch (Exception e) {
            fail(e.getCause());
        }
    }
}
