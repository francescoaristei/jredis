package org.example.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.util.*;

public class RedisDatabase {
    private static RedisDatabase instance;
    private final Map<String, Entry> database = Collections.synchronizedMap(new HashMap<>());
    private static final Logger log = LogManager.getLogger(RedisDatabase.class);


    private RedisDatabase(){}

    public static RedisDatabase getInstance() {
        if(instance == null) {
            instance = new RedisDatabase();
            instance.initializeDatabase();
        }
        return instance;
    }

    public void setValueForKey(String key, String value) {
        Entry entry = new Entry();
        database.put(key, entry);
        entry.setValue(value);
    }

    public void setValueForKeyWithEX(String key, String value, String timer) {
        Entry entry = new Entry();
        database.put(key, entry);
        entry.setValue(value);
        entry.setTimerEX(Long.parseLong(timer));
    }

    public void setValueForKeyWithPEX(String key, String value, String timer) {
        Entry entry = new Entry();
        database.put(key, entry);
        entry.setValue(value);
        entry.setTimerPEX(Long.parseLong(timer));
    }

    public void setValueForKeyWithEAXT(String key, String value, String timer) {
        Entry entry = new Entry();
        database.put(key, entry);
        entry.setValue(value);
        entry.setTimerEAXT(Long.parseLong(timer));
    }

    public Optional<Object> getValueForKey(String key) {
        if (database.get(key) == null) {
            return Optional.empty();
        }
        // timer checked only when accessed
        if (database.get(key).getValue().isEmpty()) {
            database.remove(key);
            return Optional.empty();
        }
        return database.get(key).getValue();
    }

    public Optional<String> incrementValue(String key) {
        Optional<String> result = Optional.empty();
        if (checkKey(key) == 0) {
            Entry entry = new Entry();
            database.put(key, entry);
            entry.setValue("1");
            result = Optional.of("1");
        } else {
            Entry entry = database.get(key);
            if (entry.getValue().isPresent()) {
                if (entry.getValue().get() instanceof String stringValue) {
                    try {
                        int newValue = Integer.parseInt(stringValue) + 1;
                        entry.setValue(String.valueOf(newValue));
                        result = Optional.of(String.valueOf(newValue));
                    } catch (NumberFormatException e) {
                        result = Optional.of("NPS");
                    }
                } else {

                    result = Optional.of("NAS");
                }
            }
        }
        return result;
    }

    public Optional<String> decrementValue(String key) {
        Optional<String> result = Optional.empty();
        if (checkKey(key) == 0) {
            Entry entry = new Entry();
            database.put(key, entry);
            entry.setValue("-1");
            result = Optional.of("-1");
        } else {
            Entry entry = database.get(key);
            if (entry.getValue().isPresent()) {
                if (entry.getValue().get() instanceof String stringValue) {
                    try {
                        int newValue = Integer.parseInt(stringValue) - 1;
                        entry.setValue(String.valueOf(newValue));
                        result = Optional.of(String.valueOf(newValue));
                    } catch (NumberFormatException e) {
                        result = Optional.of("NPS");
                    }
                } else {
                    result = Optional.of("NAS");
                }
            }
        }
        return result;
    }

    public int checkKey(String key) {
        return database.containsKey(key) ? 1 : 0;
    }

    public int deleteKeys(List<String> keys) {
        int counter = 0;
        for(String key: keys) {
            if (checkKey(key) == 1) {
                database.remove(key);
                counter++;
            }
        }
        return counter;
    }

    public Optional<String> headPushToList(String key, List<String> value) {
        Optional<String> response = Optional.empty();
        if (checkKey(key) == 0) {
            Entry entry = new Entry();
            database.put(key, entry);
            ArrayList<String> list = new ArrayList<>();
            ListIterator<String> li = value.listIterator(value.size());
            while(li.hasPrevious()) {
                list.add(li.previous());
            }
            entry.setValue(list);
            response = Optional.of(String.valueOf(list.size()));
        } else {
            Entry entry = database.get(key);
            if (entry.getValue().isPresent()) {
                if (entry.getValue().get() instanceof ArrayList list) {
                    ListIterator<String> li = value.listIterator(value.size());
                    while(li.hasPrevious()) {
                        list.add(li.previous());
                    }
                    entry.setValue(list);
                    response = Optional.of(String.valueOf(list.size()));
                } else {
                    response = Optional.of("NAL");
                }
            }
        }
        return response;
    }

    public Optional<String> tailPushToList(String key, List<String> value) {
        Optional<String> response = Optional.empty();
        if (checkKey(key) == 0) {
            Entry entry = new Entry();
            database.put(key, entry);
            ArrayList<String> list = new ArrayList<>(value);
            entry.setValue(list);
            response = Optional.of(String.valueOf(list.size()));
        } else {
            Entry entry = database.get(key);
            if (entry.getValue().isPresent()) {
                if (entry.getValue().get() instanceof ArrayList list) {
                    list.addAll(value);
                    entry.setValue(list);
                    response = Optional.of(String.valueOf(list.size()));
                } else {
                    response = Optional.of("NAL");
                }
            }
        }
        return response;
    }

    public Optional<List<String>> getListElements(String key, int startIndex, int endIndex) {
        Optional<List<String>> response = Optional.empty();
        Entry entry;
        if (checkKey(key) == 1 && (entry = database.get(key)).getValue().isPresent()) {
            if(entry.getValue().get() instanceof ArrayList list) {
                if (startIndex <= list.size() -1) {
                    startIndex = startIndex < 0 ? list.size() - Math.abs(startIndex) : startIndex;
                    endIndex = endIndex < 0 ? list.size() - Math.abs(endIndex) : Math.min(endIndex, list.size() - 1);
                    int newStartIndex = Math.min(startIndex, endIndex);
                    int newEndIndex = Math.max(startIndex, endIndex);
                    response = Optional.of(list.subList(newStartIndex, newEndIndex + 1));
                } else {
                    response = Optional.of(List.of());
                }
            }
        }
        return response;
    }

    public boolean saveToDisk() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("redis_database.rdb");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            for (String key: this.database.keySet()) {
                objectOutputStream.writeObject(key);
                objectOutputStream.writeObject(this.database.get(key));
            }
            log.info("Database saved into disk.");
            return true;
        } catch(IOException e) {
            log.error(e.getCause());
            return false;
        }
    }

    public void initializeDatabase() {
        try {
            log.info("Initializing database..");
            FileInputStream fileInputStream = new FileInputStream("redis_database.rdb");
            try(ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);) {
                while(true) {
                    String key = (String) objectInputStream.readObject();
                    Entry entry = (Entry) objectInputStream.readObject();
                    this.database.put(key, entry);
                }
            } catch (IOException | ClassNotFoundException e) {
                log.info("Finished reading database file.");
            }
        } catch (IOException e) {
            log.error(e.getCause());
        }
        log.info("Database initialized.");
    }
}
