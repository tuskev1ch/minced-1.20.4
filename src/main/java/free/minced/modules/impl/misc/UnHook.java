package free.minced.modules.impl.misc;

import free.minced.Minced;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;

import java.util.List;

@ModuleDescriptor(name = "UnHook", category = ModuleCategory.MISC)
public class UnHook extends Module {
    List<Module> list;

    @Override
    public void onEnable() {
        list = Minced.getInstance().getModuleHandler().getEnabledModules();
        for (Module module : list) {
            if (module.equals(this)) {
                continue;
            }

            module.disable();
        }
    }

    @Override
    public void onDisable() {
        if (list == null) return;
        for (Module module : list) {
            if (module.equals(this)) {
                continue;
            }
            module.enable();
        }
    }
}