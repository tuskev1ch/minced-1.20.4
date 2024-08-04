package free.minced.modules.impl.misc;

import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.math.MathHandler;
import free.minced.primary.time.TimerHandler;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ModuleDescriptor(name = "ContainerStealer", category = ModuleCategory.MISC)
public class ContainerStealer extends Module {

    public final NumberSetting delay = new NumberSetting("Delay",this,0,0,1500,0.1f);
    private final BooleanSetting randomDelay = new BooleanSetting("Random Delay", this, false);

    private final BooleanSetting autoClose = new BooleanSetting("Auto Close", this, false);

    private final BooleanSetting autoMyst = new BooleanSetting("Auto Myst", this, false);

    public final List<String> items = new ArrayList<>();
    private final TimerHandler autoMystDelay = new TimerHandler();
    private final TimerHandler timer = new TimerHandler();
    private final Random rnd = new Random();

    @Override
    public void onEvent(Event e) {
        if (e instanceof UpdatePlayerEvent event) {
            if (mc.player.currentScreenHandler instanceof GenericContainerScreenHandler chest) {
                for (int i = 0; i < chest.getInventory().size(); i++) {
                    Slot slot = chest.getSlot(i);
                    if (slot.hasStack()
                            && timer.every(delay.getValue().intValue() + (randomDelay.isEnabled() && delay.getValue().intValue() != 0 ? rnd.nextInt(delay.getValue().intValue()) : 0))
                            && !(mc.currentScreen.getTitle().getString().contains("Аукцион") || mc.currentScreen.getTitle().getString().contains("покупки"))) {
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
                        autoMystDelay.reset();
                    }
                }
                if (isContainerEmpty(chest) && autoClose.isEnabled())
                    mc.player.closeHandledScreen();
            }

            if (autoMyst.isEnabled() && mc.currentScreen == null && autoMystDelay.passedMs(3000)) {
                for (BlockEntity be : getBlockEntities()) {
                    if (be instanceof EnderChestBlockEntity) {
                        if (mc.player.squaredDistanceTo(be.getPos().toCenterPos()) > 39)
                            continue;
                        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(be.getPos().toCenterPos().add(MathHandler.random(-0.4f, 0.4f), 0.375, MathHandler.random(-0.4f, 0.4f)), Direction.UP, be.getPos(), false));
                        mc.player.swingHand(Hand.MAIN_HAND);
                        break;
                    }
                }
            }
        }
    }



    public static List<WorldChunk> getLoadedChunks() {
        List<WorldChunk> chunks = new ArrayList<>();
        int viewDist = mc.options.getViewDistance().getValue();
        for (int x = -viewDist; x <= viewDist; x++) {
            for (int z = -viewDist; z <= viewDist; z++) {
                WorldChunk chunk = mc.world.getChunkManager().getWorldChunk((int) mc.player.getX() / 16 + x, (int) mc.player.getZ() / 16 + z);

                if (chunk != null) chunks.add(chunk);
            }
        }
        return chunks;
    }

    public static List<BlockEntity> getBlockEntities() {
        List<BlockEntity> list = new ArrayList<>();
        for (WorldChunk chunk : getLoadedChunks())
            list.addAll(chunk.getBlockEntities().values());

        return list;
    }
    private boolean isContainerEmpty(GenericContainerScreenHandler container) {
        for (int i = 0; i < (container.getInventory().size() == 90 ? 54 : 27); i++)
            if (container.getSlot(i).hasStack()) return false;
        return true;
    }
}
