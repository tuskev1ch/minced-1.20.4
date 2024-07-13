package free.minced.modules.impl.misc;

import lombok.Getter;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.MultiBoxSetting;

@Getter
@ModuleDescriptor(name = "NoPush", category = ModuleCategory.MISC)
public class NoPush extends Module {

    private final MultiBoxSetting removeFrom = new MultiBoxSetting("Remove", this, "Blocks", "Water", "Entities");

}