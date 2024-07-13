package free.minced.primary;

public class AdjustedDisplay implements IHolder {

    public Number getScaledWidth() {
        return mc.getWindow().getScaledWidth();
    }

    public Number getScaledHeight() {
        return mc.getWindow().getScaledHeight();
    }

    public static Number getScaleFactor() {
        return mc.getWindow().getScaleFactor();
    }

}
