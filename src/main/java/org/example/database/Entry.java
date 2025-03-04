package org.example.database;
import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

public class Entry implements Serializable {
    private Object value;
    private Instant timerSetAt;
    private Instant timer;
    private boolean timerSet = false;
    private boolean timestampSet = false;

    public Optional<Object> getValue() {
        Optional<Object> value = Optional.empty();
        if (timerSet && (Instant.now().toEpochMilli() - timerSetAt.toEpochMilli() >= timer.toEpochMilli())) {
            return value;
        }
        if (timestampSet && Instant.now().isAfter(timer)) {
            return value;
        }
        value = Optional.of(this.value);
        return value;
    }

    public void setTimerEX(long timer) {
        this.timerSetAt = Instant.now();
        this.timer = Instant.ofEpochSecond(timer);
        this.timerSet = true;
        this.timestampSet = false;
    }

    public void setTimerPEX(long timer) {
        this.timerSetAt = Instant.now();
        this.timer = Instant.ofEpochMilli(timer);
        this.timerSet = true;
        this.timestampSet = false;
    }

    public void setTimerEAXT(long timestamp) {
        this.timer = Instant.ofEpochSecond(timestamp);
        this.timerSet = false;
        this.timestampSet = true;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
