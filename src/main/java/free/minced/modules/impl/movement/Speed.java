package free.minced.modules.impl.movement;


import com.google.common.collect.Lists;
import free.minced.systems.setting.impl.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import free.minced.events.Event;
import free.minced.events.impl.mobility.EventPlayerTravel;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.game.MobilityHandler;
import free.minced.systems.setting.impl.ModeSetting;
import free.minced.systems.setting.impl.NumberSetting;

@ModuleDescriptor(name = "Speed", category = ModuleCategory.MOVEMENT)

public class Speed extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "GrimCollision", "GrimCollision", "GrimDistance");
    private final BooleanSetting onlyPlayers = new BooleanSetting("Only Players", this, false, () -> !mode.is("GrimCollision"));

    public final NumberSetting speed = new NumberSetting("Speed", this, 8, 1, 8, 1);

    public final NumberSetting distance = new NumberSetting("Distance", this, 3, 0.5, 5, 0.1F, () -> !mode.is("GrimDistance"));


    public final NumberSetting radius = new NumberSetting("Radius", this, 1, 0.5, 1.5, 0.1F, () -> !mode.is("GrimCollision"));

    @Override
    public void onEvent(Event event) {
        if (mc.player == null || mc.world == null) return;
        if (event instanceof EventPlayerTravel e) {
            if (mode.is("GrimDistance") && !e.isPre()) {
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

            if ((mode.is("GrimCollision")) && !e.isPre() && MobilityHandler.isMoving()) {
                int collisions = 0;
                for (Entity ent : mc.world.getEntities()) {
                    if (!(ent instanceof PlayerEntity) && onlyPlayers.isEnabled()) continue;

                    if (ent != mc.player && (ent instanceof LivingEntity || ent instanceof BoatEntity) && mc.player.getBoundingBox().expand(radius.getValue().doubleValue()).intersects(ent.getBoundingBox()))
                        collisions++;
                }

                double[] motion = MobilityHandler.forward((speed.getValue().intValue() * 0.01) * collisions);
                mc.player.addVelocity(motion[0], 0.0, motion[1]);
            }
        }
    }

}