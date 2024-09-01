package free.minced.modules.impl.movement;

import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.mobility.EventMove;
import free.minced.events.impl.player.EventSprint;
import free.minced.events.impl.player.EventSync;
import free.minced.events.impl.player.PacketEvent;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.game.InventoryHandler;
import free.minced.primary.game.MobilityHandler;
import free.minced.systems.SharedClass;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;
import free.minced.systems.setting.impl.NumberSetting;

@ModuleDescriptor(name = "Strafe", category = ModuleCategory.MOVEMENT)
public class Strafe extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "None", "None", "Elytra");
    private final NumberSetting setSpeed = new NumberSetting("Speed", this, 1.3F, 0.0F, 2f, 0.1f, () -> !mode.is("Elytra"));
    public final BooleanSetting force = new BooleanSetting("Force", this, false, () -> !mode.is("Elytra"));

    public static double oldSpeed, contextFriction, fovval;
    public static boolean needSwap, needSprintState, disabled;
    public static int noSlowTicks;
    static long disableTime;

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMove e) {
            onMove(e);
        } else if (event instanceof EventSync e) {
            onSync(e);
        } else if (event instanceof PacketEvent.Receive e) {
            onPacketReceive(e);
        } else if (event instanceof EventSprint e) {
            actionEvent(e);
        } else if (event instanceof UpdatePlayerEvent e) {
            onUpdate(e);
        }
    }

    @Override
    public void onEnable() {
        if (mc.options == null) return;
        oldSpeed = 0.0;
        fovval = mc.options.getFovEffectScale().getValue();
        mc.options.getFovEffectScale().setValue(0d);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (mc.options == null) return;
        mc.options.getFovEffectScale().setValue(fovval);
        super.onDisable();
    }

    public void onMove(EventMove event) {
        int elytraSlot = InventoryHandler.getElytra();
        if (mode.is("Elytra") && elytraSlot != -1) {
            if (isMoving() && !mc.player.isOnGround()
                    && mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0, event.getY(), 0.0f)).iterator().hasNext() && disabled) {
                oldSpeed = setSpeed.getValue().doubleValue();
            }
        }

        if (canStrafe()) {
            if (isMoving()) {
                double[] motions = MobilityHandler.forward(calculateSpeed(event));

                event.setX(motions[0]);
                event.setZ(motions[1]);
            } else {
                oldSpeed = 0;
                event.setX(0);
                event.setZ(0);
            }
            event.setCancel(true);
        } else {
            oldSpeed = 0;
        }
    }

    public void onSync(EventSync e) {
        oldSpeed = Math.hypot(mc.player.getX() - mc.player.prevX, mc.player.getZ() - mc.player.prevZ) * contextFriction;
    }

    public void onPacketReceive(PacketEvent.Receive e) {
        if (e.getPacket() instanceof PlayerPositionLookS2CPacket) {
            oldSpeed = 0;
        }
    }

    public void actionEvent(EventSprint eventAction) {
        if (canStrafe()) {
            if (SharedClass.serverSprint != needSprintState) {
                eventAction.setSprintState(!SharedClass.serverSprint);
            }
        }
        if (needSwap) {
            eventAction.setSprintState(!mc.player.lastSprinting);
            needSwap = false;
        }
    }

    public void onUpdate(UpdatePlayerEvent event) {
        if ((mode.is("Elytra") && InventoryHandler.getElytra() != -1 && !mc.player.isOnGround() && mc.player.fallDistance > 0 && !disabled)
                && (!mc.world.getBlockCollisions(mc.player, mc.player.getBoundingBox().offset(0.0, -1.1f, 0.0f)).iterator().hasNext() || force.isEnabled())) {
            disabler(InventoryHandler.getElytra());
        }
        if (mc.player.isOnGround()) {
            disabled = false;
        }
    }
    public double calculateSpeed(EventMove move) {
        float speedAttributes = getAIMoveSpeed();
        final float frictionFactor = mc.world.getBlockState(new BlockPos.Mutable().set(mc.player.getX(), getBoundingBox().getMin(Direction.Axis.Y) - move.getY(), mc.player.getZ())).getBlock().getSlipperiness() * 0.91F;
        float n6 = mc.player.hasStatusEffect(StatusEffects.JUMP_BOOST) && mc.player.isUsingItem() ? 0.88f : (float) (oldSpeed > 0.32 && mc.player.isUsingItem() ? 0.88 : 0.91F);
        if (mc.player.isOnGround())
            n6 = frictionFactor;

        float n7 = (float) (0.1631f / Math.pow(n6, 3.0f));
        float n8;
        if (mc.player.isOnGround()) {
            n8 = speedAttributes * n7;
            if (move.getY() > 0)
                n8 += mode.is("Elytra") && InventoryHandler.getElytra() != -1 && (disabled && System.currentTimeMillis() - disableTime < 300) ? 0.65f : 0.2f;
            disabled = false;
        } else n8 = 0.0255f;

        boolean noslow = false;
        double max2 = oldSpeed + n8;
        double max = 0.0;

        if (mc.player.isUsingItem() && move.getY() <= 0 ) {
            double n10 = oldSpeed + n8 * 0.25;
            double motionY2 = move.getY();
            if (motionY2 != 0.0 && Math.abs(motionY2) < 0.08) {
                n10 += 0.055;
            }
            if (max2 > (max = Math.max(0.043, n10))) {
                noslow = true;
                ++noSlowTicks;
            } else {
                noSlowTicks = Math.max(noSlowTicks - 1, 0);
            }
        } else {
            noSlowTicks = 0;
        }

        if (noSlowTicks > 3) max2 = max - 0.019;
        else max2 = Math.max(noslow ? 0 : 0.25, max2) - (mc.player.age % 2 == 0 ? 0.001 : 0.002);

        contextFriction = n6;
        if (!mc.player.isOnGround()) {
            needSprintState = !mc.player.lastSprinting;
            needSwap = true;
        } else needSprintState = false;
        return max2;
    }

    public float getAIMoveSpeed() {
        boolean prevSprinting = mc.player.isSprinting();
        mc.player.setSprinting(false);
        float speed = mc.player.getMovementSpeed() * 1.3f;
        mc.player.setSprinting(prevSprinting);
        return speed;
    }

    public static void disabler(int elytra) {
        if (System.currentTimeMillis() - disableTime > 190L) {
            if (elytra != -2) {
                mc.interactionManager.clickSlot(0, elytra, 1, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(0, 6, 1, SlotActionType.PICKUP, mc.player);
            }

            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));

            if (elytra != -2) {
                mc.interactionManager.clickSlot(0, 6, 1, SlotActionType.PICKUP, mc.player);
                mc.interactionManager.clickSlot(0, elytra, 1, SlotActionType.PICKUP, mc.player);
            }
            disableTime = System.currentTimeMillis();
        }
        disabled = true;
    }

    public boolean canStrafe() {
        if (mc.player.isSneaking()) {
            return false;
        }
        if (mc.player.isInLava()) {
            return false;
        }
        if (Minced.getInstance().getModuleHandler().get(Speed.class).isEnabled()) {
            return false;
        }
        if (mc.player.isSubmergedInWater()) {
            return false;
        }
        return !mc.player.getAbilities().flying;
    }

    public Box getBoundingBox() {
        return new Box(mc.player.getX() - 0.1, mc.player.getY(), mc.player.getZ() - 0.1, mc.player.getX() + 0.1, mc.player.getY() + 1, mc.player.getZ() + 0.1);
    }
}