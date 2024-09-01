package free.minced.systems;


import free.minced.modules.impl.combat.BackTrack;
import free.minced.systems.helpers.IEntityLiving;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import free.minced.Minced;
import free.minced.events.EventCollects;
import free.minced.events.impl.input.binds.InputEvent;
import free.minced.events.impl.player.EventSync;
import free.minced.events.impl.player.PacketEvent;
import free.minced.modules.api.ModuleManager;
import free.minced.modules.impl.misc.UnHook;
import free.minced.systems.draggable.Draggable;
import free.minced.systems.macros.MacrosHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import static free.minced.primary.IHolder.fullNullCheck;
import static free.minced.primary.IHolder.mc;

public class SharedClass {
    public static final ExecutorService executor = Executors.newCachedThreadPool();
    public static int ticksElytraFlying, serverSideSlot;
    public static boolean lockSprint;
    public static boolean serverSprint;
    public static boolean holdMouse;

    private Map<String, Identifier> skinMap = new HashMap<>();

    public static final Identifier ARROW_LOCATION = new Identifier("minced", "textures/arrow.png");
    public static final Identifier LOGO_LOCATION = new Identifier("minced", "textures/logo.png");
    public static final Identifier GLOW_LOCATION = new Identifier("minced", "textures/glow.png");

    public static final Identifier STAR_LOCATION = new Identifier("minced", "textures/star.png");
    public static final Identifier FIRE_FLIES_LOCATION = new Identifier("minced", "textures/glow2.png");

    public static BlockPos GPS_POSITION;

    public static void onMouseKeyReleased(int button) {
        if (mc.currentScreen instanceof ChatScreen) {
            for (Draggable draggable : Minced.getInstance().getDraggableHandler().draggables.values()) { // 0
                draggable.onRelease(button);
            }
        }
        holdMouse = false;
    }

    public static void onMouseKeyPressed(int button) {
        if (button != 0 && !fullNullCheck()) {
            EventCollects.call(new InputEvent(0, button));
        }
        if (mc.currentScreen instanceof ChatScreen) {
            for (Draggable draggable : Minced.getInstance().getDraggableHandler().draggables.values()) { // 1
                draggable.onClick();
            }
        }
        holdMouse = true;
    }

    public static EntityHitResult getEntityHitResult(Entity pShooter, Vec3d pStartVec, Vec3d pEndVec, Box pBoundingBox, Predicate<Entity> pFilter, double pDistance) {
        World level = pShooter.getWorld();
        double d0 = pDistance;
        Entity entity = null;
        Vec3d vec3 = null;

        for(Entity entity1 : level.getOtherEntities(pShooter, pBoundingBox, pFilter)) {
            if (Minced.getInstance().getModuleHandler().get(BackTrack.class).isEnabled())
                if (entity1 instanceof IEntityLiving entity52) {
                    for (BackTrack.Position pos : entity52.getBackTrack()) {
                        Box axisalignedbb1 = entity1.getBoundingBox().expand((double) entity1.getTargetingMargin());

                        Vec3d entPos = entity1.getPos();
                        Box axisalignedbb = new Box(
                                axisalignedbb1.minX - entPos.x + pos.getPos().x,
                                axisalignedbb1.minY - entPos.y + pos.getPos().y,
                                axisalignedbb1.minZ - entPos.z + pos.getPos().z,
                                axisalignedbb1.maxX - entPos.x + pos.getPos().x,
                                axisalignedbb1.maxY - entPos.y + pos.getPos().y,
                                axisalignedbb1.maxZ - entPos.z + pos.getPos().z
                        );

                        Optional<Vec3d> optional = axisalignedbb.raycast(pStartVec, pEndVec);


                        if (axisalignedbb.contains(pStartVec)) {
                            if (d0 >= 0.0D) {
                                entity = entity1;
                                vec3 = optional.orElse(pStartVec);
                                d0 = 0.0D;
                            }
                        } else if (optional.isPresent()) {
                            Vec3d vector3d1 = optional.get();
                            double d1 = pStartVec.squaredDistanceTo(vector3d1);

                            if (d1 < d0 || d0 == 0.0D) {
                                if (entity1.getRootVehicle() == pShooter.getRootVehicle()) {
                                    if (d0 == 0.0D) {
                                        entity = entity1;
                                        vec3 = vector3d1;
                                    }
                                } else {
                                    entity = entity1;
                                    vec3 = vector3d1;
                                    d0 = d1;
                                }
                            }
                        }
                    }
                }

            Box Box = entity1.getBoundingBox().expand((double)entity1.getTargetingMargin());


            Optional<Vec3d> optional = Box.raycast(pStartVec, pEndVec);


            if (Box.contains(pStartVec)) {
                if (d0 >= 0.0D) {
                    entity = entity1;
                    vec3 = optional.orElse(pStartVec);
                    d0 = 0.0D;
                }
            } else if (optional.isPresent()) {
                Vec3d vec31 = optional.get();
                double d1 = pStartVec.squaredDistanceTo(vec31);
                if (d1 < d0 || d0 == 0.0D) {
                    if (entity1.getRootVehicle() == pShooter.getRootVehicle()) {
                        if (d0 == 0.0D) {
                            entity = entity1;
                            vec3 = vec31;
                        }
                    } else {
                        entity = entity1;
                        vec3 = vec31;
                        d0 = d1;
                    }
                }
            }
        }

        return entity == null ? null : new EntityHitResult(entity, vec3);
    }
    private Identifier getTexture(String n) {
        Identifier id = null;
        if (skinMap.containsKey(n))
            id = skinMap.get(n);

        for (PlayerListEntry ple : mc.getNetworkHandler().getPlayerList())
            if (n.contains(ple.getProfile().getName())) {
                id = ple.getSkinTextures().texture();
                if (!skinMap.containsKey(n))
                    skinMap.put(n, id);
                break;
            }

        return id;
    }
    public static void keyPress(int key) {
        if (!fullNullCheck()) {
            EventCollects.call(new InputEvent(1, key));
        }
        if (Minced.getInstance().getModuleHandler().get(UnHook.class).isEnabled() &&
                Minced.getInstance().getModuleHandler().get(UnHook.class).getKey() != key) return;

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
