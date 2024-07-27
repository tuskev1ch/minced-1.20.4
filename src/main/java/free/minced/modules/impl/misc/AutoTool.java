package free.minced.modules.impl.misc;

import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import net.minecraft.block.Block;
import net.minecraft.util.hit.BlockHitResult;

@ModuleDescriptor(name = "AutoTool", category = ModuleCategory.MISC)
public class AutoTool extends Module {

    private int previousSlot = -1;

    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {

            if (isNull() || mc.player.isCreative()) {
                previousSlot = -1;
                return;
            }

            if (mc.interactionManager.isBreakingBlock() && previousSlot == -1) {
                previousSlot = mc.player.getInventory().selectedSlot;
            }

            if (mc.interactionManager.isBreakingBlock()) {
                int toolSlot = findOptimalTool();
                if (toolSlot != -1) {
                    mc.player.getInventory().selectedSlot = toolSlot;
                }
            } else {
                if (previousSlot != -1) {
                    mc.player.getInventory().selectedSlot = previousSlot;
                    previousSlot = -1;
                }
            }
        }
    }

    private int findOptimalTool() {
        if (isNull()) return 0;

        if (mc.crosshairTarget instanceof BlockHitResult blockHitResult) {
            Block block = mc.world.getBlockState(blockHitResult.getBlockPos()).getBlock();
            return findTool(block);
        }
        return -1;

    }

    private int findTool(Block block) {
        int bestSlot = -1;
        float bestSpeed = 1.0f;

        for (int i = 0; i < 9; i++) {
            float speed = getMiningSpeed(i, block);

            if (speed > bestSpeed) {
                bestSpeed = speed;
                bestSlot = i;
            }
        }
        return bestSlot;
    }

    private float getMiningSpeed(int slot, Block block) {
        if (isNull()) return 0.0f;
        return mc.player.getInventory().getStack(slot).getMiningSpeedMultiplier(block.getDefaultState());
    }

    private boolean isNull() {
        return mc.player == null || mc.world == null || mc.interactionManager == null;
    }

    @Override
    public void onDisable() {
        previousSlot = -1;
        super.onDisable();
    }
}
