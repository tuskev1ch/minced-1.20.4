package free.minced.framework.animation.normal;


import lombok.Getter;

import java.util.function.Function;

import static java.lang.Math.pow;
import static java.lang.Math.sin;

@Getter
public enum Easing {

    /**
     * Linear easing function
     */
    LINEAR(x -> x),

    /**
     * Quadratic easing function that starts slow and speeds up
     */
    EASE_IN_QUAD(x -> x * x),

    /**
     * Quadratic easing function that starts fast and slows down
     */
    EASE_OUT_QUAD(x -> x * (2 - x)),

    /**
     * Quadratic easing function that starts slow, speeds up, and then slows down
     */
    EASE_IN_OUT_QUAD(x -> x < 0.5 ? 2 * x * x : -1 + (4 - 2 * x) * x),

    /**
     * Cubic easing function that starts slow and speeds up
     */
    EASE_IN_CUBIC(x -> x * x * x),

    /**
     * Cubic easing function that starts fast and slows down
     */
    EASE_OUT_CUBIC(x -> (--x) * x * x + 1),

    /**
     * Cubic easing function that starts slow, speeds up, and then slows down
     */
    EASE_IN_OUT_CUBIC(x -> x < 0.5 ? 4 * x * x * x : (x - 1) * (2 * x - 2) * (2 * x - 2) + 1),

    /**
     * Quartic easing function that starts slow and speeds up
     */
    EASE_IN_QUART(x -> x * x * x * x),

    /**
     * Quartic easing function that starts fast and slows down
     */
    EASE_OUT_QUART(x -> 1 - (--x) * x * x * x),

    /**
     * Quartic easing function that starts slow, speeds up, and then slows down
     */
    EASE_IN_OUT_QUART(x -> x < 0.5 ? 8 * x * x * x * x : 1 - 8 * (--x) * x * x * x),

    /**
     * Quintic easing function that starts slow and speeds up
     */
    EASE_IN_QUINT(x -> x * x * x * x * x),

    /**
     * Quintic easing function that starts fast and slows down
     */
    EASE_OUT_QUINT(x -> 1 + (--x) * x * x * x * x),

    /**
     * Quintic easing function that starts slow, speeds up, and then slows down
     */
    EASE_IN_OUT_QUINT(x -> x < 0.5 ? 16 * x * x * x * x * x : 1 + 16 * (--x) * x * x * x * x),

    /**
     * Sine easing function that starts slow and speeds up
     */
    EASE_IN_SINE(x -> 1 - Math.cos(x * Math.PI / 2)),

    /**
     * Sine easing function that starts fast and slows down
     */
    EASE_OUT_SINE(x -> sin(x * Math.PI / 2)),

    /**
     * Sine easing function that starts slow, speeds up, and then slows down
     */
    EASE_IN_OUT_SINE(x -> 1 - Math.cos(Math.PI * x / 2)),

    /**
     * Exponential easing function that starts slow and speeds up
     */
    EASE_IN_EXPO(x -> x == 0 ? 0 : pow(2, 10 * x - 10)),

    /**
     * Exponential easing function that starts fast and slows down
     */
    EASE_OUT_EXPO(x -> x == 1 ? 1 : 1 - pow(2, -10 * x)),

    /**
     * Exponential easing function that starts slow, speeds up, and then slows down
     */
    EASE_IN_OUT_EXPO(x -> x == 0 ? 0 : x == 1 ? 1 : x < 0.5 ? pow(2, 20 * x - 10) / 2 : (2 - pow(2, -20 * x + 10)) / 2),

    /**
     * Circular easing function that starts slow and speeds up
     */
    EASE_IN_CIRC(x -> 1 - Math.sqrt(1 - x * x)),

    /**
     * Circular easing function that starts fast and slows down
     */
    EASE_OUT_CIRC(x -> Math.sqrt(1 - (--x) * x)),

    /**
     * Circular easing function that starts slow, speeds up, and then slows down
     */
    EASE_IN_OUT_CIRC(x -> x < 0.5 ? (1 - Math.sqrt(1 - 4 * x * x)) / 2 : (Math.sqrt(1 - 4 * (x - 1) * x) + 1) / 2),

    /**
     * Sigmoid easing function
     */
    SIGMOID(x -> 1 / (1 + Math.exp(-x))),

    /**
     * Elastic easing function that starts fast, then overshoots, and finally settles into the destination value
     */
    EASE_OUT_ELASTIC(x -> x == 0 ? 0 : x == 1 ? 1 : pow(2, -10 * x) * sin((x * 10 - 0.75) * ((2 * Math.PI) / 3)) * 0.5 + 1),

    /**
     * Back easing function that starts slow and speeds up
     */
    EASE_IN_BACK(x -> (1.70158 + 1) * x * x * x - 1.70158 * x * x);

    private final Function<Double, Double> function;

    Easing(final Function<Double, Double> function) {
        this.function = function;
    }
    public double apply(double arg) {
        return function.apply(arg);
    }
}