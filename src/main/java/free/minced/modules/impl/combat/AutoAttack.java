package free.minced.modules.impl.combat;


import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.movement.Speed;
import free.minced.primary.game.PlayerHandler;
import free.minced.systems.setting.impl.BooleanSetting;

@ModuleDescriptor(name = "AutoAttack", category = ModuleCategory.COMBAT)

public class AutoAttack extends Module {
    public BooleanSetting pauseEating = new BooleanSetting("Pause If Eating", this, true);
    public BooleanSetting onlyCriticals = new BooleanSetting("Only Criticals", this, true);
    public BooleanSetting spaceOnly = new BooleanSetting("Space Only", this, false, () -> !onlyCriticals.isEnabled());

    private int delay;

    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent) {
            if (mc.player.isUsingItem() && pauseEating.isEnabled()) {
                return;
            }

            if (delay > 0) {
                delay--;
                return;
            }

            if (!autoCrit()) return;

            Entity ent = mc.targetedEntity;
            if (ent != null) {
                boolean isBeforeSprint = mc.player.isSprinting();
                if (onlyCriticals.isEnabled()) {
                    PlayerHandler.disableSprint();
                }
                mc.interactionManager.attackEntity(mc.player, ent);
                mc.player.swingHand(Hand.MAIN_HAND);
                delay = 10;
                if (onlyCriticals.isEnabled() && isBeforeSprint) {
                    PlayerHandler.enableSprint();
                }
            }

        }
    }

    @Override
    public void onDisable() {
        delay = 0;
        super.onDisable();
    }

    private boolean autoCrit() {
        boolean reasonForSkipCrit =
                !onlyCriticals.isEnabled()
                        || mc.player.getAbilities().flying
                        || mc.player.isFallFlying()
                        || mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                        || mc.player.isHoldingOntoLadder()
                        || mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos())).getBlock() == Blocks.COBWEB;

        if (PlayerHandler.getAttackStrengthScale(0.5f) < (mc.player.isOnGround() ? 1f : 0.9f))
            return false;

        boolean mergeWithSpeed = !Minced.getInstance().getModuleHandler().get(Speed.class).isEnabled() || mc.player.isOnGround();

        if (!mc.options.jumpKey.isPressed() && mergeWithSpeed && spaceOnly.isEnabled())
            return true;

        if (mc.player.isInLava())
            return true;

        if (!mc.options.jumpKey.isPressed() && PlayerHandler.isAboveWater())
            return true;

        if (!reasonForSkipCrit)
            return !mc.player.isOnGround() && mc.player.fallDistance > 0.0f;
        return true;
    }
}