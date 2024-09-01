package free.minced.primary.game;


import lombok.experimental.UtilityClass;
import net.minecraft.block.AirBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import free.minced.mixin.accesors.IInteractionManager;
import free.minced.primary.IHolder;
import free.minced.systems.SharedClass;

import java.util.Arrays;
import java.util.List;

@UtilityClass
public class InventoryHandler implements IHolder {
    private static int cachedSlot = -1;

    public static void saveSlot() {
        cachedSlot = mc.player.getInventory().selectedSlot;
    }

    public static void returnSlot() {
        if (cachedSlot != -1)
            switchTo(cachedSlot);
        cachedSlot = -1;
    }


    public static int findItem(Item item, boolean hotBar) {
        int startSlot = hotBar ? 0 : 9;
        int endSlot = hotBar ? 9 : 36;
        for (int i = startSlot; i < endSlot; ++i) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (item != itemStack.getItem()) continue;
            return i;
        }
        return -1;
    }

    public int findBestEmpySlot(boolean hotBar) {
        int emptySlot = findEmptySlot(hotBar);
        if (emptySlot != -1) {
            return emptySlot;
        }
        return findNonSwordSlot(hotBar);
    }
    private int findEmptySlot(boolean hotBar) {
        int startSlot = hotBar ? 0 : 9;
        int endSlot = hotBar ? 9 : 36;
        for (int i = startSlot; i < endSlot; ++i) {
            if (!mc.player.getInventory().getStack(i).isEmpty() || mc.player.getInventory().selectedSlot == i) continue;
            return i;
        }
        return -1;
    }

    private int findNonSwordSlot(boolean hotBar) {
        int startSlot = hotBar ? 0 : 9;
        int endSlot = hotBar ? 9 : 36;
        for (int i = startSlot; i < endSlot; ++i) {
            if (mc.player.getInventory().getStack(i).getItem() instanceof SwordItem || mc.player.getInventory().getStack(i).getItem() instanceof ElytraItem || mc.player.getInventory().selectedSlot == i) continue;
            return i;
        }
        return -1;
    }

    public static int getTool(final BlockPos pos) {
        int index = -1;
        float CurrentFastest = 1.0f;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack != ItemStack.EMPTY) {

                final float digSpeed = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
                final float destroySpeed = stack.getMiningSpeedMultiplier(mc.world.getBlockState(pos));

                if (mc.world.getBlockState(pos).getBlock() instanceof AirBlock) return -1;
                if (mc.world.getBlockState(pos).getBlock() instanceof EnderChestBlock) {
                    if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) > 0 && digSpeed + destroySpeed > CurrentFastest) {
                        CurrentFastest = digSpeed + destroySpeed;
                        index = i;
                    }
                } else if (digSpeed + destroySpeed > CurrentFastest) {
                    CurrentFastest = digSpeed + destroySpeed;
                    index = i;
                }
            }
        }
        return index;
    }
    public static int getChestplate() {
        for (int i = 0; i < 45; ++i) {
            ItemStack itemStack = mc.player.getInventory().getStack(i);
            if (itemStack.getItem() instanceof ArmorItem)
                if (((ArmorItem)itemStack.getItem()).getSlotType() == EquipmentSlot.CHEST)
                    return i == 40 ? 45 : i < 9 ? 36 + i : i;
        }
        return -1;
    }
    @Deprecated
    public static int getElytra() {
        for (ItemStack stack : mc.player.getInventory().armor) {
            if (stack.getItem() == Items.ELYTRA && stack.getDamage() < 430) {
                return -2;
            }
        }

        int slot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.getItem() == Items.ELYTRA && s.getDamage() < 430) {
                slot = i;
                break;
            }
        }

        if (slot < 9 && slot != -1) {
            slot = slot + 36;
        }

        return slot;
    }
    public static void switchTo(int slot) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        if (mc.player.getInventory().selectedSlot == slot && SharedClass.serverSideSlot == slot)
            return;
        mc.player.getInventory().selectedSlot = slot;
        //((IInteractionManager)mc.interactionManager).syncSlot();
    }

    public static SearchInvResult findInHotBar(Searcher searcher) {
        if (mc.player != null) {
            for (int i = 0; i < 9; ++i) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (searcher.isValid(stack)) {
                    return new SearchInvResult(i, true, stack);
                }
            }
        }

        return SearchInvResult.notFound();
    }

    public static SearchInvResult findItemInHotBar(List<Item> items) {
        return findInHotBar(stack -> items.contains(stack.getItem()));
    }

    public static SearchInvResult findItemInHotBar(Item... items) {
        return findItemInHotBar(Arrays.asList(items));
    }

    public static SearchInvResult findInInventory(Searcher searcher) {
        if (mc.player != null) {
            for (int i = 36; i >= 0; i--) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (searcher.isValid(stack)) {
                    if (i < 9) i += 36;
                    return new SearchInvResult(i, true, stack);
                }
            }
        }

        return SearchInvResult.notFound();
    }

    public static SearchInvResult findItemInInventory(List<Item> items) {
        return findInInventory(stack -> items.contains(stack.getItem()));
    }

    public static SearchInvResult findItemInInventory(Item... items) {
        return findItemInInventory(Arrays.asList(items));
    }

    public static void moveItem(int one, int two, boolean swap) {
        mc.interactionManager.clickSlot(0, one, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(0, two, 0, SlotActionType.PICKUP, mc.player);
        if (swap) {
            mc.interactionManager.clickSlot(0, one, 0, SlotActionType.PICKUP, mc.player);
        }
    }

    public interface Searcher {
        boolean isValid(ItemStack stack);
    }
}
