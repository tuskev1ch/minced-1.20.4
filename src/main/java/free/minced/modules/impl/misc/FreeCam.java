package free.minced.modules.impl.misc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import free.minced.events.Event;
import free.minced.events.impl.input.EventKeyboardInput;
import free.minced.events.impl.mobility.EventMove;
import free.minced.events.impl.player.EventSync;
import free.minced.events.impl.player.PacketEvent;
import free.minced.events.impl.player.UpdatePlayerEvent;
import free.minced.framework.render.DrawHandler;
import free.minced.modules.Module;
import free.minced.modules.api.ModuleCategory;
import free.minced.modules.api.ModuleDescriptor;
import free.minced.primary.IHolder;
import free.minced.primary.chat.ChatHandler;
import free.minced.primary.game.MobilityHandler;
import free.minced.systems.setting.impl.BooleanSetting;
import free.minced.systems.setting.impl.NumberSetting;
import org.lwjgl.glfw.GLFW;

import static free.minced.primary.other.KeyHandler.isKeyPressed;

@ModuleDescriptor(name = "FreeCam", category = ModuleCategory.MISC)
public class FreeCam extends Module {
    private final NumberSetting speed = new NumberSetting("Horizontal Speed", this, 1f, 0.1f, 3f, 0.1f);
    private final NumberSetting hspeed = new NumberSetting("Vertical Speed", this, 0.42f, 0.1f, 3f, 0.1f);
    private final BooleanSetting freeze = new BooleanSetting("Freeze", this, false);
    private float fakeYaw, fakePitch, prevFakeYaw, prevFakePitch, prevScroll;
    private double fakeX, fakeY, fakeZ, prevFakeX, prevFakeY, prevFakeZ;
    public LivingEntity trackEntity;

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventSync event) {
            prevFakeYaw = fakeYaw;
            prevFakePitch = fakePitch;

            if (isKeyPressed(GLFW.GLFW_KEY_ESCAPE) || isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT))
                trackEntity = null;

            if (trackEntity != null) {
                fakeYaw = trackEntity.getYaw();
                fakePitch = trackEntity.getPitch();

                prevFakeX = fakeX;
                prevFakeY = fakeY;
                prevFakeZ = fakeZ;

                fakeX = trackEntity.getX();
                fakeY = trackEntity.getY() + trackEntity.getEyeHeight(trackEntity.getPose());
                fakeZ = trackEntity.getZ();
            } else {
                fakeYaw = mc.player.getYaw();
                fakePitch = mc.player.getPitch();
            }

            if (trackEntity == null) {
                double[] motion = MobilityHandler.forward(speed.getValue().floatValue());

                prevFakeX = fakeX;
                prevFakeY = fakeY;
                prevFakeZ = fakeZ;

                fakeX += motion[0];
                fakeZ += motion[1];

                if (mc.options.jumpKey.isPressed())
                    fakeY += hspeed.getValue().floatValue();

                if (mc.options.sneakKey.isPressed())
                    fakeY -= hspeed.getValue().floatValue();
            }

        }

        if (e instanceof EventMove eventMove) {
            if (freeze.isEnabled()) {
                eventMove.setX(0.);
                eventMove.setY(0.);
                eventMove.setZ(0.);
                eventMove.setCancel(true);
            }
        }
        if (e instanceof PacketEvent.Send event) {
            if (freeze.isEnabled() && event.getPacket() instanceof PlayerMoveC2SPacket)
                e.setCancel(true);
        }
    }

    @Override
    public void onEnable() {
        mc.chunkCullingEnabled = false;
        trackEntity = null;

        fakePitch = mc.player.getPitch();
        fakeYaw = mc.player.getYaw();

        prevFakePitch = fakePitch;
        prevFakeYaw = fakeYaw;

        fakeX = mc.player.getX();
        fakeY = mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose());
        fakeZ = mc.player.getZ();

        prevFakeX = mc.player.getX();
        prevFakeY = mc.player.getY();
        prevFakeZ = mc.player.getZ();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (IHolder.fullNullCheck()) return;
        mc.chunkCullingEnabled = true;
        super.onDisable();
    }

    public float getFakeYaw() {
        return (float) DrawHandler.interpolate(prevFakeYaw, fakeYaw, mc.getTickDelta());
    }

    public float getFakePitch() {
        return (float) DrawHandler.interpolate(prevFakePitch, fakePitch, mc.getTickDelta());
    }

    public double getFakeX() {
        return DrawHandler.interpolate(prevFakeX, fakeX, mc.getTickDelta());
    }

    public double getFakeY() {
        return DrawHandler.interpolate(prevFakeY, fakeY, mc.getTickDelta());
    }

    public double getFakeZ() {
        return DrawHandler.interpolate(prevFakeZ, fakeZ, mc.getTickDelta());
    }
}