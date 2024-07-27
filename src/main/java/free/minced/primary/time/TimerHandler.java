package free.minced.primary.time;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimerHandler {

    private long millis;
    public long startTime = System.currentTimeMillis();
    public TimerHandler() {
        reset();
    }

    public boolean finished(long delay) {
        return System.currentTimeMillis() - delay >= millis;
    }
    public boolean passed(long time) {
        return System.currentTimeMillis() - millis > time;
    }

    public boolean finished(float delay) {
        return System.currentTimeMillis() - delay >= millis;
    }

    public boolean finished(double delay) {
        return System.currentTimeMillis() - delay >= millis;
    }

    public boolean every(long ms) {
        boolean passed = getMs(System.nanoTime() - millis) >= ms;
        if (passed)
            reset();
        return passed;
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - startTime > time;
    }
    public void reset() {
        this.millis = System.currentTimeMillis();
    }
    public long getMs(long time) {
        return time / 1000000L;
    }
    public long getPassedTimeMs() {
        return getMs(System.nanoTime() - startTime);
    }
    public boolean finished(long delay, boolean reset) {
        if (System.currentTimeMillis() - millis > delay) {
            if (reset) reset();
            return true;
        }

        return false;
    }

    public boolean finished(float delay, boolean reset) {
        if (System.currentTimeMillis() - millis > delay) {
            if (reset) reset();
            return true;
        }

        return false;
    }
    public long getElapsed() {
        return System.currentTimeMillis() - startTime;
    }
    public boolean reachedTime(float time) {
        return System.currentTimeMillis() - millis >= time;
    }


    public Number getElapsedTime() {
        return System.currentTimeMillis() - this.millis;
    }
    public long getTime() {
        return System.currentTimeMillis() - this.millis;
    }

    public void setTime(long l) {
        this.millis = l;
    }

    public boolean passedMs(long ms) {
        return getMs(System.nanoTime() - millis) >= ms;
    }

}