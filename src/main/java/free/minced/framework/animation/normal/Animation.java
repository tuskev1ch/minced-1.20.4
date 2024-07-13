package free.minced.framework.animation.normal;


import lombok.Getter;
import lombok.Setter;

/**
 * @author jbk
 */
@Getter @Setter
public class Animation {

    private Easing easing;
    private long duration;
    private long millis;
    private long startTime;

    private float startValue;
    private float destinationValue;
    private float value;
    private boolean finished;

    /**
     * Конструктор
     *
     * @param easing   функция, которая будет использоваться для анимации
     * @param duration продолжительность анимации в миллисекундах
     */
    public Animation(final Easing easing, final long duration) {
        this.easing = easing;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
    }

    /**
     * Обновляет анимацию.
     *
     * @param destinationValue - значение, которого достигнет анимация.
     */
    public void run(final float destinationValue) {
        this.millis = System.currentTimeMillis();
        if (this.destinationValue != destinationValue) {
            this.destinationValue = destinationValue;
            this.reset();
        } else {
            this.finished = this.millis - this.duration > this.startTime;
            if (this.finished) {
                this.value = destinationValue;
                return;
            }
        }

        final Double result = this.easing.getFunction().apply(this.getProgress());
        if (this.value > destinationValue) {
            this.value = (float) (this.startValue - (this.startValue - destinationValue) * result);
        } else {
            this.value = (float) (this.startValue + (destinationValue - this.startValue) * result);
        }
    }

    /**
     * Возвращает прогресс анимации
     *
     * @return прогресс анимации
     */
    public double getProgress() {
        return (double) (System.currentTimeMillis() - this.startTime) / (double) this.duration;
    }



    /**
     * Сбрасывает анимацию к начальному значению
     */
    public void reset() {
        this.startTime = System.currentTimeMillis();
        this.startValue = value;
        this.finished = false;
    }

    public Number getNumberValue() {
        return value;
    }
}