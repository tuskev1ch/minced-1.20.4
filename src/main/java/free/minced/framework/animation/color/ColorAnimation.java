package free.minced.framework.animation.color;


import lombok.Getter;
import lombok.Setter;
import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.framework.color.CustomColor;

import java.awt.*;

/**
 * @author jbk
 * @since 14.11.2023
 */
@Getter @Setter
public class ColorAnimation {

    private final long duration;
    private final Animation r, g, b;

    public ColorAnimation(long duration) {
        this.duration = duration;
        r = new Animation(Easing.EASE_OUT_CUBIC, duration);
        g = new Animation(Easing.EASE_OUT_CUBIC, duration);
        b = new Animation(Easing.EASE_OUT_CUBIC, duration);
    }

    public void run(Color color) {
        r.run(color.getRed());
        g.run(color.getGreen());
        b.run(color.getBlue());
    }

    public CustomColor getColor() {
        return new CustomColor(r.getNumberValue().intValue(), g.getNumberValue().intValue(), b.getNumberValue().intValue());
    }

    public void setEasing(Easing easing) {
        r.setEasing(easing);
        g.setEasing(easing);
        b.setEasing(easing);
    }

    public void setColor(Color color) {
        r.setValue(color.getRed());
        g.setValue(color.getGreen());
        b.setValue(color.getBlue());
    }

    public void setDuration(long duration) {
        r.setDuration(duration);
        g.setDuration(duration);
        b.setDuration(duration);
    }
}