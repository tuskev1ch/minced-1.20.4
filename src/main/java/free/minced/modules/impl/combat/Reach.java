package free.minced.modules.impl.combat;

import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.MultiBoxSetting;
import free.minced.systems.setting.impl.NumberSetting;

@ModuleDescriptor(name = "Reach", category = ModuleCategory.COMBAT)
public class Reach extends Module {
    private final MultiBoxSetting types = new MultiBoxSetting("Types",this,"Blocks","Players");
    private final NumberSetting range = new NumberSetting("Range",this,1,1,6,1);

    public boolean getPlayers() {
        return types.get("Players").isEnabled();
    }

    public boolean getBlocks() {
        return types.get("Blocks").isEnabled();
    }

    public float getRange() {
        return range.getValue().floatValue();
    }
}
