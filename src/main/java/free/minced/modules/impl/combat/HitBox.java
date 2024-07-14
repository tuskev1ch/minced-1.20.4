package free.minced.modules.impl.combat;


import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@ModuleDescriptor(name = "HitBox", category = ModuleCategory.COMBAT)
public class HitBox extends Module {
    public final NumberSetting size = new NumberSetting("Size", this, 0.2f,0.1f,2f,0.1f);

}
