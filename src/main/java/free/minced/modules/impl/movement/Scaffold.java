package free.minced.modules.impl.movement;

import free.minced.events.Event;
import free.minced.events.impl.player.TickEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.game.InventoryHandler;
import free.minced.primary.game.MobilityHandler;
import free.minced.primary.time.TimerHandler;
import free.minced.systems.setting.impl.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@ModuleDescriptor(name = "Scaffold", category = ModuleCategory.MISC)
public class Scaffold extends Module {
    private final BooleanSetting silent = new BooleanSetting("Silent", this, false);

    private final TimerHandler timer = new TimerHandler();
    private BlockPosWithFacing currentblock;
    private int prevY;

    @Override
    public void onEvent(Event e) {
        if (e instanceof TickEvent event) {

            preAction();
            postAction();

        }

    }

    public void preAction() {
        currentblock = null;

        if (prePlace(false) == -1) return;

        if (mc.options.jumpKey.isPressed() && !MobilityHandler.isMoving())
            prevY = (int) (Math.floor(mc.player.getY() - 1));

        if (MobilityHandler.isMoving()) {
            if (mc.options.jumpKey.isPressed()) {
                prevY = (int) (Math.floor(mc.player.getY() - 1));
            } else if (mc.player.isOnGround()) {
                mc.player.jump();
            }
        }

        BlockPos blockPos2 = prevY != -999 ?
                BlockPos.ofFloored(mc.player.getX(), prevY, mc.player.getZ())
                : new BlockPos((int) Math.floor(mc.player.getX()), (int) (Math.floor(mc.player.getY() - 1)), (int) Math.floor(mc.player.getZ()));

        if (!mc.world.getBlockState(blockPos2).isReplaceable()) return;

        currentblock = checkNearBlocksExtended(blockPos2);
    }

    public static Vec3d getEyesPos(@NotNull Entity entity) {
        return entity.getPos().add(0, entity.getEyeHeight(entity.getPose()), 0);
    }

    public static float @NotNull [] calculateAngle(Vec3d to) {
        return calculateAngle(getEyesPos(mc.player), to);
    }

    public static float @NotNull [] calculateAngle(@NotNull Vec3d from, @NotNull Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt((float) (difX * difX + difZ * difZ));

        float yD = (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0);
        float pD = (float) MathHelper.clamp(MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))), -90f, 90f);

        return new float[]{yD, pD};
    }

    public void postAction() {
        float offset = 0.3f;

        if (mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().expand(-offset, 0, -offset).offset(0, -0.5, 0)).iterator().hasNext())
            return;

        if (currentblock == null) return;
        int prevItemSwap = mc.player.getInventory().selectedSlot;

        int prevItem = prePlace(true);

        if (prevItem != -1) {
            timer.reset();

            BlockHitResult bhr = new BlockHitResult(new Vec3d((double) currentblock.position().getX() + Math.random(), currentblock.position().getY() + 0.99f, (double) currentblock.position().getZ() + Math.random()), currentblock.facing(), currentblock.position(), false);


            float[] rotations = calculateAngle(bhr.getPos());
            IHolder.sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), rotations[0], rotations[1], mc.player.isOnGround()));


            mc.interactionManager.interactBlock(mc.player, prevItem == -2 ? Hand.OFF_HAND : Hand.MAIN_HAND, bhr);

            mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(prevItem == -2 ? Hand.OFF_HAND : Hand.MAIN_HAND));

            prevY = currentblock.position().getY();

            IHolder.sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));




            if (silent.isEnabled()) {
                mc.player.getInventory().selectedSlot = prevItemSwap;
            }
        }

    }

    public static @Nullable BlockPosWithFacing checkNearBlocks(@NotNull BlockPos blockPos) {
        if (mc.world.getBlockState(blockPos.add(0, -1, 0)).isSolid())
            return new BlockPosWithFacing(blockPos.add(0, -1, 0), Direction.UP);

        else if (mc.world.getBlockState(blockPos.add(-1, 0, 0)).isSolid())
            return new BlockPosWithFacing(blockPos.add(-1, 0, 0), Direction.EAST);

        else if (mc.world.getBlockState(blockPos.add(1, 0, 0)).isSolid())
            return new BlockPosWithFacing(blockPos.add(1, 0, 0), Direction.WEST);

        else if (mc.world.getBlockState(blockPos.add(0, 0, 1)).isSolid())
            return new BlockPosWithFacing(blockPos.add(0, 0, 1), Direction.NORTH);

        else if (mc.world.getBlockState(blockPos.add(0, 0, -1)).isSolid())
            return new BlockPosWithFacing(blockPos.add(0, 0, -1), Direction.SOUTH);
        return null;
    }

    private BlockPosWithFacing checkNearBlocksExtended(BlockPos blockPos) {
        BlockPosWithFacing ret;

        ret = checkNearBlocks(blockPos);
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(-1, 0, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(1, 0, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(0, 0, 1));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(0, 0, -1));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(-2, 0, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(2, 0, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(0, 0, 2));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(0, 0, -2));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(0, -1, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(1, -1, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(-1, -1, 0));
        if (ret != null) return ret;

        ret = checkNearBlocks(blockPos.add(0, -1, 1));
        if (ret != null) return ret;

        return checkNearBlocks(blockPos.add(0, -1, -1));
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

    private int prePlace(boolean swap) {
        if (mc.player == null || mc.world == null)
            return -1;

        if (mc.player.getOffHandStack().getItem() instanceof BlockItem bi && !bi.getBlock().getDefaultState().isReplaceable())
            return -2;

        if (mc.player.getMainHandStack().getItem() instanceof BlockItem bi && !bi.getBlock().getDefaultState().isReplaceable())
            return mc.player.getInventory().selectedSlot;

        int prevSlot = mc.player.getInventory().selectedSlot;

        SearchInvResult hotbarResult = findInHotBar(i -> i.getItem() instanceof BlockItem bi && !bi.getBlock().getDefaultState().isReplaceable());

        if (swap) hotbarResult.switchTo();

        if (!hotbarResult.found()) return -1;

        return prevSlot;
    }


    public record BlockPosWithFacing(BlockPos position, Direction facing) {
    }

    public interface Searcher {
        boolean isValid(ItemStack stack);
    }

    public record SearchInvResult(int slot, boolean found, ItemStack stack) {
        private static final SearchInvResult NOT_FOUND_RESULT = new SearchInvResult(-1, false, null);

        public static SearchInvResult notFound() {
            return NOT_FOUND_RESULT;
        }

        public static SearchInvResult inOffhand(ItemStack stack) {
            return new SearchInvResult(999, true, stack);
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public boolean isHolding() {
            if (mc.player == null) return false;

            return mc.player.getInventory().selectedSlot == slot;
        }

        public boolean isInHotBar() {
            return slot < 9;
        }

        public void switchTo() {
            InventoryHandler.switchTo(slot);
        }

    }
}