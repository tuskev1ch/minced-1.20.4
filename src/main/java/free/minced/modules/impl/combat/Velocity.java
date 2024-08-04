package free.minced.modules.impl.combat;

import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import free.minced.events.Event;
import free.minced.events.impl.player.PacketEvent;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.mixin.accesors.IExplosionS2CPacket;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.systems.setting.impl.ModeSetting;

@ModuleDescriptor(name = "Velocity", category = ModuleCategory.COMBAT)
public class Velocity extends Module {

    private boolean doJump, failJump, skip, flag;
    private int grimTicks, ccCooldown;
    private final ModeSetting mode = new ModeSetting("Mode", this, "Cancel", "Cancel", "Grim");

    @Override
    public void onEvent(Event event) {
        if (event instanceof PacketEvent.Receive e) {
            if (IHolder.fullNullCheck()) return;

            if (mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater() || mc.player.isInLava()) /*&& pauseInWater.getValue()*/)
                return;

            if (mc.player != null && mc.player.isOnFire() && /*fire.getValue() &&*/ (mc.player.hurtTime > 0)) {
                return;
            }

            if (ccCooldown > 0) {
                ccCooldown--;
                return;
            }

            if (e.getPacket() instanceof EntityStatusS2CPacket pac
                    && pac.getStatus() == 31
                    && pac.getEntity(mc.world) instanceof FishingBobberEntity fishHook
                /*&& fishingHook.getValue()*/) {
                if (fishHook.getHookedEntity() == mc.player) {
                    e.setCancel(true);
                }
            }

            // MAIN VELOCITY
            if (e.getPacket() instanceof EntityVelocityUpdateS2CPacket pac) {
                if (pac.getId() == mc.player.getId()) {
                    if (mode.is("Cancel")) {
                        e.setCancel(true);
                    } else if (mode.is("Grim")) {
                        e.setCancel(true);
                        flag = true;
                    }
                }
            }

            if (e.getPacket() instanceof ExplosionS2CPacket explosion /*&& explosions.getValue()*/) {


                ((IExplosionS2CPacket) explosion).setMotionX(0);
                ((IExplosionS2CPacket) explosion).setMotionY(0);
                ((IExplosionS2CPacket) explosion).setMotionZ(0);
                if (mode.is("Grim")) {
                    flag = true;
                }
            }

            if (e.getPacket() instanceof PlayerPositionLookS2CPacket) { // flag anticheat
                if (mode.is("Grim")) {
                    ccCooldown = 5;
                }
            }
        } else if (event instanceof UpdatePlayerEvent) {
            if (mc.player != null && (mc.player.isTouchingWater() || mc.player.isSubmergedInWater()) /*&& pauseInWater.getValue()*/)
                return;

            if (mode.is("Grim")) {
                if (flag) {
                    if (ccCooldown <= 0) {
                        IHolder.sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround()));
                        IHolder.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, BlockPos.ofFloored(mc.player.getPos()), Direction.DOWN));
                    }
                    flag = false;
                }
            }
        }
    }
}