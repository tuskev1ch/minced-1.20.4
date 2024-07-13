package free.minced.modules.impl.movement;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import free.minced.events.Event;
import free.minced.events.impl.mobility.EventBreakBlock;
import free.minced.events.impl.mobility.EventCollision;
import free.minced.events.impl.player.EventSync;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.mixin.accesors.ILivingEntity;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.game.InventoryHandler;
import free.minced.primary.game.MobilityHandler;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;

import static free.minced.primary.game.InventoryHandler.getTool;

@ModuleDescriptor(name = "NoClip", category = ModuleCategory.MOVEMENT)
public class NoClip extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "None", "None", "Matrix");
    public BooleanSetting silent = new BooleanSetting("Silent", this, false, () -> !mode.is("Matrix"));
    public BooleanSetting waitBreak = new BooleanSetting("Wait Break", this, false, () -> !mode.is("Matrix"));

    public int clipTimer;

    @Override
    public void onEvent(Event event) {
        if (IHolder.fullNullCheck()) {
            return;
        }
        if (event instanceof EventCollision e) {
            BlockPos playerPos = BlockPos.ofFloored(mc.player.getPos());

            if (canNoClip() && playerInsideBlock()) {
                if (!e.getPos().equals(playerPos.down()) || mc.options.sneakKey.isPressed()) {
                    e.setState(Blocks.AIR.getDefaultState());
                }
            }
        }
        if (event instanceof EventSync e) {
            if (clipTimer > 0) clipTimer--;

            if (mode.is("Matrix") && (mc.player.horizontalCollision || playerInsideBlock()) && !mc.player.isSubmergedInWater() && !mc.player.isInLava()) {
                double[] dir = MobilityHandler.forward(0.5);

                BlockPos blockToBreak = null;

                if (mc.options.jumpKey.isPressed()) {
                    blockToBreak = BlockPos.ofFloored(mc.player.getX() + dir[0], mc.player.getY() + 2, mc.player.getZ() + dir[1]);
                } else if (mc.options.sneakKey.isPressed()) {
                    blockToBreak = BlockPos.ofFloored(mc.player.getX() + dir[0], mc.player.getY() - 1, mc.player.getZ() + dir[1]);
                } else if (MobilityHandler.isMoving()) {
                    blockToBreak = BlockPos.ofFloored(mc.player.getX() + dir[0], mc.player.getY(), mc.player.getZ() + dir[1]);
                }

                if (blockToBreak == null) return;
                int best_tool = getTool(blockToBreak);
                if (best_tool == -1) return;

                int prevItem = mc.player.getInventory().selectedSlot;


                InventoryHandler.switchTo(best_tool);

                mc.interactionManager.updateBlockBreakingProgress(blockToBreak, mc.player.getHorizontalFacing());
                mc.player.swingHand(Hand.MAIN_HAND);

                if (silent.isEnabled()) {
                    InventoryHandler.switchTo(prevItem);
                }
            }
        }
        if (event instanceof EventBreakBlock e) {
            clipTimer = 3;
        }
    }

    @Override
    public void onEnable() {
        clipTimer = 0;
        super.onEnable();
    }

    public boolean canNoClip() {
        if (mode.is("None")) return true;
        if (!waitBreak.isEnabled()) return true;
        return clipTimer != 0;
    }

    public boolean playerInsideBlock() {
        return !mc.world.isAir(BlockPos.ofFloored(mc.player.getPos()));
    }

}
