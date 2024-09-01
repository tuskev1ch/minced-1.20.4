package free.minced.modules.api;

import com.google.gson.JsonObject;
import free.minced.primary.UnknownModuleException;
import lombok.Getter;
import free.minced.Minced;
import free.minced.modules.Module;

import free.minced.modules.impl.display.*;
import free.minced.modules.impl.display.hud.*;
import free.minced.modules.impl.display.hud.impl.*;

import free.minced.modules.impl.combat.*;
import free.minced.modules.impl.misc.*;
import free.minced.modules.impl.movement.*;
import free.minced.modules.impl.render.*;


import java.util.ArrayList;
import java.util.List;

/**
 * @author jbk
 * @since 05.10.2023
 * Менеджер модулей
 */

@Getter
public class ModuleManager  {

    // список с модулями
    public static final List<Module> modules = new ArrayList<>();

    public void initModules() {

        /* Category - Display */
        addModule(new HUD());
        addModule(new ClickGUI());
        addModule(new StaffHUD());
        addModule(new KeyBinds());
        addModule(new TargetHUD());
        addModule(new PotionHUD());
        addModule(new ArrayListMod());

        /* Category - Combat */
        addModule(new Reach());
        addModule(new HitBox());
        addModule(new AntiBot());
        addModule(new AutoSwap());
        addModule(new AutoTotem());
        addModule(new TPAura());
        addModule(new BackTrack());
        addModule(new AutoPotion());
        addModule(new AutoAttack());
        addModule(new AttackAura());

        /* Category - Movement */
        addModule(new Speed());
        addModule(new NoClip());
        addModule(new Strafe());
        addModule(new Flight());
        addModule(new Velocity());
        addModule(new Scaffold());
        addModule(new AutoSprint());
        addModule(new NoSlowDown());
        addModule(new TimerModule());
        addModule(new ElytraBounce());
        addModule(new CreativeFlight());

        /* Category - Misc */
        addModule(new UnHook());
        addModule(new NoPush());
        addModule(new NoDelay());
        addModule(new GuiMove());
        addModule(new AutoTool());
        addModule(new FreeCam());
        addModule(new ItemsFix());
        addModule(new AutoWalk());
        addModule(new ItemTimer());
        addModule(new PingSpoof());
        addModule(new ElytraFix());
        addModule(new NoInteract());
        addModule(new ElytraUtils());
        addModule(new ClientSpoof());
        addModule(new NameProtect());
        addModule(new KTLeave());
        addModule(new MiddleClick());
        addModule(new ItemScroller());
        addModule(new Optimization());
        addModule(new PluginsInspector());
        addModule(new ContainerStealer());

        /* Category - Render */
        addModule(new Trails());
        addModule(new Arrows());
        addModule(new NoRender());
        addModule(new ViewModel());
        addModule(new EntityESP());
        addModule(new TargetESP());
        addModule(new ItemPhysic());
        addModule(new FullBright());
        addModule(new CustomWorld());
        addModule(new Trajectories());
        addModule(new BlowParticles());
        addModule(new WorldParticles());
        addModule(new SwingAnimations());
    }

    /**
     * Добавляет модуль в список
     *
     * @param module модуль который будет добавлен
     */
    public void addModule(Module module) {
        modules.add(module);
    }

    /**
     * Get module by class
     */
    public <T extends Module> T get(final Class<T> clazz) {
        return (T) modules.stream()
                .filter(module -> module.getClass().equals(clazz))
                .findFirst()
                .orElseThrow(() -> new UnknownModuleException(clazz.getSimpleName()));
    }


    public JsonObject save() {
        JsonObject json = new JsonObject();
        for (Module module : modules) {
            try {
                String moduleName = module.getName().toLowerCase().replace(" ", "");
                json.add(moduleName, module.save());
            } catch (Exception e) {
                Minced.LOGGER.error("[{}] Failed to save modules data", "MODULE-MANAGER");
                e.printStackTrace();
            }
        }
        return json;
    }
    public void load(JsonObject json) {
        for (Module module : modules) {
            // для избежания проблем с этим ебнутым говном
            try {
                String moduleName = module.getName().toLowerCase().replace(" ", "");

                if (!json.has(moduleName)) continue;

                module.load(json.getAsJsonObject(moduleName));


            } catch (Exception e) {
                Minced.LOGGER.error("[{}] Failed to load modules data", "MODULE-MANAGER");
                e.printStackTrace();
            }
        }
    }

    public static void onKeyPress(int key) {
        for (Module module : modules) {

            if (module == Minced.getInstance().getModuleHandler().get(ClickGUI.class)) continue;

            if (key == module.getKey()) {
                module.toggle();
            }
        }
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<>();
        for (Module module : modules) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }
}