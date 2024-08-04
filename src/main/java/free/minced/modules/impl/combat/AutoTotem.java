package free.minced.modules.impl.combat;

import com.mojang.blaze3d.systems.RenderSystem;
import free.minced.events.Event;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.events.impl.render.Render2DEvent;
import free.minced.framework.font.Fonts;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.modules.impl.display.hud.impl.TargetHUD;
import free.minced.primary.IHolder;
import free.minced.primary.game.PlayerHandler;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;
import free.minced.systems.setting.impl.MultiBoxSetting;
import free.minced.systems.setting.impl.NumberSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static free.minced.modules.impl.combat.AutoSwap.findNearestCurrentItem;

@ModuleDescriptor(name = "AutoTotem", category = ModuleCategory.COMBAT)
public class AutoTotem extends Module {
    private final ModeSetting swapMode = new ModeSetting("Swap Mode", this, "Window", "Window", "ViaVersion 1.17+");

    private final NumberSetting health = new NumberSetting("Health", this, 4F, 1F, 20F, 1F);
    private final BooleanSetting drawCounter = new BooleanSetting("Display quantity", this, true);
    private final BooleanSetting swapBack = new BooleanSetting("Back Item", this, true);

    private final BooleanSetting noBallSwitch = new BooleanSetting("Dont take with ball", this, false);
    private final MultiBoxSetting mode = new MultiBoxSetting("Condition", this, "Absort", "Obsidian", "Crystal", "Anchor", "Fall");

    private final NumberSetting obsidianRadius = new NumberSetting("Radius Obsidian", this, 6, 1, 8, 1, () -> !mode.get("Obsidian").isEnabled());
    private final NumberSetting crystalRadius = new NumberSetting("Radius Crystal", this, 6, 1, 8, 1, () -> !mode.get("Crystal").isEnabled());
    private final NumberSetting anchorRadius = new NumberSetting("Radius Anchor", this, 6, 1, 8, 1, () -> !mode.get("Anchor").isEnabled());

    @Override
    public void onEvent(Event event) {
        if (event instanceof UpdatePlayerEvent) {
            int slot = PlayerHandler.findItemSlot(Items.TOTEM_OF_UNDYING);
            boolean totemInHand = mc.player.getOffHandStack().getItem().equals(Items.TOTEM_OF_UNDYING);
            boolean handNotNull = !(mc.player.getOffHandStack().getItem() instanceof AirBlockItem);

            if (condition()) {
                if (slot >= 0) {
                    if (!totemInHand) {
                        swapItem(slot);
                        if (handNotNull && swapBack.isEnabled()) {
                            if (swapBackSlot == -1) swapBackSlot = slot;
                        }
                    }
                }
            } else if (swapBackSlot >= 0) {
                if (handNotNull && swapBack.isEnabled()) {
                    swapItem(swapBackSlot);
                }
                swapBackSlot = -1;
            }
        }
        if (event instanceof Render2DEvent event1) {
            if (!drawCounter.isEnabled())
                return;

            if (getTotemCount() > 0) {
                Fonts.SEMI_12.drawString(((Render2DEvent) event).getStack(), getTotemCount() + "x", mc.getWindow().getScaledWidth() / 2f + 10F,
                        mc.getWindow().getScaledHeight() / 2f + 24, Color.WHITE);

                event1.getStack().push();

                RenderSystem.disableBlend();

                TargetHUD.drawItemStack(event1.getContext(), Items.TOTEM_OF_UNDYING.getDefaultStack(), (int) (mc.getWindow().getScaledWidth() / 2F - 8), (int) (mc.getWindow().getScaledHeight() / 2F + 17.5f), 0.7f);

                event1.getStack().pop();
            }
        }

    }
    private int swapBackSlot = -1;
    private final ItemStack stack = new ItemStack(Items.TOTEM_OF_UNDYING);

    private boolean condition() {
        float health = mc.player.getHealth();
        if (mode.get("Absort").isEnabled()) {
            health += mc.player.getAbsorptionAmount();
        }

        if (this.health.getValue().floatValue() >= health) {
            return true;
        }

        if (!isBall()) {
            for (Entity entity : mc.world.getEntities()) {
                if (mode.get("Crystal").isEnabled()) {
                    if (entity instanceof EndCrystalEntity && mc.player.squaredDistanceTo(entity) <= crystalRadius.getValue().floatValue()) {
                        return true;
                    }
                }
            }

            if (mode.get("Anchor").isEnabled()) {
                BlockPos pos = getSphere(mc.player.getBlockPos(), obsidianRadius.getValue().floatValue(), 6, false, true, 0).stream().filter(this::IsValidBlockPosAnchor).min(Comparator.comparing(blockPos -> getDistanceToBlock(mc.player, blockPos))).orElse(null);
                return pos != null;
            }

            if (mode.get("Obsidian").isEnabled()) {
                BlockPos pos = getSphere(mc.player.getBlockPos(), anchorRadius.getValue().floatValue(), 6, false, true, 0).stream().filter(this::IsValidBlockPosObisdian).min(Comparator.comparing(blockPos -> getDistanceToBlock(mc.player, blockPos))).orElse(null);
                return pos != null;
            }
            if (mode.get("Fall").isEnabled()) {
                return mc.player.fallDistance >= 30;
            }
        }

        return false;
    }

    public boolean isBall() {
        if (!noBallSwitch.isEnabled()) {
            return false;
        }
        ItemStack stack = mc.player.getOffHandStack();
        return stack.getName().getString().toLowerCase().contains("шар") || stack.getName().getString().toLowerCase().contains("голова") || stack.getName().getString().toLowerCase().contains("head");
    }

    public void swapItem(int slot) {
        if (swapMode.is("Window")) {
            mc.interactionManager.clickSlot(0, slot, 1, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(0, 45, 1, SlotActionType.PICKUP, mc.player);
        }
        if (swapMode.is("ViaVersion 1.17+")) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 40, SlotActionType.SWAP, mc.player);
            IHolder.sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
        }
    }
   private int getTotemCount() {
       int count = 0;
       for (int i = 0; i < mc.player.getInventory().size(); i++) {
           ItemStack stack = mc.player.getInventory().getStack(i);
           if (stack.getItem().equals(Items.TOTEM_OF_UNDYING)) {
               count++;
           }
       }
       return count;
   }

    private boolean IsValidBlockPosObisdian(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return state.getBlock().equals(Blocks.OBSIDIAN);
    }

    private boolean IsValidBlockPosAnchor(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return state.getBlock().equals(Blocks.RESPAWN_ANCHOR);
    }

    private List<BlockPos> getSphere(final BlockPos blockPos, final float radius, final int height, final boolean hollow, final boolean semiHollow, final int yOffset) {
        final ArrayList<BlockPos> spherePositions = new ArrayList<>();
        final int x = blockPos.getX();
        final int y = blockPos.getY();
        final int z = blockPos.getZ();
        final int minX = x - (int) radius;
        final int maxX = x + (int) radius;
        final int minZ = z - (int) radius;
        final int maxZ = z + (int) radius;

        for (int xPos = minX; xPos <= maxX; ++xPos) {
            for (int zPos = minZ; zPos <= maxZ; ++zPos) {
                final int minY = semiHollow ? (y - (int) radius) : y;
                final int maxY = semiHollow ? (y + (int) radius) : (y + height);
                for (int yPos = minY; yPos < maxY; ++yPos) {
                    final double distance = (x - xPos) * (x - xPos) + (z - zPos) * (z - zPos) + (semiHollow ? ((y - yPos) * (y - yPos)) : 0);
                    if (distance < radius * radius && (!hollow || distance >= (radius - 1.0f) * (radius - 1.0f))) {
                        spherePositions.add(new BlockPos(xPos, yPos + yOffset, zPos));
                    }
                }
            }
        }
        return spherePositions;
    }

    private double getDistanceToBlock(Entity entity, final BlockPos blockPos) {
        return getDistance(entity.getX(), entity.getY(), entity.getZ(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    private double getDistance(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        final double x = x1 - x2;
        final double y = y1 - y2;
        final double z = z1 - z2;
        return MathHelper.sqrt((float) (x * x + y * y + z * z));
    }
    
}