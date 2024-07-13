package free.minced.modules.api;

import org.lwjgl.glfw.GLFW;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author jbk
 * @since 05.10.2023
 * Аннотация для всех модулей
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleDescriptor {

    /**
     * имя модуля
     */
    String name();

    /**
     * категория модуля
     */
    ModuleCategory category();

    /**
     * бинд (кнопка) модуля
     * Вводить каждый раз не обязательно
     */
    int key() default GLFW.GLFW_KEY_UNKNOWN;

    /**
     * видимость модуля
     * Вводить каждый раз не обязательно
     */
    boolean hidden() default false;

}