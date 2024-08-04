package free.minced.events.impl.mobility;


import free.minced.events.Event;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventMove extends Event {
    public double x, y, z;

    public EventMove( double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
