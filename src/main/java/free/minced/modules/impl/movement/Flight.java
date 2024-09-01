package free.minced.modules.impl.movement;


import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.impl.mobility.EventMove;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.framework.render.DrawHandler;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.game.InventoryHandler;
import free.minced.primary.game.MobilityHandler;
import free.minced.primary.game.PlayerHandler;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.ModeSetting;
import free.minced.systems.setting.impl.NumberSetting;

import static net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode.START_FALL_FLYING;

@ModuleDescriptor(name = "Flight", category = ModuleCategory.MOVEMENT)

public class Flight extends Module {
    public final ModeSetting mode = new ModeSetting("Mode", this, "MatrixElytra", "MatrixElytra", "Vanilla");
    public final BooleanSetting stayOffGround = new BooleanSetting("Stay Off Ground", this, false, () -> !mode.is("MatrixElytra"));

    public final NumberSetting XZspeed = new NumberSetting("Horizontal Speed", this, 0.1, 0.1, 2, 0.1);
    public final NumberSetting Yspeed = new NumberSetting("Vertical Speed", this, 0.1, 0.1, 2, 0.1);

    public static int lastStartFalling;
    private float acceleration;
    private boolean disabled, isDisabled;

    @Override
    public void onEvent(Event event) {
        if (event instanceof UpdatePlayerEvent e) {
            if (mode.is("MatrixElytra")) {

                if (mc.player.horizontalCollision ||
                        mc.player.verticalCollision ||
                        (mc.player.input.movementForward == 0.0f && mc.player.input.movementSideways == 0.0f)) {
                    acceleration = 0;
                }


                int elytra = InventoryHandler.getElytra();
                if (elytra == -1) return;
                if (mc.player.isOnGround()) {
                    disabled = false;
                    mc.player.jump();
                } else {
                    disabled = !mc.player.isOnGround() && mc.player.fallDistance > 0.1f && !isDisabled;
                }


                if (disabled) {
                    if (mc.player.age % 2 == 0 && !isDisabled) {
                        matrixDisabler(elytra);
                        isDisabled = true;
                    }
                }
                if (isDisabled) {
                    if (mc.player.age % 8 == 0) {
                        matrixDisabler(elytra);
                        ChatHandler.display("1");
                    }
                }
            } else if (mode.is("Vanilla")) {
                if (mc.player == null) return;
                if (MobilityHandler.isMoving()) {
                    final double[] dir = MobilityHandler.forward(XZspeed.getValue().doubleValue());
                    mc.player.setVelocity(dir[0], -0.1, dir[1]);
                } else mc.player.setVelocity(0, -0.1, 0);

                if (mc.options.jumpKey.isPressed()) mc.player.setVelocity(mc.player.getVelocity().add(0, Yspeed.getValue().doubleValue(), 0));
                if (mc.options.sneakKey.isPressed()) mc.player.setVelocity(mc.player.getVelocity().add(0, -Yspeed.getValue().doubleValue(), 0));

            }
        }
        if (event instanceof EventMove eventMove) {
            if (mode.is("MatrixElytra")) {
                int elytra = InventoryHandler.getElytra();
                if (elytra == -1) return;
                if (!mc.player.isOnGround() && mc.player.fallDistance > 0.25f && isDisabled) {
                    float f3 = Yspeed.getValue().floatValue();
                    if (stayOffGround.isEnabled() && !PlayerHandler.checkGround(0.25f) && lastStartFalling > 1) {
                        IHolder.mc.player.setVelocity(IHolder.mc.player.getVelocity().x, IHolder.mc.player.getVelocity().y + 0.42f, IHolder.mc.player.getVelocity().z);
                    } else {
                        if (!mc.player.isSneaking() && mc.options.jumpKey.isPressed()) {
                            IHolder.mc.player.setVelocity(IHolder.mc.player.getVelocity().x, f3, IHolder.mc.player.getVelocity().z);
                        } else if (mc.options.sneakKey.isPressed()) {
                            IHolder.mc.player.setVelocity(IHolder.mc.player.getVelocity().x, (-f3 - 0.017f), IHolder.mc.player.getVelocity().z);
                        } else {
                            IHolder.mc.player.setVelocity(IHolder.mc.player.getVelocity().x, mc.player.age % 2 == 0 ? -0.08f : 0.08f, IHolder.mc.player.getVelocity().z);
                            eventMove.setY(mc.player.age % 2 == 0 ? -0.08f : 0.08f);

                        }
                    }
                    float f2 = XZspeed.getValue().floatValue() - 0.017f;
                    f2 *= Math.min((acceleration += 9) / 100.0f, 1.0f);

                    double[] xz = MobilityHandler.forward(f2);
                    IHolder.mc.player.setVelocity(xz[0], IHolder.mc.player.getVelocity().y, xz[1]);

                    if (!MobilityHandler.isMoving()) acceleration = 0;

                }
            }
            eventMove.setCancel(true);
        }
    }

    @Override
    public void onDisable() {
        isDisabled = false;
        disabled = false;
        acceleration = 0;
        lastStartFalling = 0;
        super.onDisable();
    }

    public static void matrixDisabler(int elytra) {
        elytra = elytra >= 0 && elytra < 9 ? elytra + 36 : elytra;
        if (elytra != -2) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, elytra, 1, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 1, SlotActionType.PICKUP, mc.player);
        }
        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, START_FALL_FLYING));
        if (elytra != -2) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 6, 1, SlotActionType.PICKUP, mc.player);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, elytra, 1, SlotActionType.PICKUP, mc.player);
        }
        lastStartFalling++;
    }

    /** dont touch **/
    public double getFakeY() {
        return DrawHandler.interpolate(mc.player.prevY, mc.player.getY(), mc.getTickDelta());
    }

    public boolean getElytra() {
        int elytra = InventoryHandler.getElytra();
        return elytra != -1;
    }

    public boolean shouldFixElytra() {
        return (Minced.getInstance().getModuleHandler().get(Flight.class).isEnabled() &&
                Minced.getInstance().getModuleHandler().get(Flight.class).mode.is("MatrixElytra") ||
                Minced.getInstance().getModuleHandler().get(Speed.class).isEnabled() &&
                        Minced.getInstance().getModuleHandler().get(Speed.class).mode.is("MatrixElytra") ) &&
                Minced.getInstance().getModuleHandler().get(Flight.class).getElytra();
    }
}