package free.minced.modules.impl.render;

import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.MultiBoxSetting;
import lombok.Getter;

@Getter
@ModuleDescriptor(name = "No Render", category = ModuleCategory.RENDER)
public class NoRender extends Module {

    private final MultiBoxSetting comboBoxSetting = new MultiBoxSetting("Removals", this,
            "Block Overlay",
            "Water Overlay",
            "Fire Overlay",
            "HurtCam"
    );

    public boolean canRemoveWaterOverlay() {
        return this.isEnabled() && comboBoxSetting.get("Water Overlay").isEnabled();
    }

    public boolean canRemoveBlockOverlay() {
        return this.isEnabled() && comboBoxSetting.get("Block Overlay").isEnabled();
    }

    public boolean canRemoveFireOverlay() {
        return this.isEnabled() && comboBoxSetting.get("Fire Overlay").isEnabled();
    }

    public boolean canRemoveHurtCam() {
        return this.isEnabled() && comboBoxSetting.get("HurtCam").isEnabled();
    }
}