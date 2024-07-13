package free.minced.modules.impl.movement;

import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.game.MobilityHandler;
import free.minced.primary.other.ServerHandler;
import free.minced.systems.setting.impl.BooleanSetting;

@ModuleDescriptor(name = "AutoSprint", category = ModuleCategory.MOVEMENT)
public class AutoSprint extends Module {

    public BooleanSetting keepSprint = new BooleanSetting("Keep Sprint", this, false);
    public BooleanSetting rageSprint = new BooleanSetting("Rage Sprint", this, true);
    public BooleanSetting stopIfUsingItem = new BooleanSetting("Stop If Using Item", this, false, () -> !rageSprint.isEnabled());

    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {
            if (IHolder.fullNullCheck()) return;
            if (rageSprint.isEnabled()) {
                mc.player.setSprinting(
                        mc.player.getHungerManager().getFoodLevel() > 6
                                && !mc.player.horizontalCollision
                                && !(mc.player.input.movementForward < 0)
                                && !mc.player.isSneaking()
                                && (!mc.player.isUsingItem() || !stopIfUsingItem.isEnabled())
                                && isMoving());
            } else {
                mc.options.sprintKey.setPressed(isMoving()); // Зажимаем кнопку спринта
            }
        }
    }
}