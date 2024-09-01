package free.minced.modules.impl.misc;

import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.ModeSetting;
import free.minced.systems.setting.impl.StringSetting;
import net.minecraft.block.Block;
import net.minecraft.util.hit.BlockHitResult;

@ModuleDescriptor(name = "ClientSpoof", category = ModuleCategory.MISC)
public class ClientSpoof extends Module {
    public final ModeSetting mode = new ModeSetting("Mode",  this, "Vanilla",  "Vanilla", "Lunar1_20_4", "Lunar1_20_1", "Custom");
    private final StringSetting name = new StringSetting("Message", this, "Minced", 12, () -> !mode.is("Custom"));

    public String getClientName() {
        switch (mode.getCurrentMode()) {
            case "Vanilla" -> {
                return "vanilla";
            }
            case "Lunar1_20_4" -> {
                return "lunarclient:1.20.4";
            }
            case "Lunar1_20_1" -> {
                return "lunarclient:1.20.1";
            }
            case "Custom" -> {
                return name.getText();
            }
            default ->
            {
                return null;
            }
        }
    }
}
