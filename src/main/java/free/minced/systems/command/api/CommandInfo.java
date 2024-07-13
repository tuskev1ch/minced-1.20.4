package free.minced.systems.command.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {

    /**
     * Название команды
     */
    String name();

    /**
     * Возможные вариации написания команды
     */
    String[] aliases();

    /**
     * Описание команды (его не обязательно использовать, но желательно)
     */
    String description() default "this command has no description";

}