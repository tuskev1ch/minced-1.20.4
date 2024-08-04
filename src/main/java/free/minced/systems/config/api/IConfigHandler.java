package free.minced.systems.config.api;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jbk
 */
@Getter
@Setter
public abstract class IConfigHandler<T> {

    public List<T> contents = new ArrayList<>();
}