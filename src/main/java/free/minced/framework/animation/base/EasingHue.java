package free.minced.framework.animation.base;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lavache
 * @see <a href="https://github.com/LaVache-FR/AnimationUtil/blob/main/fr/lavache/anime/EasingHue.java">original code</a>
 */

public interface EasingHue {

    /**
     * Simple linear tweening - no EasingHue.
     */
    EasingHue LINEAR = (t, b, c, d) -> c * t / d + b;
    /**
     * Quadratic EasingHue in - accelerating from zero velocity.
     */
    EasingHue QUAD_IN = (t, b, c, d) -> c * (t /= d) * t + b;

    ///////////// QUADRATIC EasingHue: t^2 ///////////////////
    /**
     * Quadratic EasingHue out - decelerating to zero velocity.
     */
    EasingHue QUAD_OUT = (t, b, c, d) -> - c * (t /= d) * (t - 2) + b;
    /**
     * Quadratic EasingHue in/out - acceleration until halfway, then deceleration
     */
    EasingHue QUAD_IN_OUT = (t, b, c, d) -> {
        if ((t /= d / 2) < 1) return c / 2 * t * t + b;
        return - c / 2 * ((-- t) * (t - 2) - 1) + b;
    };
    /**
     * Cubic EasingHue in - accelerating from zero velocity.
     */
    EasingHue CUBIC_IN = (t, b, c, d) -> c * (t /= d) * t * t + b;


    ///////////// CUBIC EasingHue: t^3 ///////////////////////
    /**
     * Cubic EasingHue out - decelerating to zero velocity.
     */
    EasingHue CUBIC_OUT = (t, b, c, d) -> c * ((t = t / d - 1) * t * t + 1) + b;
    /**
     * Cubic EasingHue in/out - acceleration until halfway, then deceleration.
     */
    EasingHue CUBIC_IN_OUT = (t, b, c, d) -> {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t + 2) + b;
    };
    /**
     * Quartic EasingHue in - accelerating from zero velocity.
     */
    EasingHue QUARTIC_IN = (t, b, c, d) -> c * (t /= d) * t * t * t + b;

    ///////////// QUARTIC EasingHue: t^4 /////////////////////
    /**
     * Quartic EasingHue out - decelerating to zero velocity.
     */
    EasingHue QUARTIC_OUT = (t, b, c, d) -> - c * ((t = t / d - 1) * t * t * t - 1) + b;
    /**
     * Quartic EasingHue in/out - acceleration until halfway, then deceleration.
     */
    EasingHue QUARTIC_IN_OUT = (t, b, c, d) -> {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t + b;
        return - c / 2 * ((t -= 2) * t * t * t - 2) + b;
    };
    /**
     * Quintic EasingHue in - accelerating from zero velocity.
     */
    EasingHue QUINTIC_IN = (t, b, c, d) -> c * (t /= d) * t * t * t * t + b;

    ///////////// QUINTIC EasingHue: t^5  ////////////////////
    /**
     * Quintic EasingHue out - decelerating to zero velocity.
     */
    EasingHue QUINTIC_OUT = (t, b, c, d) -> c * ((t = t / d - 1) * t * t * t * t + 1) + b;
    /**
     * Quintic EasingHue in/out - acceleration until halfway, then deceleration.
     */
    EasingHue QUINTIC_IN_OUT = (t, b, c, d) -> {
        if ((t /= d / 2) < 1) return c / 2 * t * t * t * t * t + b;
        return c / 2 * ((t -= 2) * t * t * t * t + 2) + b;
    };
    /**
     * Sinusoidal EasingHue in - accelerating from zero velocity.
     */
    EasingHue SINE_IN = (t, b, c, d) -> - c * (float) Math.cos(t / d * (Math.PI / 2)) + c + b;


    ///////////// SINUSOIDAL EasingHue: sin(t) ///////////////
    /**
     * Sinusoidal EasingHue out - decelerating to zero velocity.
     */
    EasingHue SINE_OUT = (t, b, c, d) -> c * (float) Math.sin(t / d * (Math.PI / 2)) + b;
    /**
     * Sinusoidal EasingHue in/out - accelerating until halfway, then decelerating.
     */
    EasingHue SINE_IN_OUT = (t, b, c, d) -> - c / 2 * ((float) Math.cos(Math.PI * t / d) - 1) + b;
    /**
     * Exponential EasingHue in - accelerating from zero velocity.
     */
    EasingHue EXPO_IN = (t, b, c, d) -> (t == 0) ? b : c * (float) Math.pow(2, 10 * (t / d - 1)) + b;

    ///////////// EXPONENTIAL EasingHue: 2^t /////////////////
    /**
     * Exponential EasingHue out - decelerating to zero velocity.
     */
    EasingHue EXPO_OUT = (t, b, c, d) -> (t == d) ? b + c : c * (- (float) Math.pow(2, - 10 * t / d) + 1) + b;
    /**
     * Exponential EasingHue in/out - accelerating until halfway, then decelerating.
     */
    EasingHue EXPO_IN_OUT = (t, b, c, d) -> {
        if (t == 0) return b;
        if (t == d) return b + c;
        if ((t /= d / 2) < 1) return c / 2 * (float) Math.pow(2, 10 * (t - 1)) + b;
        return c / 2 * (- (float) Math.pow(2, - 10 * -- t) + 2) + b;
    };
    /**
     * Circular EasingHue in - accelerating from zero velocity.
     */
    EasingHue CIRC_IN = (t, b, c, d) -> - c * ((float) Math.sqrt(1 - (t /= d) * t) - 1) + b;


    /////////// CIRCULAR EasingHue: sqrt(1-t^2) //////////////
    /**
     * Circular EasingHue out - decelerating to zero velocity.
     */
    EasingHue CIRC_OUT = (t, b, c, d) -> c * (float) Math.sqrt(1 - (t = t / d - 1) * t) + b;
    /**
     * Circular EasingHue in/out - acceleration until halfway, then deceleration.
     */
    EasingHue CIRC_IN_OUT = (t, b, c, d) -> {
        if ((t /= d / 2) < 1) return - c / 2 * ((float) Math.sqrt(1 - t * t) - 1) + b;
        return c / 2 * ((float) Math.sqrt(1 - (t -= 2) * t) + 1) + b;
    };
    /**
     * An EasingHueIn instance using the default values.
     */
    Elastic ELASTIC_IN = new ElasticIn();

    /////////// ELASTIC EasingHue: exponentially decaying sine wave  //////////////
    /**
     * An ElasticOut instance using the default values.
     */
    Elastic ELASTIC_OUT = new ElasticOut();
    /**
     * An ElasticInOut instance using the default values.
     */
    Elastic ELASTIC_IN_OUT = new ElasticInOut();
    /**
     * An instance of BackIn using the default overshoot.
     */
    Back BACK_IN = new BackIn();
    /**
     * An instance of BackOut using the default overshoot.
     */
    Back BACK_OUT = new BackOut();
    /**
     * An instance of BackInOut using the default overshoot.
     */
    Back BACK_IN_OUT = new BackInOut();
    /**
     * Bounce EasingHue out.
     */
    EasingHue BOUNCE_OUT = (t, b, c, d) -> {
        if ((t /= d) < (1 / 2.75f)) {
            return c * (7.5625f * t * t) + b;
        } else if (t < (2 / 2.75f)) {
            return c * (7.5625f * (t -= (1.5f / 2.75f)) * t + .75f) + b;
        } else if (t < (2.5f / 2.75f)) {
            return c * (7.5625f * (t -= (2.25f / 2.75f)) * t + .9375f) + b;
        } else {
            return c * (7.5625f * (t -= (2.625f / 2.75f)) * t + .984375f) + b;
        }
    };
    /**
     * Bounce EasingHue in.
     */
    EasingHue BOUNCE_IN = (t, b, c, d) -> c - EasingHue.BOUNCE_OUT.ease(d - t, 0, c, d) + b;

    /////////// BACK EasingHue: overshooting cubic EasingHue: (s+1)*t^3 - s*t^2  //////////////
    /**
     * Bounce EasingHue in/out.
     */
    EasingHue BOUNCE_IN_OUT = (t, b, c, d) -> {
        if (t < d / 2) return EasingHue.BOUNCE_IN.ease(t * 2, 0, c, d) * .5f + b;
        return EasingHue.BOUNCE_OUT.ease(t * 2 - d, 0, c, d) * .5f + c * .5f + b;
    };

    /**
     * The basic function for EasingHue.
     *
     * @param t the time (either frames or in seconds/milliseconds)
     * @param b the beginning value
     * @param c the value changed
     * @param d the duration time
     * @return the eased value
     */
    float ease(float t, float b, float c, float d);

    /**
     * A base class for elastic EasingHues.
     */
    @Setter
    @Getter
    abstract class Elastic implements EasingHue {
        private float amplitude;
        private float period;

        /**
         * Creates a new Elastic EasingHue with the specified settings.
         *
         * @param amplitude the amplitude for the elastic function
         * @param period    the period for the elastic function
         */
        public Elastic(float amplitude, float period) {
            this.amplitude = amplitude;
            this.period = period;
        }

        /**
         * Creates a new Elastic EasingHue with default settings (-1f, 0f).
         */
        public Elastic() {
            this(- 1f, 0f);
        }

    }

    /**
     * An Elastic EasingHue used for ElasticIn functions.
     */
    class ElasticIn extends Elastic {
        public ElasticIn(float amplitude, float period) {
            super(amplitude, period);
        }

        public ElasticIn() {
            super();
        }

        public float ease(float t, float b, float c, float d) {
            float a = getAmplitude();
            float p = getPeriod();
            if (t == 0) return b;
            if ((t /= d) == 1) return b + c;
            if (p == 0) p = d * .3f;
            float s = 0;
            if (a < Math.abs(c)) {
                a = c;
                s = p / 4;
            } else s = p / (float) (2 * Math.PI) * (float) Math.asin(c / a);
            return - (a * (float) Math.pow(2, 10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
        }
    }

    /**
     * An Elastic EasingHue used for ElasticOut functions.
     */
    class ElasticOut extends Elastic {
        public ElasticOut(float amplitude, float period) {
            super(amplitude, period);
        }

        public ElasticOut() {
            super();
        }

        public float ease(float t, float b, float c, float d) {
            float a = getAmplitude();
            float p = getPeriod();
            if (t == 0) return b;
            if ((t /= d) == 1) return b + c;
            if (p == 0) p = d * .3f;
            float s = 0;
            if (a < Math.abs(c)) {
                a = c;
                s = p / 4;
            } else s = p / (float) (2 * Math.PI) * (float) Math.asin(c / a);
            return a * (float) Math.pow(2, - 10 * t) * (float) Math.sin((t * d - s) * (2 * Math.PI) / p) + c + b;
        }
    }

    /**
     * An Elastic EasingHue used for ElasticInOut functions.
     */
    class ElasticInOut extends Elastic {
        public ElasticInOut(float amplitude, float period) {
            super(amplitude, period);
        }

        public ElasticInOut() {
            super();
        }

        public float ease(float t, float b, float c, float d) {
            float a = getAmplitude();
            float p = getPeriod();
            if (t == 0) return b;
            if ((t /= d / 2) == 2) return b + c;
            if (p == 0) p = d * (.3f * 1.5f);
            float s = 0;
            if (a < Math.abs(c)) {
                a = c;
                s = p / 4f;
            } else s = p / (float) (2 * Math.PI) * (float) Math.asin(c / a);
            if (t < 1)
                return - .5f * (a * (float) Math.pow(2, 10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * Math.PI) / p)) + b;
            return a * (float) Math.pow(2, - 10 * (t -= 1)) * (float) Math.sin((t * d - s) * (2 * Math.PI) / p) * .5f + c + b;
        }
    }

    /**
     * A base class for Back EasingHues.
     */
    abstract class Back implements EasingHue {
        /**
         * The default overshoot is 10% (1.70158).
         */
        public static final float DEFAULT_OVERSHOOT = 1.70158f;

        private float overshoot;

        /**
         * Creates a new Back instance with the default overshoot (1.70158).
         */
        public Back() {
            this(DEFAULT_OVERSHOOT);
        }

        /**
         * Creates a new Back instance with the specified overshoot.
         *
         * @param overshoot the amount to overshoot by -- higher number
         *                  means more overshoot and an overshoot of 0 results in
         *                  cubic EasingHue with no overshoot
         */
        public Back(float overshoot) {
            this.overshoot = overshoot;
        }

        /**
         * Returns the overshoot for this EasingHue.
         *
         * @return this EasingHue's overshoot
         */
        public float getOvershoot() {
            return overshoot;
        }

        /**
         * Sets the overshoot to the given value.
         *
         * @param overshoot the new overshoot
         */
        public void setOvershoot(float overshoot) {
            this.overshoot = overshoot;
        }
    }

    /////////// BOUNCE EasingHue: exponentially decaying parabolic bounce  //////////////

    /**
     * Back EasingHue in - backtracking slightly, then reversing direction and moving to target.
     */
    class BackIn extends Back {
        public BackIn() {
            super();
        }

        public BackIn(float overshoot) {
            super(overshoot);
        }

        public float ease(float t, float b, float c, float d) {
            float s = getOvershoot();
            return c * (t /= d) * t * ((s + 1) * t - s) + b;
        }
    }

    /**
     * Back EasingHue out - moving towards target, overshooting it slightly, then reversing and coming back to target.
     */
    class BackOut extends Back {
        public BackOut() {
            super();
        }

        public BackOut(float overshoot) {
            super(overshoot);
        }

        public float ease(float t, float b, float c, float d) {
            float s = getOvershoot();
            return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
        }
    }

    /**
     * Back EasingHue in/out - backtracking slightly, then reversing direction and moving to target,
     * then overshooting target, reversing, and finally coming back to target.
     */
    class BackInOut extends Back {
        public BackInOut() {
            super();
        }

        public BackInOut(float overshoot) {
            super(overshoot);
        }

        public float ease(float t, float b, float c, float d) {
            float s = getOvershoot();
            if ((t /= d / 2) < 1) return c / 2 * (t * t * (((s *= (1.525)) + 1) * t - s)) + b;
            return c / 2 * ((t -= 2) * t * (((s *= (1.525)) + 1) * t + s) + 2) + b;
        }
    }
}