package free.minced.framework.animation.hue;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimationHue {

    private Number duration;
    private Number value;
    private Number initialValue;
    private EasingHue EasingHue;
    private long startTime;
    private float newValue;
    private boolean done;
    private boolean reverse;
    private boolean paused;
    private boolean repeat;
    private int repeatCount;

    private Runnable onStart;
    private Runnable onUpdate;
    private Runnable onComplete;

    public AnimationHue(EasingHue EasingHue, long duration) {
        initialize(EasingHue, duration, 0, false);
    }

    public AnimationHue(EasingHue EasingHue, float initialValue, long duration) {
        initialize(EasingHue, duration, initialValue, false);
    }

    public AnimationHue(EasingHue EasingHue, long duration, boolean reverse, boolean repeat, int repeatCount) {
        initialize(EasingHue, duration, 0, reverse);
        this.repeat = repeat;
        this.repeatCount = repeatCount;
    }

    public AnimationHue(EasingHue EasingHue, float initialValue, long duration, boolean reverse, boolean repeat, int repeatCount) {
        initialize(EasingHue, duration, initialValue, reverse);
        this.repeat = repeat;
        this.repeatCount = repeatCount;
    }

    private void initialize(EasingHue EasingHue, long duration, float initialValue, boolean reverse) {
        this.startTime = System.currentTimeMillis();
        this.EasingHue = EasingHue;
        this.duration = Math.max(0, duration);
        this.initialValue = initialValue;
        this.value = initialValue;
        this.reverse = reverse;
    }

    public void update(float newValue) {
        if (paused) return;

        long millis = System.currentTimeMillis();

        if (this.newValue != newValue) {
            this.newValue = newValue;
            this.startTime = millis;
            this.initialValue = value;
            if (onStart != null) onStart.run();
        } else {
            this.done = millis - this.duration.longValue() > this.startTime;
            if (this.done) {
                this.value = newValue;
                if (onComplete != null) onComplete.run();
                if (repeat) {
                    repeatCount--;
                    if (repeatCount > 0 || repeatCount == - 1) {
                        restart();
                    } else {
                        done = true;
                    }
                }
                return;
            }
        }

        float progress = getProgress();
        float result = EasingHue.ease(progress, 0, 1, 1);
        float deltaValue = reverse ? initialValue.floatValue() - newValue : newValue - initialValue.floatValue();
        this.value = initialValue.floatValue() + deltaValue * result;

        if (onUpdate != null) onUpdate.run();
    }

    public void restart() {
        startTime = System.currentTimeMillis();
        done = false;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public boolean isInProgress() {
        return getProgress() < 1.0;
    }

    public float getProgress() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;
        return Math.min(1.0f, (float) elapsedTime / duration.floatValue());
    }

    public float getDuration() {
        return duration.floatValue();
    }

    public float getValue() {
        return value.floatValue();
    }

    public float getInitialValue() {
        return initialValue.floatValue();
    }
}
