package free.minced.primary.math;

import lombok.Getter;
import lombok.Setter;
import free.minced.Minced;
import free.minced.primary.IHolder;
import free.minced.primary.math.MathHandler;

@Getter
@Setter
public class ScrollHandler implements IHolder {

    private float target;
    private float scroll;
    private float max;

    public ScrollHandler() {

    }

    public void handle() {

        float speed = 6;
        float wheel = Minced.getInstance().getInterfaceScreen().dWheel * (speed * 10F);
        float stretch = 0;
        // анимированое значение
        scroll = MathHandler.lerp(scroll, target, speed / 40);

        // ну хуйня дря границ йобаная
        target = Math.min(Math.max(target + (wheel / 2F), max - (wheel == 0 ? 0 : stretch)), (wheel == 0 ? 0 : stretch));
        Minced.getInstance().getInterfaceScreen().dWheel = 0;
    }

    public void reset() {
        scroll = 0;
        target = 0;
    }


}