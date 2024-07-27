package free.minced.modules.impl.misc;

import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.MultiBoxSetting;
import lombok.Getter;
// wip
@Getter
@ModuleDescriptor(name = "Optimization", category = ModuleCategory.MISC)
public class Optimization extends Module {

    private final MultiBoxSetting limits = new MultiBoxSetting("Limits", this, "Dropped Items", "Particles");
    private final BooleanSetting allParticles = new BooleanSetting("All Particles", this, false);
}