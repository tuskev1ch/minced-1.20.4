package free.minced.modules;


import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import free.minced.Minced;

import free.minced.events.Event;
import free.minced.events.EventCollects;
import free.minced.events.EventLogic;

import free.minced.framework.animation.normal.Animation;
import free.minced.framework.animation.normal.Easing;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.misc.UnHook;
import free.minced.primary.IHolder;
import free.minced.primary.module.ModuleHandler;
import free.minced.primary.time.TimerHandler;
import free.minced.systems.setting.Setting;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jbk
 * @since 05.10.2023
 * Суперкласс для всех модулей
 */

@Getter @Setter
public abstract class Module extends ModuleHandler implements IHolder, EventLogic {

    public static TimerHandler setBackTimer = new TimerHandler();
    /**
     * аннотация из которой берется информация для модулей (их категория, название, и кнопка)
     */
    private ModuleDescriptor annotation = this.getClass().getAnnotation(ModuleDescriptor.class);

    @Getter
    private final Animation animation = new Animation(Easing.EASE_IN_OUT_CUBIC, 600);

    private final Animation enableAnimation = new Animation(Easing.EASE_IN_OUT_CUBIC, 300);
    private final Animation yAnimation = new Animation(Easing.EASE_OUT_ELASTIC, 350);


    private static final Map<Class<? extends Event>, Event> lastProcessedEvents = new ConcurrentHashMap<>();

    /**
     * имя модуля
     */
    private final String name;

    /**
     * бинд (кнопка) модуля
     */
    private int key;

    /**
     * состояние модуля
     */
    private boolean enabled;

    /**
     * категория модуля
     */
    private final ModuleCategory moduleCategory;

    /**
     * видимость модуля
     */
    private boolean hidden;

    /**
     * конструктор
     */
    public Module() {
        this.name = getAnnotation().name();
        this.moduleCategory = getAnnotation().category();
        this.key = getAnnotation().key();
        this.hidden = getAnnotation().hidden();
    }

    /**
     * Сеттинги модуля
     */
    private final List<Setting> settings = new ArrayList<>();

    /**
     * Обновляет состояние модуля
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    /**
     * Используется для переключения модуля.
     * Например, если модуль был отключен - при вызове этого метода он включится, если нет - отключится.
     */

    public void toggle() {
        setEnabled(! enabled);
    }

    /**
     * Включает модуль
     */

    public void enable() {
        setEnabled(true);
    }

    /**
     * Отключает модуль
     */
    public Module set(boolean enabled) {
        this.enabled = enabled;

        return this;
    }
    public void disable() {
        setEnabled(false);
    }

    /**
     * этот метод вызывается при выключении модуля
     */
    public void onDisable() {

        EventCollects.unRegisterListener(this);
    }

    /**
     * этот метод вызывается при включении модуля
     */
    
    public final void load(JsonObject json) {

        // меняем бинд модуля
        setKey(json.get("key").getAsInt());

        // dumb fix
        if (!getName().equalsIgnoreCase("freeCam")) {
            // меняем состояние модуля
            setEnabled(json.get("enabled").getAsBoolean());
        }

        // изменяем настройки модуля
        for (Setting setting : settings) {
            if (! json.has(setting.getName())) continue;

            setting.load(json.get(setting.getName()));
        }

    }

    public void onEnable() {
        if (Minced.getInstance().getModuleHandler().get(UnHook.class).isEnabled()) return;

        EventCollects.registerListener(this);
    }

    public long getSetBackTime() {
        return setBackTimer.getPassedTimeMs();
    }
    public JsonObject save() {
        JsonObject json = new JsonObject();

        // сохраняем кнопку модуля
        json.addProperty("key", getKey());

        // сохраняем состояние модуля
        json.addProperty("enabled", isEnabled());

        // сохраняем настройки модуля
        for (Setting setting : settings) {
            json.add(setting.getName(), setting.save());
        }
        return json;
    }

    @Override
    public void onEvent(Event event) {

    }

}