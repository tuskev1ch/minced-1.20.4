package free.minced.modules.impl.misc;

import free.minced.Minced;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.StringSetting;
import net.minecraft.entity.player.PlayerEntity;

@ModuleDescriptor(name = "NameProtect", category = ModuleCategory.MISC)
public class NameProtect extends Module {
    public final BooleanSetting hideFriends = new BooleanSetting("Hide Friends", this, false);
    private final StringSetting name = new StringSetting("Message", this, "Minced", 12);


    public static String getCustomName() {
        return Minced.getInstance().getModuleHandler().get(NameProtect.class).isEnabled() ?
                Minced.getInstance().getModuleHandler().get(NameProtect.class).name.getText().replaceAll("&", "\u00a7")
                : mc.getGameProfile().getName();
    }

    public static String getCustomName(PlayerEntity entity) {
        return Minced.getInstance().getModuleHandler().get(NameProtect.class).isEnabled() ?
                Minced.getInstance().getModuleHandler().get(NameProtect.class).name.getText().replaceAll("&", "\u00a7")
                : entity.getGameProfile().getName();
    }
}