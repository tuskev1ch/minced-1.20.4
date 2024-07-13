package free.minced.modules.impl.movement;


import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import free.minced.events.Event;
import free.minced.events.impl.mobility.EventMove;
import free.minced.events.impl.mobility.EventPlayerTravel;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.game.InventoryHandler;
import free.minced.primary.game.MobilityHandler;
import free.minced.systems.setting.impl.ModeSetting;
import free.minced.systems.setting.impl.NumberSetting;

import static net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.START_FALL_FLYING;

@ModuleDescriptor(name = "Speed", category = ModuleCategory.MOVEMENT)

public class Speed extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "GrimCollision", "GrimCollision", "GrimDistance", "MatrixElytra");

    public final NumberSetting speed = new NumberSetting("Speed", this, 8, 1, 8, 1);

    public final NumberSetting distance = new NumberSetting("Distance", this, 3, 0.5, 5, 0.1F, () -> !mode.is("GrimDistance"));


    public final NumberSetting radius = new NumberSetting("Radius", this, 1, 0.5, 1.5, 0.1F, () -> !mode.is("GrimCollision"));

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventPlayerTravel e) {
            if (mode.is("GrimDistance") && !e.isPre() && getSetBackTime() > 1000) {
                for (PlayerEntity ent : Lists.newArrayList(mc.world.getPlayers())) {
                    if (ent != mc.player && mc.player.squaredDistanceTo(ent) <= distance.getValue().floatValue()) {
                        float p = mc.world.getBlockState(mc.player.getBlockPos()).getBlock().getSlipperiness();
                        float f = mc.player.isOnGround() ? p * 0.91f : 0.91f;
                        float f2 = mc.player.isOnGround() ? p : 0.99f;

                        double[] motion = MobilityHandler.forward((speed.getValue().intValue() * 0.01) * f * f2);
                        mc.player.addVelocity(motion[0], 0.0, motion[1]);
                        break;
                    }
                }
            }

            if ((mode.is("GrimCollision")) && !e.isPre() && getSetBackTime() > 1000 && MobilityHandler.isMoving()) {
                int collisions = 0;
                for (Entity ent : mc.world.getEntities())
                    if (ent != mc.player && (ent instanceof LivingEntity || ent instanceof BoatEntity) && mc.player.getBoundingBox().expand(radius.getValue().doubleValue()).intersects(ent.getBoundingBox()))
                        collisions++;

                double[] motion = MobilityHandler.forward((speed.getValue().intValue() * 0.01) * collisions);
                mc.player.addVelocity(motion[0], 0.0, motion[1]);
            }
        }
    }

}