package free.minced.events.impl.mobility;

import free.minced.events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
public class ElytraFixEvent extends Event {
    float yaw;
    float pitch;
}
