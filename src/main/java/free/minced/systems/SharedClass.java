package free.minced.systems;


import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import free.minced.Minced;
import free.minced.events.Event;
import free.minced.events.EventCollects;
import free.minced.events.impl.input.binds.InputEvent;
import free.minced.events.impl.player.EventSync;
import free.minced.events.impl.player.PacketEvent;
import free.minced.modules.api.ModuleManager;
import free.minced.modules.impl.misc.UnHook;
import free.minced.primary.IHolder;
import free.minced.primary.chat.ChatHandler;
import free.minced.systems.draggable.Draggable;
import free.minced.systems.macros.MacrosHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static free.minced.primary.IHolder.mc;

public class SharedClass {
    public static ExecutorService executor = Executors.newCachedThreadPool();
    public static int ticksElytraFlying, serverSideSlot;
    public static boolean lockSprint;
    public static boolean serverSprint;

    public static final Identifier ARROW_LOCATION = new Identifier("minced", "textures/arrow.png");


    public static BlockPos GPS_POSITION;

    public static void onMouseKeyReleased(int button) {
        for (Draggable draggable : Minced.getInstance().getDraggableHandler().draggables.values()) { // 0
            draggable.onRelease(button);
        }
    }

    public static void onMouseKeyPressed(int button) {
        if (button != 0) {
            EventCollects.call(new InputEvent(0, button));
        }
        for (Draggable draggable : Minced.getInstance().getDraggableHandler().draggables.values()) { // 1
            draggable.onClick();
        }
    }

    public static void keyPress(int key) {
        EventCollects.call(new InputEvent(1, key));
        if (!Minced.getInstance().getModuleHandler().get(UnHook.class).isEnabled()) {
            if (key == GLFW.GLFW_KEY_RIGHT_SHIFT && MinecraftClient.getInstance() != null) {
                MinecraftClient.getInstance().setScreen(Minced.getInstance().getInterfaceScreen());
            }
        }
        ModuleManager.onKeyPress(key);
        MacrosHandler.onKeyPress(key);

    }

    public static void onSyncWithServer(PacketEvent.@NotNull Send event) {
        if (event.getPacket() instanceof ClientCommandC2SPacket c) {
            if (c.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING || c.getMode() == ClientCommandC2SPacket.Mode.STOP_SPRINTING) {
                if (lockSprint) {
                    event.setCancel(true);
                    return;
                }

                switch (c.getMode()) {
                    case START_SPRINTING -> serverSprint = true;
                    case STOP_SPRINTING -> serverSprint = false;
                }
            }
        }
        if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket slot) {
            if (serverSideSlot == slot.getSelectedSlot()) {
                event.setCancel(true);
                //ChatHandler.display("Double slot packet!");
            }
            serverSideSlot = slot.getSelectedSlot();
        }
    }

    public static void onPacketReceive(PacketEvent.@NotNull Receive event) {
        if (event.getPacket() instanceof UpdateSelectedSlotS2CPacket slot) {
            serverSideSlot = slot.getSlot();
        }
    }



    public static void run(Runnable runnable, long delay) {
        executor.execute(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runnable.run();
        });
    }

    public static void run(Runnable r) {
        executor.execute(r);
    }

    public static void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (Exception ignored) {
        }
    }

    public static void onEventSync(EventSync event) {
        if (mc.player == null) {
            ticksElytraFlying = 0;
            return;
        }
        if (mc.player.isFallFlying() && mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            ticksElytraFlying++;
        } else {
            ticksElytraFlying = 0;
        }
    }
}
