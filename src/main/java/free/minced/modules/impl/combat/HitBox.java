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
    private final NumberSetting size = new NumberSetting("Size", this, 0,1,4f,1);
    @Override
    public void onEvent(Event event) {
        if(event instanceof UpdatePlayerEvent e) {
            if (mc.player == null || mc.world == null) return;
            for (PlayerEntity players : mc.world.getPlayers()) {
                if (players == mc.player) continue;

                Box originalBox = players.getBoundingBox();

                float newSize = size.getValue().floatValue();
                Vec3d center = originalBox.getCenter();
                Box newBox = new Box(center.x - newSize / 2, center.y - newSize / 2, center.z - newSize / 2,
                        center.x + newSize / 2, center.y + newSize / 2, center.z + newSize / 2);

                players.setBoundingBox(newBox);
            }
        }
    }

}
