package free.minced.systems.macros;

import lombok.Getter;
import lombok.Setter;


/**
 * @author jbk
 * @since 01.01.2024
 */
@Getter @Setter
public class Macros {

    private final String name, message;
    private int key;

    public Macros(String name, int key, String message) {
        this.name = name;
        this.message = message;
        this.key = key;
    }
}
