package free.minced.modules.impl.render;

import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.NumberSetting;

@ModuleDescriptor(name = "FullBright", category = ModuleCategory.RENDER)
public class FullBright extends Module {
    public final NumberSetting brightness = new NumberSetting("Brightness", this, 15, 0, 15, 1);
}