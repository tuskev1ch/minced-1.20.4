package free.minced.primary.game;


import io.netty.buffer.Unpooled;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import free.minced.mixin.accesors.ILivingEntity;
import free.minced.modules.impl.combat.AttackAura;
import free.minced.primary.IHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class PlayerHandler implements IHolder {

    public static PlayerHandler.InteractType getInteractType(@NotNull PlayerInteractEntityC2SPacket packet) {
        PacketByteBuf packetBuf = new PacketByteBuf(Unpooled.buffer());
        packet.write(packetBuf);

        packetBuf.readVarInt();
        return packetBuf.readEnumConstant(PlayerHandler.InteractType.class);
    }
    public enum InteractType {
        INTERACT, ATTACK, INTERACT_AT
    }

    public static boolean isPlayerInWeb() {
        Box playerBox = mc.player.getBoundingBox();
        BlockPos playerPosition = BlockPos.ofFloored(mc.player.getPos());

        return getNearbyBlockPositions(playerPosition).stream()
                .anyMatch(pos -> isBlockCobweb(playerBox, pos));
    }

    private static List<BlockPos> getNearbyBlockPositions(BlockPos center) {
        List<BlockPos> positions = new ArrayList<>();
        for (int x = center.getX() - 2; x <= center.getX() + 2; x++) {
            for (int y = center.getY() - 1; y <= center.getY() + 4; y++) {
                for (int z = center.getZ() - 2; z <= center.getZ() + 2; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }
        return positions;
    }

    private static boolean isBlockCobweb(Box playerBox, BlockPos blockPos) {
        return playerBox.intersects(new Box(blockPos)) && mc.world.getBlockState(blockPos).getBlock() == Blocks.COBWEB;
    }
    
    public static float getCurrentItemAttackStrengthDelay() {
        return (float)(1.0D / mc.player.getAttributeValue(EntityAttributes.GENERIC_ATTACK_SPEED) * 20.0D);
    }
    public static int findItemSlot(Item item) {
        return findItemSlot(item, true);
    }

    public static int findItemSlot(Item item, boolean armor) {
        if (armor) {
            for (ItemStack stack : mc.player.getInventory().armor) {
                if (stack.getItem() == item) {
                    return -2;
                }
            }
        }
        int slot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.getItem() == item) {
                slot = i;
                break;
            }
        }
        if (slot < 9 && slot != -1) {
            slot = slot + 36;
        }
        return slot;
    }
    public static float getAttackStrengthScale(float pAdjustTicks) {
        return MathHelper.clamp(((float) ((ILivingEntity) mc.player).getLastAttackedTicks() + pAdjustTicks) / getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
    }

    public static boolean isAboveWater() {
        return mc.player.isSubmergedInWater() || mc.world.getBlockState(BlockPos.ofFloored(mc.player.getPos().add(0, -0.4, 0))).getBlock() == Blocks.WATER;
    }
    public static boolean checkGround(float f2) {
        if (mc.player.getY() < 0.0) return false;
        return !mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0, -f2, 0.0)).iterator().hasNext();
    }

    private static @NotNull Vec3d getRotationVector(float yaw, float pitch) {
        return new Vec3d(MathHelper.sin(-pitch * 0.017453292F) * MathHelper.cos(yaw * 0.017453292F), -MathHelper.sin(yaw * 0.017453292F), MathHelper.cos(-pitch * 0.017453292F) * MathHelper.cos(yaw * 0.017453292F));
    }

    public static HitResult rayTrace(double dst, float yaw, float pitch) {
        Vec3d vec3d = mc.player.getCameraPosVec(mc.getTickDelta());
        Vec3d vec3d2 = getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * dst, vec3d2.y * dst, vec3d2.z * dst);
        return mc.world.raycast(new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
    }

    public static Entity getRtxTarget(float yaw, float pitch, float distance, boolean ignoreWalls) {
        Entity targetedEntity = null;
        HitResult result = ignoreWalls ? null : rayTrace(distance, yaw, pitch);
        Vec3d vec3d = mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
        double distancePow2 = Math.pow(distance, 2);
        if (result != null) distancePow2 = result.getPos().squaredDistanceTo(vec3d);
        Vec3d vec3d2 = getRotationVector(pitch, yaw);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
        Box box = mc.player.getBoundingBox().stretch(vec3d2.multiply(distance)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(mc.player, vec3d, vec3d3, box, (entity) -> !entity.isSpectator() && entity.canHit(), distancePow2);
        if (entityHitResult != null) {
            Entity entity2 = entityHitResult.getEntity();
            Vec3d vec3d4 = entityHitResult.getPos();
            double g = vec3d.squaredDistanceTo(vec3d4);
            if (g < distancePow2 || result == null) {
                if (entity2 instanceof LivingEntity) {
                    targetedEntity = entity2;
                    return targetedEntity;
                }
            }
        }
        return null;
    }
    public static void disableSprint() {
        mc.player.setSprinting(false);
        mc.options.sprintKey.setPressed(false);
        IHolder.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
    }

    public static void enableSprint() {
        mc.player.setSprinting(true);
        mc.options.sprintKey.setPressed(true);
        IHolder.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
    }

    public static float squaredDistanceFromEyes(@NotNull Vec3d vec) {
        if (mc.player == null) return 0;

        double d0 = vec.x - mc.player.getX();
        double d1 = vec.z - mc.player.getZ();
        double d2 = vec.y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        return (float) (d0 * d0 + d1 * d1 + d2 * d2);
    }
    public static float[] calcAngle(Vec3d to) {
        if (to == null) return null;
        double difX = to.x - mc.player.getEyePos().x;
        double difY = (to.y - mc.player.getEyePos().y) * -1.0;
        double difZ = to.z - mc.player.getEyePos().z;
        double dist = MathHelper.sqrt((float) (difX * difX + difZ * difZ));
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }
    public static boolean checkRtx(float yaw, float pitch, float distance, boolean ignorewalls, boolean raytrace) {
        if (!raytrace)
            return true;

        HitResult result = rayTrace(distance, yaw, pitch);
        Vec3d startPoint = mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
        double distancePow2 = Math.pow(distance, 2);

        if (result != null)
            distancePow2 = startPoint.squaredDistanceTo(result.getPos());

        Vec3d rotationVector = getRotationVector(pitch, yaw).multiply(distance);
        Vec3d endPoint = startPoint.add(rotationVector);

        Box entityArea = mc.player.getBoundingBox().stretch(rotationVector).expand(1.0, 1.0, 1.0);

        EntityHitResult ehr = null;

        double maxDistance = Math.max(distancePow2, Math.pow(ignorewalls ? distance : 0, 2));

        if (AttackAura.target != null) {
            ehr = ProjectileUtil.raycast(mc.player, startPoint, endPoint, entityArea, e -> !e.isSpectator() && e.canHit() && e == AttackAura.target, maxDistance);
        }

        if (ehr != null) {
            boolean allowedWallDistance = startPoint.squaredDistanceTo(ehr.getPos()) <= Math.pow(!ignorewalls ? distance : 0, 2);
            boolean wallMissing = result == null;
            boolean wallBehindEntity = startPoint.squaredDistanceTo(ehr.getPos()) < distancePow2;
            boolean allowWallHit = wallMissing || allowedWallDistance || wallBehindEntity;

            if (allowWallHit && startPoint.squaredDistanceTo(ehr.getPos()) <= Math.pow(distance, 2))
                return ehr.getEntity() == AttackAura.target;
        }

        return false;
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        if (to == null) return null;
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt((float) (difX * difX + difZ * difZ));
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

}
